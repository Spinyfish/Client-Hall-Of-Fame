package dev.eternal.client.command.impl;

import dev.eternal.client.Client;
import dev.eternal.client.command.Command;
import dev.eternal.client.config.Config;
import dev.eternal.client.config.ConfigManager;
import dev.eternal.client.config.ConfigRepository;
import dev.eternal.client.util.files.FileUtils;

import java.io.File;
import java.util.Date;
import java.util.Objects;

public class ConfigCommand extends Command {
  public ConfigCommand() {
    super("config", "Save, Load, List and Remove configs.", "cfg");
  }

  private final Client client = Client.singleton();
  private final ConfigRepository configRepository = client.configRepository();

  @Override
  public void run(String... args) {
    try {
      switch (args[0].toLowerCase()) {
        case "load" -> {
          ConfigManager.loadConfig(ConfigManager.fromFile(FileUtils.getFileFromFolder("Config", args[1] + ".cfg")));
          client.displayMessage(String.format("Successfully loaded the config (%s).", args[1]));
        }
        case "save" -> {
          ConfigManager.saveConfig(args[1]);
          client.displayMessage(String.format("Successfully saved the config (%s).", args[1]));
        }
        case "list" -> {
          client.displayMessage("Listing All Configs...");
          configRepository.configList().stream().map(Config::name).forEach(client::displayMessage);
        }
        case "remove" -> {
          final File file = FileUtils.getFileFromFolder("config", args[1] + ".cfg");
          if (!file.exists()) {
            error("File does not exist!");
            return;
          }
          ConfigManager.removeConfig(configRepository.fromFile(file));
          client.displayMessage(String.format("Removed config %s.", args[1]));
        }
      }
    } catch (Exception e) {
      error("<Load/Save/List/Remove> <Config Name>");
    }
  }
}
