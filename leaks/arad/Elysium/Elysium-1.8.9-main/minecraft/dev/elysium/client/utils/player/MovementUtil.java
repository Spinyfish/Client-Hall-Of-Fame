package dev.elysium.client.utils.player;

import net.minecraft.client.Minecraft;

public class MovementUtil {
    public static Minecraft mc = Minecraft.getMinecraft();

    public static float getSpeed()
    {
        return (float) Math.sqrt(mc.thePlayer.motionX * mc.thePlayer.motionX + mc.thePlayer.motionZ * mc.thePlayer.motionZ);
    }

    public static boolean isMoving()
    {
        return mc.thePlayer != null && (mc.thePlayer.movementInput.moveForward != 0F || mc.thePlayer.movementInput.moveStrafe != 0F);
    }

    public static void strafe()
    {
        strafe((float) (getSpeed()));
    }

    public static void setBPS(float speed)
    {
        speed -= 0.51;
        if(mc.thePlayer.onGround) speed -= 2.04;
        speed /= 20;
        if(isMoving())
        {
            double yaw = getDirection();
            mc.thePlayer.motionX = -Math.sin(yaw) * speed;
            mc.thePlayer.motionZ = Math.cos(yaw) * speed;
        }
    }

    public static void strafe(double speed)
    {
        if(isMoving())
        {
            double yaw = getDirection();
            mc.thePlayer.motionX = -Math.sin(yaw) * speed;
            mc.thePlayer.motionZ = Math.cos(yaw) * speed;
        }
    }

    public static void strafe(float speed, float direction)
    {
        if(isMoving())
        {
            float var1 = direction;

            if(mc.thePlayer.moveForward < 0.0F)
            {
                var1 += 180F;
            }

            float forward = 1.0F;

            if(mc.thePlayer.moveForward < 0.0F)
                forward = -0.5F;
            else if(mc.thePlayer.moveForward > 0.0F)
                forward = 0.5F;

            if(mc.thePlayer.moveStrafing > 0.0F)
            {
                var1 -= 90.0F * forward;
            }

            if(mc.thePlayer.moveStrafing < 0.0F)
            {
                var1 += 90.0F * forward;
            }
            var1 *= 0.017453292F;

            mc.thePlayer.motionX = -Math.sin(var1) * speed;
            mc.thePlayer.motionZ = Math.cos(var1) * speed;
        }
    }

    public static float getDirection()
    {
        Minecraft mc = Minecraft.getMinecraft();
        float var1 = mc.thePlayer.rotationYaw;

        if(mc.thePlayer.moveForward < 0.0F)
        {
            var1 += 180F;
        }

        float forward = 1.0F;

        if(mc.thePlayer.moveForward < 0.0F)
            forward = -0.5F;
        else if(mc.thePlayer.moveForward > 0.0F)
            forward = 0.5F;

        if(mc.thePlayer.moveStrafing > 0.0F)
        {
            var1 -= 90.0F * forward;
        }

        if(mc.thePlayer.moveStrafing < 0.0F)
        {
            var1 += 90.0F * forward;
        }
        var1 *= 0.017453292F;
        return var1;
    }
}
