package intentions.events.listeners;

import intentions.events.Event;

public class EventKey extends Event<EventKey> {
	
	public int code;

	public EventKey(int key) {
		this.code = key;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}
	
}
