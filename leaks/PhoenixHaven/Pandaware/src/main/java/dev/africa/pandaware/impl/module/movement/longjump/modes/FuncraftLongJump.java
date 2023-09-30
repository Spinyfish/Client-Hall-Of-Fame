package dev.africa.pandaware.impl.module.movement.longjump.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.longjump.LongJumpModule;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import net.minecraft.potion.Potion;

public class FuncraftLongJump extends ModuleMode<LongJumpModule> {
    public FuncraftLongJump(String name, LongJumpModule parent) {
        super(name, parent);
    }

    private double moveSpeed;
    private double lastDistance;

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            this.lastDistance = MovementUtils.getLastDistance();
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (PlayerUtils.isMathGround() && !PlayerUtils.inLiquid()) {
            double motion = 0.42F;
            motion += PlayerUtils.getJumpBoostMotion();

            event.y = mc.thePlayer.motionY = motion;

            this.moveSpeed = MovementUtils.getBaseMoveSpeed() * (mc.thePlayer.isPotionActive(Potion.moveSpeed)
                    ? 2.05f : 1.85f);
        } else {
            if (mc.thePlayer.getAirTicks() == 1) {
                this.moveSpeed *= 2.8f;
            } else {
                this.moveSpeed = this.lastDistance - (this.lastDistance / 119.0f);
            }
        }

        MovementUtils.strafe(event, moveSpeed);
    };
}
