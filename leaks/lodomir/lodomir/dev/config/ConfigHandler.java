/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.config;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Scanner;
import lodomir.dev.November;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.Setting;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.ui.notification.Notification;
import lodomir.dev.ui.notification.NotificationManager;
import lodomir.dev.ui.notification.NotificationType;
import lodomir.dev.utils.file.FileUtils;

public final class ConfigHandler {
    private final String s = File.separator;
    private final FileUtils fileUtils = new FileUtils();

    public void save(String name) {
        StringBuilder configBuilder = new StringBuilder();
        for (Module m : November.INSTANCE.getModuleManager().getModules()) {
            String moduleName = m.getName();
            configBuilder.append("Toggle_").append(moduleName).append("_").append(m.isEnabled()).append("\r\n");
            for (Setting setting : m.getSettings()) {
                if (setting instanceof BooleanSetting) {
                    configBuilder.append("BooleanSetting_").append(moduleName).append("_").append(setting.getName()).append("_").append(((BooleanSetting)setting).isEnabled()).append("\r\n");
                }
                if (setting instanceof NumberSetting) {
                    configBuilder.append("NumberSetting_").append(moduleName).append("_").append(setting.getName()).append("_").append(((NumberSetting)setting).getValue()).append("\r\n");
                }
                if (!(setting instanceof ModeSetting)) continue;
                configBuilder.append("ModeSetting_").append(moduleName).append("_").append(setting.getName()).append("_").append(((ModeSetting)setting).getMode()).append("\r\n");
            }
        }
        this.fileUtils.saveFile("Config" + this.s + name + ".txt", true, configBuilder.toString());
        NotificationManager.show(new Notification(NotificationType.SUCCESS, "Config Manager", "Config saved " + name, 5));
    }

    public void load(String name) {
        String config = this.fileUtils.loadFile("Config" + this.s + name + ".txt");
        if (config == null) {
            NotificationManager.show(new Notification(NotificationType.ERROR, "Config Manager", "Config doesn't exist", 5));
            return;
        }
        String[] configLines = config.split("\r\n");
        for (Module m : November.INSTANCE.getModuleManager().getModules()) {
            if (!m.isEnabled()) continue;
            m.toggle();
        }
        for (String line : configLines) {
            Module module;
            String[] split = line.split("_");
            if (split.length < 3) continue;
            if (split[0].contains("Toggle") && split[2].contains("true") && November.INSTANCE.getModuleManager().getModule(split[1]) != null && !(module = Objects.requireNonNull(November.INSTANCE.getModuleManager().getModule(split[1]))).isEnabled()) {
                module.toggle();
            }
            Setting setting = November.INSTANCE.getModuleManager().getSetting(split[1], split[2]);
            if (November.INSTANCE.getModuleManager().getModule(split[1]) == null) continue;
            if (split[0].contains("BooleanSetting") && setting instanceof BooleanSetting) {
                if (split[3].contains("true")) {
                    ((BooleanSetting)setting).setEnabled(true);
                }
                if (split[3].contains("false")) {
                    ((BooleanSetting)setting).setEnabled(false);
                }
            }
            if (split[0].contains("NumberSetting") && setting instanceof NumberSetting) {
                ((NumberSetting)setting).setValue(Double.parseDouble(split[3]));
            }
            if (!split[0].contains("ModeSetting") || !(setting instanceof ModeSetting)) continue;
            ((ModeSetting)setting).setMode(split[3]);
        }
        NotificationManager.show(new Notification(NotificationType.SUCCESS, "Config Manager", "Config loaded " + name, 5));
    }

    public void loadFromList(List<String> list) {
        for (Module m : November.INSTANCE.getModuleManager().getModules()) {
            if (!m.isEnabled() || m.getName().toLowerCase().contains("clickgui")) continue;
            m.toggle();
        }
        for (String line : list) {
            Module module;
            if (line == null) {
                return;
            }
            String[] split = line.split("_");
            if (November.INSTANCE.getModuleManager().getModule(split[1]) != null && (module = Objects.requireNonNull(November.INSTANCE.getModuleManager().getModule(split[1]))).getName().toLowerCase().contains("clickgui")) continue;
            if (split[0].contains("Toggle") && split[2].contains("true") && November.INSTANCE.getModuleManager().getModule(split[1]) != null && !(module = Objects.requireNonNull(November.INSTANCE.getModuleManager().getModule(split[1]))).isEnabled()) {
                module.toggle();
            }
            Setting setting = November.INSTANCE.getModuleManager().getSetting(split[1], split[2]);
            if (split[0].contains("BooleanSetting") && setting instanceof BooleanSetting) {
                if (split[3].contains("true")) {
                    ((BooleanSetting)setting).setEnabled(true);
                }
                if (split[3].contains("false")) {
                    ((BooleanSetting)setting).setEnabled(false);
                }
            }
            if (split[0].contains("NumberSetting") && setting instanceof NumberSetting) {
                ((NumberSetting)setting).setValue(Double.parseDouble(split[3]));
            }
            if (!split[0].contains("ModeSetting") || !(setting instanceof ModeSetting)) continue;
            ((ModeSetting)setting).setMode(split[3]);
        }
    }

    public void list() {
        File configFolder;
        if (!this.fileUtils.exists("Config\\")) {
            NotificationManager.show(new Notification(NotificationType.ERROR, "Config Manager", "There are no configs created", 5));
        }
        if ((configFolder = this.fileUtils.getFileOrPath("Config\\")).listFiles() == null || Objects.requireNonNull(configFolder.listFiles()).length < 1) {
            NotificationManager.show(new Notification(NotificationType.ERROR, "Config Manager", "There are no configs created", 5));
        } else {
            November.Log("List of config files: ");
            for (File file : Objects.requireNonNull(configFolder.listFiles())) {
                November.Log(" - " + file.getName().replace(".txt", ""));
            }
        }
    }

    public void delete(String name) {
        if (this.fileUtils.exists("Config\\" + name + ".txt")) {
            NotificationManager.show(new Notification(NotificationType.ERROR, "Config Manager", "Config doesn't exist", 5));
            return;
        }
        this.fileUtils.delete("Config\\" + name + ".txt");
        NotificationManager.show(new Notification(NotificationType.SUCCESS, "Config Manager", "Config" + name + " has been deleted", 5));
    }

    public void loadFromRes(String name) {
        File loadFile;
        URL defaultImage = ConfigHandler.class.getResource(this.s + "assets" + this.s + "minecraft" + this.s + "November" + this.s + "defaultcfg" + this.s + name + ".txt");
        try {
            loadFile = new File(defaultImage.toURI());
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            NotificationManager.show(new Notification(NotificationType.ERROR, "Config Manager", "Error while loading config", 5));
            return;
        }
        NotificationManager.show(new Notification(NotificationType.INFO, "Config Manager", loadFile.getAbsolutePath(), 5));
        boolean exists = loadFile.exists();
        if (!exists) {
            NotificationManager.show(new Notification(NotificationType.ERROR, "Config Manager", "Error while loading config", 5));
            return;
        }
        Scanner scan = null;
        try {
            scan = new Scanner(loadFile);
        }
        catch (IOException e1) {
            NotificationManager.show(new Notification(NotificationType.ERROR, "Config Manager", "Error while loading config", 5));
            e1.printStackTrace();
        }
        for (Module m : November.INSTANCE.getModuleManager().getModules()) {
            if (!m.isEnabled()) continue;
            m.toggle();
        }
        while (true) {
            Module module;
            assert (scan != null);
            if (!scan.hasNextLine()) break;
            String line = scan.nextLine();
            if (line == null) {
                return;
            }
            String[] spit = line.split("_");
            if (spit[0].contains("Toggle") && spit[2].contains("true") && November.INSTANCE.getModuleManager().getModule(spit[1]) != null && !(module = Objects.requireNonNull(November.INSTANCE.getModuleManager().getModule(spit[1]))).isEnabled()) {
                module.toggle();
            }
            Setting setting = November.INSTANCE.getModuleManager().getSetting(spit[1], spit[2]);
            if (spit[0].contains("BooleanSetting") && setting instanceof BooleanSetting) {
                if (spit[3].contains("true")) {
                    ((BooleanSetting)setting).setEnabled(true);
                }
                if (spit[3].contains("false")) {
                    ((BooleanSetting)setting).setEnabled(false);
                }
            }
            if (spit[0].contains("NumberSetting") && setting instanceof NumberSetting) {
                ((NumberSetting)setting).setValue(Double.parseDouble(spit[3]));
            }
            if (!spit[0].contains("ModeSetting") || !(setting instanceof ModeSetting)) continue;
            ((ModeSetting)setting).setMode(spit[3]);
        }
        NotificationManager.show(new Notification(NotificationType.SUCCESS, "Config Manager", "Config loaded" + name, 5));
    }
}

