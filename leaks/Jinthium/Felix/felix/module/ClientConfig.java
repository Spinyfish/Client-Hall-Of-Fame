package felix.module;

import java.io.*;
import java.util.Properties;

import felix.Client;

public class ClientConfig {

    private String configName;
    private File configFile;

    public ClientConfig(String configName) {
        this.configName = configName;
        this.configFile = new File(Client.INSTANCE.getConfigDirectory() + "/" + getConfigName() + ".txt");
    }

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    public File getConfigFile() {
        return configFile;
    }

    public void setConfigFile(File configFile) {
        this.configFile = configFile;
    }
}