/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.November;
import lodomir.dev.event.impl.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.BooleanSetting;

public class Debug
extends Module {
    public static BooleanSetting packets = new BooleanSetting("Packets", false);
    public BooleanSetting hurttime = new BooleanSetting("Hurttime", false);
    public BooleanSetting ground = new BooleanSetting("Grounded", false);
    public BooleanSetting velocity = new BooleanSetting("Velocity", false);
    public BooleanSetting speedinair = new BooleanSetting("Speed In Air", false);
    public BooleanSetting reach = new BooleanSetting("Reach", false);
    public BooleanSetting cps = new BooleanSetting("Cps", false);

    public Debug() {
        super("Debug", 0, Category.OTHER);
        this.addSetting(packets);
        this.addSetting(this.hurttime);
        this.addSetting(this.ground);
        this.addSetting(this.velocity);
        this.addSetting(this.speedinair);
        this.addSetting(this.reach);
        this.addSetting(this.cps);
    }

    public void log(String message) {
        November.Log(message);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (this.hurttime.isEnabled()) {
            this.log("Hurttime: " + Debug.mc.thePlayer.hurtTime);
        } else if (this.ground.isEnabled()) {
            this.log("Grounded: " + Debug.mc.thePlayer.onGround);
        } else if (this.velocity.isEnabled()) {
            this.log("Velocity: " + Debug.mc.thePlayer.velocityChanged);
        } else if (this.speedinair.isEnabled()) {
            this.log("Speed in air:" + Debug.mc.thePlayer.speedInAir);
        } else if (this.reach.isEnabled()) {
            this.log("");
        } else {
            this.log("");
        }
        super.onUpdate(event);
    }
}

