package com.craftworks.pearclient.event.impl;

import com.craftworks.pearclient.event.Event;

import net.minecraft.entity.Entity;

public class EventDamageEntity extends Event{

	private Entity entity;
	
	public EventDamageEntity(Entity entity) {
		this.entity = entity;
	}

	public Entity getEntity() {
		return entity;
	}
}