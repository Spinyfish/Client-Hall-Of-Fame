package haptic.event.impl;

import haptic.event.Event;

public class EventKeyPressed extends Event {
	
	private int key;
	
	public EventKeyPressed(int key) {
		super(false);
		this.key = key;
	}
	
	public int getKey() {
		return key;
	}
	
}