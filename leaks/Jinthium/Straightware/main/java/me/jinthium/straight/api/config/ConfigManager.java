package me.jinthium.straight.api.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.internal.LinkedTreeMap;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.notification.NotificationType;
import me.jinthium.straight.api.setting.Setting;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.manager.DragManager;
import me.jinthium.straight.impl.settings.*;
import me.jinthium.straight.impl.settings.mode.ModuleMode;
import net.minecraft.client.Minecraft;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

public class ConfigManager {
    public final List<LocalConfig> localConfigs = new ArrayList<>();
    public File defaultConfig;
    public final File file = new File(Minecraft.getMinecraft().mcDataDir, "/Straightware/Configs");
    private final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();

    public void collectConfigs() {
        localConfigs.clear();
        file.mkdirs();

        //For each config in the config folder it adds it to the list and removes the ".json" from the name
        Arrays.stream(Objects.requireNonNull(file.listFiles())).forEach(f -> localConfigs.add(new LocalConfig(f.getName().split("\\.")[0])));
    }

    public LocalConfig getConfig(String name){
        return localConfigs.stream().filter(config -> config.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }


    /**
     * Saving config method
     *
     * @see ConfigManager#serialize() to serialize the modules and settings
     */
    public void saveConfig(String name, String content) {
        LocalConfig localConfig = new LocalConfig(name);
        localConfig.getFile().getParentFile().mkdirs();
        try {
            DragManager.saveDragData();
            Files.writeString(localConfig.getFile().toPath(), content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveConfig(String name) {
        saveConfig(name, serialize());
    }


    public boolean delete(String configName) {
        List<LocalConfig> configsMatch = localConfigs.stream().filter(localConfig -> localConfig.getName().equals(configName)).toList();
        try {
            LocalConfig configToDelete = configsMatch.get(0);
            Files.deleteIfExists(configToDelete.getFile().toPath());
        } catch (IOException | IndexOutOfBoundsException e) {
            e.printStackTrace();
            Client.INSTANCE.getNotificationManager().post("Config Manager", "Failed to delete config!", NotificationType.WARNING);
            return false;
        }
        return true;
    }

    public void saveDefaultConfig() {
        defaultConfig.getParentFile().mkdirs();
        try {
            DragManager.saveDragData();
            Files.writeString(defaultConfig.toPath(), serialize());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("Failed to save " + defaultConfig);
        }
    }

    public String serialize() {
        for (Module module : Client.INSTANCE.getModuleManager().getModules()) {
            List<ConfigSetting> settings = new ArrayList<>();
            for (Setting setting : module.getSettingsList()) {
                ConfigSetting cfgSetting = new ConfigSetting(null, null);
                cfgSetting.name = setting.name;
                cfgSetting.value = setting.getConfigValue();
                settings.add(cfgSetting);
            }
            module.cfgSettings = settings.toArray(new ConfigSetting[0]);
        }
        return gson.toJson(Client.INSTANCE.getModuleManager().getModules());
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

        DragManager.loadDragData();
        for (Module module : Client.INSTANCE.getModuleManager().getModules()) {
            for (Module configModule : modules) {
                if (module.getName().equalsIgnoreCase(configModule.getName())) {
                    try {
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
                                        BooleanSetting booleanSetting = (BooleanSetting) setting;
                                        ((BooleanSetting) setting).setState(Boolean.parseBoolean(String.valueOf(cfgSetting.value)));
                                    }
                                    if (setting instanceof ModeSetting ms) {
                                        String value = String.valueOf(cfgSetting.value);
                                        if (ms.modes.contains(value)) {
                                            ms.setCurrentMode(value);
                                        } else {
                                            ms.setCurrentMode(ms.modes.get(0));
                                            System.out.printf("The value of setting %s in module %s was reset%n", ms.name, module.getName());
                                        }
                                    }
                                    if(setting instanceof NewModeSetting nms){
                                        String value = String.valueOf(cfgSetting.value);
                                        if(nms.getValues().contains(nms.findByString(value))){
                                            nms.setValue(nms.findByString(value));
                                            module.setCurrentMode(nms.getCurrentMode());
                                        }else{
                                            nms.setValue(nms.getValues().get(0));
                                            module.setCurrentMode(nms.getValues().get(0));
                                            System.out.println("Failed to get mode, reset!");
                                        }

                                        module.setCurrentMode(nms.getCurrentMode());
                                    }
                                    if (setting instanceof NumberSetting ss) {
                                        double value;
                                        try {
                                            value = Double.parseDouble(String.valueOf(cfgSetting.value));
                                        } catch (NumberFormatException e) {
                                            value = ss.getDefaultValue();
                                            System.out.printf("The value of setting %s in module %s was reset", ss.name, module.getName());
                                        }
                                        ss.setValue(value);
                                        ss.getImEquivalent().set((float)value);
                                    }
                                    if (setting instanceof ColorSetting colorSetting) {
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
                                            StringSetting stringSetting = (StringSetting) setting;
                                            stringSetting.setString(value);
                                            stringSetting.getImEquiv().set(value);
                                        }
                                    }

                                    if (setting instanceof MultiBoolSetting) {
                                        try {
                                            LinkedTreeMap<String, Boolean> boolMap = (LinkedTreeMap<String, Boolean>) cfgSetting.value;
                                            MultiBoolSetting mbs = (MultiBoolSetting) setting;
                                            for (String s : boolMap.keySet()) {
                                                BooleanSetting childSetting = mbs.getSetting(s);
                                                if (childSetting != null && boolMap.get(s) != null) {
                                                    childSetting.setState(boolMap.get(s));
                                                }
                                            }
                                        }catch(Exception ex){
                                            ex.printStackTrace();
                                        }
                                    }
                                }
                            }
                        }
                        if(module.isEnabled())
                            module.toggle();


                        if (module.isEnabled() != configModule.isEnabled()) {
                            module.toggle();
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