package de.lirium.impl.command.impl;

import de.lirium.Client;
import de.lirium.impl.command.CommandFeature;
import de.lirium.impl.module.ModuleFeature;

@CommandFeature.Info(name = "toggle", alias = {"t"})
public class ToggleFeature extends CommandFeature {

    @Override
    public String[] getArguments() {
        return new String[]{"[Module]"};
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length == 1) {
            final ModuleFeature feature = Client.INSTANCE.getModuleManager().get(args[0]);
            if (feature != null) {
                feature.setEnabled(!feature.isEnabled());
                sendMessage((feature.isEnabled() ? "§aEnabled" : "§cDisabled") + " §e§l" + feature.getName());
            } else
                sendMessage("§e§l" + args[0] + " §cis not a valid module!");
            return true;
        }
        return false;
    }
}