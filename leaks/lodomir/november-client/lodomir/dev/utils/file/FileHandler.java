/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.file;

import java.io.File;
import java.util.Objects;
import lodomir.dev.November;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.Setting;
import lodomir.dev.settings.impl.BooleanSetting;
import lodomir.dev.settings.impl.ModeSetting;
import lodomir.dev.settings.impl.NumberSetting;
import lodomir.dev.utils.file.FileUtils;

public class FileHandler {
    private final String s = File.separator;
    private final FileUtils fileUtils = new FileUtils();

    public void save() {
        StringBuilder configBuilder = new StringBuilder();
        for (Module m : November.INSTANCE.getModuleManager().getModules()) {
            String moduleName = m.getName();
            configBuilder.append("Toggle_").append(moduleName).append("_").append(m.isEnabled()).append("\r\n");
            for (Setting s : m.getSettings()) {
                if (s instanceof BooleanSetting) {
                    configBuilder.append("BooleanSetting_").append(moduleName).append("_").append(s.getName()).append("_").append(((BooleanSetting)s).isEnabled()).append("\r\n");
                }
                if (s instanceof NumberSetting) {
                    configBuilder.append("NumberSetting_").append(moduleName).append("_").append(s.getName()).append("_").append(((NumberSetting)s).getValue()).append("\r\n");
                }
                if (!(s instanceof ModeSetting)) continue;
                configBuilder.append("ModeSetting_").append(moduleName).append("_").append(s.getName()).append("_").append(((ModeSetting)s).getMode()).append("\r\n");
            }
            configBuilder.append("Bind_").append(moduleName).append("_").append(m.getKey()).append("\r\n");
        }
        this.fileUtils.saveFile("settings.txt", true, configBuilder.toString());
    }

    public void load() {
        String config = this.fileUtils.loadFile("settings.txt");
        if (config == null) {
            return;
        }
        String[] configLines = config.split("\r\n");
        for (Module m : November.INSTANCE.getModuleManager().getModules()) {
            if (!m.isEnabled()) continue;
            m.toggle();
        }
        for (Module m : November.INSTANCE.getModuleManager().getModules()) {
            if (!m.isEnabled()) continue;
            m.toggle();
        }
        for (String line : configLines) {
            Module m;
            Module module;
            if (line == null) {
                return;
            }
            String[] split = line.split("_");
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
            if (split[0].contains("ModeSetting") && setting instanceof ModeSetting) {
                ((ModeSetting)setting).setMode(split[3]);
            }
            if (!split[0].contains("Bind") || (m = November.INSTANCE.getModuleManager().getModule(split[1])) == null) continue;
            Objects.requireNonNull(m).setKey(Integer.parseInt(split[2]));
        }
    }
}

