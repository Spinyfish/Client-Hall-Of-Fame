package felix.module.impl.player;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.concurrent.CopyOnWriteArrayList;

import felix.api.annotations.Handler;
import felix.events.packet.EventPacketSend;
import felix.events.player.EventMotionUpdate;
import felix.module.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C08PacketPlayerBlockPlacement;
import net.minecraft.network.play.client.C0APacketAnimation;

public class Blink extends Module {
	
	private ArrayList<Packet> packetList = new ArrayList<>();
	
	public Blink() {
		super("Blink", 0, ModuleCategory.PLAYER);
	}
	
	public void onEnable() {
		super.onEnable();
	}
	
	public void onDisable() {
		super.onDisable();
		try {
			for (Packet packets : packetList) {
				mc.getNetHandler().addToSendQueueNoEvent(packets);
			}
			packetList.clear();
		}
		catch (final ConcurrentModificationException e) {
			e.printStackTrace();
		}
	}
	
	@Handler
	public void onSendPacket(final EventPacketSend event) {
		final Packet p = event.getPacket();
		if (p instanceof C02PacketUseEntity || p instanceof C0APacketAnimation || p instanceof C03PacketPlayer || p instanceof C07PacketPlayerDigging || p instanceof C08PacketPlayerBlockPlacement) {
			event.setCancelled(true);
			packetList.add(p);
		}
	}
}
