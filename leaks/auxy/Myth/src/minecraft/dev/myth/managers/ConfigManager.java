/**
 * @project Myth
 * @author CodeMan
 * @at 07.08.22, 15:46
 */
package dev.myth.managers;

import com.google.gson.JsonObject;
import dev.myth.api.config.Config;
import dev.myth.api.interfaces.IMethods;
import dev.myth.api.logger.Logger;
import dev.myth.api.manager.Manager;
import dev.myth.api.utils.FileUtil;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.gui.GuiMainMenu;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class ConfigManager implements Manager, IMethods {

    @Getter private final Map<String, Config> configs = new HashMap<>();
    private final File configFolder = new File(MC.mcDataDir, "myth/config");
    private final File configFile = new File(MC.mcDataDir, "myth/config.json");
    private Config activeConfig;
    @Getter @Setter private boolean loadVisuals;

    @Override
    public void run() {
        if (configFile.exists()) {
            JsonObject object = FileUtil.readJsonFromFile(configFile.getAbsolutePath());
            if(object.has("loadVisuals")) {
                loadVisuals = object.get("loadVisuals").getAsBoolean();
            }
        }

        if(!configFolder.exists()) configFolder.mkdirs();
        if(!configFolder.isDirectory()) {
            Logger.doLog(Logger.Type.ERROR, "Config folder is not a directory!");
            return;
        }
        loadClientSettings();
        for(File file : Objects.requireNonNull(configFolder.listFiles())) {
            if(file.isFile() && file.getName().endsWith(".myth")) {
                String name = file.getName().replace(".myth", "");
                Config config = new Config(name, name.equalsIgnoreCase("default"));
                configs.put(config.getName(), config);
            }
        }

        if(getConfig("default") == null) {
            Config config = new Config("default", true);
            config.write();
            configs.put(config.getName(), config);
        } else {
            getConfig("default").read(true);
        }
    }

    @Override
    public void shutdown() {
        saveClientSettings();
        if(getConfig("default") == null) {
            Config config = new Config("default", true);
            config.write();
        } else {
            getConfig("default").write();
        }

        JsonObject object = new JsonObject();
        object.addProperty("loadVisuals", loadVisuals);
        FileUtil.writeJsonToFile(object, configFile.getAbsolutePath());
    }

    public Config getConfig(String name) {
        return configs.keySet().stream().filter(key -> key.equalsIgnoreCase(name)).findFirst().map(configs::get).orElse(null);
    }

    public Config createConfig(String name) {
        if(getConfig(name) != null) {
            doLog(Logger.Type.ERROR, "Config with name " + name + " already exists!");
            return null;
        }
        Config config = new Config(name, false);
        config.write();
        configs.put(config.getName(), config);
        return config;
    }

    public void saveConfig(Config config) {
        config.write();
    }

    public void saveConfig(String name) {
        saveConfig(configs.get(name));
    }

    public void loadConfig(Config config) {
        activeConfig = config;
        config.read();
    }

    public void loadConfig(String name) {
        loadConfig(configs.get(name));
    }

    public void loadClientSettings() {
        final File settingsFile = new File(new File(MC.mcDataDir, "myth/"), "client.settings");
        if(settingsFile.exists()) {
            JsonObject bgObject = FileUtil.readJsonFromFile(settingsFile.getAbsolutePath());
            if(bgObject.has("background_id")) GuiMainMenu.bgId = bgObject.get("background_id").getAsInt();
            if(bgObject.has("shaders")) GuiMainMenu.shaders = bgObject.get("shaders").getAsBoolean();
        }
    }

    public void saveClientSettings() {
        JsonObject bgObject = new JsonObject();
        bgObject.addProperty("background_id", GuiMainMenu.bgId);
        bgObject.addProperty("shaders", GuiMainMenu.shaders);
        FileUtil.writeJsonToFile(bgObject, new File(new File(MC.mcDataDir, "myth/"), "client.settings").getAbsolutePath());
    }
}
