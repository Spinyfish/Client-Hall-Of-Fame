package cc.swift.commands.impl;

import cc.swift.commands.Command;
import cc.swift.util.ChatUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.util.ChatComponentText;

public final class SayCommand extends Command {
    public SayCommand() {
        super("Say", "Sends raw message", new String[]{"s"});
    }

    @Override
    public void onCommand(String[] args) {
        String message = String.join(" ", args);
        ChatUtil.printChatMessage("Message sent: " + message);
        mc.thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(message));
    }
}
