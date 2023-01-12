package igbt.astolfy.events.listeners;

import igbt.astolfy.events.Event;

public class EventMove extends Event<EventMove> {
	
	private boolean cancelled;
    
	private double x, y, z;
	
	public EventMove(double x, double y, double z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
