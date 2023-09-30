package igbt.astolfy.events.listeners;

import igbt.astolfy.events.Event;

public class EventRender2D extends Event<EventRender2D> { 
	private double width, height;
	
	public EventRender2D(double width, double height) {
		this.width = width / 2;
		this.height = height / 2;
	}
	
	public double getWidth() {
		return width;
	}
	
	public double getHeight() {
		return height;
	}
}
