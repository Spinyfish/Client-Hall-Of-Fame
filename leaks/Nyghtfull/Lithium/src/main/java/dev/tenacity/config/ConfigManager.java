package dev.tenacity.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import dev.tenacity.Tenacity;
import dev.tenacity.module.Category;
import dev.tenacity.module.Module;
import dev.tenacity.module.settings.Setting;
import dev.tenacity.module.settings.impl.*;
import dev.tenacity.ui.notifications.NotificationManager;
import dev.tenacity.ui.notifications.NotificationType;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class ConfigManager {
    public static final List<LocalConfig> localConfigs = new ArrayList<>();
    public static boolean loadVisuals = true;
    public static File defaultConfig;
    public final File file = new File(Minecraft.getMinecraft().mcDataDir, "/Tenacity/Configs");
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public void collectConfigs() {
        localConfigs.clear();
        file.mkdirs();

        //For each config in the config folder it adds it to the list and removes the ".json" from the name
        Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(f -> localConfigs.add(new LocalConfig(f.getName().split("\\.")[0])));
    }


    /**
     * Saving config method
     *
     * @see ConfigManager#serialize() to serialize the modules and settings
     */
    public boolean saveConfig(String name, String content) {
        LocalConfig localConfig = new LocalConfig(name);
        localConfig.getFile().getParentFile().mkdirs();
        try {
            Files.write(localConfig.getFile().toPath(), content.getBytes(StandardCharsets.UTF_8));
            collectConfigs();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveConfig(String name) {
        return saveConfig(name, serialize());
    }


    public boolean delete(String configName) {
        List<LocalConfig> configsMatch = localConfigs.stream().filter(localConfig -> localConfig.getName().equals(configName)).collect(Collectors.toList());
        try {
            LocalConfig configToDelete = configsMatch.get(0);
            Files.deleteIfExists(configToDelete.getFile().toPath());
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            NotificationManager.post(NotificationType.WARNING, "Config Manager", "Failed to delete config!");
            return false;
        }
        return true;
    }

    public void saveDefaultConfig() {
        defaultConfig.getParentFile().mkdirs();
        try {
            Files.write(defaultConfig.toPath(), serialize().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save " + defaultConfig);
        }
    }

    public String serialize() {
        for (Module module : Tenacity.INSTANCE.getModuleCollection().getModules()) {
            if (module.getCategory().equals(Category.SCRIPTS)) continue;
            List<ConfigSetting> settings = new ArrayList<>();
            for (Setting setting : module.getSettingsList()) {
                ConfigSetting cfgSetting = new ConfigSetting(null, null);
                cfgSetting.name = setting.name;
                cfgSetting.value = setting.getConfigValue();
                settings.add(cfgSetting);
            }
            module.cfgSettings = settings.toArray(new ConfigSetting[0]);
        }
        return gson.toJson(Tenacity.INSTANCE.getModuleCollection().getModules());
    }

    public String readConfigData(Path configPath) {
        try {
            return new String(Files.readAllBytes(configPath));
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
    }

    public boolean loadConfig(String data) {
        return loadConfig(data, false);
    }

    public boolean loadConfig(String data, boolean keybinds) {
        Module[] modules = gson.fromJson(data, Module[].class);

        for (Module module : Tenacity.INSTANCE.getModuleCollection().getModules()) {
            if (!keybinds) {
                if (!loadVisuals && module.getCategory().equals(Category.RENDER)) continue;
            }
            if (module.getCategory().equals(Category.SCRIPTS)) continue;

            for (Module configModule : modules) {
                if (module.getName().equalsIgnoreCase(configModule.getName())) {
                    try {
                        if (module.isEnabled() != configModule.isEnabled()) {
                            module.toggleSilent();
                        }
                        for (Setting setting : module.getSettingsList()) {
                            for (ConfigSetting cfgSetting : configModule.cfgSettings) {
                                if (setting.name.equals(cfgSetting.name)) {
                                    if (setting instanceof KeybindSetting) {
                                        if (keybinds) {
                                            KeybindSetting keybindSetting = (KeybindSetting) setting;
                                            keybindSetting.setCode(Double.valueOf(String.valueOf(cfgSetting.value)).intValue());
                                        }
                                    }
                                    if (setting instanceof BooleanSetting) {
                                        ((BooleanSetting) setting).setState(Boolean.parseBoolean(String.valueOf(cfgSetting.value)));
                                    }
                                    if (setting instanceof ModeSetting) {
                                        String value = String.valueOf(cfgSetting.value);
                                        ModeSetting ms = (ModeSetting) setting;
                                        if (ms.modes.contains(value)) {
                                            ms.setCurrentMode(value);
                                        } else {
                                            ms.setCurrentMode(ms.modes.get(0));
                                            Tenacity.LOGGER.info(String.format("The value of setting %s in module %s was reset", ms.name, module.getName()));
                                        }
                                    }
                                    if (setting instanceof NumberSetting) {
                                        NumberSetting ss = (NumberSetting) setting;
                                        double value;
                                        try {
                                            value = Double.parseDouble(String.valueOf(cfgSetting.value));
                                        } catch (NumberFormatException e) {
                                            value = ss.getDefaultValue();
                                            Tenacity.LOGGER.info(String.format("The value of setting %s in module %s was reset", ss.name, module.getName()));
                                        }
                                        ss.setValue(value);
                                    }
                                    if (setting instanceof MultipleBoolSetting) {
                                        LinkedTreeMap<String, Boolean> boolMap = (LinkedTreeMap<String, Boolean>) cfgSetting.value;
                                        MultipleBoolSetting mbs = (MultipleBoolSetting) setting;
                                        for (String s : boolMap.keySet()) {
                                            BooleanSetting childSetting = mbs.getSetting(s);
                                            if (childSetting != null && boolMap.get(s) != null) {
                                                childSetting.setState(boolMap.get(s));
                                            }
                                        }
                                    }
                                    if (setting instanceof ColorSetting) {
                                        ColorSetting colorSetting = (ColorSetting) setting;
                                        //If it is rainbow
                                        if (JsonParser.parseString(cfgSetting.value.toString()).isJsonObject()) {
                                            JsonObject colorObject = JsonParser.parseString(cfgSetting.value.toString()).getAsJsonObject();
                                            colorSetting.setRainbow(true);
                                            float saturation = colorObject.get("saturation").getAsFloat();
                                            int speed = colorObject.get("speed").getAsInt();
                                            colorSetting.getRainbow().setSaturation(saturation);
                                            colorSetting.getRainbow().setSpeed(speed);
                                        } else {
                                            int color = Double.valueOf(String.valueOf(cfgSetting.value)).intValue();
                                            Color c = new Color(color);
                                            float[] hsb = Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), null);
                                            colorSetting.setColor(hsb[0], hsb[1], hsb[2]);
                                        }
                                    }
                                    if (setting instanceof StringSetting) {
                                        String value = String.valueOf(cfgSetting.value);
                                        if (value != null) {
                                            ((StringSetting) setting).setString(value);
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return true;
    }
}
