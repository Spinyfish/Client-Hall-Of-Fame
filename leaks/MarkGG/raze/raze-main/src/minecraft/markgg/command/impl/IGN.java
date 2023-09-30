package markgg.command.impl;

import markgg.Client;
import markgg.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class IGN extends Command {

	public IGN() {
		super("ign", "Shows your ingame name", "ign", "ig");
	}

	@Override
	public void onCommand(String[] args, String command) {
		Client.addChatMessage("Your IGN is: §a" + Minecraft.getMinecraft().session.getUsername());
	}

}
