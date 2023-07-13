package me.r.touchgrass.file.files.deprecated;

import me.r.touchgrass.touchgrass;
import me.r.touchgrass.file.FileManager;
import me.r.touchgrass.settings.Setting;

/**
 * Created by r on 03/02/2021
 */
@Deprecated
public class SettingsComboBoxFile {

    private static final FileManager ComboSetting = new FileManager("combobox", "touchgrass");

    public SettingsComboBoxFile() {
        try {
            loadState();
        } catch (Exception e) {
        }
    }

    public static void saveState() {
        try {
            ComboSetting.clear();
            for (Setting setting : touchgrass.getClient().settingsManager.getSettings()) {
                if(setting.isModeMode()) {
                    String line = (setting.getName() + ":" + setting.getParentMod().getName() + (String.valueOf(setting.getMode()) != null ? ":" + setting.getMode() : ""));
                    ComboSetting.write(line);
                }
            }
        } catch (Exception e) {
        }
    }

    public static void loadState() {
        try {
            for (String s : ComboSetting.read()) {
                for (Setting setting : touchgrass.getClient().settingsManager.getSettings()) {
                    String name = s.split(":")[0];
                    String modname = s.split(":")[1];
                    String Setting = String.valueOf(s.split(":")[2]);
                    if (setting.getName().equalsIgnoreCase(name) && setting.getParentMod().getName().equalsIgnoreCase(modname)) {
                        setting.setMode(Setting);
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
