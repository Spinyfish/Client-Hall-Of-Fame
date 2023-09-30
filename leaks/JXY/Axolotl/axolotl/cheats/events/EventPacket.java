package axolotl.cheats.events;

import net.minecraft.network.Packet;

public class EventPacket extends Event {
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public boolean getCancelled() {
		return cancelled;
	}

	public int sendType;
	public Packet packet;
	
	public Packet getPacket() {
		return this.packet;
	}
	
	public void setPacket(Packet packet) {
		this.packet = packet;
	}
	
	public EventPacket(Packet packet, EventType eventType, int sendType) {
		this.packet = packet;
		this.eventType = eventType;
		this.sendType = sendType;
	}
	
}
