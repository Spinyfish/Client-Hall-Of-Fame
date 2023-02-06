package dev.elysium.client.command.impl;

import dev.elysium.client.Elysium;
import dev.elysium.client.command.Command;
import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VClip extends Command {
	
	public VClip()
	{
		super("VClip", "Clips you or something", "vclip <value>", "vc");
	}

	@Override
	public void onCommand(String[] args, String command)
	{
		if(args.length == 1)
		{
			try {
				Minecraft mc = Minecraft.getMinecraft();

				double clipheight = Double.parseDouble(args[0]);

				if(clipheight == 0) {
					Elysium.getInstance().addChatMessage("Please use a valid value.");
					return;
				}

				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY+clipheight, mc.thePlayer.posZ);
				Elysium.getInstance().addChatMessage("Clipped " + (clipheight > 0 ? clipheight : -clipheight) + " blocks " + (clipheight > 0 ? "up." : "down."));
			} catch (NumberFormatException e) {
				Elysium.getInstance().addChatMessage("Please use a valid value.");
			}
		}
	}
}