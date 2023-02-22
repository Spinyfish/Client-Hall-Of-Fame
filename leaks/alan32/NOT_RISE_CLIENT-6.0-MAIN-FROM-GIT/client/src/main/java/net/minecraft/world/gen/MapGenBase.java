package net.minecraft.world.gen;

import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

import java.util.Random;

public class MapGenBase {
    /**
     * The number of Chunks to gen-check in any given direction.
     */
    protected int range = 8;

    /**
     * The RNG used by the MapGen classes.
     */
    protected Random rand = new Random();

    /**
     * This world object.
     */
    protected World worldObj;

    public void generate(final IChunkProvider chunkProviderIn, final World worldIn, final int x, final int z, final ChunkPrimer chunkPrimerIn) {
        final int i = this.range;
        this.worldObj = worldIn;
        this.rand.setSeed(worldIn.getSeed());
        final long j = this.rand.nextLong();
        final long k = this.rand.nextLong();

        for (int l = x - i; l <= x + i; ++l) {
            for (int i1 = z - i; i1 <= z + i; ++i1) {
                final long j1 = (long) l * j;
                final long k1 = (long) i1 * k;
                this.rand.setSeed(j1 ^ k1 ^ worldIn.getSeed());
                this.recursiveGenerate(worldIn, l, i1, x, z, chunkPrimerIn);
            }
        }
    }

    /**
     * Recursively called by generate()
     */
    protected void recursiveGenerate(final World worldIn, final int chunkX, final int chunkZ, final int p_180701_4_, final int p_180701_5_, final ChunkPrimer chunkPrimerIn) {
    }
}
