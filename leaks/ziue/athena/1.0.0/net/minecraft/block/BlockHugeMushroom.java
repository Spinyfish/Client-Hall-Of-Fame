package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.block.state.*;
import net.minecraft.util.*;

public class BlockHugeMushroom extends Block
{
    public static final PropertyEnum<EnumType> VARIANT;
    private final Block smallBlock;
    
    public BlockHugeMushroom(final Material p_i46392_1_, final MapColor p_i46392_2_, final Block p_i46392_3_) {
        super(p_i46392_1_, p_i46392_2_);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockHugeMushroom.VARIANT, EnumType.ALL_OUTSIDE));
        this.smallBlock = p_i46392_3_;
    }
    
    @Override
    public int quantityDropped(final Random random) {
        return Math.max(0, random.nextInt(10) - 7);
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state) {
        switch (state.getValue(BlockHugeMushroom.VARIANT)) {
            case ALL_STEM: {
                return MapColor.clothColor;
            }
            case ALL_INSIDE: {
                return MapColor.sandColor;
            }
            case STEM: {
                return MapColor.sandColor;
            }
            default: {
                return super.getMapColor(state);
            }
        }
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemFromBlock(this.smallBlock);
    }
    
    @Override
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Item.getItemFromBlock(this.smallBlock);
    }
    
    @Override
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState();
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockHugeMushroom.VARIANT, EnumType.byMetadata(meta));
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockHugeMushroom.VARIANT).getMetadata();
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockHugeMushroom.VARIANT });
    }
    
    static {
        VARIANT = PropertyEnum.create("variant", EnumType.class);
    }
    
    public enum EnumType implements IStringSerializable
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
        
        private EnumType(final int meta, final String name) {
            this.meta = meta;
            this.name = name;
        }
        
        public int getMetadata() {
            return this.meta;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= EnumType.META_LOOKUP.length) {
                meta = 0;
            }
            final EnumType blockhugemushroom$enumtype = EnumType.META_LOOKUP[meta];
            return (blockhugemushroom$enumtype == null) ? EnumType.META_LOOKUP[0] : blockhugemushroom$enumtype;
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        static {
            META_LOOKUP = new EnumType[16];
            for (final EnumType blockhugemushroom$enumtype : values()) {
                EnumType.META_LOOKUP[blockhugemushroom$enumtype.getMetadata()] = blockhugemushroom$enumtype;
            }
        }
    }
}
