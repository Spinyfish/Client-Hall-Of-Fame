package club.pulsive.api.config;

import club.pulsive.api.main.Pulsive;
import lombok.Getter;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

@Getter
public final class ConfigManager {

    private Path configDirectory = null;
    private final Map<Config, File> configs = new HashMap<>();

    private Config currentConfig;

    public void init() {
        configDirectory = Paths.get(String.valueOf(Pulsive.INSTANCE.getClientDir()), "configs");
        configs.clear();
        try {
            configDirectory.toFile().mkdirs();
            Files.list(configDirectory).map(Path::toFile).forEach(file -> configs.put(new Config(file.getName().replace(".json", "")), file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public boolean load(Config config) {
        if (config != null)
            if (config.load()) {
                currentConfig = config;
                return true;
            }
        return false;
    }

    public boolean add(Config config) {
        configs.put(config, Paths.get(String.valueOf(Pulsive.INSTANCE.getClientDir()), "configs", config.getName()).toFile());
        return config.save();
    }

    public boolean remove(Config config) {
        configs.remove(config);
        return config.delete();
    }

    public boolean save(Config config) {
        if (config != null)
            return config.save();
        return false;
    }

    public boolean load(String name) {
        return load(getConfigByName(name));
    }

    public boolean save(String name) {
        return save(getConfigByName(name));
    }

    public Config getConfigByName(String name) {
        return configs.keySet().stream().filter(config -> config.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

}
