package net.optifine;

import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;

import java.util.Comparator;

public class ChunkPosComparator implements Comparator<ChunkCoordIntPair> {
    private final int chunkPosX;
    private final int chunkPosZ;
    private final double yawRad;
    private final double pitchNorm;

    public ChunkPosComparator(final int chunkPosX, final int chunkPosZ, final double yawRad, final double pitchRad) {
        this.chunkPosX = chunkPosX;
        this.chunkPosZ = chunkPosZ;
        this.yawRad = yawRad;
        this.pitchNorm = 1.0D - MathHelper.clamp_double(Math.abs(pitchRad) / (Math.PI / 2D), 0.0D, 1.0D);
    }

    public int compare(final ChunkCoordIntPair cp1, final ChunkCoordIntPair cp2) {
        final int i = this.getDistSq(cp1);
        final int j = this.getDistSq(cp2);
        return i - j;
    }

    private int getDistSq(final ChunkCoordIntPair cp) {
        final int i = cp.chunkXPos - this.chunkPosX;
        final int j = cp.chunkZPos - this.chunkPosZ;
        int k = i * i + j * j;
        final double d0 = MathHelper.atan2(j, i);
        double d1 = Math.abs(d0 - this.yawRad);

        if (d1 > Math.PI) {
            d1 = (Math.PI * 2D) - d1;
        }

        k = (int) ((double) k * 1000.0D * this.pitchNorm * d1 * d1);
        return k;
    }
}
