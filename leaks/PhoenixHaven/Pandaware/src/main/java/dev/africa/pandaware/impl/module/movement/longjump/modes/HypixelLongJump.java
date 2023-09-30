package dev.africa.pandaware.impl.module.movement.longjump.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.longjump.LongJumpModule;
import dev.africa.pandaware.utils.client.Printer;
import dev.africa.pandaware.utils.client.ServerUtils;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.potion.Potion;

public class HypixelLongJump extends ModuleMode<LongJumpModule> {
    private double lastDistance;
    private double movespeed;

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            this.lastDistance = MovementUtils.getLastDistance();
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if ((ServerUtils.isOnServer("mc.hypixel.net") || ServerUtils.isOnServer("hypixel.net")) && !ServerUtils.compromised) {
            if (MovementUtils.isMoving()) {
                if (mc.thePlayer.onGround) {
                    this.movespeed = MovementUtils.getBaseMoveSpeed() * (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 2.1 : 2.2);
                    mc.thePlayer.jump();
                    event.y = mc.thePlayer.motionY = 0.42f + (mc.thePlayer.isPotionActive(Potion.jump)
                            ? PlayerUtils.getJumpBoostMotion() * 1.1 : 0);
                }

                if (!mc.thePlayer.onGround) {
                    this.movespeed = this.lastDistance * 0.91f;
                }

                if (mc.thePlayer.fallDistance > 0 && mc.thePlayer.fallDistance < 0.3 && !mc.thePlayer.isPotionActive(Potion.jump)) {
                    mc.thePlayer.motionY = 1E-3 + MovementUtils.getHypixelFunny() *
                            (mc.thePlayer.isPotionActive(Potion.moveSpeed) ? 1E-6 : 1E-4);
                }

                MovementUtils.strafe(event, Math.max(MovementUtils.getBaseMoveSpeed(), this.movespeed));
            }
        } else {
            mc.thePlayer.motionX = mc.thePlayer.motionZ = mc.thePlayer.motionY *= Math.random();
        }
    };

    public HypixelLongJump(String name, LongJumpModule parent) {
        super(name, parent);
    }

    @Override
    public void onEnable() {
        this.lastDistance = MovementUtils.getLastDistance();
        this.movespeed = MovementUtils.getBaseMoveSpeed();
    }
}
