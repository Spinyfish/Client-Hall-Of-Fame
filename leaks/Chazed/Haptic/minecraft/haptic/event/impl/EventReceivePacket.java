package haptic.event.impl;

import haptic.event.Event;
import net.minecraft.network.Packet;

public class EventReceivePacket extends Event {
	
	private Packet packet;

	public EventReceivePacket(Packet packet) {
		super(false);
		this.packet = packet;
	}

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

}