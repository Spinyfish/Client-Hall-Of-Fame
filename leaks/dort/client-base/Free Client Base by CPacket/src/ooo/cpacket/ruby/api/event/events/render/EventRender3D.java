package ooo.cpacket.ruby.api.event.events.render;

import ooo.cpacket.ruby.api.event.events.AbstractSkippableEvent;

public class EventRender3D extends AbstractSkippableEvent {

	public float partialTicks;

	public EventRender3D(float partialTicks) {
		this.partialTicks = partialTicks;
	}
}
