package de.lirium.impl.command.impl;

import de.lirium.Client;
import de.lirium.impl.command.CommandFeature;
import de.lirium.impl.module.ModuleFeature;

@CommandFeature.Info(name = "vclip")
public class VClipFeature extends CommandFeature {

    @Override
    public String[] getArguments() {
        return new String[]{"[Height]"};
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length == 1) {
            try {
                final double height = Double.parseDouble(args[0]);
                setPosition(getX(), getY() + height, getZ());
                return true;
            } catch (Exception exception) {
                exception.printStackTrace();
                sendMessage("Please provide a correct number");
                return false;
            }
        }
        return false;
    }
}