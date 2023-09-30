package dev.africa.pandaware.impl.module.movement.longjump.modes;

import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.longjump.LongJumpModule;
import dev.africa.pandaware.impl.setting.EnumSetting;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.potion.Potion;

public class NCPLongJump extends ModuleMode<LongJumpModule> {
    private final EnumSetting<NCPMode> mode = new EnumSetting<>("Mode", NCPMode.OLD);

    private double moveSpeed;
    private double lastDistance;
    private boolean jumped;

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        if (event.getEventState() == Event.EventState.PRE) {
            lastDistance = MovementUtils.getLastDistance();
        }
    };

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        switch (mode.getValue()) {
            case NEW:
                if (MovementUtils.isMoving()) {
                    if (PlayerUtils.isMathGround()) {
                        event.y = mc.thePlayer.motionY = 0.42f + (mc.thePlayer.isPotionActive(Potion.jump)
                                ? PlayerUtils.getJumpBoostMotion() : 0);
                        moveSpeed = MovementUtils.getBaseMoveSpeed() * 1.9675;
                        jumped = true;
                    } else if (jumped) {
                        moveSpeed = lastDistance - lastDistance * 0.01f;
                    } else {
                        moveSpeed = lastDistance - lastDistance / 240;
                    }
                    if (mc.thePlayer.getAirTicks() == 5 && !mc.thePlayer.isPotionActive(Potion.jump))
                        event.y = mc.thePlayer.motionY = 0.01f;

                    MovementUtils.strafe(event, moveSpeed = Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed()));
                }
                break;
            case OLD:
                if (MovementUtils.isMoving()) {
                    if (PlayerUtils.isMathGround()) {
                        event.y = mc.thePlayer.motionY = 0.42f + (mc.thePlayer.isPotionActive(Potion.jump)
                                ? PlayerUtils.getJumpBoostMotion() : 0);
                        moveSpeed = MovementUtils.getBaseMoveSpeed() * 2.2;
                        jumped = true;
                    } else if (jumped) {
                        moveSpeed = lastDistance - lastDistance * 0.01f;
                    } else {
                        moveSpeed = lastDistance - lastDistance / 240;
                    }
                    if (mc.thePlayer.getAirTicks() == 5 && !mc.thePlayer.isPotionActive(Potion.jump))
                        event.y = mc.thePlayer.motionY = 0.01;

                    MovementUtils.strafe(event, moveSpeed = Math.max(moveSpeed, MovementUtils.getBaseMoveSpeed()));
                }
                break;
        }
    };

    public NCPLongJump(String name, LongJumpModule parent) {
        super(name, parent);

        this.registerSettings(this.mode);
    }

    @Override
    public void onDisable() {
        moveSpeed = 0;
        lastDistance = 0;
    }

    @AllArgsConstructor
    @Getter
    private enum NCPMode {
        NEW("New"),
        OLD("Old");

        private final String label;

        @Override
        public String toString() {
            return label;
        }
    }
}
