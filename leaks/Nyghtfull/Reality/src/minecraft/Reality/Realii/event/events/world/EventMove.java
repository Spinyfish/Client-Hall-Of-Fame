/*
 * Decompiled with CFR 0_132.
 */
package Reality.Realii.event.events.world;

import net.minecraft.client.Minecraft;
import Reality.Realii.event.Event;

public class EventMove
extends Event {
    public static double x;
    public static double y;
    public static double z;
    private double motionX;
    private double motionY;
    private double motionZ;

    public EventMove(double x, double y, double z) {
        EventMove.x = x;
        EventMove.y = y;
        EventMove.z = z;
        this.motionX = x;
        this.motionY = y;
        this.motionZ = z;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        EventMove.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        EventMove.y = y;
    }

    public double getZ() {
        return z;
    }

    public void setZ(double z) {
        EventMove.z = z;
    }

    public void setMoveSpeed(double speed) {
        double forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
        double strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
        float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            setX(0.0);
            setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float) (forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float) (forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                forward = forward > 0.0 ? 1.0 : -1.0;
            }
            setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f))
                    + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f))
                    - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }


public void setMoveSpeednonstrafe(double speed) {
    double forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
    double strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
    float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
    if (forward == 0.0 && strafe == 0.0) {
        setX(0.0);
        setZ(0.0);
    } else {
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += (float) (forward > 0.0 ? -5 : 5);
            } else if (strafe < 0.0) {
                yaw += (float) (forward > 0.0 ? 5 : -5);
            }
            strafe = 0.0;
            forward = forward > 0.0 ? 1.0 : -1.0;
        }
        setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f))
                + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
        setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f))
                - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
    }
}

public void setMoveSpeed2(double speed) {
    double forward = Minecraft.getMinecraft().thePlayer.movementInput.moveForward;
    double strafe = Minecraft.getMinecraft().thePlayer.movementInput.moveStrafe;
    float yaw = Minecraft.getMinecraft().thePlayer.rotationYaw;
    if (forward == 0.0 && strafe == 0.0) {
        setX(0.0);
        setZ(0.0);
    } else {
        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += (float) (forward > 0.0 ? -0 : 0);
            } else if (strafe < 0.0) {
                yaw += (float) (forward > 0.0 ? 0 : -0);
            }
            strafe = 0.0;
            forward = forward > 0.0 ? 1.0 : -1.0;
        }
        setX(forward * speed * Math.cos(Math.toRadians(yaw + 0.0f))
                + strafe * speed * Math.sin(Math.toRadians(yaw + 0.0f)));
        setZ(forward * speed * Math.sin(Math.toRadians(yaw + 0.0f))
                - strafe * speed * Math.cos(Math.toRadians(yaw + 0.0f)));
    }
}

}


