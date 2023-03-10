/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.movement;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;

public class KeepSprint
extends Module {
    public KeepSprint() {
        super("KeepSprint", 0, Category.MOVEMENT);
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        super.onGetPacket(event);
    }
}

