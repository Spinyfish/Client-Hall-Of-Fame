package net.minecraft.block;

import net.minecraft.block.material.*;
import net.minecraft.block.properties.*;
import net.minecraft.creativetab.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.block.state.*;
import net.minecraft.util.*;

public class BlockStoneBrick extends Block
{
    public static final PropertyEnum<EnumType> VARIANT;
    public static final int DEFAULT_META;
    public static final int MOSSY_META;
    public static final int CRACKED_META;
    public static final int CHISELED_META;
    
    public BlockStoneBrick() {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockStoneBrick.VARIANT, EnumType.DEFAULT));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockStoneBrick.VARIANT).getMetadata();
    }
    
    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        for (final EnumType blockstonebrick$enumtype : EnumType.values()) {
            list.add(new ItemStack(itemIn, 1, blockstonebrick$enumtype.getMetadata()));
        }
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockStoneBrick.VARIANT, EnumType.byMetadata(meta));
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockStoneBrick.VARIANT).getMetadata();
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockStoneBrick.VARIANT });
    }
    
    static {
        VARIANT = PropertyEnum.create("variant", EnumType.class);
        DEFAULT_META = EnumType.DEFAULT.getMetadata();
        MOSSY_META = EnumType.MOSSY.getMetadata();
        CRACKED_META = EnumType.CRACKED.getMetadata();
        CHISELED_META = EnumType.CHISELED.getMetadata();
    }
    
    public enum EnumType implements IStringSerializable
    {
        DEFAULT(0, "stonebrick", "default"), 
        MOSSY(1, "mossy_stonebrick", "mossy"), 
        CRACKED(2, "cracked_stonebrick", "cracked"), 
        CHISELED(3, "chiseled_stonebrick", "chiseled");
        
        private static final EnumType[] META_LOOKUP;
        private final int meta;
        private final String name;
        private final String unlocalizedName;
        
        private EnumType(final int meta, final String name, final String unlocalizedName) {
            this.meta = meta;
            this.name = name;
            this.unlocalizedName = unlocalizedName;
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
            for (final EnumType blockstonebrick$enumtype : values()) {
                EnumType.META_LOOKUP[blockstonebrick$enumtype.getMetadata()] = blockstonebrick$enumtype;
            }
        }
    }
}
