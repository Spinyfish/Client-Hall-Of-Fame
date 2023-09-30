package de.fanta.events.listeners;

import de.fanta.events.Event;
import de.fanta.events.EventType;
import net.minecraft.block.BlockOldLeaf;

public class EventNoClip extends Event<EventNoClip>{
	
	public boolean noClip;
	
	public EventNoClip(boolean noClip) {
		this.noClip = noClip;
	}
	
	 public EventType type;
    public void setType(EventType type) {
        this.type = type;
    }

   
}
