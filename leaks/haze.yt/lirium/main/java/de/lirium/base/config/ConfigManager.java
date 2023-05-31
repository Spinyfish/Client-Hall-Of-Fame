package de.lirium.base.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.lirium.Client;
import de.lirium.base.setting.SettingRegistry;
import de.lirium.impl.module.ModuleFeature;
import de.lirium.util.interfaces.Logger;
import god.buddy.aot.BCompiler;
import i.dupx.launcher.CLAPI;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.util.Util;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;

public class ConfigManager {

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File DIR = new File(Client.DIR, "Configs");

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static void save(String name) throws IOException {
        final File file = new File(DIR, name + ".cfg");
        String author = CLAPI.getCLUsername();
        String modifiedBy = null;
        if (!DIR.exists())
            if (DIR.mkdir())
                Logger.print("Created config directory");
        if (!file.exists()) {
            if (file.createNewFile())
                Logger.print("Created config file: " + name);
        } else {
            final JsonObject base = gson.fromJson(new FileReader(file), JsonObject.class);
            final String originalAuthor = getString("author", base);
            if (base.has("author") && !originalAuthor.equalsIgnoreCase(author))
                modifiedBy = author;
            author = originalAuthor;
        }

        final JsonObject base = new JsonObject();
        base.addProperty("author", author);
        if (modifiedBy != null && !Client.INSTANCE.isDeveloper())
            base.addProperty("modifiedBy", modifiedBy);
        base.addProperty("creationDate", System.currentTimeMillis());
        final JsonObject modules = new JsonObject();
        Client.INSTANCE.getModuleManager().getFeatures().forEach(moduleFeature -> {
            final JsonObject module = new JsonObject();
            module.addProperty("enabled", moduleFeature.isEnabled());
            module.addProperty("bypassing", moduleFeature.isBypassing());
            final JsonObject settings = new JsonObject();
            SettingRegistry.getSettings(moduleFeature).stream().filter(iSetting -> !iSetting.isVisual()).forEach(iSetting -> settings.addProperty(iSetting.getName(), iSetting.getValue().toString()));
            module.add("settings", settings);
            modules.add(moduleFeature.getName(), module);
        });
        base.add("modules", modules);
        final FileWriter writer = new FileWriter(file);
        writer.write(gson.toJson(base));
        writer.close();
    }

    public static ConfigData load(String name) throws Exception {
        return load(name, false);
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static ConfigData load(String name, boolean visuals) throws Exception {
        final File file = new File(DIR, name + ".cfg");
        if (!DIR.exists())
            if (DIR.mkdir())
                Logger.print("Created config directory");
        if (file.exists()) {
            resetValues();
            final JsonObject json = gson.fromJson(new FileReader(file), JsonObject.class);
            if (json.has("modules")) {
                final JsonObject modules = json.get("modules").getAsJsonObject();
                modules.entrySet().forEach(entry -> {
                    final ModuleFeature feature = Client.INSTANCE.getModuleManager().get(entry.getKey());
                    if (feature != null) {
                        if ((visuals || feature.getCategory() != ModuleFeature.Category.VISUAL) && feature.getCategory() != ModuleFeature.Category.UI) {
                            final JsonObject module = entry.getValue().getAsJsonObject();
                            if (module.has("enabled"))
                                feature.setEnabled(module.get("enabled").getAsBoolean());
                            if (module.has("bypassing"))
                                feature.setBypassing(module.get("bypassing").getAsBoolean());
                            if (module.has("settings")) {
                                final JsonObject settings = module.get("settings").getAsJsonObject();
                                SettingRegistry.getValues().get(feature).forEach(iSetting -> {
                                    if (settings.has(iSetting.getDisplay())) {
                                        iSetting.tryChangeField("value", settings.get(iSetting.getDisplay()).getAsString());
                                    } else {
                                        System.out.println(iSetting.getDisplay() + " not found");
                                    }
                                });
                            }
                        }
                    } else {
                        System.out.println("Module " + entry.getKey() + " not found!");
                    }
                });
            }
            return new ConfigData(getString("author", json), json.has("modifiedBy") ? json.get("modifiedBy").getAsString() : null, getLong("creationDate", json), true);
        } else
            return new ConfigData(false);
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static ConfigData loadOnline(String name, boolean visuals) throws Exception {
        final URL url = new URL("https://raw.githubusercontent.com/Lirium-Team/Configs/master/" + name + ".cfg");

        resetValues();

        final Scanner scanner = new Scanner(url.openStream(), "UTF-8");
        final StringBuilder stringBuilder = new StringBuilder();
        while (scanner.hasNextLine()) {
            stringBuilder.append(scanner.nextLine());
        }
        final JsonObject json = gson.fromJson(stringBuilder.toString(), JsonObject.class);
        if (json.has("modules")) {
            final JsonObject modules = json.get("modules").getAsJsonObject();
            modules.entrySet().forEach(entry -> {
                final ModuleFeature feature = Client.INSTANCE.getModuleManager().get(entry.getKey());
                if (feature != null) {
                    if ((visuals || feature.getCategory() != ModuleFeature.Category.VISUAL) && feature.getCategory() != ModuleFeature.Category.UI) {
                        final JsonObject module = entry.getValue().getAsJsonObject();
                        if (module.has("enabled"))
                            feature.setEnabled(module.get("enabled").getAsBoolean());
                        if (module.has("bypassing"))
                            feature.setBypassing(module.get("bypassing").getAsBoolean());
                        if (module.has("settings")) {
                            final JsonObject settings = module.get("settings").getAsJsonObject();
                            SettingRegistry.getValues().get(feature).forEach(iSetting -> {
                                if (settings.has(iSetting.getDisplay())) {
                                    iSetting.tryChangeField("value", settings.get(iSetting.getDisplay()).getAsString());
                                } else {
                                    System.out.println(iSetting.getDisplay() + " not found");
                                }
                            });
                        }
                    }
                } else {
                    System.out.println("Module " + entry.getKey() + " not found!");
                }
            });
        }
        return new ConfigData(getString("author", json), json.has("modifiedBy") ? json.get("modifiedBy").getAsString() : null, getLong("creationDate", json), true);
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static List<String> getOnline() {
        URL url;
        final List<String> result = new ArrayList<>();
        try {
            url = new URL("https://github.com/Lirium-Team/Configs");
            final Scanner sc = new Scanner(url.openStream());
            String line;
            while (sc.hasNext()) {
                line = sc.nextLine();
                if (line.contains(".cfg"))
                    result.add(line.replaceAll("\\<.*?\\>", "").replace(" ", "").replace(".cfg", ""));
            }
        } catch (final IOException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static List<String> getConfigs() {
        final ArrayList<String> configs = new ArrayList<>();
        if (DIR.exists()) {
            for (final File file : Objects.requireNonNull(DIR.listFiles())) {
                if (file.getName().endsWith(".cfg")) {
                    configs.add(file.getName().replace(".cfg", ""));
                }
            }
        }
        return configs;
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static void openFolder() {
        if (!DIR.exists())
            if (DIR.mkdir())
                Logger.print("Created config directory");
        final String absolutePath = DIR.getAbsolutePath();

        if (Util.getOSType() == Util.EnumOS.OSX) {
            try {
                Runtime.getRuntime().exec(new String[]{"/usr/bin/open", absolutePath});
            } catch (IOException ignored) {
            }
        } else if (Util.getOSType() == Util.EnumOS.WINDOWS) {
            String s1 = String.format("cmd.exe /C start \"Open file\" \"%s\"", absolutePath);
            try {
                Runtime.getRuntime().exec(s1);
            } catch (IOException ignored) {
            }
        }
    }

    private static String getString(String name, JsonObject json) {
        return json.has(name) ? json.get(name).getAsString() : "unknown";
    }

    private static long getLong(String name, JsonObject json) {
        return json.has(name) ? json.get(name).getAsLong() : -1;
    }

    private static void resetValues() {
        Client.INSTANCE.getModuleManager().getFeatures().forEach(moduleFeature -> {
            if (moduleFeature.getCategory() != ModuleFeature.Category.UI && moduleFeature.getCategory() != ModuleFeature.Category.VISUAL) {
                moduleFeature.setEnabled(false);
                moduleFeature.setBypassing(false);
                SettingRegistry.getSettings(moduleFeature).forEach(iSetting -> {
                    if (!iSetting.isVisual())
                        iSetting.reset();
                });
            }
        });
    }

    @AllArgsConstructor
    @Getter
    @Setter
    public static class ConfigData {
        final String author, modifiedBy;
        final long creationDate;
        final boolean loaded;

        public ConfigData(boolean loaded) {
            this.loaded = loaded;
            this.author = null;
            this.modifiedBy = null;
            this.creationDate = -1;
        }
    }
}
