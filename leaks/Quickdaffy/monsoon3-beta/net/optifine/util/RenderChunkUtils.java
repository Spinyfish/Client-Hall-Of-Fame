/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.util;

import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.MathHelper;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;

public class RenderChunkUtils {
    public static int getCountBlocks(RenderChunk renderChunk) {
        ExtendedBlockStorage[] aextendedblockstorage = renderChunk.getChunk().getBlockStorageArray();
        if (aextendedblockstorage == null) {
            return 0;
        }
        int i = renderChunk.getPosition().getY() >> 4;
        ExtendedBlockStorage extendedblockstorage = aextendedblockstorage[i];
        return extendedblockstorage == null ? 0 : extendedblockstorage.getBlockRefCount();
    }

    public static double getRelativeBufferSize(RenderChunk renderChunk) {
        int i = RenderChunkUtils.getCountBlocks(renderChunk);
        double d0 = RenderChunkUtils.getRelativeBufferSize(i);
        return d0;
    }

    public static double getRelativeBufferSize(int blockCount) {
        double d0 = (double)blockCount / 4096.0;
        double d1 = (d0 *= 0.995) * 2.0 - 1.0;
        d1 = MathHelper.clamp_double(d1, -1.0, 1.0);
        return MathHelper.sqrt_double(1.0 - d1 * d1);
    }
}

