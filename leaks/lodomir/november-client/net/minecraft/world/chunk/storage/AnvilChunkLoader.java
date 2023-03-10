/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.world.chunk.storage;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.NextTickListEntry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.NibbleArray;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.chunk.storage.RegionFileCache;
import net.minecraft.world.storage.IThreadedFileIO;
import net.minecraft.world.storage.ThreadedFileIOBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class AnvilChunkLoader
implements IChunkLoader,
IThreadedFileIO {
    private static final Logger logger = LogManager.getLogger();
    private Map<ChunkCoordIntPair, NBTTagCompound> chunksToRemove = new ConcurrentHashMap<ChunkCoordIntPair, NBTTagCompound>();
    private Set<ChunkCoordIntPair> pendingAnvilChunksCoordinates = Collections.newSetFromMap(new ConcurrentHashMap());
    private final File chunkSaveLocation;
    private boolean field_183014_e = false;

    public AnvilChunkLoader(File chunkSaveLocationIn) {
        this.chunkSaveLocation = chunkSaveLocationIn;
    }

    @Override
    public Chunk loadChunk(World worldIn, int x, int z) throws IOException {
        ChunkCoordIntPair chunkcoordintpair = new ChunkCoordIntPair(x, z);
        NBTTagCompound nbttagcompound = this.chunksToRemove.get(chunkcoordintpair);
        if (nbttagcompound == null) {
            DataInputStream datainputstream = RegionFileCache.getChunkInputStream(this.chunkSaveLocation, x, z);
            if (datainputstream == null) {
                return null;
            }
            nbttagcompound = CompressedStreamTools.read(datainputstream);
        }
        return this.checkedReadChunkFromNBT(worldIn, x, z, nbttagcompound);
    }

    protected Chunk checkedReadChunkFromNBT(World worldIn, int x, int z, NBTTagCompound p_75822_4_) {
        if (!p_75822_4_.hasKey("Level", 10)) {
            logger.error("Chunk file at " + x + "," + z + " is missing level data, skipping");
            return null;
        }
        NBTTagCompound nbttagcompound = p_75822_4_.getCompoundTag("Level");
        if (!nbttagcompound.hasKey("Sections", 9)) {
            logger.error("Chunk file at " + x + "," + z + " is missing block data, skipping");
            return null;
        }
        Chunk chunk = this.readChunkFromNBT(worldIn, nbttagcompound);
        if (!chunk.isAtLocation(x, z)) {
            logger.error("Chunk file at " + x + "," + z + " is in the wrong location; relocating. (Expected " + x + ", " + z + ", got " + chunk.xPosition + ", " + chunk.zPosition + ")");
            nbttagcompound.setInteger("xPos", x);
            nbttagcompound.setInteger("zPos", z);
            chunk = this.readChunkFromNBT(worldIn, nbttagcompound);
        }
        return chunk;
    }

    @Override
    public void saveChunk(World worldIn, Chunk chunkIn) throws MinecraftException, IOException {
        worldIn.checkSessionLock();
        try {
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            NBTTagCompound nbttagcompound1 = new NBTTagCompound();
            nbttagcompound.setTag("Level", nbttagcompound1);
            this.writeChunkToNBT(chunkIn, worldIn, nbttagcompound1);
            this.addChunkToPending(chunkIn.getChunkCoordIntPair(), nbttagcompound);
        }
        catch (Exception exception) {
            logger.error("Failed to save chunk", (Throwable)exception);
        }
    }

    protected void addChunkToPending(ChunkCoordIntPair p_75824_1_, NBTTagCompound p_75824_2_) {
        if (!this.pendingAnvilChunksCoordinates.contains(p_75824_1_)) {
            this.chunksToRemove.put(p_75824_1_, p_75824_2_);
        }
        ThreadedFileIOBase.getThreadedIOInstance().queueIO(this);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public boolean writeNextIO() {
        boolean lvt_3_1_;
        if (this.chunksToRemove.isEmpty()) {
            if (this.field_183014_e) {
                logger.info("ThreadedAnvilChunkStorage ({}): All chunks are saved", new Object[]{this.chunkSaveLocation.getName()});
            }
            return false;
        }
        ChunkCoordIntPair chunkcoordintpair = this.chunksToRemove.keySet().iterator().next();
        try {
            this.pendingAnvilChunksCoordinates.add(chunkcoordintpair);
            NBTTagCompound nbttagcompound = this.chunksToRemove.remove(chunkcoordintpair);
            if (nbttagcompound != null) {
                try {
                    this.func_183013_b(chunkcoordintpair, nbttagcompound);
                }
                catch (Exception exception) {
                    logger.error("Failed to save chunk", (Throwable)exception);
                }
            }
            lvt_3_1_ = true;
        }
        finally {
            this.pendingAnvilChunksCoordinates.remove(chunkcoordintpair);
        }
        return lvt_3_1_;
    }

    private void func_183013_b(ChunkCoordIntPair p_183013_1_, NBTTagCompound p_183013_2_) throws IOException {
        DataOutputStream dataoutputstream = RegionFileCache.getChunkOutputStream(this.chunkSaveLocation, p_183013_1_.chunkXPos, p_183013_1_.chunkZPos);
        CompressedStreamTools.write(p_183013_2_, dataoutputstream);
        dataoutputstream.close();
    }

    @Override
    public void saveExtraChunkData(World worldIn, Chunk chunkIn) throws IOException {
    }

    @Override
    public void chunkTick() {
    }

    @Override
    public void saveExtraData() {
        try {
            this.field_183014_e = true;
            while (true) {
                if (this.writeNextIO()) continue;
            }
        }
        catch (Throwable throwable) {
            this.field_183014_e = false;
            throw throwable;
        }
    }

    private void writeChunkToNBT(Chunk chunkIn, World worldIn, NBTTagCompound p_75820_3_) {
        p_75820_3_.setByte("V", (byte)1);
        p_75820_3_.setInteger("xPos", chunkIn.xPosition);
        p_75820_3_.setInteger("zPos", chunkIn.zPosition);
        p_75820_3_.setLong("LastUpdate", worldIn.getTotalWorldTime());
        p_75820_3_.setIntArray("HeightMap", chunkIn.getHeightMap());
        p_75820_3_.setBoolean("TerrainPopulated", chunkIn.isTerrainPopulated());
        p_75820_3_.setBoolean("LightPopulated", chunkIn.isLightPopulated());
        p_75820_3_.setLong("InhabitedTime", chunkIn.getInhabitedTime());
        ExtendedBlockStorage[] aextendedblockstorage = chunkIn.getBlockStorageArray();
        NBTTagList nbttaglist = new NBTTagList();
        boolean flag = !worldIn.provider.getHasNoSky();
        for (ExtendedBlockStorage extendedblockstorage : aextendedblockstorage) {
            if (extendedblockstorage == null) continue;
            NBTTagCompound nbttagcompound = new NBTTagCompound();
            nbttagcompound.setByte("Y", (byte)(extendedblockstorage.getYLocation() >> 4 & 0xFF));
            byte[] abyte = new byte[extendedblockstorage.getData().length];
            NibbleArray nibblearray = new NibbleArray();
            NibbleArray nibblearray1 = null;
            for (int i = 0; i < extendedblockstorage.getData().length; ++i) {
                char c0 = extendedblockstorage.getData()[i];
                int j = i & 0xF;
                int k = i >> 8 & 0xF;
                int l = i >> 4 & 0xF;
                if (c0 >> 12 != 0) {
                    if (nibblearray1 == null) {
                        nibblearray1 = new NibbleArray();
                    }
                    nibblearray1.set(j, k, l, c0 >> 12);
                }
                abyte[i] = (byte)(c0 >> 4 & 0xFF);
                nibblearray.set(j, k, l, c0 & 0xF);
            }
            nbttagcompound.setByteArray("Blocks", abyte);
            nbttagcompound.setByteArray("Data", nibblearray.getData());
            if (nibblearray1 != null) {
                nbttagcompound.setByteArray("Add", nibblearray1.getData());
            }
            nbttagcompound.setByteArray("BlockLight", extendedblockstorage.getBlocklightArray().getData());
            if (flag) {
                nbttagcompound.setByteArray("SkyLight", extendedblockstorage.getSkylightArray().getData());
            } else {
                nbttagcompound.setByteArray("SkyLight", new byte[extendedblockstorage.getBlocklightArray().getData().length]);
            }
            nbttaglist.appendTag(nbttagcompound);
        }
        p_75820_3_.setTag("Sections", nbttaglist);
        p_75820_3_.setByteArray("Biomes", chunkIn.getBiomeArray());
        chunkIn.setHasEntities(false);
        NBTTagList nbttaglist1 = new NBTTagList();
        for (int i1 = 0; i1 < chunkIn.getEntityLists().length; ++i1) {
            for (Entity entity : chunkIn.getEntityLists()[i1]) {
                NBTTagCompound nbttagcompound1;
                if (!entity.writeToNBTOptional(nbttagcompound1 = new NBTTagCompound())) continue;
                chunkIn.setHasEntities(true);
                nbttaglist1.appendTag(nbttagcompound1);
            }
        }
        p_75820_3_.setTag("Entities", nbttaglist1);
        NBTTagList nbttaglist2 = new NBTTagList();
        for (TileEntity tileentity : chunkIn.getTileEntityMap().values()) {
            NBTTagCompound nbttagcompound2 = new NBTTagCompound();
            tileentity.writeToNBT(nbttagcompound2);
            nbttaglist2.appendTag(nbttagcompound2);
        }
        p_75820_3_.setTag("TileEntities", nbttaglist2);
        List<NextTickListEntry> list = worldIn.getPendingBlockUpdates(chunkIn, false);
        if (list != null) {
            long j1 = worldIn.getTotalWorldTime();
            NBTTagList nbttaglist3 = new NBTTagList();
            for (NextTickListEntry nextticklistentry : list) {
                NBTTagCompound nbttagcompound3 = new NBTTagCompound();
                ResourceLocation resourcelocation = (ResourceLocation)Block.blockRegistry.getNameForObject(nextticklistentry.getBlock());
                nbttagcompound3.setString("i", resourcelocation == null ? "" : resourcelocation.toString());
                nbttagcompound3.setInteger("x", nextticklistentry.position.getX());
                nbttagcompound3.setInteger("y", nextticklistentry.position.getY());
                nbttagcompound3.setInteger("z", nextticklistentry.position.getZ());
                nbttagcompound3.setInteger("t", (int)(nextticklistentry.scheduledTime - j1));
                nbttagcompound3.setInteger("p", nextticklistentry.priority);
                nbttaglist3.appendTag(nbttagcompound3);
            }
            p_75820_3_.setTag("TileTicks", nbttaglist3);
        }
    }

    private Chunk readChunkFromNBT(World worldIn, NBTTagCompound p_75823_2_) {
        NBTTagList nbttaglist3;
        NBTTagList nbttaglist2;
        NBTTagList nbttaglist1;
        int i = p_75823_2_.getInteger("xPos");
        int j = p_75823_2_.getInteger("zPos");
        Chunk chunk = new Chunk(worldIn, i, j);
        chunk.setHeightMap(p_75823_2_.getIntArray("HeightMap"));
        chunk.setTerrainPopulated(p_75823_2_.getBoolean("TerrainPopulated"));
        chunk.setLightPopulated(p_75823_2_.getBoolean("LightPopulated"));
        chunk.setInhabitedTime(p_75823_2_.getLong("InhabitedTime"));
        NBTTagList nbttaglist = p_75823_2_.getTagList("Sections", 10);
        int k = 16;
        ExtendedBlockStorage[] aextendedblockstorage = new ExtendedBlockStorage[k];
        boolean flag = !worldIn.provider.getHasNoSky();
        for (int l = 0; l < nbttaglist.tagCount(); ++l) {
            NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(l);
            byte i1 = nbttagcompound.getByte("Y");
            ExtendedBlockStorage extendedblockstorage = new ExtendedBlockStorage(i1 << 4, flag);
            byte[] abyte = nbttagcompound.getByteArray("Blocks");
            NibbleArray nibblearray = new NibbleArray(nbttagcompound.getByteArray("Data"));
            NibbleArray nibblearray1 = nbttagcompound.hasKey("Add", 7) ? new NibbleArray(nbttagcompound.getByteArray("Add")) : null;
            char[] achar = new char[abyte.length];
            for (int j1 = 0; j1 < achar.length; ++j1) {
                int k1 = j1 & 0xF;
                int l1 = j1 >> 8 & 0xF;
                int i2 = j1 >> 4 & 0xF;
                int j2 = nibblearray1 != null ? nibblearray1.get(k1, l1, i2) : 0;
                achar[j1] = (char)(j2 << 12 | (abyte[j1] & 0xFF) << 4 | nibblearray.get(k1, l1, i2));
            }
            extendedblockstorage.setData(achar);
            extendedblockstorage.setBlocklightArray(new NibbleArray(nbttagcompound.getByteArray("BlockLight")));
            if (flag) {
                extendedblockstorage.setSkylightArray(new NibbleArray(nbttagcompound.getByteArray("SkyLight")));
            }
            extendedblockstorage.removeInvalidBlocks();
            aextendedblockstorage[i1] = extendedblockstorage;
        }
        chunk.setStorageArrays(aextendedblockstorage);
        if (p_75823_2_.hasKey("Biomes", 7)) {
            chunk.setBiomeArray(p_75823_2_.getByteArray("Biomes"));
        }
        if ((nbttaglist1 = p_75823_2_.getTagList("Entities", 10)) != null) {
            for (int k2 = 0; k2 < nbttaglist1.tagCount(); ++k2) {
                NBTTagCompound nbttagcompound1 = nbttaglist1.getCompoundTagAt(k2);
                Entity entity = EntityList.createEntityFromNBT(nbttagcompound1, worldIn);
                chunk.setHasEntities(true);
                if (entity == null) continue;
                chunk.addEntity(entity);
                Entity entity1 = entity;
                NBTTagCompound nbttagcompound4 = nbttagcompound1;
                while (nbttagcompound4.hasKey("Riding", 10)) {
                    Entity entity2 = EntityList.createEntityFromNBT(nbttagcompound4.getCompoundTag("Riding"), worldIn);
                    if (entity2 != null) {
                        chunk.addEntity(entity2);
                        entity1.mountEntity(entity2);
                    }
                    entity1 = entity2;
                    nbttagcompound4 = nbttagcompound4.getCompoundTag("Riding");
                }
            }
        }
        if ((nbttaglist2 = p_75823_2_.getTagList("TileEntities", 10)) != null) {
            for (int l2 = 0; l2 < nbttaglist2.tagCount(); ++l2) {
                NBTTagCompound nbttagcompound2 = nbttaglist2.getCompoundTagAt(l2);
                TileEntity tileentity = TileEntity.createAndLoadEntity(nbttagcompound2);
                if (tileentity == null) continue;
                chunk.addTileEntity(tileentity);
            }
        }
        if (p_75823_2_.hasKey("TileTicks", 9) && (nbttaglist3 = p_75823_2_.getTagList("TileTicks", 10)) != null) {
            for (int i3 = 0; i3 < nbttaglist3.tagCount(); ++i3) {
                NBTTagCompound nbttagcompound3 = nbttaglist3.getCompoundTagAt(i3);
                Block block = nbttagcompound3.hasKey("i", 8) ? Block.getBlockFromName(nbttagcompound3.getString("i")) : Block.getBlockById(nbttagcompound3.getInteger("i"));
                worldIn.scheduleBlockUpdate(new BlockPos(nbttagcompound3.getInteger("x"), nbttagcompound3.getInteger("y"), nbttagcompound3.getInteger("z")), block, nbttagcompound3.getInteger("t"), nbttagcompound3.getInteger("p"));
            }
        }
        return chunk;
    }
}

