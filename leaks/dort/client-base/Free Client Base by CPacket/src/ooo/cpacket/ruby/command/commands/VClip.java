package ooo.cpacket.ruby.command.commands;

import net.minecraft.network.play.client.C03PacketPlayer.C04PacketPlayerPosition;
import ooo.cpacket.ruby.ClientBase;
import ooo.cpacket.ruby.command.Command;

public class VClip extends Command {

	public VClip() {
		super("vclip", "up", "down", "tpdown", "tpup");
	}

	@Override
	public void run(String[] args) {
		try {
			double x = Double.parseDouble(args[1]);
			mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + x, mc.thePlayer.posZ);
		}catch (Exception e){
			ClientBase.INSTANCE.chat("Error (Invalid arguments?)");
		}
	}

}
