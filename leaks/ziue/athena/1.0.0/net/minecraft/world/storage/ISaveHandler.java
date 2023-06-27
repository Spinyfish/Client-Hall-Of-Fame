package net.minecraft.world.storage;

import net.minecraft.world.*;
import net.minecraft.world.chunk.storage.*;
import net.minecraft.nbt.*;
import java.io.*;

public interface ISaveHandler
{
    WorldInfo loadWorldInfo();
    
    void checkSessionLock() throws MinecraftException;
    
    IChunkLoader getChunkLoader(final WorldProvider p0);
    
    void saveWorldInfoWithPlayer(final WorldInfo p0, final NBTTagCompound p1);
    
    void saveWorldInfo(final WorldInfo p0);
    
    IPlayerFileData getPlayerNBTManager();
    
    void flush();
    
    File getWorldDirectory();
    
    File getMapFileFromName(final String p0);
    
    String getWorldDirectoryName();
}
