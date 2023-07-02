package me.eldodebug.soar.management.mods.impl;

import java.text.DecimalFormat;

import me.eldodebug.soar.management.events.EventTarget;
import me.eldodebug.soar.management.events.impl.EventDamageEntity;
import me.eldodebug.soar.management.events.impl.EventRender2D;
import me.eldodebug.soar.management.events.impl.EventRenderShadow;
import me.eldodebug.soar.management.mods.HudMod;
import net.minecraft.util.MovingObjectPosition;

public class ReachDisplayMod extends HudMod{

	private DecimalFormat format = new DecimalFormat("0.##");
	
	private double distance = 0;
	private long hitTime =  -1;
	
	public ReachDisplayMod() {
		super("Reach Display", "Display your reach");
	}
	
	@EventTarget
	public void onRender2D(EventRender2D event) {
		super.onRender2D();
	}
	
	@EventTarget
	public void onRenderShadow(EventRenderShadow event) {
		super.onRenderShadow();
	}
	
	@EventTarget
	public void onDamageEntity(EventDamageEntity event) {
		if(mc.objectMouseOver != null && mc.objectMouseOver.hitVec != null && mc.objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
			distance = mc.objectMouseOver.hitVec.distanceTo(mc.thePlayer.getPositionEyes(1.0F));
			hitTime = System.currentTimeMillis();
		}
	}
	
	@Override
	public String getText() {
		if((System.currentTimeMillis() - hitTime) > 5000) {
			distance = 0;
		}
		if(distance == 0) {
			return "Hasn't attacked";
		}else {
			return format.format(distance) + " blocks";
		}
	}
}
