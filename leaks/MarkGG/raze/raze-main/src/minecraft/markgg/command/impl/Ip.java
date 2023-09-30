package markgg.command.impl;

import markgg.Client;
import markgg.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class Ip extends Command {

	public Ip() {
		super("ip", "Shows the servers ip", "ip", "i");
	}

	@Override
	public void onCommand(String[] args, String command) {
		if(!Minecraft.getMinecraft().isSingleplayer())
			Client.addChatMessage("You are playing on server with IP: §a" + Minecraft.getMinecraft().getCurrentServerData().serverIP);
		else
			Client.addChatMessage("You are playing on a singleplayer world.");
	}

}
