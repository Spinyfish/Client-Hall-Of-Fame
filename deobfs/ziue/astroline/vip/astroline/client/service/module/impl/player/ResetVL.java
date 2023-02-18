/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.event.impl.move.EventPreUpdate
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 */
package vip.astroline.client.service.module.impl.player;

import vip.astroline.client.service.event.impl.move.EventPreUpdate;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;

public class ResetVL
extends Module {
    private int jumped;
    private double y;

    public ResetVL() {
        super("ResetVL", Category.Player, 0, false);
    }

    @EventTarget
    public void onUpdate(EventPreUpdate e) {
        if (ResetVL.mc.thePlayer.onGround && this.jumped < 25) {
            ResetVL.mc.thePlayer.motionY = 0.11;
            ++this.jumped;
        }
        if (this.jumped < 25) {
            ResetVL.mc.thePlayer.posY = this.y;
            ResetVL.mc.timer.timerSpeed = 2.25f;
        } else {
            ResetVL.mc.timer.timerSpeed = 1.0f;
            this.enableModule();
        }
    }

    public void onEnable() {
        this.jumped = 0;
        this.y = ResetVL.mc.thePlayer.posY;
        super.onEnable();
    }
}
