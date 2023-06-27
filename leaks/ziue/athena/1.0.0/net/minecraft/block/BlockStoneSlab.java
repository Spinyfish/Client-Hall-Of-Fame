package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.creativetab.*;
import net.minecraft.init.*;
import net.minecraft.world.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.block.state.*;
import net.minecraft.block.material.*;
import net.minecraft.util.*;

public abstract class BlockStoneSlab extends BlockSlab
{
    public static final PropertyBool SEAMLESS;
    public static final PropertyEnum<EnumType> VARIANT;
    
    public BlockStoneSlab() {
        super(Material.rock);
        IBlockState iblockstate = this.blockState.getBaseState();
        if (this.isDouble()) {
            iblockstate = iblockstate.withProperty((IProperty<Comparable>)BlockStoneSlab.SEAMLESS, false);
        }
        else {
            iblockstate = iblockstate.withProperty(BlockStoneSlab.HALF, EnumBlockHalf.BOTTOM);
        }
        this.setDefaultState(iblockstate.withProperty(BlockStoneSlab.VARIANT, EnumType.STONE));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemFromBlock(Blocks.stone_slab);
    }
    
    @Override
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Item.getItemFromBlock(Blocks.stone_slab);
    }
    
    @Override
    public String getUnlocalizedName(final int meta) {
        return super.getUnlocalizedName() + "." + EnumType.byMetadata(meta).getUnlocalizedName();
    }
    
    @Override
    public IProperty<?> getVariantProperty() {
        return BlockStoneSlab.VARIANT;
    }
    
    @Override
    public Object getVariant(final ItemStack stack) {
        return EnumType.byMetadata(stack.getMetadata() & 0x7);
    }
    
    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        if (itemIn != Item.getItemFromBlock(Blocks.double_stone_slab)) {
            for (final EnumType blockstoneslab$enumtype : EnumType.values()) {
                if (blockstoneslab$enumtype != EnumType.WOOD) {
                    list.add(new ItemStack(itemIn, 1, blockstoneslab$enumtype.getMetadata()));
                }
            }
        }
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(BlockStoneSlab.VARIANT, EnumType.byMetadata(meta & 0x7));
        if (this.isDouble()) {
            iblockstate = iblockstate.withProperty((IProperty<Comparable>)BlockStoneSlab.SEAMLESS, (meta & 0x8) != 0x0);
        }
        else {
            iblockstate = iblockstate.withProperty(BlockStoneSlab.HALF, ((meta & 0x8) == 0x0) ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
        }
        return iblockstate;
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= state.getValue(BlockStoneSlab.VARIANT).getMetadata();
        if (this.isDouble()) {
            if (state.getValue((IProperty<Boolean>)BlockStoneSlab.SEAMLESS)) {
                i |= 0x8;
            }
        }
        else if (state.getValue(BlockStoneSlab.HALF) == EnumBlockHalf.TOP) {
            i |= 0x8;
        }
        return i;
    }
    
    @Override
    protected BlockState createBlockState() {
        return this.isDouble() ? new BlockState(this, new IProperty[] { BlockStoneSlab.SEAMLESS, BlockStoneSlab.VARIANT }) : new BlockState(this, new IProperty[] { BlockStoneSlab.HALF, BlockStoneSlab.VARIANT });
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockStoneSlab.VARIANT).getMetadata();
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state) {
        return state.getValue(BlockStoneSlab.VARIANT).func_181074_c();
    }
    
    static {
        SEAMLESS = PropertyBool.create("seamless");
        VARIANT = PropertyEnum.create("variant", EnumType.class);
    }
    
    public enum EnumType implements IStringSerializable
    {
        STONE(0, MapColor.stoneColor, "stone"), 
        SAND(1, MapColor.sandColor, "sandstone", "sand"), 
        WOOD(2, MapColor.woodColor, "wood_old", "wood"), 
        COBBLESTONE(3, MapColor.stoneColor, "cobblestone", "cobble"), 
        BRICK(4, MapColor.redColor, "brick"), 
        SMOOTHBRICK(5, MapColor.stoneColor, "stone_brick", "smoothStoneBrick"), 
        NETHERBRICK(6, MapColor.netherrackColor, "nether_brick", "netherBrick"), 
        QUARTZ(7, MapColor.quartzColor, "quartz");
        
        private static final EnumType[] META_LOOKUP;
        private final int meta;
        private final MapColor field_181075_k;
        private final String name;
        private final String unlocalizedName;
        
        private EnumType(final int p_i46381_3_, final MapColor p_i46381_4_, final String p_i46381_5_) {
            this(p_i46381_3_, p_i46381_4_, p_i46381_5_, p_i46381_5_);
        }
        
        private EnumType(final int p_i46382_3_, final MapColor p_i46382_4_, final String p_i46382_5_, final String p_i46382_6_) {
            this.meta = p_i46382_3_;
            this.field_181075_k = p_i46382_4_;
            this.name = p_i46382_5_;
            this.unlocalizedName = p_i46382_6_;
        }
        
        public int getMetadata() {
            return this.meta;
        }
        
        public MapColor func_181074_c() {
            return this.field_181075_k;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= EnumType.META_LOOKUP.length) {
                meta = 0;
            }
            return EnumType.META_LOOKUP[meta];
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }
        
        static {
            META_LOOKUP = new EnumType[values().length];
            for (final EnumType blockstoneslab$enumtype : values()) {
                EnumType.META_LOOKUP[blockstoneslab$enumtype.getMetadata()] = blockstoneslab$enumtype;
            }
        }
    }
}
