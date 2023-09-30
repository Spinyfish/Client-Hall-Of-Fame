package net.minecraft.world.biome;

import com.google.common.collect.Lists;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.LongHashMap;

import java.util.List;

public class BiomeCache {
    /**
     * Reference to the WorldChunkManager
     */
    private final WorldChunkManager chunkManager;

    /**
     * The last time this BiomeCache was cleaned, in milliseconds.
     */
    private long lastCleanupTime;
    private final LongHashMap<BiomeCache.Block> cacheMap = new LongHashMap();
    private final List<BiomeCache.Block> cache = Lists.newArrayList();

    public BiomeCache(final WorldChunkManager chunkManagerIn) {
        this.chunkManager = chunkManagerIn;
    }

    /**
     * Returns a biome cache block at location specified.
     */
    public BiomeCache.Block getBiomeCacheBlock(int x, int z) {
        x = x >> 4;
        z = z >> 4;
        final long i = (long) x & 4294967295L | ((long) z & 4294967295L) << 32;
        BiomeCache.Block biomecache$block = this.cacheMap.getValueByKey(i);

        if (biomecache$block == null) {
            biomecache$block = new BiomeCache.Block(x, z);
            this.cacheMap.add(i, biomecache$block);
            this.cache.add(biomecache$block);
        }

        biomecache$block.lastAccessTime = MinecraftServer.getCurrentTimeMillis();
        return biomecache$block;
    }

    public BiomeGenBase func_180284_a(final int x, final int z, final BiomeGenBase p_180284_3_) {
        final BiomeGenBase biomegenbase = this.getBiomeCacheBlock(x, z).getBiomeGenAt(x, z);
        return biomegenbase == null ? p_180284_3_ : biomegenbase;
    }

    /**
     * Removes BiomeCacheBlocks from this cache that haven't been accessed in at least 30 seconds.
     */
    public void cleanupCache() {
        final long i = MinecraftServer.getCurrentTimeMillis();
        final long j = i - this.lastCleanupTime;

        if (j > 7500L || j < 0L) {
            this.lastCleanupTime = i;

            for (int k = 0; k < this.cache.size(); ++k) {
                final BiomeCache.Block biomecache$block = this.cache.get(k);
                final long l = i - biomecache$block.lastAccessTime;

                if (l > 30000L || l < 0L) {
                    this.cache.remove(k--);
                    final long i1 = (long) biomecache$block.xPosition & 4294967295L | ((long) biomecache$block.zPosition & 4294967295L) << 32;
                    this.cacheMap.remove(i1);
                }
            }
        }
    }

    /**
     * Returns the array of cached biome types in the BiomeCacheBlock at the given location.
     */
    public BiomeGenBase[] getCachedBiomes(final int x, final int z) {
        return this.getBiomeCacheBlock(x, z).biomes;
    }

    public class Block {
        public float[] rainfallValues = new float[256];
        public BiomeGenBase[] biomes = new BiomeGenBase[256];
        public int xPosition;
        public int zPosition;
        public long lastAccessTime;

        public Block(final int x, final int z) {
            this.xPosition = x;
            this.zPosition = z;
            BiomeCache.this.chunkManager.getRainfall(this.rainfallValues, x << 4, z << 4, 16, 16);
            BiomeCache.this.chunkManager.getBiomeGenAt(this.biomes, x << 4, z << 4, 16, 16, false);
        }

        public BiomeGenBase getBiomeGenAt(final int x, final int z) {
            return this.biomes[x & 15 | (z & 15) << 4];
        }
    }
}
