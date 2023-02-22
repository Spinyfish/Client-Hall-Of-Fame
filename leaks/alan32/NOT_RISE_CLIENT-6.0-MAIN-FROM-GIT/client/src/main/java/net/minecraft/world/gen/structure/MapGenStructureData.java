package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.WorldSavedData;

public class MapGenStructureData extends WorldSavedData {
    private NBTTagCompound tagCompound = new NBTTagCompound();

    public MapGenStructureData(final String name) {
        super(name);
    }

    /**
     * reads in data from the NBTTagCompound into this MapDataBase
     */
    public void readFromNBT(final NBTTagCompound nbt) {
        this.tagCompound = nbt.getCompoundTag("Features");
    }

    /**
     * write data to NBTTagCompound from this MapDataBase, similar to Entities and TileEntities
     */
    public void writeToNBT(final NBTTagCompound nbt) {
        nbt.setTag("Features", this.tagCompound);
    }

    /**
     * Writes the NBT tag of an instance of this structure type to the internal NBT tag, using the chunkcoordinates as
     * the key
     */
    public void writeInstance(final NBTTagCompound tagCompoundIn, final int chunkX, final int chunkZ) {
        this.tagCompound.setTag(formatChunkCoords(chunkX, chunkZ), tagCompoundIn);
    }

    public static String formatChunkCoords(final int chunkX, final int chunkZ) {
        return "[" + chunkX + "," + chunkZ + "]";
    }

    public NBTTagCompound getTagCompound() {
        return this.tagCompound;
    }
}
