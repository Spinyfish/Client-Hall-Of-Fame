/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.util.EnumFacing
 */
package vip.astroline.client.storage.utils.render.chunk;

import net.minecraft.util.EnumFacing;

public class BetterChunksData {
    public long timeStamp;
    public EnumFacing chunkFacing;

    public BetterChunksData(long timeStamp, EnumFacing chunkFacing) {
        this.timeStamp = timeStamp;
        this.chunkFacing = chunkFacing;
    }
}
