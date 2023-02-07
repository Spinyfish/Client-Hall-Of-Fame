package org.crystalpvp.anticheat.api.event;

import org.bukkit.Bukkit;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class BaseEvent extends Event {

	private static final HandlerList handlers = new HandlerList();

	public static HandlerList getHandlerList() {
		return handlers;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public boolean call() {
		Bukkit.getServer().getPluginManager().callEvent(this);
		return this instanceof Cancellable && ((Cancellable) this).isCancelled();
	}

}
