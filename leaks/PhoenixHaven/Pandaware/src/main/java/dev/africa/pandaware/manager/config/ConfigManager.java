package dev.africa.pandaware.manager.config;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.config.Config;
import dev.africa.pandaware.api.interfaces.Initializable;
import dev.africa.pandaware.impl.container.Container;
import lombok.Getter;

import java.io.File;
import java.util.concurrent.TimeUnit;

@Getter
public class ConfigManager extends Container<Config> implements Initializable {
    private File configFolder;

    private Config clientConfig;

    @Override
    public void init() {
        File rootFolder = Client.getInstance().getFileManager().getRootFolder();

        this.configFolder = new File(rootFolder, "configs");

        this.clientConfig = new Config("clientConfig", new File(rootFolder, "clientConfig.json"));
        if (this.clientConfig.getFile().exists()) {
            this.clientConfig.load(true);
        } else {
            this.clientConfig.save(true);
        }
        Client.getInstance().getExecutor().scheduleAtFixedRate(() ->
                this.clientConfig.save(true), 15L, 15L, TimeUnit.SECONDS);

        if (!this.configFolder.exists()) {
            this.configFolder.mkdir();
            return;
        }

        this.discoverConfigs();
    }

    @Override
    public void shutdown() {
        this.clientConfig.save(true);
    }

    public void discoverConfigs() {
        this.getItems().clear();

        if (this.configFolder.listFiles().length > 0) {
            for (File file : this.configFolder.listFiles()) {
                if (file.isDirectory()) {
                    return;
                }

                this.getItems().add(new Config(file.getName().replace(".json", ""), file));
            }
        }
    }

    public Config getConfig(String name) {
        return this.getItems().stream().filter(config -> config.getName()
                .equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Config createConfig(String name) {
        Config config = new Config(name, new File(this.configFolder, name + ".json"));

        this.getItems().add(config);

        return config;
    }
}
