package net.minecraft.world.gen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityMinecartChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMobSpawner;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.WeightedRandomChestContent;
import net.minecraft.world.World;

public class StructureMineshaftPieces
{
    private static final List CHEST_CONTENT_WEIGHT_LIST = Lists.newArrayList(new WeightedRandomChestContent[] {new WeightedRandomChestContent(Items.iron_ingot, 0, 1, 5, 10), new WeightedRandomChestContent(Items.gold_ingot, 0, 1, 3, 5), new WeightedRandomChestContent(Items.redstone, 0, 4, 9, 5), new WeightedRandomChestContent(Items.dye, EnumDyeColor.BLUE.getDyeDamage(), 4, 9, 5), new WeightedRandomChestContent(Items.diamond, 0, 1, 2, 3), new WeightedRandomChestContent(Items.coal, 0, 3, 8, 10), new WeightedRandomChestContent(Items.bread, 0, 1, 3, 15), new WeightedRandomChestContent(Items.iron_pickaxe, 0, 1, 1, 1), new WeightedRandomChestContent(Item.getItemFromBlock(Blocks.rail), 0, 4, 8, 1), new WeightedRandomChestContent(Items.melon_seeds, 0, 2, 4, 10), new WeightedRandomChestContent(Items.pumpkin_seeds, 0, 2, 4, 10), new WeightedRandomChestContent(Items.saddle, 0, 1, 1, 3), new WeightedRandomChestContent(Items.iron_horse_armor, 0, 1, 1, 1)});

    public static void registerStructurePieces()
    {
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Corridor.class, "MSCorridor");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Cross.class, "MSCrossing");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Room.class, "MSRoom");
        MapGenStructureIO.registerStructureComponent(StructureMineshaftPieces.Stairs.class, "MSStairs");
    }

    private static StructureComponent func_175892_a(List listIn, Random rand, int x, int y, int z, EnumFacing facing, int type)
    {
        int var7 = rand.nextInt(100);
        StructureBoundingBox var8;

        if (var7 >= 80)
        {
            var8 = StructureMineshaftPieces.Cross.func_175813_a(listIn, rand, x, y, z, facing);

            if (var8 != null)
            {
                return new StructureMineshaftPieces.Cross(type, rand, var8, facing);
            }
        }
        else if (var7 >= 70)
        {
            var8 = StructureMineshaftPieces.Stairs.func_175812_a(listIn, rand, x, y, z, facing);

            if (var8 != null)
            {
                return new StructureMineshaftPieces.Stairs(type, rand, var8, facing);
            }
        }
        else
        {
            var8 = StructureMineshaftPieces.Corridor.func_175814_a(listIn, rand, x, y, z, facing);

            if (var8 != null)
            {
                return new StructureMineshaftPieces.Corridor(type, rand, var8, facing);
            }
        }

        return null;
    }

    private static StructureComponent func_175890_b(StructureComponent componentIn, List listIn, Random rand, int x, int y, int z, EnumFacing facing, int type)
    {
        if (type > 8)
        {
            return null;
        }
        else if (Math.abs(x - componentIn.getBoundingBox().minX) <= 80 && Math.abs(z - componentIn.getBoundingBox().minZ) <= 80)
        {
            StructureComponent var8 = func_175892_a(listIn, rand, x, y, z, facing, type + 1);

            if (var8 != null)
            {
                listIn.add(var8);
                var8.buildComponent(componentIn, listIn, rand);
            }

            return var8;
        }
        else
        {
            return null;
        }
    }

    public static class Corridor extends StructureComponent
    {
        private boolean hasRails;
        private boolean hasSpiders;
        private boolean spawnerPlaced;
        private int sectionCount;

        public Corridor() {}

        protected void writeStructureToNBT(NBTTagCompound tagCompound)
        {
            tagCompound.setBoolean("hr", this.hasRails);
            tagCompound.setBoolean("sc", this.hasSpiders);
            tagCompound.setBoolean("hps", this.spawnerPlaced);
            tagCompound.setInteger("Num", this.sectionCount);
        }

        protected void readStructureFromNBT(NBTTagCompound tagCompound)
        {
            this.hasRails = tagCompound.getBoolean("hr");
            this.hasSpiders = tagCompound.getBoolean("sc");
            this.spawnerPlaced = tagCompound.getBoolean("hps");
            this.sectionCount = tagCompound.getInteger("Num");
        }

        public Corridor(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing)
        {
            super(type);
            this.coordBaseMode = facing;
            this.boundingBox = structurebb;
            this.hasRails = rand.nextInt(3) == 0;
            this.hasSpiders = !this.hasRails && rand.nextInt(23) == 0;

            if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH)
            {
                this.sectionCount = structurebb.getXSize() / 5;
            }
            else
            {
                this.sectionCount = structurebb.getZSize() / 5;
            }
        }

        public static StructureBoundingBox func_175814_a(List p_175814_0_, Random rand, int x, int y, int z, EnumFacing facing)
        {
            StructureBoundingBox var6 = new StructureBoundingBox(x, y, z, x, y + 2, z);
            int var7;

            for (var7 = rand.nextInt(3) + 2; var7 > 0; --var7)
            {
                int var8 = var7 * 5;

                switch (StructureMineshaftPieces.SwitchEnumFacing.FACING_LOOKUP[facing.ordinal()])
                {
                    case 1:
                        var6.maxX = x + 2;
                        var6.minZ = z - (var8 - 1);
                        break;

                    case 2:
                        var6.maxX = x + 2;
                        var6.maxZ = z + (var8 - 1);
                        break;

                    case 3:
                        var6.minX = x - (var8 - 1);
                        var6.maxZ = z + 2;
                        break;

                    case 4:
                        var6.maxX = x + (var8 - 1);
                        var6.maxZ = z + 2;
                }

                if (StructureComponent.findIntersecting(p_175814_0_, var6) == null)
                {
                    break;
                }
            }

            return var7 > 0 ? var6 : null;
        }

        public void buildComponent(StructureComponent componentIn, List listIn, Random rand)
        {
            int var4 = this.getComponentType();
            int var5 = rand.nextInt(4);

            if (this.coordBaseMode != null)
            {
                switch (StructureMineshaftPieces.SwitchEnumFacing.FACING_LOOKUP[this.coordBaseMode.ordinal()])
                {
                    case 1:
                        if (var5 <= 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, this.coordBaseMode, var4);
                        }
                        else if (var5 == 2)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.WEST, var4);
                        }
                        else
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, EnumFacing.EAST, var4);
                        }

                        break;

                    case 2:
                        if (var5 <= 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, this.coordBaseMode, var4);
                        }
                        else if (var5 == 2)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.WEST, var4);
                        }
                        else
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ - 3, EnumFacing.EAST, var4);
                        }

                        break;

                    case 3:
                        if (var5 <= 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, this.coordBaseMode, var4);
                        }
                        else if (var5 == 2)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, var4);
                        }
                        else
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
                        }

                        break;

                    case 4:
                        if (var5 <= 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ, this.coordBaseMode, var4);
                        }
                        else if (var5 == 2)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.minZ - 1, EnumFacing.NORTH, var4);
                        }
                        else
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX - 3, this.boundingBox.minY - 1 + rand.nextInt(3), this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
                        }
                }
            }

            if (var4 < 8)
            {
                int var6;
                int var7;

                if (this.coordBaseMode != EnumFacing.NORTH && this.coordBaseMode != EnumFacing.SOUTH)
                {
                    for (var6 = this.boundingBox.minX + 3; var6 + 3 <= this.boundingBox.maxX; var6 += 5)
                    {
                        var7 = rand.nextInt(5);

                        if (var7 == 0)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, var6, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, var4 + 1);
                        }
                        else if (var7 == 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, var6, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var4 + 1);
                        }
                    }
                }
                else
                {
                    for (var6 = this.boundingBox.minZ + 3; var6 + 3 <= this.boundingBox.maxZ; var6 += 5)
                    {
                        var7 = rand.nextInt(5);

                        if (var7 == 0)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, var6, EnumFacing.WEST, var4 + 1);
                        }
                        else if (var7 == 1)
                        {
                            StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, var6, EnumFacing.EAST, var4 + 1);
                        }
                    }
                }
            }
        }

        protected boolean generateChestContents(World worldIn, StructureBoundingBox boundingBoxIn, Random rand, int x, int y, int z, List listIn, int max)
        {
            BlockPos var9 = new BlockPos(this.getXWithOffset(x, z), this.getYWithOffset(y), this.getZWithOffset(x, z));

            if (boundingBoxIn.isVecInside(var9) && worldIn.getBlockState(var9).getBlock().getMaterial() == Material.air)
            {
                int var10 = rand.nextBoolean() ? 1 : 0;
                worldIn.setBlockState(var9, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, var10)), 2);
                EntityMinecartChest var11 = new EntityMinecartChest(worldIn, (double)((float)var9.getX() + 0.5F), (double)((float)var9.getY() + 0.5F), (double)((float)var9.getZ() + 0.5F));
                WeightedRandomChestContent.generateChestContents(rand, listIn, var11, max);
                worldIn.spawnEntityInWorld(var11);
                return true;
            }
            else
            {
                return false;
            }
        }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
            {
                return false;
            }
            else
            {
                boolean var4 = false;
                boolean var5 = true;
                boolean var6 = false;
                boolean var7 = true;
                int var8 = this.sectionCount * 5 - 1;
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 0, 2, 1, var8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.func_175805_a(worldIn, structureBoundingBoxIn, randomIn, 0.8F, 0, 2, 0, 2, 2, var8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);

                if (this.hasSpiders)
                {
                    this.func_175805_a(worldIn, structureBoundingBoxIn, randomIn, 0.6F, 0, 0, 0, 2, 1, var8, Blocks.web.getDefaultState(), Blocks.air.getDefaultState(), false);
                }

                int var9;
                int var10;

                for (var9 = 0; var9 < this.sectionCount; ++var9)
                {
                    var10 = 2 + var9 * 5;
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, var10, 0, 1, var10, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 0, var10, 2, 1, var10, Blocks.oak_fence.getDefaultState(), Blocks.air.getDefaultState(), false);

                    if (randomIn.nextInt(4) == 0)
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, var10, 0, 2, var10, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 2, 2, var10, 2, 2, var10, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                    }
                    else
                    {
                        this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 2, var10, 2, 2, var10, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                    }

                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, var10 - 1, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, var10 - 1, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 0, 2, var10 + 1, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.1F, 2, 2, var10 + 1, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, var10 - 2, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, var10 - 2, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 0, 2, var10 + 2, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 2, 2, var10 + 2, Blocks.web.getDefaultState());
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 1, 2, var10 - 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));
                    this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.05F, 1, 2, var10 + 1, Blocks.torch.getStateFromMeta(EnumFacing.UP.getIndex()));

                    if (randomIn.nextInt(100) == 0)
                    {
                        this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 2, 0, var10 - 1, WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, new WeightedRandomChestContent[] {Items.enchanted_book.getRandom(randomIn)}), 3 + randomIn.nextInt(4));
                    }

                    if (randomIn.nextInt(100) == 0)
                    {
                        this.generateChestContents(worldIn, structureBoundingBoxIn, randomIn, 0, 0, var10 + 1, WeightedRandomChestContent.func_177629_a(StructureMineshaftPieces.CHEST_CONTENT_WEIGHT_LIST, new WeightedRandomChestContent[] {Items.enchanted_book.getRandom(randomIn)}), 3 + randomIn.nextInt(4));
                    }

                    if (this.hasSpiders && !this.spawnerPlaced)
                    {
                        int var11 = this.getYWithOffset(0);
                        int var12 = var10 - 1 + randomIn.nextInt(3);
                        int var13 = this.getXWithOffset(1, var12);
                        var12 = this.getZWithOffset(1, var12);
                        BlockPos var14 = new BlockPos(var13, var11, var12);

                        if (structureBoundingBoxIn.isVecInside(var14))
                        {
                            this.spawnerPlaced = true;
                            worldIn.setBlockState(var14, Blocks.mob_spawner.getDefaultState(), 2);
                            TileEntity var15 = worldIn.getTileEntity(var14);

                            if (var15 instanceof TileEntityMobSpawner)
                            {
                                ((TileEntityMobSpawner)var15).getSpawnerBaseLogic().setEntityName("CaveSpider");
                            }
                        }
                    }
                }

                for (var9 = 0; var9 <= 2; ++var9)
                {
                    for (var10 = 0; var10 <= var8; ++var10)
                    {
                        byte var17 = -1;
                        IBlockState var18 = this.getBlockStateFromPos(worldIn, var9, var17, var10, structureBoundingBoxIn);

                        if (var18.getBlock().getMaterial() == Material.air)
                        {
                            byte var19 = -1;
                            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), var9, var19, var10, structureBoundingBoxIn);
                        }
                    }
                }

                if (this.hasRails)
                {
                    for (var9 = 0; var9 <= var8; ++var9)
                    {
                        IBlockState var16 = this.getBlockStateFromPos(worldIn, 1, -1, var9, structureBoundingBoxIn);

                        if (var16.getBlock().getMaterial() != Material.air && var16.getBlock().isFullBlock())
                        {
                            this.randomlyPlaceBlock(worldIn, structureBoundingBoxIn, randomIn, 0.7F, 1, 0, var9, Blocks.rail.getStateFromMeta(this.getMetadataWithOffset(Blocks.rail, 0)));
                        }
                    }
                }

                return true;
            }
        }
    }

    public static class Cross extends StructureComponent
    {
        private EnumFacing corridorDirection;
        private boolean isMultipleFloors;

        public Cross() {}

        protected void writeStructureToNBT(NBTTagCompound tagCompound)
        {
            tagCompound.setBoolean("tf", this.isMultipleFloors);
            tagCompound.setInteger("D", this.corridorDirection.getHorizontalIndex());
        }

        protected void readStructureFromNBT(NBTTagCompound tagCompound)
        {
            this.isMultipleFloors = tagCompound.getBoolean("tf");
            this.corridorDirection = EnumFacing.getHorizontal(tagCompound.getInteger("D"));
        }

        public Cross(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing)
        {
            super(type);
            this.corridorDirection = facing;
            this.boundingBox = structurebb;
            this.isMultipleFloors = structurebb.getYSize() > 3;
        }

        public static StructureBoundingBox func_175813_a(List listIn, Random rand, int x, int y, int z, EnumFacing facing)
        {
            StructureBoundingBox var6 = new StructureBoundingBox(x, y, z, x, y + 2, z);

            if (rand.nextInt(4) == 0)
            {
                var6.maxY += 4;
            }

            switch (StructureMineshaftPieces.SwitchEnumFacing.FACING_LOOKUP[facing.ordinal()])
            {
                case 1:
                    var6.minX = x - 1;
                    var6.maxX = x + 3;
                    var6.minZ = z - 4;
                    break;

                case 2:
                    var6.minX = x - 1;
                    var6.maxX = x + 3;
                    var6.maxZ = z + 4;
                    break;

                case 3:
                    var6.minX = x - 4;
                    var6.minZ = z - 1;
                    var6.maxZ = z + 3;
                    break;

                case 4:
                    var6.maxX = x + 4;
                    var6.minZ = z - 1;
                    var6.maxZ = z + 3;
            }

            return StructureComponent.findIntersecting(listIn, var6) != null ? null : var6;
        }

        public void buildComponent(StructureComponent componentIn, List listIn, Random rand)
        {
            int var4 = this.getComponentType();

            switch (StructureMineshaftPieces.SwitchEnumFacing.FACING_LOOKUP[this.corridorDirection.ordinal()])
            {
                case 1:
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, var4);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, var4);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, var4);
                    break;

                case 2:
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, var4);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, var4);
                    break;

                case 3:
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, var4);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.WEST, var4);
                    break;

                case 4:
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, var4);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, EnumFacing.EAST, var4);
            }

            if (this.isMultipleFloors)
            {
                if (rand.nextBoolean())
                {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, var4);
                }

                if (rand.nextBoolean())
                {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.WEST, var4);
                }

                if (rand.nextBoolean())
                {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.minZ + 1, EnumFacing.EAST, var4);
                }

                if (rand.nextBoolean())
                {
                    StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + 1, this.boundingBox.minY + 3 + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
                }
            }
        }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
            {
                return false;
            }
            else
            {
                if (this.isMultipleFloors)
                {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.minY + 3 - 1, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.maxY - 2, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.maxY - 2, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY + 3, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.minY + 3, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                }
                else
                {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                }

                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.minX + 1, this.boundingBox.maxY, this.boundingBox.minZ + 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX + 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.minX + 1, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.minZ + 1, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.minZ + 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.maxX - 1, this.boundingBox.minY, this.boundingBox.maxZ - 1, this.boundingBox.maxX - 1, this.boundingBox.maxY, this.boundingBox.maxZ - 1, Blocks.planks.getDefaultState(), Blocks.air.getDefaultState(), false);

                for (int var4 = this.boundingBox.minX; var4 <= this.boundingBox.maxX; ++var4)
                {
                    for (int var5 = this.boundingBox.minZ; var5 <= this.boundingBox.maxZ; ++var5)
                    {
                        if (this.getBlockStateFromPos(worldIn, var4, this.boundingBox.minY - 1, var5, structureBoundingBoxIn).getBlock().getMaterial() == Material.air)
                        {
                            this.setBlockState(worldIn, Blocks.planks.getDefaultState(), var4, this.boundingBox.minY - 1, var5, structureBoundingBoxIn);
                        }
                    }
                }

                return true;
            }
        }
    }

    public static class Room extends StructureComponent
    {
        private List roomsLinkedToTheRoom = Lists.newLinkedList();

        public Room() {}

        public Room(int type, Random rand, int x, int z)
        {
            super(type);
            this.boundingBox = new StructureBoundingBox(x, 50, z, x + 7 + rand.nextInt(6), 54 + rand.nextInt(6), z + 7 + rand.nextInt(6));
        }

        public void buildComponent(StructureComponent componentIn, List listIn, Random rand)
        {
            int var4 = this.getComponentType();
            int var6 = this.boundingBox.getYSize() - 3 - 1;

            if (var6 <= 0)
            {
                var6 = 1;
            }

            int var5;
            StructureComponent var7;
            StructureBoundingBox var8;

            for (var5 = 0; var5 < this.boundingBox.getXSize(); var5 += 4)
            {
                var5 += rand.nextInt(this.boundingBox.getXSize());

                if (var5 + 3 > this.boundingBox.getXSize())
                {
                    break;
                }

                var7 = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + var5, this.boundingBox.minY + rand.nextInt(var6) + 1, this.boundingBox.minZ - 1, EnumFacing.NORTH, var4);

                if (var7 != null)
                {
                    var8 = var7.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(var8.minX, var8.minY, this.boundingBox.minZ, var8.maxX, var8.maxY, this.boundingBox.minZ + 1));
                }
            }

            for (var5 = 0; var5 < this.boundingBox.getXSize(); var5 += 4)
            {
                var5 += rand.nextInt(this.boundingBox.getXSize());

                if (var5 + 3 > this.boundingBox.getXSize())
                {
                    break;
                }

                var7 = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX + var5, this.boundingBox.minY + rand.nextInt(var6) + 1, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);

                if (var7 != null)
                {
                    var8 = var7.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(var8.minX, var8.minY, this.boundingBox.maxZ - 1, var8.maxX, var8.maxY, this.boundingBox.maxZ));
                }
            }

            for (var5 = 0; var5 < this.boundingBox.getZSize(); var5 += 4)
            {
                var5 += rand.nextInt(this.boundingBox.getZSize());

                if (var5 + 3 > this.boundingBox.getZSize())
                {
                    break;
                }

                var7 = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY + rand.nextInt(var6) + 1, this.boundingBox.minZ + var5, EnumFacing.WEST, var4);

                if (var7 != null)
                {
                    var8 = var7.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(this.boundingBox.minX, var8.minY, var8.minZ, this.boundingBox.minX + 1, var8.maxY, var8.maxZ));
                }
            }

            for (var5 = 0; var5 < this.boundingBox.getZSize(); var5 += 4)
            {
                var5 += rand.nextInt(this.boundingBox.getZSize());

                if (var5 + 3 > this.boundingBox.getZSize())
                {
                    break;
                }

                var7 = StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY + rand.nextInt(var6) + 1, this.boundingBox.minZ + var5, EnumFacing.EAST, var4);

                if (var7 != null)
                {
                    var8 = var7.getBoundingBox();
                    this.roomsLinkedToTheRoom.add(new StructureBoundingBox(this.boundingBox.maxX - 1, var8.minY, var8.minZ, this.boundingBox.maxX, var8.maxY, var8.maxZ));
                }
            }
        }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
            {
                return false;
            }
            else
            {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.minY, this.boundingBox.maxZ, Blocks.dirt.getDefaultState(), Blocks.air.getDefaultState(), true);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 1, this.boundingBox.minZ, this.boundingBox.maxX, Math.min(this.boundingBox.minY + 3, this.boundingBox.maxY), this.boundingBox.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                Iterator var4 = this.roomsLinkedToTheRoom.iterator();

                while (var4.hasNext())
                {
                    StructureBoundingBox var5 = (StructureBoundingBox)var4.next();
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, var5.minX, var5.maxY - 2, var5.minZ, var5.maxX, var5.maxY, var5.maxZ, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                }

                this.randomlyRareFillWithBlocks(worldIn, structureBoundingBoxIn, this.boundingBox.minX, this.boundingBox.minY + 4, this.boundingBox.minZ, this.boundingBox.maxX, this.boundingBox.maxY, this.boundingBox.maxZ, Blocks.air.getDefaultState(), false);
                return true;
            }
        }

        protected void writeStructureToNBT(NBTTagCompound tagCompound)
        {
            NBTTagList var2 = new NBTTagList();
            Iterator var3 = this.roomsLinkedToTheRoom.iterator();

            while (var3.hasNext())
            {
                StructureBoundingBox var4 = (StructureBoundingBox)var3.next();
                var2.appendTag(var4.toNBTTagIntArray());
            }

            tagCompound.setTag("Entrances", var2);
        }

        protected void readStructureFromNBT(NBTTagCompound tagCompound)
        {
            NBTTagList var2 = tagCompound.getTagList("Entrances", 11);

            for (int var3 = 0; var3 < var2.tagCount(); ++var3)
            {
                this.roomsLinkedToTheRoom.add(new StructureBoundingBox(var2.getIntArrayAt(var3)));
            }
        }
    }

    public static class Stairs extends StructureComponent
    {

        public Stairs() {}

        public Stairs(int type, Random rand, StructureBoundingBox structurebb, EnumFacing facing)
        {
            super(type);
            this.coordBaseMode = facing;
            this.boundingBox = structurebb;
        }

        protected void writeStructureToNBT(NBTTagCompound tagCompound) {}

        protected void readStructureFromNBT(NBTTagCompound tagCompound) {}

        public static StructureBoundingBox func_175812_a(List listIn, Random rand, int x, int y, int z, EnumFacing facing)
        {
            StructureBoundingBox var6 = new StructureBoundingBox(x, y - 5, z, x, y + 2, z);

            switch (StructureMineshaftPieces.SwitchEnumFacing.FACING_LOOKUP[facing.ordinal()])
            {
                case 1:
                    var6.maxX = x + 2;
                    var6.minZ = z - 8;
                    break;

                case 2:
                    var6.maxX = x + 2;
                    var6.maxZ = z + 8;
                    break;

                case 3:
                    var6.minX = x - 8;
                    var6.maxZ = z + 2;
                    break;

                case 4:
                    var6.maxX = x + 8;
                    var6.maxZ = z + 2;
            }

            return StructureComponent.findIntersecting(listIn, var6) != null ? null : var6;
        }

        public void buildComponent(StructureComponent componentIn, List listIn, Random rand)
        {
            int var4 = this.getComponentType();

            if (this.coordBaseMode != null)
            {
                switch (StructureMineshaftPieces.SwitchEnumFacing.FACING_LOOKUP[this.coordBaseMode.ordinal()])
                {
                    case 1:
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.minZ - 1, EnumFacing.NORTH, var4);
                        break;

                    case 2:
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX, this.boundingBox.minY, this.boundingBox.maxZ + 1, EnumFacing.SOUTH, var4);
                        break;

                    case 3:
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.minX - 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.WEST, var4);
                        break;

                    case 4:
                        StructureMineshaftPieces.func_175890_b(componentIn, listIn, rand, this.boundingBox.maxX + 1, this.boundingBox.minY, this.boundingBox.minZ, EnumFacing.EAST, var4);
                }
            }
        }

        public boolean addComponentParts(World worldIn, Random randomIn, StructureBoundingBox structureBoundingBoxIn)
        {
            if (this.isLiquidInStructureBoundingBox(worldIn, structureBoundingBoxIn))
            {
                return false;
            }
            else
            {
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5, 0, 2, 7, 1, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 0, 7, 2, 2, 8, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);

                for (int var4 = 0; var4 < 5; ++var4)
                {
                    this.fillWithBlocks(worldIn, structureBoundingBoxIn, 0, 5 - var4 - (var4 < 4 ? 1 : 0), 2 + var4, 2, 7 - var4, 2 + var4, Blocks.air.getDefaultState(), Blocks.air.getDefaultState(), false);
                }

                return true;
            }
        }
    }

    static final class SwitchEnumFacing
    {
        static final int[] FACING_LOOKUP = new int[EnumFacing.values().length];

        static
        {
            try
            {
                FACING_LOOKUP[EnumFacing.NORTH.ordinal()] = 1;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.SOUTH.ordinal()] = 2;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.WEST.ordinal()] = 3;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try
            {
                FACING_LOOKUP[EnumFacing.EAST.ordinal()] = 4;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
