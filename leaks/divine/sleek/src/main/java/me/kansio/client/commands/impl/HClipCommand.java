package me.kansio.client.commands.impl;

import me.kansio.client.commands.Command;
import me.kansio.client.commands.CommandData;
import me.kansio.client.utils.chat.ChatUtil;
import me.kansio.client.utils.player.PlayerUtil;

@CommandData(
        name = "hclip",
        description = "Horizontally clips you a certain amount"
)
public class HClipCommand extends Command {
    @Override
    public void run(String[] args) {
        try {
            double amount = Double.parseDouble(args[0]);
            double[] xAndZ = PlayerUtil.teleportForward(amount);
            mc.thePlayer.setPosition(mc.thePlayer.posX + xAndZ[0], mc.thePlayer.posY, mc.thePlayer.posZ + xAndZ[1]);
            ChatUtil.log("Clipped " + (amount == 1 ? "1 block" : amount + " blocks"));
        } catch (Exception e) {
            e.printStackTrace();
            ChatUtil.log(".hclip <amount>");
        }
    }
}
