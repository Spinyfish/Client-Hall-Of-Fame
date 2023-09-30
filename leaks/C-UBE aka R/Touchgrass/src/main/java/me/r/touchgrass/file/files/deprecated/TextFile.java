package me.r.touchgrass.file.files.deprecated;

import me.r.touchgrass.touchgrass;
import me.r.touchgrass.file.FileManager;
import me.r.touchgrass.settings.Setting;

/**
 * Created by r on 04/01/2022
 */
@Deprecated
public class TextFile {

    private static final FileManager TextList = new FileManager("text", "touchgrass");


    public TextFile() {
        try {
            loadState();
        } catch (Exception e) {
        }
    }

    public static void saveState() {
        try {
            TextList.clear();
            for (Setting setting : touchgrass.getClient().settingsManager.getSettings()) {
                if(setting.getText() != null) {
                    String line = (setting.getName() + ":" + setting.getParentMod().getName() + ":\"" + setting.getText() + "\"");
                    TextList.write(line);
                }
            }
        } catch (Exception e) {
        }
    }

    public static void loadState() {
        try {
            for (String s : TextList.read()) {
                for (Setting setting : touchgrass.getClient().settingsManager.getSettings()) {
                    String name = s.split(":")[0];
                    String modname = s.split(":")[1];
                    String text = String.valueOf(s.split("\"")[1]);
                    if (setting.getName().equalsIgnoreCase(name) && setting.getParentMod().getName().equalsIgnoreCase(modname)) {
                        setting.setText(text);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
