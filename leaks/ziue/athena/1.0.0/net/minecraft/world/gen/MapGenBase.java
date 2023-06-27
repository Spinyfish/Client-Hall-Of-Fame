package net.minecraft.world.gen;

import java.util.*;
import net.minecraft.world.*;
import net.minecraft.world.chunk.*;

public class MapGenBase
{
    protected int range;
    protected Random rand;
    protected World worldObj;
    
    public MapGenBase() {
        this.range = 8;
        this.rand = new Random();
    }
    
    public void generate(final IChunkProvider chunkProviderIn, final World worldIn, final int x, final int z, final ChunkPrimer chunkPrimerIn) {
        final int i = this.range;
        this.worldObj = worldIn;
        this.rand.setSeed(worldIn.getSeed());
        final long j = this.rand.nextLong();
        final long k = this.rand.nextLong();
        for (int l = x - i; l <= x + i; ++l) {
            for (int i2 = z - i; i2 <= z + i; ++i2) {
                final long j2 = l * j;
                final long k2 = i2 * k;
                this.rand.setSeed(j2 ^ k2 ^ worldIn.getSeed());
                this.recursiveGenerate(worldIn, l, i2, x, z, chunkPrimerIn);
            }
        }
    }
    
    protected void recursiveGenerate(final World worldIn, final int chunkX, final int chunkZ, final int p_180701_4_, final int p_180701_5_, final ChunkPrimer chunkPrimerIn) {
    }
}
