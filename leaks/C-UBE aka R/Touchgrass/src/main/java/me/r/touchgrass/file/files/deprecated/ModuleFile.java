package me.r.touchgrass.file.files.deprecated;

import me.r.touchgrass.module.Module;
import me.r.touchgrass.touchgrass;
import me.r.touchgrass.file.FileManager;

/**
 * Created by r on 08/02/2021
 */
@Deprecated
public class ModuleFile {

    private static final FileManager ModuleList = new FileManager("modules", "touchgrass");

    public ModuleFile() {
        try {
            loadModules();
        } catch (Exception e) {
        }
    }

    public static void saveModules() {
        try {
            ModuleList.clear();
            for (Module module : touchgrass.getClient().moduleManager.getModules()) {
                String line = (module.getName() + ":" + module.isEnabled());
                ModuleList.write(line);
            }
        } catch (Exception e) {
        }
    }

    public static void loadModules() {
        try {
            for (String s : ModuleList.read()) {
                for (Module module : touchgrass.getClient().moduleManager.getModules()) {
                    String name = s.split(":")[0];
                    boolean toggled = Boolean.parseBoolean(s.split(":")[1]);
                    if (module.getName().equalsIgnoreCase(name) && toggled) {
                        module.toggle();
                    }
                }
            }
        } catch (Exception e) {
        }
    }
}