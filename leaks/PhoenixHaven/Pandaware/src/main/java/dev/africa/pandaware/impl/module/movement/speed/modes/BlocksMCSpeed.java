package dev.africa.pandaware.impl.module.movement.speed.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.utils.player.MovementUtils;
import net.minecraft.potion.Potion;

public class BlocksMCSpeed extends ModuleMode<SpeedModule> {
    public BlocksMCSpeed(String name, SpeedModule parent) {
        super(name, parent);
    }

    private double moveSpeed;
    private double lastDistance;
    private boolean jumpered;

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            lastDistance = MovementUtils.getLastDistance();
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onStabChildren = event -> {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                double speedAmplifier = (mc.thePlayer.isPotionActive(Potion.moveSpeed)
                        ? ((mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * 0.025) : 0);
                event.y = mc.thePlayer.motionY = 0.42f;
                moveSpeed = 0.48 + speedAmplifier;
                jumpered = true;
            } else if (jumpered) {
                moveSpeed = lastDistance - 0.66F * (lastDistance - MovementUtils.getBaseMoveSpeed());
                jumpered = false;
            } else {
                moveSpeed = lastDistance - lastDistance / 190;
                if (mc.thePlayer.moveStrafing != 0) {
                    moveSpeed -= 0.02;
                }
            }
            MovementUtils.strafe(event, moveSpeed);
        }
    };

    public void onEnable() {
        lastDistance = 0;
        moveSpeed = 0;
        jumpered = false;
    }
}
