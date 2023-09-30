package dev.africa.pandaware.impl.command.client;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.api.module.interfaces.Category;
import dev.africa.pandaware.utils.client.Printer;
import org.lwjgl.input.Keyboard;

import java.util.Objects;

@CommandInformation(name = "Panic", description = "Disables all modules")
public class PanicCommand extends Command {

    @Override
    public void process(String[] arguments) {
        Client.getInstance().getModuleManager().getAllModules().forEach(
                module ->
                {
                    if (module.getData().isEnabled()) {
                        if(!(Objects.equals(module.getData().getCategory().getLabel(), Category.VISUAL.getLabel()))) {
                            module.toggle(false);
                            Printer.chat("Toggled: " + module.getData().getName() + " | Category: " + module.getData().getCategory().getLabel());
                        }
                    }
                }
                        );
    }
}
