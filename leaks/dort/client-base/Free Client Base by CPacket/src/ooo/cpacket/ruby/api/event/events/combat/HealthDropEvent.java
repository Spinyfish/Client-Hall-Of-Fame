package ooo.cpacket.ruby.api.event.events.combat;

import ooo.cpacket.ruby.api.event.IEvent;

public class HealthDropEvent implements IEvent {
	
	private float health;
	private float oldHealth;
	
	public HealthDropEvent(float health, float oldHealth) {
		this.health = health;
		this.oldHealth = oldHealth;
	}
	
}
