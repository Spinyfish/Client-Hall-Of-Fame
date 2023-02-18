/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.storage.utils.render.chunk.BetterChunkUtils
 */
package vip.astroline.client.service.module.impl.render;

import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.storage.utils.render.chunk.BetterChunkUtils;

public class ChunkAnimations
extends Module {
    public static BetterChunkUtils betterChunkUtils;

    public ChunkAnimations() {
        super("ChunkAnimations", Category.Render, 0, false);
    }

    public void onEnable() {
        betterChunkUtils = new BetterChunkUtils();
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
