/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.impl.module.movement;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.module.Category;
import wtf.monsoon.api.module.Module;
import wtf.monsoon.api.setting.Setting;
import wtf.monsoon.impl.event.EventUpdate;
import wtf.monsoon.impl.module.player.Scaffold;

public class Sprint
extends Module {
    public final Setting<Boolean> omni = new Setting<Boolean>("Omni", false).describedBy("Whether to sprint in all directions.");
    @EventLink
    public final Listener<EventUpdate> eventUpdateListener = e -> {
        if (this.player.isMoving() && !Wrapper.getModule(Scaffold.class).isEnabled()) {
            if (this.omni.getValue().booleanValue()) {
                this.mc.thePlayer.setSprinting(true);
            } else if (this.player.isMoving() && this.mc.thePlayer.moveForward >= Math.abs(this.mc.thePlayer.moveStrafing)) {
                this.mc.thePlayer.setSprinting(true);
            }
        }
    };

    public Sprint() {
        super("Sprint", "Always sprint", Category.MOVEMENT);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }
}

