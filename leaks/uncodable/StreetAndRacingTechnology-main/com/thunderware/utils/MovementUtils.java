package com.thunderware.utils;

import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.potion.Potion;

public class MovementUtils {
	static Minecraft mc = Minecraft.getMinecraft();
	
	//Get base NCP speed
	public static double getNCPBaseSpeed() {
        double base = 0.2873;
        if (mc.thePlayer != null && mc.thePlayer.isPotionActive(1)) {
            base *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return base;
    }
	
	public static float getSpeed() {
        float vel = (float)Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
        return vel;
    }

	//Strafe
	public static void setMotion(float speed) {
    	if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
	        mc.thePlayer.motionX = (-(Math.sin(getDirection()) * speed));
	        mc.thePlayer.motionZ = (Math.cos(getDirection()) * speed);
    	}
    }
	
	public static double[] getMotion(float speed) {
		float yaw = getDirection();
		
    	double[] values = {0.0,0.0};
    	values[0] = (-(Math.sin(getDirection()) * speed));
    	values[1] = (Math.cos(getDirection()) * speed);
    	
    	return values;
    }
    
    public static void setMotion(double speed) {
    	if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
	        mc.thePlayer.motionX = (-(Math.sin(getDirection()) * speed));
	        mc.thePlayer.motionZ = (Math.cos(getDirection()) * speed);
    	}
    }
    
    public static void setMotion() {
    	if(mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0) {
	        mc.thePlayer.motionX = (-(Math.sin(getDirection()) * getSpeed()));
	        mc.thePlayer.motionZ = (Math.cos(getDirection()) * getSpeed());
    	}
    }
    
    public static float getDirection() {
        float var1 = mc.thePlayer.rotationYaw;

        if(mc.thePlayer.moveForward < 0)
            var1 += 180F;
        float forward = 1F;
        if(mc.thePlayer.moveForward < 0)
            forward = -.5F;
        else if(mc.thePlayer.moveForward > 0)
            forward = .5F;
        else
            forward = 1F;

        if(mc.thePlayer.moveStrafing > 0)
            var1 -= 90F * forward;
        if(mc.thePlayer.moveStrafing < 0)
            var1 += 90F * forward;
        var1 *= .017453292F;
        return var1;
    }
    
    public static boolean isMoveKeysDown() {
    	return  mc.gameSettings.keyBindForward.isKeyDown() || 
    			mc.gameSettings.keyBindLeft.isKeyDown() ||
    			mc.gameSettings.keyBindRight.isKeyDown() ||
    			mc.gameSettings.keyBindBack.isKeyDown();
    }
	
}
