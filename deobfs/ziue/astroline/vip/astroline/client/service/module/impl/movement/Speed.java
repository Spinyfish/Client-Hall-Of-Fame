/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.impl.move.EventMove
 *  vip.astroline.client.service.event.impl.move.EventUpdate
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.value.FloatValue
 */
package vip.astroline.client.service.module.impl.movement;

import vip.astroline.client.service.event.impl.move.EventMove;
import vip.astroline.client.service.event.impl.move.EventUpdate;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.value.FloatValue;

public class Speed
extends Module {
    private FloatValue speed = new FloatValue("Speed", "Speed", 0.28f, 0.0f, 2.0f, 0.01f);
    private FloatValue timerSpeed = new FloatValue("Speed", "Timer", 1.0f, 1.0f, 5.0f, 0.1f);

    public Speed() {
        super("Speed", Category.Movement, 47, false);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (Speed.mc.thePlayer.onGround && (Speed.mc.gameSettings.keyBindRight.pressed || Speed.mc.gameSettings.keyBindLeft.pressed || Speed.mc.gameSettings.keyBindForward.pressed || Speed.mc.gameSettings.keyBindBack.pressed)) {
            Speed.mc.thePlayer.jump();
        } else {
            if (Speed.mc.thePlayer.onGround) return;
            if (!(Speed.mc.gameSettings.keyBindRight.pressed || Speed.mc.gameSettings.keyBindLeft.pressed || Speed.mc.gameSettings.keyBindForward.pressed)) {
                if (!Speed.mc.gameSettings.keyBindBack.pressed) return;
            }
            Speed.mc.thePlayer.setSprinting(true);
        }
    }

    @EventTarget
    public void onMove(EventMove event) {
        if (!(Speed.mc.gameSettings.keyBindRight.pressed || Speed.mc.gameSettings.keyBindLeft.pressed || Speed.mc.gameSettings.keyBindForward.pressed)) {
            if (!Speed.mc.gameSettings.keyBindBack.pressed) return;
        }
        Speed.setMoveSpeed(event, this.speed.getValue().floatValue());
    }

    public static void setMoveSpeed(EventMove event, double speed) {
        double forward = Speed.mc.thePlayer.moveForward;
        double strafe = Speed.mc.thePlayer.moveStrafing;
        float yaw = Speed.mc.thePlayer.rotationYaw;
        if (forward == 0.0 && strafe == 0.0) {
            event.setX(0.0);
            event.setZ(0.0);
        } else {
            if (forward != 0.0) {
                if (strafe > 0.0) {
                    yaw += (float)(forward > 0.0 ? -45 : 45);
                } else if (strafe < 0.0) {
                    yaw += (float)(forward > 0.0 ? 45 : -45);
                }
                strafe = 0.0;
                if (forward > 0.0) {
                    forward = 1.0;
                } else if (forward < 0.0) {
                    forward = -1.0;
                }
            }
            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f)));
        }
    }

    public void onEnable() {
        Speed.mc.timer.timerSpeed = this.timerSpeed.getValue().floatValue();
        super.onEnable();
    }

    public void onDisable() {
        Speed.mc.timer.timerSpeed = 1.0f;
        super.onDisable();
    }
}
