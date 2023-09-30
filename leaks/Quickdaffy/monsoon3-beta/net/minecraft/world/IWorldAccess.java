/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;

public interface IWorldAccess {
    public void markBlockForUpdate(BlockPos var1);

    public void notifyLightSet(BlockPos var1);

    public void markBlockRangeForRenderUpdate(int var1, int var2, int var3, int var4, int var5, int var6);

    public void playSound(String var1, double var2, double var4, double var6, float var8, float var9);

    public void playSoundToNearExcept(EntityPlayer var1, String var2, double var3, double var5, double var7, float var9, float var10);

    public void spawnParticle(int var1, boolean var2, double var3, double var5, double var7, double var9, double var11, double var13, int ... var15);

    public void onEntityAdded(Entity var1);

    public void onEntityRemoved(Entity var1);

    public void playRecord(String var1, BlockPos var2);

    public void broadcastSound(int var1, BlockPos var2, int var3);

    public void playAuxSFX(EntityPlayer var1, int var2, BlockPos var3, int var4);

    public void sendBlockBreakProgress(int var1, BlockPos var2, int var3);
}

