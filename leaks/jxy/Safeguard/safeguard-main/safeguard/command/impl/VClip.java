package intentions.command.impl;

import intentions.Client;
import intentions.command.Command;
import intentions.modules.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;

public class VClip extends Command {

	public VClip() {
		super("VClip", "Clips up and down", "vclip <amount>", new String[] {"vc", "vclip"});
	}

	@Override
	public void onCommand(String[] args, String command) {
		if(args.length > 0) {
			String a = args[0];
			
			try {
				
				double amount = Double.parseDouble(a);
				
				Minecraft mc = Minecraft.getMinecraft();	
				
				mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + amount, mc.thePlayer.posZ);
			} catch(Exception e) {
				Client.addChatMessage("That isn't a number!");
			}
			
		} else {
			Client.addChatMessage("Enter amount to clip");
		}
	}
}
