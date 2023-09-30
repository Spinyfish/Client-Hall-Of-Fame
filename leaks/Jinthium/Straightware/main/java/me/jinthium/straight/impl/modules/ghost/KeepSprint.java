package me.jinthium.straight.impl.modules.ghost;


import io.mxngo.echo.Callback;
import io.mxngo.echo.EventCallback;
import me.jinthium.straight.api.module.Module;
import me.jinthium.straight.api.setting.ParentAttribute;
import me.jinthium.straight.impl.event.movement.KeepSprintEvent;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.settings.BooleanSetting;
import me.jinthium.straight.impl.settings.NumberSetting;

public class KeepSprint extends Module {

    private final NumberSetting slowDownVelocity = new NumberSetting("Hit Slow Down During Velocity", 0.6, 0, 1, 0.05);
    private final NumberSetting slowDownNormal = new NumberSetting("Hit Slow Down Normal", 0.6, 0, 1, 0.05);
    private final NumberSetting bufferDecrease = new NumberSetting("Buffer Decrease", 1, 0.1, 10, 0.1);
    private final NumberSetting maxBuffer = new NumberSetting("Max Buffer", 5, 1, 10, 1);
    private final BooleanSetting sprintSlowDownVelocity = new BooleanSetting("Velocity Hit Sprint", false);
    private final BooleanSetting sprintSlowDownNormal = new BooleanSetting("Normal Hit Sprint", false);
    private final BooleanSetting bufferAbuse = new BooleanSetting("Buffer Abuse", false);
    private final BooleanSetting onlyInAir = new BooleanSetting("Only In Air", false);

    private boolean resetting;
    private double combo;
    
    public KeepSprint(){
        super("Keep Sprint", Category.GHOST);
        this.bufferDecrease.addParent(bufferAbuse, ParentAttribute.BOOLEAN_CONDITION);
        this.maxBuffer.addParent(bufferAbuse, ParentAttribute.BOOLEAN_CONDITION);
        this.addSettings(slowDownVelocity, slowDownNormal, bufferDecrease, maxBuffer, sprintSlowDownVelocity, sprintSlowDownNormal, bufferAbuse, onlyInAir);
    }

    @Callback
    final EventCallback<KeepSprintEvent> keepSprintEventEventCallback = event -> {
        if (mc.thePlayer.onGround && this.onlyInAir.isEnabled()) {
            return;
        }

        if (this.bufferAbuse.isEnabled()) {
            if (this.combo < this.maxBuffer.getValue().intValue() && !this.resetting) {
                this.combo++;
            } else {
                if (this.combo > 0) {
                    this.combo = Math.max(0, this.combo - this.bufferDecrease.getValue());
                    this.resetting = true;
                    return;
                } else {
                    this.resetting = false;
                }
            }
        } else {
            this.combo = 0;
        }

        if (mc.thePlayer.hurtTime > 0) {
            event.setSlowDown(this.slowDownVelocity.getValue());
            event.setSprint(this.sprintSlowDownVelocity.isEnabled());
        } else {
            event.setSlowDown(this.slowDownNormal.getValue());
            event.setSprint(this.sprintSlowDownNormal.isEnabled());
        }
    };
}
