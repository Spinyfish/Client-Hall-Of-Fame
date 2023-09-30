/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.impl.event.EventMove;

public class BlocksMCSpeed
extends ModeProcessor {
    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if (this.player.isMoving()) {
            if (this.player.isOnGround()) {
                e.setY(0.41999998688698);
                this.player.jump(0.42f);
            }
            this.player.setSpeed((EventMove)e, (double)this.player.getSpeed());
        }
    };

    public BlocksMCSpeed(Module parentModule) {
        super(parentModule);
    }
}

