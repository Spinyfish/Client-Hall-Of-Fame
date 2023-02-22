package net.minecraft.world.storage;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagShort;
import net.minecraft.world.WorldSavedData;

import java.io.*;
import java.util.List;
import java.util.Map;

public class MapStorage {
    private final ISaveHandler saveHandler;
    protected Map<String, WorldSavedData> loadedDataMap = Maps.newHashMap();
    private final List<WorldSavedData> loadedDataList = Lists.newArrayList();
    private final Map<String, Short> idCounts = Maps.newHashMap();

    public MapStorage(final ISaveHandler saveHandlerIn) {
        this.saveHandler = saveHandlerIn;
        this.loadIdCounts();
    }

    /**
     * Loads an existing MapDataBase corresponding to the given String id from disk, instantiating the given Class, or
     * returns null if none such file exists. args: Class to instantiate, String dataid
     */
    public WorldSavedData loadData(final Class<? extends WorldSavedData> clazz, final String dataIdentifier) {
        WorldSavedData worldsaveddata = this.loadedDataMap.get(dataIdentifier);

        if (worldsaveddata != null) {
            return worldsaveddata;
        } else {
            if (this.saveHandler != null) {
                try {
                    final File file1 = this.saveHandler.getMapFileFromName(dataIdentifier);

                    if (file1 != null && file1.exists()) {
                        try {
                            worldsaveddata = clazz.getConstructor(new Class[]{String.class}).newInstance(new Object[]{dataIdentifier});
                        } catch (final Exception exception) {
                            throw new RuntimeException("Failed to instantiate " + clazz.toString(), exception);
                        }

                        final FileInputStream fileinputstream = new FileInputStream(file1);
                        final NBTTagCompound nbttagcompound = CompressedStreamTools.readCompressed(fileinputstream);
                        fileinputstream.close();
                        worldsaveddata.readFromNBT(nbttagcompound.getCompoundTag("data"));
                    }
                } catch (final Exception exception1) {
                    exception1.printStackTrace();
                }
            }

            if (worldsaveddata != null) {
                this.loadedDataMap.put(dataIdentifier, worldsaveddata);
                this.loadedDataList.add(worldsaveddata);
            }

            return worldsaveddata;
        }
    }

    /**
     * Assigns the given String id to the given MapDataBase, removing any existing ones of the same id.
     */
    public void setData(final String dataIdentifier, final WorldSavedData data) {
        if (this.loadedDataMap.containsKey(dataIdentifier)) {
            this.loadedDataList.remove(this.loadedDataMap.remove(dataIdentifier));
        }

        this.loadedDataMap.put(dataIdentifier, data);
        this.loadedDataList.add(data);
    }

    /**
     * Saves all dirty loaded MapDataBases to disk.
     */
    public void saveAllData() {
        for (int i = 0; i < this.loadedDataList.size(); ++i) {
            final WorldSavedData worldsaveddata = this.loadedDataList.get(i);

            if (worldsaveddata.isDirty()) {
                this.saveData(worldsaveddata);
                worldsaveddata.setDirty(false);
            }
        }
    }

    /**
     * Saves the given MapDataBase to disk.
     */
    private void saveData(final WorldSavedData p_75747_1_) {
        if (this.saveHandler != null) {
            try {
                final File file1 = this.saveHandler.getMapFileFromName(p_75747_1_.mapName);

                if (file1 != null) {
                    final NBTTagCompound nbttagcompound = new NBTTagCompound();
                    p_75747_1_.writeToNBT(nbttagcompound);
                    final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setTag("data", nbttagcompound);
                    final FileOutputStream fileoutputstream = new FileOutputStream(file1);
                    CompressedStreamTools.writeCompressed(nbttagcompound1, fileoutputstream);
                    fileoutputstream.close();
                }
            } catch (final Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    /**
     * Loads the idCounts Map from the 'idcounts' file.
     */
    private void loadIdCounts() {
        try {
            this.idCounts.clear();

            if (this.saveHandler == null) {
                return;
            }

            final File file1 = this.saveHandler.getMapFileFromName("idcounts");

            if (file1 != null && file1.exists()) {
                final DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1));
                final NBTTagCompound nbttagcompound = CompressedStreamTools.read(datainputstream);
                datainputstream.close();

                for (final String s : nbttagcompound.getKeySet()) {
                    final NBTBase nbtbase = nbttagcompound.getTag(s);

                    if (nbtbase instanceof NBTTagShort) {
                        final NBTTagShort nbttagshort = (NBTTagShort) nbtbase;
                        final short short1 = nbttagshort.getShort();
                        this.idCounts.put(s, Short.valueOf(short1));
                    }
                }
            }
        } catch (final Exception exception) {
            exception.printStackTrace();
        }
    }

    /**
     * Returns an unique new data id for the given prefix and saves the idCounts map to the 'idcounts' file.
     */
    public int getUniqueDataId(final String key) {
        Short oshort = this.idCounts.get(key);

        if (oshort == null) {
            oshort = Short.valueOf((short) 0);
        } else {
            oshort = Short.valueOf((short) (oshort.shortValue() + 1));
        }

        this.idCounts.put(key, oshort);

        if (this.saveHandler == null) {
            return oshort.shortValue();
        } else {
            try {
                final File file1 = this.saveHandler.getMapFileFromName("idcounts");

                if (file1 != null) {
                    final NBTTagCompound nbttagcompound = new NBTTagCompound();

                    for (final String s : this.idCounts.keySet()) {
                        final short short1 = this.idCounts.get(s).shortValue();
                        nbttagcompound.setShort(s, short1);
                    }

                    final DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file1));
                    CompressedStreamTools.write(nbttagcompound, dataoutputstream);
                    dataoutputstream.close();
                }
            } catch (final Exception exception) {
                exception.printStackTrace();
            }

            return oshort.shortValue();
        }
    }
}
