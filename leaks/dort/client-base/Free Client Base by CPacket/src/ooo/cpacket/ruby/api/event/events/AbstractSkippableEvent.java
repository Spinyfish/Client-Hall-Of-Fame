package ooo.cpacket.ruby.api.event.events;

import ooo.cpacket.ruby.api.event.IEvent;

public abstract class AbstractSkippableEvent implements IEvent {
	
	private boolean isSkipped;
	
	protected AbstractSkippableEvent() {
		
	}
	
	public boolean getCancelledState() {
		return this.isSkipped;
	}
	
	public void setSkip(boolean isCancelled) {
		this.isSkipped = isCancelled;
	}
	
}
