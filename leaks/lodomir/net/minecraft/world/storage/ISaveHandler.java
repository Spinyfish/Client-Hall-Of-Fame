/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.storage.IPlayerFileData;
import net.minecraft.world.storage.WorldInfo;

public interface ISaveHandler {
    public WorldInfo loadWorldInfo();

    public void checkSessionLock() throws MinecraftException;

    public IChunkLoader getChunkLoader(WorldProvider var1);

    public void saveWorldInfoWithPlayer(WorldInfo var1, NBTTagCompound var2);

    public void saveWorldInfo(WorldInfo var1);

    public IPlayerFileData getPlayerNBTManager();

    public void flush();

    public File getWorldDirectory();

    public File getMapFileFromName(String var1);

    public String getWorldDirectoryName();
}

