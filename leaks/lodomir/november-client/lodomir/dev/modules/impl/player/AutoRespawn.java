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

public class AutoRespawn
extends Module {
    public AutoRespawn() {
        super("AutoRespawn", 0, Category.PLAYER);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        if (!AutoRespawn.mc.thePlayer.isEntityAlive()) {
            AutoRespawn.mc.thePlayer.respawnPlayer();
        }
    }
}

