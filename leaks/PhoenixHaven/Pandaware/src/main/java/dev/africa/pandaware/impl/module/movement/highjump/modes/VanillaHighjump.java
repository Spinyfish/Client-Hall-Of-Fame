package dev.africa.pandaware.impl.module.movement.highjump.modes;

import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.module.mode.ModuleMode;
import dev.africa.pandaware.impl.event.player.MoveEvent;
import dev.africa.pandaware.impl.module.movement.highjump.HighJumpModule;

public class VanillaHighjump extends ModuleMode<HighJumpModule> {
    @EventHandler
    EventCallback<MoveEvent> onMove = event -> {
        if (mc.thePlayer.onGround) {
            if (!this.getParent().getOnlyOnJump().getValue()) {
                event.y = mc.thePlayer.motionY = this.getParent().getHeight().getValue().floatValue();
            } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
                event.y = mc.thePlayer.motionY = this.getParent().getHeight().getValue().floatValue();
            }
        }
    };

    public VanillaHighjump(String name, HighJumpModule parent) {
        super(name, parent);
    }
}
