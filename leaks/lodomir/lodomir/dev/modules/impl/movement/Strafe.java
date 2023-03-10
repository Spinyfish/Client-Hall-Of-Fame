/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import lodomir.dev.utils.player.MovementUtils;

public class Strafe
extends Module {
    public Strafe() {
        super("Strafe", 0, Category.MOVEMENT);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        MovementUtils.strafe();
        super.onUpdate(event);
    }
}

