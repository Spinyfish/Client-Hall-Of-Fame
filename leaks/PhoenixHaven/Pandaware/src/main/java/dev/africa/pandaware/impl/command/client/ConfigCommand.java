package dev.africa.pandaware.impl.command.client;


import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import dev.africa.pandaware.api.config.Config;
import dev.africa.pandaware.utils.client.Printer;

import java.awt.*;
import java.io.IOException;

@CommandInformation(name = "Config", aliases = {"C", "Conf"}, description = "Config command")
public class ConfigCommand extends Command {
    @Override
    public void process(String[] arguments) {
        if (arguments.length >= 2) {
            String mode = arguments[1].toUpperCase();

            switch (mode) {
                case "SAVE": {
                    if (arguments.length <= 2) {
                        this.sendInvalidArgumentsMessage("save [name]");
                        return;
                    }

                    String configName = arguments[2];

                    Config config = Client.getInstance().getConfigManager().getConfig(configName);
                    if (config == null) {
                        config = Client.getInstance().getConfigManager().createConfig(configName);
                    }

                    config.save(true);
                    Printer.chat("§aSaved §7" + config.getName() + "§a successfully!");
                    break;
                }

                case "LOAD": {
                    if (arguments.length <= 2) {
                        this.sendInvalidArgumentsMessage("load [name]");
                        return;
                    }

                    String configName = arguments[2];

                    Config config = Client.getInstance().getConfigManager().getConfig(configName);
                    if (config == null) {
                        Printer.chat("§cConfig §7" + configName + "§c does not exist!");
                        return;
                    }

                    config.load(false);
                    Printer.chat("§aLoaded §7" + config.getName() + "§a successfully!");
                    break;
                }

                case "REMOVE":
                case "DELETE": {
                    if (arguments.length <= 2) {
                        this.sendInvalidArgumentsMessage(mode + " [name]");
                        return;
                    }

                    String configName = arguments[2];

                    Config config = Client.getInstance().getConfigManager().getConfig(configName);
                    if (config == null) {
                        Printer.chat("§cConfig §7" + configName + "§c does not exist!");
                        return;
                    }

                    config.delete();
                    Printer.chat("§cDeleted §7" + config.getName() + "§a successfully!");
                    break;
                }

                case "LIST": {
                    Printer.chat("§aConfigs:");
                    Client.getInstance().getConfigManager().getItems().forEach(config ->
                            Printer.chat("§7" + config.getName()));

                    Printer.chat("§aConfigs listed successfully!");
                    break;
                }

                case "FOLDER": {
                    try {
                        Desktop.getDesktop().open(Client.getInstance().getConfigManager().getConfigFolder());
                        Printer.chat("§aDirectory opened successfully!");
                    } catch (IOException e) {
                        Printer.chat("§aFailed to open directory!");
                    }
                    break;
                }

                case "RELOAD": {
                    Client.getInstance().getConfigManager().discoverConfigs();
                    Printer.chat("§aReloaded all configs");
                    break;
                }
            }
        } else {
            this.sendInvalidArgumentsMessage(
                    "load", "save", "delete", "remove",
                    "list", "folder", "reload"
            );
        }
    }
}
