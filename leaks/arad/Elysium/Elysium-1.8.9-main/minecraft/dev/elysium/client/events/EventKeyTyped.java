package dev.elysium.client.events;

import dev.elysium.base.events.types.Event;
import dev.elysium.client.Elysium;

public class EventKeyTyped extends Event {

	private int keyCode;
	
	public int getKeyCode() {
		return keyCode;
	}

	public void setKeyCode(int keyCode) {
		this.keyCode = keyCode;
	}

	public EventKeyTyped(int keyCode) {
		this.keyCode = keyCode;
	}
	
}
