/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement.speed;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.ModeProcessor;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventMove;

public class NorulesSpeed
extends ModeProcessor {
    private final Setting<Float> timerSpeed = new Setting<Float>("Timer Speed", Float.valueOf(1.0f)).minimum(Float.valueOf(1.0f)).maximum(Float.valueOf(2.0f)).incrementation(Float.valueOf(0.05f)).describedBy("The timer speed while using the module Speed.");
    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if (this.player.isMoving()) {
            if (this.player.isOnGround()) {
                this.mc.getTimer().timerSpeed = 0.85f;
                this.player.setSpeed((EventMove)e, (double)this.player.getSpeed());
                this.mc.thePlayer.motionY = 0.42f;
                e.setY(0.42f);
            } else {
                this.mc.getTimer().timerSpeed = this.timerSpeed.getValue().floatValue();
                this.mc.getTimer().timerSpeed = this.mc.thePlayer.motionY > 0.0 ? (this.mc.getTimer().timerSpeed += 0.2f) : (this.mc.getTimer().timerSpeed -= 0.1f);
                this.player.setSpeed((EventMove)e, this.player.getBaseMoveSpeed() * (double)1.4f);
            }
        }
    };

    public NorulesSpeed(Module parentModule) {
        super(parentModule);
    }

    @Override
    public Setting[] getModeSettings() {
        return new Setting[]{this.timerSpeed};
    }
}

