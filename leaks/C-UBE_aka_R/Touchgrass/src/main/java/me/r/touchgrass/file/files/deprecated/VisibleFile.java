package me.r.touchgrass.file.files.deprecated;

import me.r.touchgrass.touchgrass;
import me.r.touchgrass.file.FileManager;
import me.r.touchgrass.module.Module;

/**
 * Created by r on 24/12/2021
 */
@Deprecated
public class VisibleFile {

    private static final FileManager VisibleList = new FileManager("visible", "touchgrass");

    // TODO: put all module settings into a json instead of seperate txt files some time

    public VisibleFile() {
        try {
            loadState();
        } catch (Exception e) {
        }
    }

    public static void saveState() {
        try {
            VisibleList.clear();
            for (Module mod : touchgrass.getClient().moduleManager.getModules()) {
                String line = (mod.getName() + ":" + mod.isVisible());
                VisibleList.write(line);
            }
        } catch (Exception e) {
        }
    }

    public static void loadState() {
        try {
            for (String s : VisibleList.read()) {
                for (Module mod : touchgrass.getClient().moduleManager.getModules()) {
                    String name = s.split(":")[0];
                    boolean visible = Boolean.parseBoolean(s.split(":")[1]);
                    if (mod.getName().equalsIgnoreCase(name)) {
                        mod.setVisible(visible);
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}