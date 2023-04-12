package markgg.command.impl;

import markgg.Client;
import markgg.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class Discord extends Command {

	public Discord() {
		super("discord", "Shows our discord invite link", "discord", "disc");
	}

	@Override
	public void onCommand(String[] args, String command) {
		Client.addChatMessage("Our discord server: §adiscord.gg/EFfRcQ2neq");
	}

}
