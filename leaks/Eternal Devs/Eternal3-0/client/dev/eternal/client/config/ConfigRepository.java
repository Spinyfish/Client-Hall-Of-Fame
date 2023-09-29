package dev.eternal.client.config;

import dev.eternal.client.util.files.FileUtils;
import lombok.Getter;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
public class ConfigRepository {

  private final List<Config> configList = new ArrayList<>();

  public void init() {
    FileUtils.getFilesFromFolder(new File(FileUtils.getEternalFolder(), "config").toPath(), ".cfg")
        .stream()
        .map(ConfigManager::fromFile)
        .forEach(configList::add);
  }

  public Config fromFile(File file) {
    return configList.stream().filter(config -> config.configFile().equals(file)).findFirst().orElse(null);
  }

}