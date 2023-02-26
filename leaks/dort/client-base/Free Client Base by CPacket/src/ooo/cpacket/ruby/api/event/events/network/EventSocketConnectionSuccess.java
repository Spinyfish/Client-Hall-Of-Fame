package ooo.cpacket.ruby.api.event.events.network;

import java.net.Proxy;
import java.net.Socket;

import ooo.cpacket.ruby.api.event.IEvent;

public class EventSocketConnectionSuccess implements IEvent {
	
	private Socket socket = new Socket(Proxy.NO_PROXY);
	
	public EventSocketConnectionSuccess(Socket socket) {
		this.socket = socket;
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
}
