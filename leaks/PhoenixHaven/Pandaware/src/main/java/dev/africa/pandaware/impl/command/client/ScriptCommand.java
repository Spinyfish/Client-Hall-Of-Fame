package dev.africa.pandaware.impl.command.client;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import dev.africa.pandaware.utils.client.Printer;

import java.awt.*;
import java.io.IOException;

@CommandInformation(name = "Script", aliases = "s")
public class ScriptCommand extends Command {

    @Override
    public void process(String[] arguments) {
        if (arguments.length >= 2) {
            String mode = arguments[1].toLowerCase();

            switch (mode) {
                case "reload": {
                    Client.getInstance().getScriptManager().reloadScripts();

                    Printer.chat("§aScripts reloaded successfully!");
                    break;
                }

                case "list": {
                    Printer.chat("§aScripts:");
                    Client.getInstance().getScriptManager().getItems().forEach(script -> {
                        String name = script.getModule().getData().getName();
                        String fileName = script.getFile().getName();

                        Printer.chat("§7" + name + " §f- §7" + fileName);
                    });

                    Printer.chat("§aScripts listed successfully!");
                    break;
                }

                case "folder": {
                    try {
                        Desktop.getDesktop().open(Client.getInstance().getScriptManager().getScriptFolder());
                        Printer.chat("§aDirectory opened successfully!");
                    } catch (IOException e) {
                        Printer.chat("§aFailed to open directory!");
                    }
                    break;
                }
            }
        } else {
            this.sendInvalidArgumentsMessage(
                    "reload", "list", "folder"
            );
        }
    }
}