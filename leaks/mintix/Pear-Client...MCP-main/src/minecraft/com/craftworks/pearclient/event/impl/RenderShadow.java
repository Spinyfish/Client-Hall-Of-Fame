package com.craftworks.pearclient.event.impl;

import com.craftworks.pearclient.event.Event;

public class RenderShadow extends Event {

	private float partialTicks;
	
	public RenderShadow(float partialTicks) {
		this.partialTicks = partialTicks;
	}

	public float getPartialTicks() {
		return partialTicks;
	}
}