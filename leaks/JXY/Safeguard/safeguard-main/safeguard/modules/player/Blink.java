package intentions.modules.player;

import java.util.ArrayList;

import intentions.Client;
import intentions.events.listeners.EventPacket;
import intentions.modules.Module;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.network.Packet;

public class Blink extends Module {

	public Blink() {
		super("Blink", 0, Category.PLAYER, "Stop sending packets then send them all at once to basically teleport you", false);
	}
	
	private ArrayList<Packet> packets = new ArrayList<Packet>();
	
	public void onEnable() {
		Client.addChatMessage("Blinking..");
	}
	
	public void onDisable() {
		Client.addChatMessage("Blinked!");
		for(Packet p : packets) {
			mc.thePlayer.sendQueue.addToSendQueue(p);
		}
	}
	
	public void onSendPacket(EventPacket e) {
		e.setCancelled(true);
		packets.add(e.getPacket());
	}
	
}
