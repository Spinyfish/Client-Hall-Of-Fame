package net.minecraft.world.gen;

import java.util.Random;
import net.minecraft.world.World;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;

public class MapGenBase
{
    /** The number of Chunks to gen-check in any given direction. */
    protected int range = 8;

    /** The RNG used by the MapGen classes. */
    protected Random rand = new Random();

    /** This world object. */
    protected World worldObj;

    public void generate(IChunkProvider chunkProviderIn, World worldIn, int x, int z, ChunkPrimer chunkPrimerIn)
    {
        int var6 = this.range;
        this.worldObj = worldIn;
        this.rand.setSeed(worldIn.getSeed());
        long var7 = this.rand.nextLong();
        long var9 = this.rand.nextLong();

        for (int var11 = x - var6; var11 <= x + var6; ++var11)
        {
            for (int var12 = z - var6; var12 <= z + var6; ++var12)
            {
                long var13 = (long)var11 * var7;
                long var15 = (long)var12 * var9;
                this.rand.setSeed(var13 ^ var15 ^ worldIn.getSeed());
                this.recursiveGenerate(worldIn, var11, var12, x, z, chunkPrimerIn);
            }
        }
    }

    /**
     * Recursively called by generate()
     */
    protected void recursiveGenerate(World worldIn, int chunkX, int chunkZ, int p_180701_4_, int p_180701_5_, ChunkPrimer chunkPrimerIn) {}
}
