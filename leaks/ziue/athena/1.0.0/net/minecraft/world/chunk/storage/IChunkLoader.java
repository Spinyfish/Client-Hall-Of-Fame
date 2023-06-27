package net.minecraft.world.chunk.storage;

import net.minecraft.world.chunk.*;
import java.io.*;
import net.minecraft.world.*;

public interface IChunkLoader
{
    Chunk loadChunk(final World p0, final int p1, final int p2) throws IOException;
    
    void saveChunk(final World p0, final Chunk p1) throws MinecraftException, IOException;
    
    void saveExtraChunkData(final World p0, final Chunk p1) throws IOException;
    
    void chunkTick();
    
    void saveExtraData();
}
