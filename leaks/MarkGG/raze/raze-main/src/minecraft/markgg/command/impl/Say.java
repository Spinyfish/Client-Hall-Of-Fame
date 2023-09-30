package markgg.command.impl;

import markgg.Client;
import markgg.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C01PacketChatMessage;

public class Say extends Command {

	public Say() {
		super("Say", "Says things in chat", "say", "s");
	}

	@Override
	public void onCommand(String[] args, String command) {
		(Minecraft.getMinecraft()).thePlayer.sendQueue.addToSendQueue((Packet)new C01PacketChatMessage(String.join(" ", (CharSequence[])args)));
	}

}
