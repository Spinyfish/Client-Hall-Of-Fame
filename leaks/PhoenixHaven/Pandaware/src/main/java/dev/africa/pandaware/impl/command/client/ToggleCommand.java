package dev.africa.pandaware.impl.command.client;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.utils.client.Printer;

@CommandInformation(name = "Toggle", aliases = "t")
public class ToggleCommand extends Command {
    @Override
    public void process(String[] arguments) {
        if (arguments.length >= 2) {
            Module module = Client.getInstance().getModuleManager().getMap().values()
                    .stream()
                    .filter(m -> m.getData().getName().replace(" ", "").equalsIgnoreCase(arguments[1]))
                    .findFirst()
                    .orElse(null);

            if (module != null) {
                module.toggle();

                Printer.chat("§aToggled §7" + module.getData().getName() +
                        (module.getData().isEnabled() ? "§a on" : "§c off"));
            } else {
                Printer.chat("§cModule not found");
            }
        } else {
            this.sendInvalidArgumentsMessage("module", "key");
        }
    }
}