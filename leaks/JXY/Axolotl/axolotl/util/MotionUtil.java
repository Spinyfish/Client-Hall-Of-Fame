package axolotl.util;

import axolotl.cheats.events.StrafeEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;

public class MotionUtil {
	
	public static Minecraft mc = Minecraft.getMinecraft();

    public static double getBaseMoveSpeed() {
        double baseSpeed = mc.thePlayer.capabilities.getWalkSpeed() * 2.873;
        if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
            baseSpeed /= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSlowdown).getAmplifier() + 1);
        }
        if (mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
            baseSpeed *= 1.0 + 0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1);
        }
        return baseSpeed;
    }

	public static void setMotion(StrafeEvent e, double speed) {
        float yaw = mc.thePlayer.rotationYaw;
        if ((e.forward == 0.0D) && (e.strafe == 0.0D)) {
            mc.thePlayer.motionX = 0;
            mc.thePlayer.motionZ = 0;
        } else {
            if (e.forward != 0.0D) {
                if (e.strafe > 0.0D) {
                    yaw += (e.forward > 0.0D ? -45 : 45);
                } else if (e.strafe < 0.0D) {
                    yaw += (e.forward > 0.0D ? 45 : -45);
                }
                e.strafe = 0;
                if (e.forward > 0.0D) {
                    e.forward = 1;
                } else if (e.forward < 0.0D) {
                    e.forward = -1;
                }
            }
            double cos = Math.cos(Math.toRadians(yaw + 90.0F));
            double sin = Math.sin(Math.toRadians(yaw + 90.0F));
            mc.thePlayer.motionX = e.forward * speed * cos + e.strafe * speed * sin;
            mc.thePlayer.motionZ = e.forward * speed * sin - e.strafe * speed * cos;
        }
    
	}

    public static double getSpeed() {
        return (float) Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static void setSpeed(float speed) {
        mc.thePlayer.motionX = (-(Math.sin(getDirection()) * speed));
        mc.thePlayer.motionZ = (Math.cos(getDirection()) * speed);
    }

    public static void setSpeed(double speed) {
        mc.thePlayer.motionX = (-(Math.sin(getDirection()) * speed));
        mc.thePlayer.motionZ = (Math.cos(getDirection()) * speed);
    }

    public static float getDirection() {

        float var1 = mc.thePlayer.rotationYaw;

        float forward;

        if(mc.thePlayer.moveForward < 0) {
            var1 += 180F;
            forward = -.5F;
        } else if(mc.thePlayer.moveForward > 0)
            forward = .5F;
        else
            forward = 1F;

        if(mc.thePlayer.moveStrafing > 0)
            var1 -= 90F * forward;
        else if(mc.thePlayer.moveStrafing < 0)
            var1 += 90F * forward;
        var1 *= .017453292F;
        return var1;
    }



}
