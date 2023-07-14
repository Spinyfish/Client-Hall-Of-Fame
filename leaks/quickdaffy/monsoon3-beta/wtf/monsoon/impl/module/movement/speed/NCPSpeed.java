/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.potion.Potion;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.impl.event.EventMove;

public class NCPSpeed
extends ModeProcessor {
    private boolean reset;
    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        this.mc.getTimer().timerSpeed = this.mc.thePlayer.ticksExisted % 20 <= 9 ? 1.05f : 0.98f;
        if (this.player.isMoving()) {
            if (this.player.isOnGround()) {
                this.reset = false;
                this.player.jump((EventMove)e);
                this.player.setSpeed((EventMove)e, (double)(this.player.getSpeed() * 1.01f));
                e.setY(0.41f);
                if (this.mc.thePlayer.isPotionActive(Potion.moveSpeed)) {
                    this.player.setSpeed((EventMove)e, (double)(this.player.getSpeed() * (1.0f + 0.1f * (float)(this.mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1))));
                }
            }
            this.player.setSpeed(this.player.getSpeed() * 1.0035f);
            if ((double)this.player.getSpeed() < 0.277) {
                this.reset = true;
            }
            if (this.reset) {
                this.player.setSpeed((EventMove)e, (double)0.277f);
            }
        } else {
            this.mc.thePlayer.motionX = 0.0;
            e.setX(0.0);
            this.mc.thePlayer.motionZ = 0.0;
            e.setZ(0.0);
            this.reset = true;
        }
    };

    public NCPSpeed(Module parentModule) {
        super(parentModule);
    }
}

