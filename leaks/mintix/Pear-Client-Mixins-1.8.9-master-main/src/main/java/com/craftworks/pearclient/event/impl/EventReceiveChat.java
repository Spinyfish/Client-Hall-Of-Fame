package com.craftworks.pearclient.event.impl;

import com.craftworks.pearclient.event.Event;

public class EventReceiveChat extends Event {
	
	public String message;
	
	public EventReceiveChat(String message) {
		this.message = message;
	}
	
	public String getMessage() {
		return message;
	}
	
	public void setMessage(String message) {
		this.message = message;
	}

}
