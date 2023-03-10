/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.player;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.settings.impl.NumberSetting;

public class FastMine
extends Module {
    private NumberSetting speed = new NumberSetting("Speed", 0.1, 1.0, 0.5, 0.1);

    public FastMine() {
        super("FastMine", 0, Category.PLAYER);
        this.addSetting(this.speed);
    }

    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        FastMine.mc.playerController.blockHitDelay = 0;
        if ((double)FastMine.mc.playerController.curBlockDamageMP > 1.0 - this.speed.getValue()) {
            FastMine.mc.playerController.curBlockDamageMP = 1.0f;
        }
    }
}

