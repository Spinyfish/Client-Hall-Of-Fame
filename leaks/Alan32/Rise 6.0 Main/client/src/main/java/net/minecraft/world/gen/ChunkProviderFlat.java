package net.minecraft.world.gen;

import com.google.common.collect.Lists;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.chunk.IChunkProvider;
import net.minecraft.world.gen.feature.WorldGenDungeons;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraft.world.gen.structure.*;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class ChunkProviderFlat implements IChunkProvider {
    private final World worldObj;
    private final Random random;
    private final IBlockState[] cachedBlockIDs = new IBlockState[256];
    private final FlatGeneratorInfo flatWorldGenInfo;
    private final List<MapGenStructure> structureGenerators = Lists.newArrayList();
    private final boolean hasDecoration;
    private final boolean hasDungeons;
    private WorldGenLakes waterLakeGenerator;
    private WorldGenLakes lavaLakeGenerator;

    public ChunkProviderFlat(final World worldIn, final long seed, final boolean generateStructures, final String flatGeneratorSettings) {
        this.worldObj = worldIn;
        this.random = new Random(seed);
        this.flatWorldGenInfo = FlatGeneratorInfo.createFlatGeneratorFromString(flatGeneratorSettings);

        if (generateStructures) {
            final Map<String, Map<String, String>> map = this.flatWorldGenInfo.getWorldFeatures();

            if (map.containsKey("village")) {
                final Map<String, String> map1 = map.get("village");

                if (!map1.containsKey("size")) {
                    map1.put("size", "1");
                }

                this.structureGenerators.add(new MapGenVillage(map1));
            }

            if (map.containsKey("biome_1")) {
                this.structureGenerators.add(new MapGenScatteredFeature(map.get("biome_1")));
            }

            if (map.containsKey("mineshaft")) {
                this.structureGenerators.add(new MapGenMineshaft(map.get("mineshaft")));
            }

            if (map.containsKey("stronghold")) {
                this.structureGenerators.add(new MapGenStronghold(map.get("stronghold")));
            }

            if (map.containsKey("oceanmonument")) {
                this.structureGenerators.add(new StructureOceanMonument(map.get("oceanmonument")));
            }
        }

        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lake")) {
            this.waterLakeGenerator = new WorldGenLakes(Blocks.water);
        }

        if (this.flatWorldGenInfo.getWorldFeatures().containsKey("lava_lake")) {
            this.lavaLakeGenerator = new WorldGenLakes(Blocks.lava);
        }

        this.hasDungeons = this.flatWorldGenInfo.getWorldFeatures().containsKey("dungeon");
        int j = 0;
        int k = 0;
        boolean flag = true;

        for (final FlatLayerInfo flatlayerinfo : this.flatWorldGenInfo.getFlatLayers()) {
            for (int i = flatlayerinfo.getMinY(); i < flatlayerinfo.getMinY() + flatlayerinfo.getLayerCount(); ++i) {
                final IBlockState iblockstate = flatlayerinfo.func_175900_c();

                if (iblockstate.getBlock() != Blocks.air) {
                    flag = false;
                    this.cachedBlockIDs[i] = iblockstate;
                }
            }

            if (flatlayerinfo.func_175900_c().getBlock() == Blocks.air) {
                k += flatlayerinfo.getLayerCount();
            } else {
                j += flatlayerinfo.getLayerCount() + k;
                k = 0;
            }
        }

        worldIn.func_181544_b(j);
        this.hasDecoration = !flag && this.flatWorldGenInfo.getWorldFeatures().containsKey("decoration");
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(final int x, final int z) {
        final ChunkPrimer chunkprimer = new ChunkPrimer();

        for (int i = 0; i < this.cachedBlockIDs.length; ++i) {
            final IBlockState iblockstate = this.cachedBlockIDs[i];

            if (iblockstate != null) {
                for (int j = 0; j < 16; ++j) {
                    for (int k = 0; k < 16; ++k) {
                        chunkprimer.setBlockState(j, i, k, iblockstate);
                    }
                }
            }
        }

        for (final MapGenBase mapgenbase : this.structureGenerators) {
            mapgenbase.generate(this, this.worldObj, x, z, chunkprimer);
        }

        final Chunk chunk = new Chunk(this.worldObj, chunkprimer, x, z);
        final BiomeGenBase[] abiomegenbase = this.worldObj.getWorldChunkManager().loadBlockGeneratorData(null, x * 16, z * 16, 16, 16);
        final byte[] abyte = chunk.getBiomeArray();

        for (int l = 0; l < abyte.length; ++l) {
            abyte[l] = (byte) abiomegenbase[l].biomeID;
        }

        chunk.generateSkylightMap();
        return chunk;
    }

    /**
     * Checks to see if a chunk exists at x, z
     */
    public boolean chunkExists(final int x, final int z) {
        return true;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(final IChunkProvider p_73153_1_, final int p_73153_2_, final int p_73153_3_) {
        final int i = p_73153_2_ * 16;
        final int j = p_73153_3_ * 16;
        final BlockPos blockpos = new BlockPos(i, 0, j);
        final BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(new BlockPos(i + 16, 0, j + 16));
        boolean flag = false;
        this.random.setSeed(this.worldObj.getSeed());
        final long k = this.random.nextLong() / 2L * 2L + 1L;
        final long l = this.random.nextLong() / 2L * 2L + 1L;
        this.random.setSeed((long) p_73153_2_ * k + (long) p_73153_3_ * l ^ this.worldObj.getSeed());
        final ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(p_73153_2_, p_73153_3_);

        for (final MapGenStructure mapgenstructure : this.structureGenerators) {
            final boolean flag1 = mapgenstructure.generateStructure(this.worldObj, this.random, chunkcoordintpair);

            if (mapgenstructure instanceof MapGenVillage) {
                flag |= flag1;
            }
        }

        if (this.waterLakeGenerator != null && !flag && this.random.nextInt(4) == 0) {
            this.waterLakeGenerator.generate(this.worldObj, this.random, blockpos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
        }

        if (this.lavaLakeGenerator != null && !flag && this.random.nextInt(8) == 0) {
            final BlockPos blockpos1 = blockpos.add(this.random.nextInt(16) + 8, this.random.nextInt(this.random.nextInt(248) + 8), this.random.nextInt(16) + 8);

            if (blockpos1.getY() < this.worldObj.func_181545_F() || this.random.nextInt(10) == 0) {
                this.lavaLakeGenerator.generate(this.worldObj, this.random, blockpos1);
            }
        }

        if (this.hasDungeons) {
            for (int i1 = 0; i1 < 8; ++i1) {
                (new WorldGenDungeons()).generate(this.worldObj, this.random, blockpos.add(this.random.nextInt(16) + 8, this.random.nextInt(256), this.random.nextInt(16) + 8));
            }
        }

        if (this.hasDecoration) {
            biomegenbase.decorate(this.worldObj, this.random, blockpos);
        }
    }

    public boolean func_177460_a(final IChunkProvider p_177460_1_, final Chunk p_177460_2_, final int p_177460_3_, final int p_177460_4_) {
        return false;
    }

    /**
     * Two modes of operation: if passed true, save all Chunks in one go.  If passed false, save up to two chunks.
     * Return true if all chunks have been saved.
     */
    public boolean saveChunks(final boolean p_73151_1_, final IProgressUpdate progressCallback) {
        return true;
    }

    /**
     * Save extra data not associated with any Chunk.  Not saved during autosave, only during world unload.  Currently
     * unimplemented.
     */
    public void saveExtraData() {
    }

    /**
     * Unloads chunks that are marked to be unloaded. This is not guaranteed to unload every such chunk.
     */
    public boolean unloadQueuedChunks() {
        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave() {
        return true;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString() {
        return "FlatLevelSource";
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(final EnumCreatureType creatureType, final BlockPos pos) {
        final BiomeGenBase biomegenbase = this.worldObj.getBiomeGenForCoords(pos);
        return biomegenbase.getSpawnableList(creatureType);
    }

    public BlockPos getStrongholdGen(final World worldIn, final String structureName, final BlockPos position) {
        if ("Stronghold".equals(structureName)) {
            for (final MapGenStructure mapgenstructure : this.structureGenerators) {
                if (mapgenstructure instanceof MapGenStronghold) {
                    return mapgenstructure.getClosestStrongholdPos(worldIn, position);
                }
            }
        }

        return null;
    }

    public int getLoadedChunkCount() {
        return 0;
    }

    public void recreateStructures(final Chunk p_180514_1_, final int p_180514_2_, final int p_180514_3_) {
        for (final MapGenStructure mapgenstructure : this.structureGenerators) {
            mapgenstructure.generate(this, this.worldObj, p_180514_2_, p_180514_3_, null);
        }
    }

    public Chunk provideChunk(final BlockPos blockPosIn) {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
