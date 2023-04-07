package intentions.events.listeners;

import net.minecraft.network.Packet;

public class EventPacket {

	public Packet packet;
	public boolean eventSend = true;
	
	public EventPacket(Packet packet, boolean eventSend) {
		this.packet = packet;
		this.eventSend = eventSend;
	}
	
	private boolean cancelled = false;
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public boolean getCancelled() {
		return cancelled;
	}
	public Packet getPacket() {
		return packet;
	}
	public boolean getEventSend() {
		return eventSend;
	}
	
}
