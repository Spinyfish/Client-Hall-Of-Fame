/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world.chunk.storage;

import java.io.File;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldProviderEnd;
import net.minecraft.world.WorldProviderHell;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.ThreadedFileIOBase;
import net.minecraft.world.storage.WorldInfo;

public class AnvilSaveHandler
extends SaveHandler {
    public AnvilSaveHandler(File savesDirectory, String p_i2142_2_, boolean storePlayerdata) {
        super(savesDirectory, p_i2142_2_, storePlayerdata);
    }

    @Override
    public IChunkLoader getChunkLoader(WorldProvider provider) {
        File file1 = this.getWorldDirectory();
        if (provider instanceof WorldProviderHell) {
            File file3 = new File(file1, "DIM-1");
            file3.mkdirs();
            return new AnvilChunkLoader(file3);
        }
        if (provider instanceof WorldProviderEnd) {
            File file2 = new File(file1, "DIM1");
            file2.mkdirs();
            return new AnvilChunkLoader(file2);
        }
        return new AnvilChunkLoader(file1);
    }

    @Override
    public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
        worldInformation.setSaveVersion(19133);
        super.saveWorldInfoWithPlayer(worldInformation, tagCompound);
    }

    @Override
    public void flush() {
        try {
            ThreadedFileIOBase.getThreadedIOInstance().waitForFinish();
        }
        catch (InterruptedException interruptedexception) {
            interruptedexception.printStackTrace();
        }
        RegionFileCache.clearRegionFileReferences();
    }
}

