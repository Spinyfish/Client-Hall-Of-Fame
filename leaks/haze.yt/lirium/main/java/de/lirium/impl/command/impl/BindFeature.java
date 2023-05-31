package de.lirium.impl.command.impl;

import de.lirium.Client;
import de.lirium.impl.command.CommandFeature;
import de.lirium.impl.module.ModuleFeature;
import org.lwjgl.input.Keyboard;

import java.util.Locale;

@CommandFeature.Info(name = "bind")
public class BindFeature extends CommandFeature {

    @Override
    public String[] getArguments() {
        return new String[]{"<module> {key}"};
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length == 1) {
            final ModuleFeature feature = Client.INSTANCE.getModuleManager().get(args[0]);
            if (feature != null)
                sendMessage("§e" + feature.getName() + "§7 is bound to §e" + Keyboard.getKeyName(feature.getKeyBind()));
            else
                sendMessage("§e§l" + args[0] + " §cis not a valid module!");
            return true;
        }
        if (args.length == 2) {
            final ModuleFeature feature = Client.INSTANCE.getModuleManager().get(args[0]);
            if (feature != null) {
                try {
                    final int key = args[1].toLowerCase().startsWith("mouse") ? Integer.parseInt(args[1].substring(5)) - 100 : Keyboard.getKeyIndex(args[1].toUpperCase());
                    feature.setKeyBind(key);
                    if (key != 0)
                        sendMessage("§aBound §e§l" + feature.getName() + " §ato §e§l" + args[1].toUpperCase());
                    else
                        sendMessage("§aUnbound §e§l" + feature.getName());
                } catch (NumberFormatException e) {
                    sendMessage("§e§l" + args[1].substring(5) + " §cis not a valid number!");
                }
            } else
                sendMessage("§e§l" + args[0] + " §cis not a valid module!");
            return true;
        }
        return false;
    }
}
