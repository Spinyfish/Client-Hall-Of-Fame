package de.fanta.events.listeners;

import de.fanta.events.Event;

public class EventNoRotate extends Event<EventNoRotate> {
	
	public float yaw, pitch;
	
	public EventNoRotate(float yaw, float pitch) {
		this.yaw = yaw;
		this.pitch = pitch;
	}
}
