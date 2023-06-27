package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.creativetab.*;
import net.minecraft.block.material.*;
import net.minecraft.init.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.util.*;

public class BlockDirt extends Block
{
    public static final PropertyEnum<DirtType> VARIANT;
    public static final PropertyBool SNOWY;
    
    protected BlockDirt() {
        super(Material.ground);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockDirt.VARIANT, DirtType.DIRT).withProperty((IProperty<Comparable>)BlockDirt.SNOWY, false));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state) {
        return state.getValue(BlockDirt.VARIANT).func_181066_d();
    }
    
    @Override
    public IBlockState getActualState(IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        if (state.getValue(BlockDirt.VARIANT) == DirtType.PODZOL) {
            final Block block = worldIn.getBlockState(pos.up()).getBlock();
            state = state.withProperty((IProperty<Comparable>)BlockDirt.SNOWY, block == Blocks.snow || block == Blocks.snow_layer);
        }
        return state;
    }
    
    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        list.add(new ItemStack(this, 1, DirtType.DIRT.getMetadata()));
        list.add(new ItemStack(this, 1, DirtType.COARSE_DIRT.getMetadata()));
        list.add(new ItemStack(this, 1, DirtType.PODZOL.getMetadata()));
    }
    
    @Override
    public int getDamageValue(final World worldIn, final BlockPos pos) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        return (iblockstate.getBlock() != this) ? 0 : iblockstate.getValue(BlockDirt.VARIANT).getMetadata();
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockDirt.VARIANT, DirtType.byMetadata(meta));
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockDirt.VARIANT).getMetadata();
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockDirt.VARIANT, BlockDirt.SNOWY });
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        DirtType blockdirt$dirttype = state.getValue(BlockDirt.VARIANT);
        if (blockdirt$dirttype == DirtType.PODZOL) {
            blockdirt$dirttype = DirtType.DIRT;
        }
        return blockdirt$dirttype.getMetadata();
    }
    
    static {
        VARIANT = PropertyEnum.create("variant", DirtType.class);
        SNOWY = PropertyBool.create("snowy");
    }
    
    public enum DirtType implements IStringSerializable
    {
        DIRT(0, "dirt", "default", MapColor.dirtColor), 
        COARSE_DIRT(1, "coarse_dirt", "coarse", MapColor.dirtColor), 
        PODZOL(2, "podzol", MapColor.obsidianColor);
        
        private static final DirtType[] METADATA_LOOKUP;
        private final int metadata;
        private final String name;
        private final String unlocalizedName;
        private final MapColor field_181067_h;
        
        private DirtType(final int p_i46396_3_, final String p_i46396_4_, final MapColor p_i46396_5_) {
            this(p_i46396_3_, p_i46396_4_, p_i46396_4_, p_i46396_5_);
        }
        
        private DirtType(final int p_i46397_3_, final String p_i46397_4_, final String p_i46397_5_, final MapColor p_i46397_6_) {
            this.metadata = p_i46397_3_;
            this.name = p_i46397_4_;
            this.unlocalizedName = p_i46397_5_;
            this.field_181067_h = p_i46397_6_;
        }
        
        public int getMetadata() {
            return this.metadata;
        }
        
        public String getUnlocalizedName() {
            return this.unlocalizedName;
        }
        
        public MapColor func_181066_d() {
            return this.field_181067_h;
        }
        
        @Override
        public String toString() {
            return this.name;
        }
        
        public static DirtType byMetadata(int metadata) {
            if (metadata < 0 || metadata >= DirtType.METADATA_LOOKUP.length) {
                metadata = 0;
            }
            return DirtType.METADATA_LOOKUP[metadata];
        }
        
        @Override
        public String getName() {
            return this.name;
        }
        
        static {
            METADATA_LOOKUP = new DirtType[values().length];
            for (final DirtType blockdirt$dirttype : values()) {
                DirtType.METADATA_LOOKUP[blockdirt$dirttype.getMetadata()] = blockdirt$dirttype;
            }
        }
    }
}
