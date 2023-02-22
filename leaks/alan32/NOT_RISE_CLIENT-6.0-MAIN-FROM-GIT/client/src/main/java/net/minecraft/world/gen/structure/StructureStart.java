package net.minecraft.world.gen.structure;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

public abstract class StructureStart {
    protected LinkedList<StructureComponent> components = new LinkedList();
    protected StructureBoundingBox boundingBox;
    private int chunkPosX;
    private int chunkPosZ;

    public StructureStart() {
    }

    public StructureStart(final int chunkX, final int chunkZ) {
        this.chunkPosX = chunkX;
        this.chunkPosZ = chunkZ;
    }

    public StructureBoundingBox getBoundingBox() {
        return this.boundingBox;
    }

    public LinkedList<StructureComponent> getComponents() {
        return this.components;
    }

    /**
     * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
     */
    public void generateStructure(final World worldIn, final Random rand, final StructureBoundingBox structurebb) {
        final Iterator<StructureComponent> iterator = this.components.iterator();

        while (iterator.hasNext()) {
            final StructureComponent structurecomponent = iterator.next();

            if (structurecomponent.getBoundingBox().intersectsWith(structurebb) && !structurecomponent.addComponentParts(worldIn, rand, structurebb)) {
                iterator.remove();
            }
        }
    }

    /**
     * Calculates total bounding box based on components' bounding boxes and saves it to boundingBox
     */
    protected void updateBoundingBox() {
        this.boundingBox = StructureBoundingBox.getNewBoundingBox();

        for (final StructureComponent structurecomponent : this.components) {
            this.boundingBox.expandTo(structurecomponent.getBoundingBox());
        }
    }

    public NBTTagCompound writeStructureComponentsToNBT(final int chunkX, final int chunkZ) {
        final NBTTagCompound nbttagcompound = new NBTTagCompound();
        nbttagcompound.setString("id", MapGenStructureIO.getStructureStartName(this));
        nbttagcompound.setInteger("ChunkX", chunkX);
        nbttagcompound.setInteger("ChunkZ", chunkZ);
        nbttagcompound.setTag("BB", this.boundingBox.toNBTTagIntArray());
        final NBTTagList nbttaglist = new NBTTagList();

        for (final StructureComponent structurecomponent : this.components) {
            nbttaglist.appendTag(structurecomponent.createStructureBaseNBT());
        }

        nbttagcompound.setTag("Children", nbttaglist);
        this.writeToNBT(nbttagcompound);
        return nbttagcompound;
    }

    public void writeToNBT(final NBTTagCompound tagCompound) {
    }

    public void readStructureComponentsFromNBT(final World worldIn, final NBTTagCompound tagCompound) {
        this.chunkPosX = tagCompound.getInteger("ChunkX");
        this.chunkPosZ = tagCompound.getInteger("ChunkZ");

        if (tagCompound.hasKey("BB")) {
            this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
        }

        final NBTTagList nbttaglist = tagCompound.getTagList("Children", 10);

        for (int i = 0; i < nbttaglist.tagCount(); ++i) {
            this.components.add(MapGenStructureIO.getStructureComponent(nbttaglist.getCompoundTagAt(i), worldIn));
        }

        this.readFromNBT(tagCompound);
    }

    public void readFromNBT(final NBTTagCompound tagCompound) {
    }

    /**
     * offsets the structure Bounding Boxes up to a certain height, typically 63 - 10
     */
    protected void markAvailableHeight(final World worldIn, final Random rand, final int p_75067_3_) {
        final int i = worldIn.func_181545_F() - p_75067_3_;
        int j = this.boundingBox.getYSize() + 1;

        if (j < i) {
            j += rand.nextInt(i - j);
        }

        final int k = j - this.boundingBox.maxY;
        this.boundingBox.offset(0, k, 0);

        for (final StructureComponent structurecomponent : this.components) {
            structurecomponent.func_181138_a(0, k, 0);
        }
    }

    protected void setRandomHeight(final World worldIn, final Random rand, final int p_75070_3_, final int p_75070_4_) {
        final int i = p_75070_4_ - p_75070_3_ + 1 - this.boundingBox.getYSize();
        int j = 1;

        if (i > 1) {
            j = p_75070_3_ + rand.nextInt(i);
        } else {
            j = p_75070_3_;
        }

        final int k = j - this.boundingBox.minY;
        this.boundingBox.offset(0, k, 0);

        for (final StructureComponent structurecomponent : this.components) {
            structurecomponent.func_181138_a(0, k, 0);
        }
    }

    /**
     * currently only defined for Villages, returns true if Village has more than 2 non-road components
     */
    public boolean isSizeableStructure() {
        return true;
    }

    public boolean func_175788_a(final ChunkCoordIntPair pair) {
        return true;
    }

    public void func_175787_b(final ChunkCoordIntPair pair) {
    }

    public int getChunkPosX() {
        return this.chunkPosX;
    }

    public int getChunkPosZ() {
        return this.chunkPosZ;
    }
}
