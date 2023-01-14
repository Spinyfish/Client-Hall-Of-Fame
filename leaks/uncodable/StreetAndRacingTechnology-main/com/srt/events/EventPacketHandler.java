package com.srt.events;

import net.minecraft.network.Packet;

public class EventPacketHandler<T> {

	public boolean cancelled;
	public Packet packet;
	public EventDirection direction;
	
	public boolean isCancelled() {
		return cancelled;
	}
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public EventDirection getDirection() {
		return direction;
	}
	public void setDirection(EventDirection direction) {
		this.direction = direction;
	}
	public boolean isIncoming() {
		if(direction == null)
			return false;
		
		return direction == EventDirection.INCOMING;
	}
	public boolean isOutgoing() {
		if(direction == null)
			return false;
		
		return direction == EventDirection.OUTGOING;
	}
}
