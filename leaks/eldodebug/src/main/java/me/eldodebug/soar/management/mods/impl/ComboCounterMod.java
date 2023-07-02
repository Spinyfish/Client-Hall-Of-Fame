package me.eldodebug.soar.management.mods.impl;

import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventAttackEntity;
import me.eldodebug.soar.management.events.impl.EventDamageEntity;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.events.impl.EventTick;
import me.eldodebug.soar.management.mods.HudMod;

public class ComboCounterMod extends HudMod{

	private long hitTime = -1;
	private int combo;
	private int possibleTarget;
	
	public ComboCounterMod() {
		super("Combo Counter", "Display Combo");
	}
	
	@EventTarget
	public void onTick(EventTick event) {
		if((System.currentTimeMillis() - hitTime) > 2000) {
			combo = 0;
		}
	}
	
	@EventTarget
	public void onAttackEntity(EventAttackEntity event) {
		possibleTarget = event.getEntity().getEntityId();
	}
	
	@EventTarget
	public void onDamageEntity(EventDamageEntity event) {
		if(event.getEntity().getEntityId() == possibleTarget) {
			combo++;
			possibleTarget = -1;
			hitTime = System.currentTimeMillis();
		}
		else if(event.getEntity() == mc.thePlayer) {
			combo = 0;
		}
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {
		super.onRender2D();
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		super.onRenderShadow();
	}
	
	@Override
	public String getText() {
		if(combo == 0) {
			return "No Combo";
		}else {
			return combo + " Combo";
		}
	}
}
