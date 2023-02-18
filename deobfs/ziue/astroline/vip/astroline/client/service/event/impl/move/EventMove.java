/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.MathHelper
 *  net.minecraft.util.MovementInput
 *  vip.astroline.client.service.event.Event
 */
package vip.astroline.client.service.event.impl.move;

import net.minecraft.client.Minecraft;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;
import vip.astroline.client.service.event.Event;

public class EventMove
extends Event {
    public double x;
    public double y;
    public double z;

    public EventMove(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getX() {
        return this.x;
    }

    public double getZ() {
        return this.z;
    }

    public double getY() {
        return this.y;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void setZ(double z) {
        this.z = z;
    }

    public void setMoveSpeed(double moveSpeed) {
        Minecraft mc = Minecraft.getMinecraft();
        MovementInput movementInput = mc.thePlayer.movementInput;
        double moveForward = movementInput.getMoveForward();
        double moveStrafe = movementInput.getMoveStrafe();
        double yaw = mc.thePlayer.rotationYaw;
        double modifier = moveForward == 0.0 ? 90.0 : (moveForward < 0.0 ? -45.0 : 45.0);
        boolean moving = moveForward != 0.0 || moveStrafe != 0.0;
        yaw += moveForward < 0.0 ? 180.0 : 0.0;
        if (moveStrafe < 0.0) {
            yaw += modifier;
        } else if (moveStrafe > 0.0) {
            yaw -= modifier;
        }
        if (moving) {
            this.setX(-((double)MathHelper.sin((float)((float)Math.toRadians(yaw))) * moveSpeed));
            this.setZ((double)MathHelper.cos((float)((float)Math.toRadians(yaw))) * moveSpeed);
        } else {
            this.setX(0.0);
            this.setZ(0.0);
        }
    }
}
