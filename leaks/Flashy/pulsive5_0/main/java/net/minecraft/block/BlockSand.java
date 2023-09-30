package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.List;

public class BlockSand extends BlockFalling
{
    public static final PropertyEnum<EnumType> VARIANT = PropertyEnum.<EnumType>create("variant", EnumType.class);

    public BlockSand()
    {
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT, EnumType.SAND));
    }

    public int damageDropped(IBlockState state)
    {
        return ((EnumType)state.getValue(VARIANT)).getMetadata();
    }

    public void getSubBlocks(Item itemIn, CreativeTabs tab, List<ItemStack> list)
    {
        for (EnumType blocksand$enumtype : EnumType.values())
        {
            list.add(new ItemStack(itemIn, 1, blocksand$enumtype.getMetadata()));
        }
    }

    public MapColor getMapColor(IBlockState state)
    {
        return ((EnumType)state.getValue(VARIANT)).getMapColor();
    }

    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(VARIANT, EnumType.byMetadata(meta));
    }

    public int getMetaFromState(IBlockState state)
    {
        return ((EnumType)state.getValue(VARIANT)).getMetadata();
    }

    protected BlockState createBlockState()
    {
        return new BlockState(this, new IProperty[] {VARIANT});
    }

    public static enum EnumType implements IStringSerializable
    {
        SAND(0, "sand", "default", MapColor.sandColor),
        RED_SAND(1, "red_sand", "red", MapColor.adobeColor);

        private static final EnumType[] META_LOOKUP = new EnumType[values().length];
        private final int meta;
        private final String name;
        private final MapColor mapColor;
        private final String unlocalizedName;

        private EnumType(int meta, String name, String unlocalizedName, MapColor mapColor)
        {
            this.meta = meta;
            this.name = name;
            this.mapColor = mapColor;
            this.unlocalizedName = unlocalizedName;
        }

        public int getMetadata()
        {
            return this.meta;
        }

        public String toString()
        {
            return this.name;
        }

        public MapColor getMapColor()
        {
            return this.mapColor;
        }

        public static EnumType byMetadata(int meta)
        {
            if (meta < 0 || meta >= META_LOOKUP.length)
            {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName()
        {
            return this.name;
        }

        public String getUnlocalizedName()
        {
            return this.unlocalizedName;
        }

        static {
            for (EnumType blocksand$enumtype : values())
            {
                META_LOOKUP[blocksand$enumtype.getMetadata()] = blocksand$enumtype;
            }
        }
    }
}
