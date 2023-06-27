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

public abstract class BlockStoneSlabNew extends BlockSlab
{
    public static final PropertyBool SEAMLESS;
    public static final PropertyEnum<EnumType> VARIANT;
    
    public BlockStoneSlabNew() {
        super(Material.rock);
        IBlockState iblockstate = this.blockState.getBaseState();
        if (this.isDouble()) {
            iblockstate = iblockstate.withProperty((IProperty<Comparable>)BlockStoneSlabNew.SEAMLESS, false);
        }
        else {
            iblockstate = iblockstate.withProperty(BlockStoneSlabNew.HALF, EnumBlockHalf.BOTTOM);
        }
        this.setDefaultState(iblockstate.withProperty(BlockStoneSlabNew.VARIANT, EnumType.RED_SANDSTONE));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".red_sandstone.name");
    }
    
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemFromBlock(Blocks.stone_slab2);
    }
    
    @Override
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Item.getItemFromBlock(Blocks.stone_slab2);
    }
    
    @Override
    public String getUnlocalizedName(final int meta) {
        return super.getUnlocalizedName() + "." + EnumType.byMetadata(meta).getUnlocalizedName();
    }
    
    @Override
    public IProperty<?> getVariantProperty() {
        return BlockStoneSlabNew.VARIANT;
    }
    
    @Override
    public Object getVariant(final ItemStack stack) {
        return EnumType.byMetadata(stack.getMetadata() & 0x7);
    }
    
    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        if (itemIn != Item.getItemFromBlock(Blocks.double_stone_slab2)) {
            for (final EnumType blockstoneslabnew$enumtype : EnumType.values()) {
                list.add(new ItemStack(itemIn, 1, blockstoneslabnew$enumtype.getMetadata()));
            }
        }
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(BlockStoneSlabNew.VARIANT, EnumType.byMetadata(meta & 0x7));
        if (this.isDouble()) {
            iblockstate = iblockstate.withProperty((IProperty<Comparable>)BlockStoneSlabNew.SEAMLESS, (meta & 0x8) != 0x0);
        }
        else {
            iblockstate = iblockstate.withProperty(BlockStoneSlabNew.HALF, ((meta & 0x8) == 0x0) ? EnumBlockHalf.BOTTOM : EnumBlockHalf.TOP);
        }
        return iblockstate;
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i |= state.getValue(BlockStoneSlabNew.VARIANT).getMetadata();
        if (this.isDouble()) {
            if (state.getValue((IProperty<Boolean>)BlockStoneSlabNew.SEAMLESS)) {
                i |= 0x8;
            }
        }
        else if (state.getValue(BlockStoneSlabNew.HALF) == EnumBlockHalf.TOP) {
            i |= 0x8;
        }
        return i;
    }
    
    @Override
    protected BlockState createBlockState() {
        return this.isDouble() ? new BlockState(this, new IProperty[] { BlockStoneSlabNew.SEAMLESS, BlockStoneSlabNew.VARIANT }) : new BlockState(this, new IProperty[] { BlockStoneSlabNew.HALF, BlockStoneSlabNew.VARIANT });
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state) {
        return state.getValue(BlockStoneSlabNew.VARIANT).func_181068_c();
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockStoneSlabNew.VARIANT).getMetadata();
    }
    
    static {
        SEAMLESS = PropertyBool.create("seamless");
        VARIANT = PropertyEnum.create("variant", EnumType.class);
    }
    
    public enum EnumType implements IStringSerializable
    {
        RED_SANDSTONE(0, "red_sandstone", BlockSand.EnumType.RED_SAND.getMapColor());
        
        private static final EnumType[] META_LOOKUP;
        private final int meta;
        private final String name;
        private final MapColor field_181069_e;
        
        private EnumType(final int p_i46391_3_, final String p_i46391_4_, final MapColor p_i46391_5_) {
            this.meta = p_i46391_3_;
            this.name = p_i46391_4_;
            this.field_181069_e = p_i46391_5_;
        }
        
        public int getMetadata() {
            return this.meta;
        }
        
        public MapColor func_181068_c() {
            return this.field_181069_e;
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
            return this.name;
        }
        
        static {
            META_LOOKUP = new EnumType[values().length];
            for (final EnumType blockstoneslabnew$enumtype : values()) {
                EnumType.META_LOOKUP[blockstoneslabnew$enumtype.getMetadata()] = blockstoneslabnew$enumtype;
            }
        }
    }
}
