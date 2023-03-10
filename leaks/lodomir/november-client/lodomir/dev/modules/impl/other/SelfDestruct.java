/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.other;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.November;
import lodomir.dev.event.impl.game.EventUpdate;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;

public class SelfDestruct
extends Module {
    public SelfDestruct() {
        super("SelfDestruct", 0, Category.OTHER);
    }

    @Subscribe
    public void onUpdate(EventUpdate event) {
        for (Module m : November.INSTANCE.getModuleManager().getModules()) {
            m.setEnabled(false);
        }
    }
}

