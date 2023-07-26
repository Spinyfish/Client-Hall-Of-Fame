package com.craftworks.pearclient.event.impl;

import com.craftworks.pearclient.event.Event;

public class EventRender3D extends Event {
	
	private float partialTicks;
	    
    public EventRender3D(float partialTicks) {
        this.partialTicks = partialTicks;
    }

    public float getPartialTicks() {
        return partialTicks;
    }

    public void setPartialTicks(float partialTicks) {
        this.partialTicks = partialTicks;
    }

}
