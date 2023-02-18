/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.impl.move.EventUpdate
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.storage.utils.other.MoveUtils
 */
package vip.astroline.client.service.module.impl.movement;

import vip.astroline.client.service.event.impl.move.EventUpdate;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.storage.utils.other.MoveUtils;

public class Fly
extends Module {
    public Fly() {
        super("Fly", Category.Movement, 33, false);
    }

    @EventTarget
    public void onUpdate(EventUpdate event) {
        Fly.mc.thePlayer.motionY = 0.0;
        MoveUtils.setMotion((double)2.0);
        Fly.mc.thePlayer.onGround = true;
    }

    public void onEnable() {
        this.setDisplayName("Vanilla");
        Fly.mc.thePlayer.motionY = 0.0;
        Fly.mc.thePlayer.onGround = true;
        super.onEnable();
    }

    public void onDisable() {
        Fly.mc.thePlayer.onGround = false;
        super.onDisable();
    }
}
