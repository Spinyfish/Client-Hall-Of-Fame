/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

public class BlockHugeMushroom
extends Block {
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.create("variant", EnumType.class);
    private final Block smallBlock;

    public BlockHugeMushroom(Material p_i46392_1_, MapColor p_i46392_2_, Block p_i46392_3_) {
        super(p_i46392_1_, p_i46392_2_);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.ALL_OUTSIDE));
        this.smallBlock = p_i46392_3_;
    }

    @Override
    public int quantityDropped(Random random) {
        return Math.max(0, random.nextInt(10) - 7);
    }

    @Override
    public MapColor getMapColor(IBlockState state) {
        switch (state.getValue(VARIANT)) {
            case ALL_STEM: {
                return MapColor.clothColor;
            }
            case ALL_INSIDE: {
                return MapColor.sandColor;
            }
            case STEM: {
                return MapColor.sandColor;
            }
        }
        return super.getMapColor(state);
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this.smallBlock);
    }

    @Override
    public Item getItem(World worldIn, BlockPos pos) {
        return Item.getItemFromBlock(this.smallBlock);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState();
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, VARIANT);
    }

    public static enum EnumType implements IStringSerializable
    {
        NORTH_WEST(1, "north_west"),
        NORTH(2, "north"),
        NORTH_EAST(3, "north_east"),
        WEST(4, "west"),
        CENTER(5, "center"),
        EAST(6, "east"),
        SOUTH_WEST(7, "south_west"),
        SOUTH(8, "south"),
        SOUTH_EAST(9, "south_east"),
        STEM(10, "stem"),
        ALL_INSIDE(0, "all_inside"),
        ALL_OUTSIDE(14, "all_outside"),
        ALL_STEM(15, "all_stem");

        private static final EnumType[] META_LOOKUP;
        private final int meta;
        private final String name;

        private EnumType(int meta, String name) {
            this.meta = meta;
            this.name = name;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String toString() {
            return this.name;
        }

        public static EnumType byMetadata(int meta) {
            EnumType blockhugemushroom$enumtype;
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }
            return (blockhugemushroom$enumtype = META_LOOKUP[meta]) == null ? META_LOOKUP[0] : blockhugemushroom$enumtype;
        }

        @Override
        public String getName() {
            return this.name;
        }

        static {
            META_LOOKUP = new EnumType[16];
            EnumType[] enumTypeArray = EnumType.values();
            int n = enumTypeArray.length;
            for (int i = 0; i < n; ++i) {
                EnumType blockhugemushroom$enumtype;
                EnumType.META_LOOKUP[blockhugemushroom$enumtype.getMetadata()] = blockhugemushroom$enumtype = enumTypeArray[i];
            }
        }
    }
}

