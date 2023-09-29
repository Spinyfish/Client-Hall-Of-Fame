package me.wavelength.baseclient.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;

public class MovementUtils {


    public static Minecraft mc = Minecraft.getMinecraft();

    public static void setMotion(double speed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;
        if ((forward == 0.0D) && (strafe == 0.0D)) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                yaw += (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (forward > 0.0D ? 45 : -45);
                }
                strafe = 0.0D;
                if (forward > 0.0D) {
                    forward = 1;
                } else if (forward < 0.0D) {
                    forward = -1;
                }
            }
            mc.thePlayer.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F));
        }
    }
    
    public static boolean isMoving(){
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }
    
    public static double getBaseMoveSpeed() {
        double baseSpeed = 0.2873D;

        if (mc.thePlayer.isPotionActive(Potion.moveSpeed))
            baseSpeed = 1.0D + 0.2D* (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);

        return baseSpeed;
    }
    
    public static void setSpeed(double moveSpeed, float yaw, double strafe, double forward) {
        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45 : 45);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45 : -45);
            }
            strafe = 0.0D;
            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }
        if (strafe > 0.0D) {
            strafe = 1.0D;
        } else if (strafe < 0.0D) {
            strafe = -1.0D;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        mc.thePlayer.motionX = forward * moveSpeed * mx + strafe * moveSpeed * mz;
        mc.thePlayer.motionZ = forward * moveSpeed * mz - strafe * moveSpeed * mx;
    }

    public static void setSpeed(double moveSpeed) {
        if(moveSpeed == 0) {
            setSpeed(getBaseMoveSpeed() - getBaseMoveSpeed() * 1.433, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe, mc.thePlayer.movementInput.moveForward);
        } else {
            setSpeed(moveSpeed, mc.thePlayer.rotationYaw, mc.thePlayer.movementInput.moveStrafe, mc.thePlayer.movementInput.moveForward);
        }
    }
    
    
    public static float getDirection() {
    	Minecraft mc = Minecraft.getMinecraft();
    	float var1 = mc.thePlayer.rotationYaw;
    	
    	if(mc.thePlayer.moveForward > 0.0F) { // if the player walks backward !
    		var1 += 180.0F;
    	}
    	
    	float forward = 1.0F;
    	
    	if(mc.thePlayer.moveForward  < 0.0F) {
    		forward = -0.5F;
    	}else  if(mc.thePlayer.moveForward > 0.0F){
    		forward = 0.5F;
    	}
    	
    	if(mc.thePlayer.moveStrafing > 0.0F) {
    		var1 -= 90.0F * forward;
    	}
    	
    	if(mc.thePlayer.moveStrafing < 0.0F) {
    		var1 += 90.0F * forward;
    	}
    	
    	var1 *= 0.017453292F;
    	return var1;
    	
    }
    
}
