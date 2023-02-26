package ooo.cpacket.ruby.api.event.events.network;

import java.net.Socket;

import net.minecraft.network.Packet;
import ooo.cpacket.ruby.api.event.events.AbstractSkippableEvent;

public class EventPacket extends AbstractSkippableEvent {
	
	private Packet packet;
	
	private Socket parentSocket;
	
	public EventPacket(Packet packet, Socket parentSocket) {
		this.packet = packet;
		this.parentSocket = parentSocket;
	}

	public Socket getParentSocket() {
		return this.parentSocket;
	}
	
	public Packet getPacket() {
		return this.packet;
	}
	
	public void setPacket(Packet packet) {
		this.packet = packet;
	}
	
	
}
