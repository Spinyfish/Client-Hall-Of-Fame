package me.eldodebug.soar.management.events.impl;

import me.eldodebug.soar.management.events.Event;

public class EventRenderSelectedItem extends Event{

	private int color;
	
	public EventRenderSelectedItem(int color) {
		this.color = color;
	}
	
	public int getColor() {
		return color;
	}
}
