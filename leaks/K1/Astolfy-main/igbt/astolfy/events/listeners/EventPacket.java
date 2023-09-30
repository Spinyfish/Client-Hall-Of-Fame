package igbt.astolfy.events.listeners;

import igbt.astolfy.events.Event;
import igbt.astolfy.events.EventDirection;
import igbt.astolfy.events.EventPacketHandler;
import net.minecraft.network.Packet;

public class EventPacket extends Event<EventPacket> { 
	private Packet packet;
	private EventDirection direction;
	private boolean cancelled;
	
	public EventPacket(EventDirection direction, Packet packet) {
		this.packet = packet;
		this.direction = direction;
	}
	
	public Packet getPacket() {
		return packet;
	}

	@Override
	public EventDirection getDirection() {
		return direction;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
