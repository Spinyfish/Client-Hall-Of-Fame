/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.impl.move.EventUpdate
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 */
package vip.astroline.client.service.module.impl.movement;

import vip.astroline.client.service.event.impl.move.EventUpdate;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;

public class Sprint
extends Module {
    public Sprint() {
        super("Sprint", Category.Movement, 0, false);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        if (Sprint.mc.thePlayer.isSprinting()) return;
        Sprint.mc.thePlayer.setSprinting(true);
    }

    public void onEnable() {
        Sprint.mc.thePlayer.setSprinting(true);
        super.onEnable();
    }

    public void onDisable() {
        Sprint.mc.thePlayer.setSprinting(false);
        super.onDisable();
    }
}
