package dev.africa.pandaware.impl.command.client;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import dev.africa.pandaware.utils.client.Printer;

@CommandInformation(name = "Help", description = "Shows all commands")
public class HelpCommand extends Command {
    @Override
    public void process(String[] arguments) {
        Printer.chat("§7Commands:");

        Client.getInstance().getCommandManager().getItems().forEach(command -> {
            StringBuilder builder = new StringBuilder("§7[");
            if (!command.getName().equals("sus")) {
                builder.append("§f").append(command.getName());
                if (command.getAliases().length > 0) {
                    builder.append("§7, ");

                    for (int i = 0; i < command.getAliases().length; i++) {
                        builder.append("§f").append(command.getAliases()[i]);

                        if (i != command.getAliases().length - 1) {
                            builder.append("§7, ");
                        }
                    }
                }
                builder.append("§7]");

                Printer.chat("§7Name: §d" + command.getName() +
                        " §7- §b" + builder +
                        " §7- §d" + command.getDescription());
            }
        });
    }
}
