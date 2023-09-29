package de.fanta.events;

import de.fanta.events.listeners.PlayerMoveEvent;
import net.minecraft.network.Packet;


public abstract class EventCancellable extends Event {
	
	


	    private Packet packet;
	    private boolean cancelled;

	    protected EventCancellable() {
	    }
	    public Packet getPacket() {
	        return this.packet;
	    }
	    /**
	     * @see com.darkmagician6.eventapi.events.Cancellable.isCancelled
	     */
	    @Override
	    public boolean isCancelled() {
	        return cancelled;
	    }

	    /**
	     * @see com.darkmagician6.eventapi.events.Cancellable.setCancelled
	     */
	    @Override
	    public void setCancelled(boolean state) {
	        cancelled = state;
	    }
}