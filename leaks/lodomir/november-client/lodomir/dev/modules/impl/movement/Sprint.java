/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.November;
import lodomir.dev.event.impl.player.EventStrafe;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.modules.impl.player.Scaffold;
import lodomir.dev.settings.impl.BooleanSetting;

public class Sprint
extends Module {
    public static BooleanSetting omni = new BooleanSetting("Omni", false);

    public Sprint() {
        super("Sprint", 0, Category.MOVEMENT);
        this.addSetting(omni);
    }

    @Override
    @Subscribe
    public void onStrafe(EventStrafe event) {
        if (November.INSTANCE.getModuleManager().getModule("Scaffold").isEnabled() && Scaffold.sprint.isMode("None")) {
            return;
        }
        if (Sprint.mc.thePlayer.moveForward > 0.0f && !Sprint.mc.thePlayer.isCollidedHorizontally) {
            Sprint.mc.thePlayer.setSprinting(true);
        }
    }

    @Override
    public void onDisable() {
        Sprint.mc.thePlayer.setSprinting(false);
        super.onDisable();
    }
}

