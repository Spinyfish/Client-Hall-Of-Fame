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
import wtf.monsoon.impl.event.EventPreMotion;

public class FuncraftSpeed
extends ModeProcessor {
    private double speed;
    private double lastDist;
    private boolean prevOnGround;
    private final Setting<Boolean> funcraftTimerOption = new Setting<Boolean>("Funcraft Timer", true).describedBy("Whether to use the Funcraft timer");
    @EventLink
    public final Listener<EventMove> eventMoveListener = e -> {
        if (this.player.isMoving()) {
            this.speed = this.player.getBaseMoveSpeed();
            if (this.mc.thePlayer.onGround && !this.prevOnGround) {
                this.mc.getTimer().timerSpeed = 1.0f;
                this.prevOnGround = true;
                e.setY(0.41999998688698);
                this.mc.thePlayer.motionY = 0.42;
                this.speed *= 2.1;
            } else if (this.prevOnGround) {
                double difference = 0.66 * (this.lastDist - this.player.getBaseMoveSpeed());
                this.speed = this.lastDist - difference;
                this.prevOnGround = false;
            } else {
                this.speed = this.lastDist - this.lastDist / 159.0;
            }
            if (this.funcraftTimerOption.getValue().booleanValue()) {
                this.mc.getTimer().timerSpeed = 1.15f;
            }
            this.player.setSpeed((EventMove)e, Math.max(this.player.getBaseMoveSpeed(), this.speed));
        }
    };
    @EventLink
    public final Listener<EventPreMotion> eventPreMotionListener = e -> {
        double xDist = this.mc.thePlayer.posX - this.mc.thePlayer.prevPosX;
        double zDist = this.mc.thePlayer.posZ - this.mc.thePlayer.prevPosZ;
        this.lastDist = Math.sqrt(xDist * xDist + zDist * zDist);
    };

    public FuncraftSpeed(Module parentModule) {
        super(parentModule);
    }

    @Override
    public Setting[] getModeSettings() {
        return new Setting[]{this.funcraftTimerOption};
    }
}

