package de.fanta.events.listeners;

import de.fanta.events.Event;

public class EventSilentMove extends Event<EventSilentMove> {
	public boolean silent;

	public EventSilentMove(boolean silent) {
		this.silent = silent;
	}
}
