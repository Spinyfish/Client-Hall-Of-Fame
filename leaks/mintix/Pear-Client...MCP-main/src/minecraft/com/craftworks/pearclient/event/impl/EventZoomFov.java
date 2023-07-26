package com.craftworks.pearclient.event.impl;

import com.craftworks.pearclient.event.Event;

public class EventZoomFov extends Event {

	private float fov;
	
	public EventZoomFov(float fov) {
		this.fov = fov;
	}

	public float getFov() {
		return fov;
	}

	public void setFov(float fov) {
		this.fov = fov;
	}
}