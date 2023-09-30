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

public class VanillaSpeed
extends ModeProcessor {
    private final Setting<Double> speedValue = new Setting<Double>("Speed", 0.5).minimum(0.0).maximum(2.0).incrementation(0.05).describedBy("The speed you will go.");
    private final Setting<Float> timerSpeed = new Setting<Float>("Timer Speed", Float.valueOf(1.0f)).minimum(Float.valueOf(1.0f)).maximum(Float.valueOf(2.0f)).incrementation(Float.valueOf(0.05f)).describedBy("The timer speed while using the module Speed.");
    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if (this.player.isMoving()) {
            if (this.player.isOnGround()) {
                this.player.setSpeed((EventMove)e, this.speedValue.getValue() * 2.0);
                e.setY(0.41999998688698);
                this.mc.thePlayer.motionY = 0.42;
            } else {
                this.player.setSpeed((EventMove)e, this.speedValue.getValue() * 4.0);
                this.mc.getTimer().timerSpeed = this.timerSpeed.getValue().floatValue();
            }
        }
    };

    public VanillaSpeed(Module parentModule) {
        super(parentModule);
    }

    @Override
    public Setting[] getModeSettings() {
        return new Setting[]{this.speedValue, this.timerSpeed};
    }
}

