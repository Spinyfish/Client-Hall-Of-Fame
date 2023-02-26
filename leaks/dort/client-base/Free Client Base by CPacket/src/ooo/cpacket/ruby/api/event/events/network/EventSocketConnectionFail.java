package ooo.cpacket.ruby.api.event.events.network;

import java.net.Proxy;
import java.net.Socket;

import ooo.cpacket.ruby.api.event.IEvent;

public class EventSocketConnectionFail implements IEvent {
	private Socket socket;
	private Exception exception;
	
	public EventSocketConnectionFail(Socket socket, Exception execption) {
		this.socket = socket;
		this.exception = execption;
	}
	
	public Exception getException() {
		return this.exception;
		
	}
	
	public Socket getSocket() {
		return this.socket;
	}
}
