// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.events;

import net.minecraft.client.Minecraft;

public class EventMove extends Event
{
    public double x;
    public double y;
    public double z;
    
    public EventMove(final double x, final double y, final double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }
    
    public double getX() {
        return this.x;
    }
    
    public void setX(final double x) {
        this.x = x;
    }
    
    public double getY() {
        return this.y;
    }
    
    public void setY(final double y) {
        this.y = y;
    }
    
    public double getZ() {
        return this.z;
    }
    
    public void setZ(final double z) {
        this.z = z;
    }
    
    public void setSpeed(final double moveSpeed) {
        float forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        float strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            Minecraft.getMinecraft().thePlayer.motionX = 0.0;
            Minecraft.getMinecraft().thePlayer.motionZ = 0.0;
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0f) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0f) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0f;
                if (forward > 0.0f) {
                    forward = 1.0f;
                }
                else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            final double xDist = forward * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f));
            final double zDist = forward * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f));
            Minecraft.getMinecraft().thePlayer.motionX = xDist;
            Minecraft.getMinecraft().thePlayer.motionZ = zDist;
            this.setX(xDist);
            this.setZ(zDist);
        }
    }
    
    public void setSpeed(final double moveSpeed, float yaw) {
        float forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        float strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        if (forward == 0.0 && strafe == 0.0) {
            Minecraft.getMinecraft().thePlayer.motionX = 0.0;
            Minecraft.getMinecraft().thePlayer.motionZ = 0.0;
        }
        else {
            if (forward != 0.0) {
                if (strafe > 0.0f) {
                    yaw += ((forward > 0.0) ? -45 : 45);
                }
                else if (strafe < 0.0f) {
                    yaw += ((forward > 0.0) ? 45 : -45);
                }
                strafe = 0.0f;
                if (forward > 0.0f) {
                    forward = 1.0f;
                }
                else if (forward < 0.0f) {
                    forward = -1.0f;
                }
            }
            final double xDist = forward * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f));
            final double zDist = forward * moveSpeed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * moveSpeed * Math.cos(Math.toRadians(yaw + 90.0f));
            Minecraft.getMinecraft().thePlayer.motionX = xDist;
            Minecraft.getMinecraft().thePlayer.motionZ = zDist;
            this.setX(xDist);
            this.setZ(zDist);
        }
    }
}
