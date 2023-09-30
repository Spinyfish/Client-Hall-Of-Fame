package axolotl.cheats.events;

public class EventUpdate extends Event {
	
	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
	public boolean getCancelled() {
		return cancelled;
	}
	
	public EventUpdate(EventType eventType) {
		this.eventType = eventType;
	}
	
}
