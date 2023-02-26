package ooo.cpacket.ruby.api.event.events.network;

import net.minecraft.network.play.server.S40PacketDisconnect;
import ooo.cpacket.ruby.api.event.IEvent;

public class EventDisconnected implements IEvent {
	
	private S40PacketDisconnect packet;
	
	private boolean selfDisconnect;
	
	public EventDisconnected(S40PacketDisconnect packet, boolean selfDisconnect) {
		this.packet = packet;
		this.selfDisconnect = selfDisconnect;
	}
	
	public String getDisconnectMessage() {
		if (this.selfDisconnect) {
			return "Self Disconnect";
		}
		return this.packet.func_149165_c().getUnformattedText();
	}
}
