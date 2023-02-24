package me.kansio.client.commands.impl;

import me.kansio.client.commands.Command;
import me.kansio.client.commands.CommandData;
import me.kansio.client.utils.chat.ChatUtil;

@CommandData(
        name = "vclip",
        description = "Vertically clips you a certain amount"
)
public class VClipCommand extends Command {

    @Override
    public void run(String[] args) {
        if (args.length > 0) {
            ChatUtil.log("Vclipped " + Integer.parseInt(args[0]) + " Blocks");
            mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + Integer.parseInt(args[0]), mc.thePlayer.posZ);
        } else {
            ChatUtil.log(".vclip <amount>");
        }
    }
}
