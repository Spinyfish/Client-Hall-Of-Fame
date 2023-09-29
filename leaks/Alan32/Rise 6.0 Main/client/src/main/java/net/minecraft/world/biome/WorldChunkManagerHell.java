package net.minecraft.world.biome;

import net.minecraft.util.BlockPos;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class WorldChunkManagerHell extends WorldChunkManager {
    /**
     * The biome generator object.
     */
    private final BiomeGenBase biomeGenerator;

    /**
     * The rainfall in the world
     */
    private final float rainfall;

    public WorldChunkManagerHell(final BiomeGenBase p_i45374_1_, final float p_i45374_2_) {
        this.biomeGenerator = p_i45374_1_;
        this.rainfall = p_i45374_2_;
    }

    /**
     * Returns the biome generator
     */
    public BiomeGenBase getBiomeGenerator(final BlockPos pos) {
        return this.biomeGenerator;
    }

    /**
     * Returns an array of biomes for the location input.
     */
    public BiomeGenBase[] getBiomesForGeneration(BiomeGenBase[] biomes, final int x, final int z, final int width, final int height) {
        if (biomes == null || biomes.length < width * height) {
            biomes = new BiomeGenBase[width * height];
        }

        Arrays.fill(biomes, 0, width * height, this.biomeGenerator);
        return biomes;
    }

    /**
     * Returns a list of rainfall values for the specified blocks. Args: listToReuse, x, z, width, length.
     */
    public float[] getRainfall(float[] listToReuse, final int x, final int z, final int width, final int length) {
        if (listToReuse == null || listToReuse.length < width * length) {
            listToReuse = new float[width * length];
        }

        Arrays.fill(listToReuse, 0, width * length, this.rainfall);
        return listToReuse;
    }

    /**
     * Returns biomes to use for the blocks and loads the other data like temperature and humidity onto the
     * WorldChunkManager Args: oldBiomeList, x, z, width, depth
     */
    public BiomeGenBase[] loadBlockGeneratorData(BiomeGenBase[] oldBiomeList, final int x, final int z, final int width, final int depth) {
        if (oldBiomeList == null || oldBiomeList.length < width * depth) {
            oldBiomeList = new BiomeGenBase[width * depth];
        }

        Arrays.fill(oldBiomeList, 0, width * depth, this.biomeGenerator);
        return oldBiomeList;
    }

    /**
     * Return a list of biomes for the specified blocks. Args: listToReuse, x, y, width, length, cacheFlag (if false,
     * don't check biomeCache to avoid infinite loop in BiomeCacheBlock)
     *
     * @param cacheFlag If false, don't check biomeCache to avoid infinite loop in BiomeCacheBlock
     */
    public BiomeGenBase[] getBiomeGenAt(final BiomeGenBase[] listToReuse, final int x, final int z, final int width, final int length, final boolean cacheFlag) {
        return this.loadBlockGeneratorData(listToReuse, x, z, width, length);
    }

    public BlockPos findBiomePosition(final int x, final int z, final int range, final List<BiomeGenBase> biomes, final Random random) {
        return biomes.contains(this.biomeGenerator) ? new BlockPos(x - range + random.nextInt(range * 2 + 1), 0, z - range + random.nextInt(range * 2 + 1)) : null;
    }

    /**
     * checks given Chunk's Biomes against List of allowed ones
     */
    public boolean areBiomesViable(final int p_76940_1_, final int p_76940_2_, final int p_76940_3_, final List<BiomeGenBase> p_76940_4_) {
        return p_76940_4_.contains(this.biomeGenerator);
    }
}
