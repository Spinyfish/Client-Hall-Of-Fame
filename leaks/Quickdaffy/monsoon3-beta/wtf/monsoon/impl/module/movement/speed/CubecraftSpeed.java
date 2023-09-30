/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.impl.event.EventMove;

public class CubecraftSpeed
extends ModeProcessor {
    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if (this.player.isMoving()) {
            if (this.player.isOnGround()) {
                this.player.setSpeed((EventMove)e, this.player.getBaseMoveSpeed() * 5.0);
                this.mc.thePlayer.motionY = 0.42f;
                e.setY(0.42f);
            } else {
                this.player.setSpeed((EventMove)e, this.player.getBaseMoveSpeed() * (double)2.2f);
            }
        }
        if (this.mc.thePlayer.motionY == 0.08307781780646721) {
            this.mc.thePlayer.motionY = -0.25;
        }
    };

    public CubecraftSpeed(Module parentModule) {
        super(parentModule);
    }
}

