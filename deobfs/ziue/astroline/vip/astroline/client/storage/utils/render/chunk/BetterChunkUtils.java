/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.client.renderer.GlStateManager
 *  net.minecraft.client.renderer.chunk.RenderChunk
 *  net.minecraft.util.BlockPos
 *  net.minecraft.util.EnumFacing
 *  vip.astroline.client.storage.utils.render.chunk.BetterChunksData
 */
package vip.astroline.client.storage.utils.render.chunk;

import java.util.WeakHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.chunk.RenderChunk;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import vip.astroline.client.storage.utils.render.chunk.BetterChunksData;

public class BetterChunkUtils {
    public static WeakHashMap<RenderChunk, BetterChunksData> chunkHolder;

    public BetterChunkUtils() {
        chunkHolder = new WeakHashMap();
    }

    public static void render(RenderChunk renderChunk) {
        long startTime;
        if (chunkHolder == null) {
            return;
        }
        if (!chunkHolder.containsKey(renderChunk)) return;
        BetterChunksData betterChunksData = chunkHolder.get(renderChunk);
        long time = betterChunksData.timeStamp;
        if (time == -1L) {
            betterChunksData.timeStamp = time = System.currentTimeMillis();
        }
        if ((startTime = System.currentTimeMillis() - time) < 1000L) {
            BetterChunkUtils.animate(renderChunk, betterChunksData, 1000, startTime);
        } else {
            chunkHolder.remove(renderChunk);
        }
    }

    public void setPosition(RenderChunk renderChunk, BlockPos position) {
        if (Minecraft.getMinecraft().thePlayer == null) return;
        BlockPos blockPos = Minecraft.getMinecraft().thePlayer.getPosition();
        blockPos = blockPos.add(0, -blockPos.getY(), 0);
        BlockPos chunksSeparatedWithRadius = position.add(8, -position.getY(), 8);
        EnumFacing chunkFacing = null;
        BetterChunksData animationData = new BetterChunksData(-1L, chunkFacing);
        chunkHolder.put(renderChunk, animationData);
    }

    public static void animate(RenderChunk renderChunk, BetterChunksData betterChunksData, int animationDuration, long timeDif) {
        double y = renderChunk.getPosition().getY();
        double tempy = y / (double)animationDuration * (double)timeDif;
        GlStateManager.translate((double)0.0, (double)(-y + tempy), (double)0.0);
    }
}
