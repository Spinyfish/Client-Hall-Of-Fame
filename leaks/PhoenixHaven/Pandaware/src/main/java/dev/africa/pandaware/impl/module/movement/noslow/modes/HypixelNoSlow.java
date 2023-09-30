package dev.africa.pandaware.impl.module.movement.noslow.modes;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.Event;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MotionEvent;
import dev.africa.pandaware.impl.event.player.SafeWalkEvent;
import dev.africa.pandaware.impl.event.player.StepEvent;
import dev.africa.pandaware.impl.module.movement.noslow.NoSlowModule;
import dev.africa.pandaware.impl.module.movement.speed.SpeedModule;
import dev.africa.pandaware.impl.setting.BooleanSetting;
import dev.africa.pandaware.utils.client.ServerUtils;
import dev.africa.pandaware.utils.player.MovementUtils;
import dev.africa.pandaware.utils.player.PlayerUtils;

public class HypixelNoSlow extends ModuleMode<NoSlowModule> {
    private final BooleanSetting slabSafe = new BooleanSetting("Slab Protection", false);
    private final BooleanSetting safeWalk = new BooleanSetting("Safewalk", false);

    public HypixelNoSlow(String name, NoSlowModule parent) {
        super(name, parent);

        this.registerSettings(this.slabSafe, this.safeWalk);
    }

    @EventHandler
    EventCallback<MotionEvent> onMotion = event -> {
        boolean usingItem = mc.thePlayer.isUsingItem() && mc.thePlayer.getCurrentEquippedItem() != null && MovementUtils.isMoving();

        if ((ServerUtils.isOnServer("mc.hypixel.net") || ServerUtils.isOnServer("hypixel.net")) && !(ServerUtils.compromised)) {
            if (usingItem &&
                    !Client.getInstance().getModuleManager().getByClass(SpeedModule.class).getData().isEnabled()
                    && event.getEventState() == Event.EventState.PRE &&
                    PlayerUtils.isMathGround() && mc.thePlayer.ticksExisted % 2 == 0) {
                event.setY(event.getY() + 0.05);
                event.setOnGround(false);
            }
        }
    };

    @EventHandler
    EventCallback<StepEvent> onStep = event -> {
        if (mc.thePlayer != null && mc.theWorld != null) {
            boolean usingItem = mc.thePlayer.isUsingItem() && mc.thePlayer.getCurrentEquippedItem() != null && MovementUtils.isMoving();

            if ((usingItem && MovementUtils.isMoving()) && this.slabSafe.getValue() &&
                    !Client.getInstance().getModuleManager().getByClass(SpeedModule.class).getData().isEnabled()) {
                event.setStepHeight(0);
            }
        }
    };

    @EventHandler
    EventCallback<SafeWalkEvent> onSafeWalk = event -> {
        if (mc.thePlayer != null && mc.theWorld != null) {
            boolean usingItem = mc.thePlayer.isUsingItem() && mc.thePlayer.getCurrentEquippedItem() != null && MovementUtils.isMoving();
            if (this.safeWalk.getValue() && usingItem &&
                    !Client.getInstance().getModuleManager().getByClass(SpeedModule.class).getData().isEnabled()) {
                event.cancel();
            }
        }
    };
}
