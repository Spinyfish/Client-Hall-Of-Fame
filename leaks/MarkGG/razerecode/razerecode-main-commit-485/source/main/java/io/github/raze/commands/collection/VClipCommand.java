package io.github.raze.commands.collection;

import io.github.raze.commands.system.BaseCommand;

public class VClipCommand extends BaseCommand {

    public VClipCommand() {
        super("VClip", "Set your vertical position", "vclip <Height>", "vc");
    }

    public String onCommand(String[] arguments, String command) {
        if (arguments.length == 1) {
            return "Usage: " + getSyntax();
        }

        double height = Double.parseDouble(arguments[1]);
        mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + height, mc.thePlayer.posZ);

        return String.format("Clipped you %s blocks %s.", Math.abs(height), (height > 0) ? "up" : "down");
    }

}
