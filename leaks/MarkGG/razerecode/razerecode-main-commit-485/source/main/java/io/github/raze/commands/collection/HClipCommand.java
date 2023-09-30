package io.github.raze.commands.collection;

import io.github.raze.commands.system.BaseCommand;

public class HClipCommand extends BaseCommand {

    public HClipCommand() {
        super("HClip", "Set your horizontal position", "hclip <Width>", "hc");
    }

    public String onCommand(String[] arguments, String command) {
        if (arguments.length == 1) {
            return "Usage: " + getSyntax();
        }

        double height = Double.parseDouble(arguments[1]);
        mc.thePlayer.setPosition(mc.thePlayer.posX + height, mc.thePlayer.posY, mc.thePlayer.posZ);

        return String.format("Clipped you %s blocks %s.", Math.abs(height), (height > 0) ? "left" : "right");
    }

}
