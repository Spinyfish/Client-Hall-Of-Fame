package com.craftworks.pearclient.event.impl;

import com.craftworks.pearclient.event.Event;

import net.minecraft.item.ItemStack;

public class EventItemPickup extends Event {

	public final ItemStack item;
	public final int stackSize;
	
	public EventItemPickup(ItemStack item, int stackSize) {
		this.item = item;
		this.stackSize = stackSize;
	}
}