/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.EventRender3D;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;

public class BlockOverlay
extends Module {
    public BlockOverlay() {
        super("BlockOverlay", 0, Category.RENDER);
    }

    @Subscribe
    public void on3D(EventRender3D event) {
    }
}

