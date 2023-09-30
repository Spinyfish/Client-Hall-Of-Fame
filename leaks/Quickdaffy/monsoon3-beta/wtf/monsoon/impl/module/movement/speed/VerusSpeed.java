/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.impl.event.EventPreMotion;

public class VerusSpeed
extends ModeProcessor {
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        if (this.player.isMoving()) {
            if (this.player.isOnGround()) {
                if (!this.mc.gameSettings.keyBindJump.isKeyDown()) {
                    this.player.setSpeed(this.player.getBaseMoveSpeed());
                    this.player.jump();
                }
            } else {
                this.player.setSpeed((double)this.player.getSpeed() * 1.03);
            }
        }
    };

    public VerusSpeed(Module parentModule) {
        super(parentModule);
    }
}

