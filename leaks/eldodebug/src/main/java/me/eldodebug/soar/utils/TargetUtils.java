package me.eldodebug.soar.utils;

import me.eldodebug.soar.utils.server.ServerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;

public class TargetUtils {

	private static Entity target;
	private static Minecraft mc = Minecraft.getMinecraft();
	private static TimerUtils timer = new TimerUtils();
	
	public static void onUpdate() {
		if(mc.objectMouseOver != null && mc.objectMouseOver.entityHit != null) {
			if(mc.objectMouseOver.entityHit instanceof EntityLivingBase && mc.objectMouseOver.entityHit instanceof EntityPlayer && ServerUtils.isInTablist(mc.objectMouseOver.entityHit)) {
				target = mc.objectMouseOver.entityHit;
				timer.reset();
			}
		}else {
			if(timer.delay(2500)) {
				target = null;
				timer.reset();
			}
		}
		
		if(target != null) {
			
			if(target.isDead) {
				target = null;
			}
			
			if(mc.thePlayer.isDead) {
				target = null;
			}
			
			if(mc.thePlayer != null && target != null) {
				if(target.isInvisible()) {
					target = null;
				}
			}
			
			if(mc.thePlayer != null && target != null) {
				if(target.getDistanceToEntity(mc.thePlayer) > 12) {
					target = null;
				}
			}
		}
	}
	
	public static void onLoadWorld() {
		target = null;
	}
	
	public static Entity getTarget() {
		return target;
	}

	public static void setTarget(Entity target) {
		TargetUtils.target = target;
	}
}
