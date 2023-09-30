/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventPreMotion;

public class YPortSpeed
extends ModeProcessor {
    private final Setting<Double> speedValue = new Setting<Double>("Speed", 0.5).minimum(0.0).maximum(2.0).incrementation(0.05).describedBy("The speed you will go.");
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (this.player.isMoving()) {
            this.mc.getTimer().timerSpeed = 1.0f;
            if (this.player.isOnGround()) {
                this.player.jump();
                this.mc.thePlayer.motionY = 0.42f;
                this.player.setSpeed(this.player.getSpeed());
            } else {
                this.player.setSpeed(this.speedValue.getValue());
                if (!this.mc.gameSettings.keyBindJump.isKeyDown()) {
                    this.mc.thePlayer.motionY = -0.42f;
                }
            }
        } else {
            this.player.setSpeed(0.0);
        }
    };

    public YPortSpeed(Module parentModule) {
        super(parentModule);
    }

    @Override
    public Setting[] getModeSettings() {
        return new Setting[]{this.speedValue};
    }
}

