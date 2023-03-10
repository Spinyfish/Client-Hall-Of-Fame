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

public class FastPlace
extends Module {
    public FastPlace() {
        super("FastPlace", 0, Category.PLAYER);
    }

    @Subscribe
    public void onUpdate(EventUpdate e) {
        FastPlace.mc.rightClickDelayTimer = 0;
    }

    @Override
    public void onDisable() {
        FastPlace.mc.rightClickDelayTimer = 6;
        super.onDisable();
    }
}

