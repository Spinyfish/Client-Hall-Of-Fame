package net.minecraft.world.gen.structure;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.BiomeGenBase;

public class MapGenVillage extends MapGenStructure
{
    /** A list of all the biomes villages can spawn in. */
    public static final List villageSpawnBiomes = Arrays.asList(new BiomeGenBase[] {BiomeGenBase.plains, BiomeGenBase.desert, BiomeGenBase.savanna});

    /** World terrain type, 0 for normal, 1 for flat map */
    private int terrainType;
    private int field_82665_g;
    private int field_82666_h;

    public MapGenVillage()
    {
        this.field_82665_g = 32;
        this.field_82666_h = 8;
    }

    public MapGenVillage(Map p_i2093_1_)
    {
        this();
        Iterator var2 = p_i2093_1_.entrySet().iterator();

        while (var2.hasNext())
        {
            Entry var3 = (Entry)var2.next();

            if (((String)var3.getKey()).equals("size"))
            {
                this.terrainType = MathHelper.parseIntWithDefaultAndMax((String)var3.getValue(), this.terrainType, 0);
            }
            else if (((String)var3.getKey()).equals("distance"))
            {
                this.field_82665_g = MathHelper.parseIntWithDefaultAndMax((String)var3.getValue(), this.field_82665_g, this.field_82666_h + 1);
            }
        }
    }

    public String getStructureName()
    {
        return "Village";
    }

    protected boolean canSpawnStructureAtCoords(int chunkX, int chunkZ)
    {
        int var3 = chunkX;
        int var4 = chunkZ;

        if (chunkX < 0)
        {
            chunkX -= this.field_82665_g - 1;
        }

        if (chunkZ < 0)
        {
            chunkZ -= this.field_82665_g - 1;
        }

        int var5 = chunkX / this.field_82665_g;
        int var6 = chunkZ / this.field_82665_g;
        Random var7 = this.worldObj.setRandomSeed(var5, var6, 10387312);
        var5 *= this.field_82665_g;
        var6 *= this.field_82665_g;
        var5 += var7.nextInt(this.field_82665_g - this.field_82666_h);
        var6 += var7.nextInt(this.field_82665_g - this.field_82666_h);

        if (var3 == var5 && var4 == var6)
        {
            boolean var8 = this.worldObj.getWorldChunkManager().areBiomesViable(var3 * 16 + 8, var4 * 16 + 8, 0, villageSpawnBiomes);

            if (var8)
            {
                return true;
            }
        }

        return false;
    }

    protected StructureStart getStructureStart(int chunkX, int chunkZ)
    {
        return new MapGenVillage.Start(this.worldObj, this.rand, chunkX, chunkZ, this.terrainType);
    }

    public static class Start extends StructureStart
    {
        private boolean hasMoreThanTwoComponents;

        public Start() {}

        public Start(World worldIn, Random rand, int x, int z, int p_i2092_5_)
        {
            super(x, z);
            List var6 = StructureVillagePieces.getStructureVillageWeightedPieceList(rand, p_i2092_5_);
            StructureVillagePieces.Start var7 = new StructureVillagePieces.Start(worldIn.getWorldChunkManager(), 0, rand, (x << 4) + 2, (z << 4) + 2, var6, p_i2092_5_);
            this.components.add(var7);
            var7.buildComponent(var7, this.components, rand);
            List var8 = var7.field_74930_j;
            List var9 = var7.field_74932_i;
            int var10;

            while (!var8.isEmpty() || !var9.isEmpty())
            {
                StructureComponent var11;

                if (var8.isEmpty())
                {
                    var10 = rand.nextInt(var9.size());
                    var11 = (StructureComponent)var9.remove(var10);
                    var11.buildComponent(var7, this.components, rand);
                }
                else
                {
                    var10 = rand.nextInt(var8.size());
                    var11 = (StructureComponent)var8.remove(var10);
                    var11.buildComponent(var7, this.components, rand);
                }
            }

            this.updateBoundingBox();
            var10 = 0;
            Iterator var13 = this.components.iterator();

            while (var13.hasNext())
            {
                StructureComponent var12 = (StructureComponent)var13.next();

                if (!(var12 instanceof StructureVillagePieces.Road))
                {
                    ++var10;
                }
            }

            this.hasMoreThanTwoComponents = var10 > 2;
        }

        public boolean isSizeableStructure()
        {
            return this.hasMoreThanTwoComponents;
        }

        public void writeToNBT(NBTTagCompound tagCompound)
        {
            super.writeToNBT(tagCompound);
            tagCompound.setBoolean("Valid", this.hasMoreThanTwoComponents);
        }

        public void readFromNBT(NBTTagCompound tagCompound)
        {
            super.readFromNBT(tagCompound);
            this.hasMoreThanTwoComponents = tagCompound.getBoolean("Valid");
        }
    }
}
