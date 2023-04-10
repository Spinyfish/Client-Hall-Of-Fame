package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;
import net.minecraft.client.Minecraft;

@CommandInfo(name = "Vclip", alias = "vclip", syntax = ".vclip [number]", description = "Vclip to ")
public class VclipCommand extends Command {
    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        if (args.length == 1) {

            Minecraft mc = Minecraft.getMinecraft();
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + Double.parseDouble(args[0]), mc.thePlayer.posZ);
        } else if(args.length == 2) {
            Minecraft.getMinecraft().thePlayer.setPosition(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.posY + Double.parseDouble(args[0]), Minecraft.getMinecraft().thePlayer.posZ);
        } else {
            Crispy.addChatMessage(getSyntax());
        }
    }
}
