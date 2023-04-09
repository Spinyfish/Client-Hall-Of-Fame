package tech.dort.dortware.api.config;

import tech.dort.dortware.api.manager.Manager;

import java.io.File;
import java.util.ArrayList;

public class ConfigManager extends Manager<Config> {

    public ConfigManager() {
        super(new ArrayList<>());
    }

    @Override
    public void onCreated() {
        if (!getObjects().isEmpty()) {
            getObjects().clear();
        }
        File f = new File("Dortware/configs/");
        if (!f.exists())
            f.mkdirs();
        for (File file : f.listFiles()) {
            this.add(new Config(file.getName()));
        }
    }

    public Config find(String configName) {
        return getObjects().stream().filter(cfg -> cfg.getName().equalsIgnoreCase(configName)).findFirst().orElse(null);
    }

    public void removeIfAdded(String configName) {
        getObjects().removeIf(config -> configName.equalsIgnoreCase(config.getName()));
    }

    public boolean saveIfAdded(String name) {
        for (Config config : getObjects()) {
            if (config.getName().equalsIgnoreCase(name)) {
                config.save();
                return true;
            }
        }
        return false;
    }
}
