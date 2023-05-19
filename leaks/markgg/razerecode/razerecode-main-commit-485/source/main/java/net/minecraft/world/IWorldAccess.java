package net.minecraft.world;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPosition;

public interface IWorldAccess
{
    void markBlockForUpdate(BlockPosition pos);

    void notifyLightSet(BlockPosition pos);

    void markBlockRangeForRenderUpdate(int x1, int y1, int z1, int x2, int y2, int z2);

    void playSound(String soundName, double x, double y, double z, float volume, float pitch);

    void playSoundToNearExcept(EntityPlayer except, String soundName, double x, double y, double z, float volume, float pitch);

    void spawnParticle(int particleID, boolean ignoreRange, double xCoord, double yCoord, double zCoord, double xOffset, double yOffset, double zOffset, int... parameters);

    void onEntityAdded(Entity entityIn);

    void onEntityRemoved(Entity entityIn);

    void playRecord(String recordName, BlockPosition blockPositionIn);

    void broadcastSound(int soundID, BlockPosition pos, int data);

    void playAuxSFX(EntityPlayer player, int sfxType, BlockPosition blockPositionIn, int data);

    void sendBlockBreakProgress(int breakerId, BlockPosition pos, int progress);
}
