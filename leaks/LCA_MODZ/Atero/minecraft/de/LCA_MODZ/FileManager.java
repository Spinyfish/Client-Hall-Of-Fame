package de.LCA_MODZ;

import com.google.gson.JsonObject;
import de.verschwiegener.atero.Management;

import java.io.File;

public class FileManager {

    private final File CLIENT_DIRECTORY = new File("Atero");
    private final File CONFIG_DIRECTORY = new File(CLIENT_DIRECTORY, "Config");

    public void setupClientEnvironment() {
        SimpleJson.createConfigEnvironment(CLIENT_DIRECTORY);
        SimpleJson.createConfigEnvironment(CONFIG_DIRECTORY);
    }

    public void writeClientData() {
        JsonObject jsonObject = new JsonObject();
        Management.instance.modulemgr.getModules().forEach(module -> {
            jsonObject.addProperty(module.getName() + " Enabled", module.isEnabled());
            jsonObject.addProperty(module.getName() + " Key", module.getKey());
        });
        SimpleJson.writeConfigFile(CLIENT_DIRECTORY, "Module", jsonObject);
    }

    public void readClientData() {
        if(!new File(CLIENT_DIRECTORY, "Module.json").exists()) {
            return;
        }
        JsonObject jsonObject = SimpleJson.readConfigFile(CLIENT_DIRECTORY, "Module");
        Management.instance.modulemgr.getModules().forEach(module -> {
            module.setEnabled(jsonObject.has(module.getName() + " Enabled") && jsonObject.get(module.getName() + " Enabled").getAsBoolean());
            module.setKey(jsonObject.has(module.getName() + " Key") ? jsonObject.get(module.getName() + " Key").getAsInt() : 0);
        });
    }

    public void saveConfig(String name) {
        JsonObject jsonObject = new JsonObject();
    }

    public File getConfigDirectory() {
        return CONFIG_DIRECTORY;
    }

}
