package net.minecraft.world.gen.structure;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;

public abstract class StructureStart
{
    /** List of all StructureComponents that are part of this structure */
    protected LinkedList components = new LinkedList();
    protected StructureBoundingBox boundingBox;
    private int chunkPosX;
    private int chunkPosZ;

    public StructureStart() {}

    public StructureStart(int chunkX, int chunkZ)
    {
        this.chunkPosX = chunkX;
        this.chunkPosZ = chunkZ;
    }

    public StructureBoundingBox getBoundingBox()
    {
        return this.boundingBox;
    }

    public LinkedList getComponents()
    {
        return this.components;
    }

    /**
     * Keeps iterating Structure Pieces and spawning them until the checks tell it to stop
     */
    public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb)
    {
        Iterator var4 = this.components.iterator();

        while (var4.hasNext())
        {
            StructureComponent var5 = (StructureComponent)var4.next();

            if (var5.getBoundingBox().intersectsWith(structurebb) && !var5.addComponentParts(worldIn, rand, structurebb))
            {
                var4.remove();
            }
        }
    }

    /**
     * Calculates total bounding box based on components' bounding boxes and saves it to boundingBox
     */
    protected void updateBoundingBox()
    {
        this.boundingBox = StructureBoundingBox.getNewBoundingBox();
        Iterator var1 = this.components.iterator();

        while (var1.hasNext())
        {
            StructureComponent var2 = (StructureComponent)var1.next();
            this.boundingBox.expandTo(var2.getBoundingBox());
        }
    }

    public NBTTagCompound writeStructureComponentsToNBT(int chunkX, int chunkZ)
    {
        NBTTagCompound var3 = new NBTTagCompound();
        var3.setString("id", MapGenStructureIO.getStructureStartName(this));
        var3.setInteger("ChunkX", chunkX);
        var3.setInteger("ChunkZ", chunkZ);
        var3.setTag("BB", this.boundingBox.toNBTTagIntArray());
        NBTTagList var4 = new NBTTagList();
        Iterator var5 = this.components.iterator();

        while (var5.hasNext())
        {
            StructureComponent var6 = (StructureComponent)var5.next();
            var4.appendTag(var6.createStructureBaseNBT());
        }

        var3.setTag("Children", var4);
        this.writeToNBT(var3);
        return var3;
    }

    public void writeToNBT(NBTTagCompound tagCompound) {}

    public void readStructureComponentsFromNBT(World worldIn, NBTTagCompound tagCompound)
    {
        this.chunkPosX = tagCompound.getInteger("ChunkX");
        this.chunkPosZ = tagCompound.getInteger("ChunkZ");

        if (tagCompound.hasKey("BB"))
        {
            this.boundingBox = new StructureBoundingBox(tagCompound.getIntArray("BB"));
        }

        NBTTagList var3 = tagCompound.getTagList("Children", 10);

        for (int var4 = 0; var4 < var3.tagCount(); ++var4)
        {
            this.components.add(MapGenStructureIO.getStructureComponent(var3.getCompoundTagAt(var4), worldIn));
        }

        this.readFromNBT(tagCompound);
    }

    public void readFromNBT(NBTTagCompound tagCompound) {}

    /**
     * offsets the structure Bounding Boxes up to a certain height, typically 63 - 10
     */
    protected void markAvailableHeight(World worldIn, Random rand, int p_75067_3_)
    {
        int var4 = 63 - p_75067_3_;
        int var5 = this.boundingBox.getYSize() + 1;

        if (var5 < var4)
        {
            var5 += rand.nextInt(var4 - var5);
        }

        int var6 = var5 - this.boundingBox.maxY;
        this.boundingBox.offset(0, var6, 0);
        Iterator var7 = this.components.iterator();

        while (var7.hasNext())
        {
            StructureComponent var8 = (StructureComponent)var7.next();
            var8.getBoundingBox().offset(0, var6, 0);
        }
    }

    protected void setRandomHeight(World worldIn, Random rand, int p_75070_3_, int p_75070_4_)
    {
        int var5 = p_75070_4_ - p_75070_3_ + 1 - this.boundingBox.getYSize();
        boolean var6 = true;
        int var10;

        if (var5 > 1)
        {
            var10 = p_75070_3_ + rand.nextInt(var5);
        }
        else
        {
            var10 = p_75070_3_;
        }

        int var7 = var10 - this.boundingBox.minY;
        this.boundingBox.offset(0, var7, 0);
        Iterator var8 = this.components.iterator();

        while (var8.hasNext())
        {
            StructureComponent var9 = (StructureComponent)var8.next();
            var9.getBoundingBox().offset(0, var7, 0);
        }
    }

    /**
     * currently only defined for Villages, returns true if Village has more than 2 non-road components
     */
    public boolean isSizeableStructure()
    {
        return true;
    }

    public boolean func_175788_a(ChunkCoordIntPair pair)
    {
        return true;
    }

    public void func_175787_b(ChunkCoordIntPair pair) {}

    public int getChunkPosX()
    {
        return this.chunkPosX;
    }

    public int getChunkPosZ()
    {
        return this.chunkPosZ;
    }
}
