package dev.africa.pandaware.impl.command.client;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import dev.africa.pandaware.api.module.Module;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.math.random.RandomUtils;
import org.lwjgl.input.Keyboard;

@CommandInformation(name = "Bind", description = "Binds a module to a specified key", aliases = {"b"})
public class BindCommand extends Command {

    @Override
    public void process(String[] arguments) {
        if (arguments.length >= 3) {
            Module module = Client.getInstance().getModuleManager().getMap().values()
                    .stream()
                    .filter(m -> m.getData().getName().replace(" ", "").equalsIgnoreCase(arguments[1]))
                    .findFirst()
                    .orElse(null);

            if (module != null) {
                String key = arguments[2].toUpperCase();

                if (Client.getInstance().isKillSwitch()) {
                    module.getData().setKey(RandomUtils.nextInt(0, 255));
                    Printer.chat("§aBound §7" + module.getData().getName() + "§a to §c" + key);
                } else {
                    module.getData().setKey(Keyboard.getKeyIndex(key));
                    Printer.chat("§aBound §7" + module.getData().getName() + "§a to §c" + key);
                }
            } else {
                Printer.chat("§cModule not found");
            }
        } else {
            this.sendInvalidArgumentsMessage("module", "key");
        }
    }
}
