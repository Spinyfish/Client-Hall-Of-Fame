package net.minecraft.block;

import net.minecraft.block.properties.*;
import net.minecraft.creativetab.*;
import net.minecraft.block.material.*;
import net.minecraft.block.state.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.util.*;

public class BlockPrismarine extends Block
{
    public static final PropertyEnum<EnumType> VARIANT;
    public static final int ROUGH_META;
    public static final int BRICKS_META;
    public static final int DARK_META;
    
    public BlockPrismarine() {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(BlockPrismarine.VARIANT, EnumType.ROUGH));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }
    
    @Override
    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + "." + EnumType.ROUGH.getUnlocalizedName() + ".name");
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state) {
        return (state.getValue(BlockPrismarine.VARIANT) == EnumType.ROUGH) ? MapColor.cyanColor : MapColor.diamondColor;
    }
    
    @Override
    public int damageDropped(final IBlockState state) {
        return state.getValue(BlockPrismarine.VARIANT).getMetadata();
    }
    
    @Override
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(BlockPrismarine.VARIANT).getMetadata();
    }
    
    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, new IProperty[] { BlockPrismarine.VARIANT });
    }
    
    @Override
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(BlockPrismarine.VARIANT, EnumType.byMetadata(meta));
    }
    
    @Override
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        list.add(new ItemStack(itemIn, 1, BlockPrismarine.ROUGH_META));
        list.add(new ItemStack(itemIn, 1, BlockPrismarine.BRICKS_META));
        list.add(new ItemStack(itemIn, 1, BlockPrismarine.DARK_META));
    }
    
    static {
        VARIANT = PropertyEnum.create("variant", EnumType.class);
        ROUGH_META = EnumType.ROUGH.getMetadata();
        BRICKS_META = EnumType.BRICKS.getMetadata();
        DARK_META = EnumType.DARK.getMetadata();
    }
    
    public enum EnumType implements IStringSerializable
    {
        ROUGH(0, "prismarine", "rough"), 
        BRICKS(1, "prismarine_bricks", "bricks"), 
        DARK(2, "dark_prismarine", "dark");
        
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
            for (final EnumType blockprismarine$enumtype : values()) {
                EnumType.META_LOOKUP[blockprismarine$enumtype.getMetadata()] = blockprismarine$enumtype;
            }
        }
    }
}
