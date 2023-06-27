package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import net.minecraft.util.*;

public class BlockSandStone extends Block
{
    public static final PropertyEnum<EnumType> TYPE;
    
    public BlockSandStone() {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockSandStone.TYPE, EnumType.DEFAULT));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockSandStone.TYPE).getMetadata();
    }
    
    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        for (final EnumType blocksandstone$enumtype : EnumType.values()) {
            list.add(new ItemStack(itemIn, 1, blocksandstone$enumtype.getMetadata()));
        }
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state) {
        return MapColor.sandColor;
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockSandStone.TYPE, EnumType.byMetadata(meta));
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockSandStone.TYPE).getMetadata();
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockSandStone.TYPE });
    }
    
    static {
        TYPE = PropertyEnum.create("type", EnumType.class);
    }
    
    public enum EnumType implements IStringSerializable
    {
        DEFAULT(0, "sandstone", "default"), 
        CHISELED(1, "chiseled_sandstone", "chiseled"), 
        SMOOTH(2, "smooth_sandstone", "smooth");
        
        private static final EnumType[] META_LOOKUP;
        private final int metadata;
        private final String name;
        private final String unlocalizedName;
        
        private EnumType(final int meta, final String name, final String unlocalizedName) {
            this.metadata = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
        }
        
        public int getMetadata() {
            return this.metadata;
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
            for (final EnumType blocksandstone$enumtype : values()) {
                EnumType.META_LOOKUP[blocksandstone$enumtype.getMetadata()] = blocksandstone$enumtype;
            }
        }
    }
}
