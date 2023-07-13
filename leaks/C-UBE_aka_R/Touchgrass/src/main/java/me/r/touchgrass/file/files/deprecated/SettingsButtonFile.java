package me.r.touchgrass.file.files.deprecated;

import me.r.touchgrass.touchgrass;
import me.r.touchgrass.file.FileManager;
import me.r.touchgrass.settings.Setting;

/**
 * Created by r on 03/02/2021
 */
@Deprecated
public class SettingsButtonFile {

    private static final FileManager ButtonList = new FileManager("button", "touchgrass");

    public SettingsButtonFile() {
        try {
            loadState();
        } catch (Exception e) {
        }
    }

    public static void saveState() {
        try {
            ButtonList.clear();
            for (Setting setting : touchgrass.getClient().settingsManager.getSettings()) {
                if(setting.isModeButton()) {
                    String line = (setting.getName() + ":" + setting.getParentMod().getName() + ":" + setting.isEnabled());
                    ButtonList.write(line);
                }
            }
        } catch (Exception e) {
        }
    }

    public static void loadState() {
        try {
            for (String s : ButtonList.read()) {
                for (Setting setting : touchgrass.getClient().settingsManager.getSettings()) {
                    String name = s.split(":")[0];
                    String modname = s.split(":")[1];
                    boolean toggled = Boolean.parseBoolean(s.split(":")[2]);
                    if (setting.getName().equalsIgnoreCase(name) && setting.getParentMod().getName().equalsIgnoreCase(modname)) {
                        setting.setState(toggled);
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}
