package dev.africa.pandaware.impl.module.movement.longjump.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.longjump.LongJumpModule;
import dev.africa.pandaware.utils.player.MovementUtils;

public class VanillaLongjump extends ModuleMode<LongJumpModule> {
    public VanillaLongjump(String name, LongJumpModule parent) {
        super(name, parent);
    }

    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (MovementUtils.isMoving()) {
            if (mc.thePlayer.onGround) {
                event.y = mc.thePlayer.motionY = 0.42F;
            }

            MovementUtils.strafe(event, this.getParent().getSpeed().getValue().floatValue());
        } else {
            MovementUtils.strafe(event, 0);
        }
    };
}
