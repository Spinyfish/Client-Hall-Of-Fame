package me.eldodebug.soar.management.events.impl;

import me.eldodebug.soar.management.events.Event;
import net.minecraft.entity.Entity;

public class EventRenderHitbox extends Event {

	private Entity entity;
	private double x, y, z;
	private float entityYaw;
	private float partialTicks;
	
	public EventRenderHitbox(Entity entity, double x, double y, double z, float entityYaw, float partialTicks) {
		this.entity = entity;
		this.x = x;
		this.y = y;
		this.z = z;
		this.entityYaw = entityYaw;
		this.partialTicks = partialTicks;
	}

	public Entity getEntity() {
		return entity;
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

	public float getEntityYaw() {
		return entityYaw;
	}

	public float getPartialTicks() {
		return partialTicks;
	}
}
