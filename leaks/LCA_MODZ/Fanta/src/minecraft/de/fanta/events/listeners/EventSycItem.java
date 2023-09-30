package de.fanta.events.listeners;

import de.fanta.events.Event;

public class EventSycItem extends Event {
	 public static EventSycItem INSTANCE;
    public int slot;

    public EventSycItem(int slot) {
    	  INSTANCE = this;
        this.slot = slot;
    }


}
