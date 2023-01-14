package com.srt.events.listeners;

import com.srt.events.Event;
import com.srt.events.EventDirection;
import com.srt.events.EventPacketHandler;

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
	
	public void setPacket(Packet p) {
		packet = p;
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
