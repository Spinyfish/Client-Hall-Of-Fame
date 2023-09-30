package dev.africa.pandaware.impl.command.client;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import dev.africa.pandaware.utils.client.Printer;

@CommandInformation(name = "Online")
public class OnlineCommand extends Command {

    @Override
    public void process(String[] arguments) {
        Printer.chat("§aOnline users:");

        Client.getInstance()
                .getUserManager()
                .getItems()
                .forEach(user -> Printer.chat("§a" + user.getName()));
    }
}