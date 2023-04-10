package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.entity.monster.EntityGuardian;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import net.minecraft.world.ChunkCoordIntPair;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class StructureOceanMonument extends MapGenStructure
{
    private int field_175800_f;
    private int field_175801_g;
    public static final List field_175802_d = Arrays.asList(new BiomeGenBase[] {BiomeGenBase.ocean, BiomeGenBase.deepOcean, BiomeGenBase.river, BiomeGenBase.frozenOcean, BiomeGenBase.frozenRiver});
    private static final List field_175803_h = Lists.newArrayList();

    public StructureOceanMonument()
    {
        this.field_175800_f = 32;
        this.field_175801_g = 5;
    }

    public StructureOceanMonument(Map p_i45608_1_)
    {
        this();
        Iterator var2 = p_i45608_1_.entrySet().iterator();

        while (var2.hasNext())
        {
            Entry var3 = (Entry)var2.next();

            if (((String)var3.getKey()).equals("spacing"))
            {
                this.field_175800_f = MathHelper.parseIntWithDefaultAndMax((String)var3.getValue(), this.field_175800_f, 1);
            }
            else if (((String)var3.getKey()).equals("separation"))
            {
                this.field_175801_g = MathHelper.parseIntWithDefaultAndMax((String)var3.getValue(), this.field_175801_g, 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Monument";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int var3 = chunkX;
        int var4 = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.field_175800_f - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.field_175800_f - 1;
        }

        int var5 = chunkX / this.field_175800_f;
        int var6 = chunkZ / this.field_175800_f;
        Random var7 = this.worldObj.setRandomSeed(var5, var6, 10387313);
        var5 *= this.field_175800_f;
        var6 *= this.field_175800_f;
        var5 += (var7.nextInt(this.field_175800_f - this.field_175801_g) + var7.nextInt(this.field_175800_f - this.field_175801_g)) / 2;
        var6 += (var7.nextInt(this.field_175800_f - this.field_175801_g) + var7.nextInt(this.field_175800_f - this.field_175801_g)) / 2;

        if (var3 == var5 && var4 == var6)
        {
            if (this.worldObj.getWorldChunkManager().getBiomeGenerator(new BlockPos(var3 * 16 + 8, 64, var4 * 16 + 8), (BiomeGenBase)null) != BiomeGenBase.deepOcean)
            {
                return false;
            }

            boolean var8 = this.worldObj.getWorldChunkManager().areBiomesViable(var3 * 16 + 8, var4 * 16 + 8, 29, field_175802_d);

            if (var8)
            {
                return true;
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new StructureOceanMonument.StartMonument(this.worldObj, this.rand, chunkX, chunkZ);
    }

    public List func_175799_b()
    {
        return field_175803_h;
    }

    static
    {
        field_175803_h.add(new BiomeGenBase.SpawnListEntry(EntityGuardian.class, 1, 2, 4));
    }

    public static class StartMonument extends StructureStart
    {
        private Set field_175791_c = Sets.newHashSet();
        private boolean field_175790_d;

        public StartMonument() {}

        public StartMonument(World worldIn, Random p_i45607_2_, int p_i45607_3_, int p_i45607_4_)
        {
            super(p_i45607_3_, p_i45607_4_);
            this.func_175789_b(worldIn, p_i45607_2_, p_i45607_3_, p_i45607_4_);
        }

        private void func_175789_b(World worldIn, Random p_175789_2_, int p_175789_3_, int p_175789_4_)
        {
            p_175789_2_.setSeed(worldIn.getSeed());
            long var5 = p_175789_2_.nextLong();
            long var7 = p_175789_2_.nextLong();
            long var9 = (long)p_175789_3_ * var5;
            long var11 = (long)p_175789_4_ * var7;
            p_175789_2_.setSeed(var9 ^ var11 ^ worldIn.getSeed());
            int var13 = p_175789_3_ * 16 + 8 - 29;
            int var14 = p_175789_4_ * 16 + 8 - 29;
            EnumFacing var15 = EnumFacing.Plane.HORIZONTAL.random(p_175789_2_);
            this.components.add(new StructureOceanMonumentPieces.MonumentBuilding(p_175789_2_, var13, var14, var15));
            this.updateBoundingBox();
            this.field_175790_d = true;
        }

        public void generateStructure(World worldIn, Random rand, StructureBoundingBox structurebb)
        {
            if (!this.field_175790_d)
            {
                this.components.clear();
                this.func_175789_b(worldIn, rand, this.getChunkPosX(), this.getChunkPosZ());
            }

            super.generateStructure(worldIn, rand, structurebb);
        }

        public boolean func_175788_a(ChunkCoordIntPair pair)
        {
            return this.field_175791_c.contains(pair) ? false : super.func_175788_a(pair);
        }

        public void func_175787_b(ChunkCoordIntPair pair)
        {
            super.func_175787_b(pair);
            this.field_175791_c.add(pair);
        }

        public void writeToNBT(NBTTagCompound tagCompound)
        {
            super.writeToNBT(tagCompound);
            NBTTagList var2 = new NBTTagList();
            Iterator var3 = this.field_175791_c.iterator();

            while (var3.hasNext())
            {
                ChunkCoordIntPair var4 = (ChunkCoordIntPair)var3.next();
                NBTTagCompound var5 = new NBTTagCompound();
                var5.setInteger("X", var4.chunkXPos);
                var5.setInteger("Z", var4.chunkZPos);
                var2.appendTag(var5);
            }

            tagCompound.setTag("Processed", var2);
        }

        public void readFromNBT(NBTTagCompound tagCompound)
        {
            super.readFromNBT(tagCompound);

            if (tagCompound.hasKey("Processed", 9))
            {
                NBTTagList var2 = tagCompound.getTagList("Processed", 10);

                for (int var3 = 0; var3 < var2.tagCount(); ++var3)
                {
                    NBTTagCompound var4 = var2.getCompoundTagAt(var3);
                    this.field_175791_c.add(new ChunkCoordIntPair(var4.getInteger("X"), var4.getInteger("Z")));
                }
            }
        }
    }
}
