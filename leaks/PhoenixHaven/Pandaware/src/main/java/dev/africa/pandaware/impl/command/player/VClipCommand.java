package dev.africa.pandaware.impl.command.player;


import dev.africa.pandaware.api.command.Command;
import dev.africa.pandaware.api.command.interfaces.CommandInformation;
import dev.africa.pandaware.utils.client.Printer;
import net.minecraft.util.MathHelper;

@CommandInformation(name = "VClip", description = "Clips vertically")
public class VClipCommand extends Command {
    @Override
    public void process(String[] arguments) {
        try {
            mc.thePlayer.setPositionAndUpdate(mc.thePlayer.posX, mc.thePlayer.posY
                    + Double.parseDouble(arguments[1]), mc.thePlayer.posZ);
            Printer.chat("Teleported " + Double.parseDouble(arguments[1]) + " blocks");
        } catch (Exception e) {
            this.sendInvalidArgumentsMessage("Height");
        }
    }
}
