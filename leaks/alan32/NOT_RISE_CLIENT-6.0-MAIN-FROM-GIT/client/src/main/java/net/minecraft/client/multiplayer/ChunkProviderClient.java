package net.minecraft.client.multiplayer;

import com.google.common.collect.Lists;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.util.LongHashMap;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraft.world.chunk.IChunkProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ChunkProviderClient implements IChunkProvider {
    private static final Logger logger = LogManager.getLogger();

    /**
     * The completely empty chunk used by ChunkProviderClient when chunkMapping doesn't contain the requested
     * coordinates.
     */
    private final Chunk blankChunk;
    private final LongHashMap<Chunk> chunkMapping = new LongHashMap();
    private final List<Chunk> chunkListing = Lists.newArrayList();

    /**
     * Reference to the World object.
     */
    private final World worldObj;

    public ChunkProviderClient(final World worldIn) {
        this.blankChunk = new EmptyChunk(worldIn, 0, 0);
        this.worldObj = worldIn;
    }

    /**
     * Checks to see if a chunk exists at x, z
     */
    public boolean chunkExists(final int x, final int z) {
        return true;
    }

    /**
     * Unload chunk from ChunkProviderClient's hashmap. Called in response to a Packet50PreChunk with its mode field set
     * to false
     */
    public void unloadChunk(final int p_73234_1_, final int p_73234_2_) {
        final Chunk chunk = this.provideChunk(p_73234_1_, p_73234_2_);

        if (!chunk.isEmpty()) {
            chunk.onChunkUnload();
        }

        this.chunkMapping.remove(ChunkCoordIntPair.chunkXZ2Int(p_73234_1_, p_73234_2_));
        this.chunkListing.remove(chunk);
    }

    /**
     * loads or generates the chunk at the chunk location specified
     */
    public Chunk loadChunk(final int p_73158_1_, final int p_73158_2_) {
        final Chunk chunk = new Chunk(this.worldObj, p_73158_1_, p_73158_2_);
        this.chunkMapping.add(ChunkCoordIntPair.chunkXZ2Int(p_73158_1_, p_73158_2_), chunk);
        this.chunkListing.add(chunk);
        chunk.setChunkLoaded(true);
        return chunk;
    }

    /**
     * Will return back a chunk, if it doesn't exist and its not a MP client it will generates all the blocks for the
     * specified chunk from the map seed and chunk seed
     */
    public Chunk provideChunk(final int x, final int z) {
        final Chunk chunk = this.chunkMapping.getValueByKey(ChunkCoordIntPair.chunkXZ2Int(x, z));
        return chunk == null ? this.blankChunk : chunk;
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
        final long i = System.currentTimeMillis();

        for (final Chunk chunk : this.chunkListing) {
            chunk.func_150804_b(System.currentTimeMillis() - i > 5L);
        }

        if (System.currentTimeMillis() - i > 100L) {
            logger.info("Warning: Clientside chunk ticking took {} ms", new Object[]{Long.valueOf(System.currentTimeMillis() - i)});
        }

        return false;
    }

    /**
     * Returns if the IChunkProvider supports saving.
     */
    public boolean canSave() {
        return false;
    }

    /**
     * Populates chunk with ores etc etc
     */
    public void populate(final IChunkProvider p_73153_1_, final int p_73153_2_, final int p_73153_3_) {
    }

    public boolean func_177460_a(final IChunkProvider p_177460_1_, final Chunk p_177460_2_, final int p_177460_3_, final int p_177460_4_) {
        return false;
    }

    /**
     * Converts the instance data to a readable string.
     */
    public String makeString() {
        return "MultiplayerChunkCache: " + this.chunkMapping.getNumHashElements() + ", " + this.chunkListing.size();
    }

    public List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(final EnumCreatureType creatureType, final BlockPos pos) {
        return null;
    }

    public BlockPos getStrongholdGen(final World worldIn, final String structureName, final BlockPos position) {
        return null;
    }

    public int getLoadedChunkCount() {
        return this.chunkListing.size();
    }

    public void recreateStructures(final Chunk p_180514_1_, final int p_180514_2_, final int p_180514_3_) {
    }

    public Chunk provideChunk(final BlockPos blockPosIn) {
        return this.provideChunk(blockPosIn.getX() >> 4, blockPosIn.getZ() >> 4);
    }
}
