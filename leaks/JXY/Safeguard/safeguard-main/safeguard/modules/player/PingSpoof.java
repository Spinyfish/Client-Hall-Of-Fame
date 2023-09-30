package intentions.modules.player;

import intentions.events.listeners.EventPacket;
import intentions.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.server.S12PacketEntityVelocity;

public class PingSpoof extends Module {

	public PingSpoof() {
		super("PingSpoof", 0, Category.PLAYER, "Make your ping go high to disable anticheats", true);
	}
	
	public void onSendPacket(EventPacket p) {
		
		Packet packet = p.getPacket();
		
		if(packet instanceof C00PacketKeepAlive)
			p.setCancelled(!(mc.thePlayer.ticksExisted % 3 == 0));
		
	}
	
}
