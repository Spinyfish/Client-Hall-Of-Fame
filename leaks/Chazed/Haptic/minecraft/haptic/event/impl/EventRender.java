package haptic.event.impl;

import haptic.event.Event;

public class EventRender extends Event {
	
	private final float partialTicks;
	
	public EventRender(float partialTicks) {
		super(false);
		this.partialTicks = partialTicks;
	}

	public float getPartialTicks() {
		return partialTicks;
	}

}
