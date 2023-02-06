package dev.elysium.client.command.impl;

import dev.elysium.client.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class Say extends Command {
	
	public Say()
	{
		super("Say", "Says something", "say", "s");
	}

	@Override
	public void onCommand(String[] args, String command)
	{
		Minecraft.getMinecraft().thePlayer.sendQueue.addToSendQueue(new C01PacketChatMessage(String.join(" ", args)));
	}
	
}
