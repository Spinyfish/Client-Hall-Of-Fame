package com.srt.events.listeners;

import com.srt.events.Event;

public class EventStep extends Event<EventStep> {
	public double stepHeight;
	private boolean pre;

	public EventStep(double stepHeight,boolean pre){
		this.pre = pre;
		this.stepHeight = stepHeight;
	}
	
}
