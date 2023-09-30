/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk.storage;

import java.io.IOException;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;

public interface IChunkLoader {
    public Chunk loadChunk(World var1, int var2, int var3) throws IOException;

    public void saveChunk(World var1, Chunk var2) throws MinecraftException, IOException;

    public void saveExtraChunkData(World var1, Chunk var2) throws IOException;

    public void chunkTick();

    public void saveExtraData();
}

