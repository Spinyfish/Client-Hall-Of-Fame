package net.minecraft.world.chunk;

import java.util.List;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.util.BlockPosition;
import net.minecraft.util.IProgressUpdate;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public interface IChunkProvider
{
    boolean chunkExists(int x, int z);

    Chunk provideChunk(int x, int z);

    Chunk provideChunk(BlockPosition blockPositionIn);

    void populate(IChunkProvider chunkProvider, int x, int z);

    boolean populateChunk(IChunkProvider chunkProvider, Chunk chunkIn, int x, int z);

    boolean saveChunks(boolean saveAllChunks, IProgressUpdate progressCallback);

    boolean unloadQueuedChunks();

    boolean canSave();

    String makeString();

    List<BiomeGenBase.SpawnListEntry> getPossibleCreatures(EnumCreatureType creatureType, BlockPosition pos);

    BlockPosition getStrongholdGen(World worldIn, String structureName, BlockPosition position);

    int getLoadedChunkCount();

    void recreateStructures(Chunk chunkIn, int x, int z);

    void saveExtraData();
}
