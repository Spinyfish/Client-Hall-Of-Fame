package markgg.events.listeners;

import markgg.events.Event;

public class EventChat extends Event<EventChat>  {
	
	public String message;

	public EventChat(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
}
