package ooo.cpacket.ruby.api.event.events.render;

import net.minecraft.entity.Entity;
import ooo.cpacket.ruby.api.event.IEvent;
import ooo.cpacket.ruby.api.event.events.AbstractSkippableEvent;

public class NametagRenderEvent extends AbstractSkippableEvent {
	private String name;
	private float health;
	private Entity entity;

	public NametagRenderEvent(String name, float health, Entity entity) {
		this.name = name;
		this.health = health;
		this.entity = entity;
	}

	public String getName() {
		return name;
	}

	public float getHealth() {
		return health;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setHealth(float health) {
		this.health = health;
	}

	public Entity getEntity() {
		return this.entity;
	}

}
