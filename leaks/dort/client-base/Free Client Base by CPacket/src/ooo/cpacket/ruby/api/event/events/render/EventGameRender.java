package ooo.cpacket.ruby.api.event.events.render;

import net.minecraft.client.gui.ScaledResolution;
import ooo.cpacket.ruby.api.event.IEvent;

public class EventGameRender implements IEvent {
	
	public float ticks;
	public ScaledResolution sr;
	
	public EventGameRender(float ticks, ScaledResolution sr) {
		this.ticks = ticks;
		this.sr = sr;
	}
	
}
