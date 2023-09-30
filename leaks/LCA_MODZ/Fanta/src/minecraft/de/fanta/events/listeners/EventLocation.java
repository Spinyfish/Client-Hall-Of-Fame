package de.fanta.events.listeners;

import de.fanta.events.Event;

public class EventLocation extends Event<EventLocation> {

	public float yaw, pitch;
	
	public EventLocation(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}
	
}
