package de.lirium.impl.command.impl;

import de.lirium.Client;
import de.lirium.impl.command.CommandFeature;

@CommandFeature.Info(name = "reload", alias = "rl")
public class ReloadFeature extends CommandFeature {

    @Override
    public boolean execute(String[] args) {
        Client.INSTANCE.unhook();
        Client.INSTANCE.hook();
        sendMessage("Reloaded client!");
        return true;
    }
}