package me.r.touchgrass.file.files.deprecated;

import me.r.touchgrass.module.Module;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.file.FileManager;

/**
 * Created by r on 03/02/2021
 */
@Deprecated
public class KeybindFile {

    private static final FileManager bindList = new FileManager("binds", "touchgrass");

    public KeybindFile() {
        try {
            loadKeybinds();
        } catch (Exception e) {
        }
    }

    public static void saveKeybinds() {
        try {
            bindList.clear();
            for (Module module : touchgrass.getClient().moduleManager.getModules()) {
                String line = (module.getName() + ":" + module.getKeybind());
                bindList.write(line);
            }
        } catch (Exception e) {
        }
    }

    public static void loadKeybinds() {
        try {
            for (String s : bindList.read()) {
                for (Module module : touchgrass.getClient().moduleManager.getModules()) {
                    String name = s.split(":")[0];
                    int key = Integer.parseInt(s.split(":")[1]);
                    if (module.getName().equalsIgnoreCase(name)) {
                        module.setKeyBind(key);
                    }
                }
            }
        } catch (Exception e) {
        }
    }

}