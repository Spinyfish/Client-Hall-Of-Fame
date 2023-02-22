package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.StatCollector;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public abstract class BlockStoneSlabNew extends BlockSlab {
    public static final PropertyBool SEAMLESS = PropertyBool.create("seamless");
    public static final PropertyEnum<BlockStoneSlabNew.EnumType> VARIANT = PropertyEnum.create("variant", BlockStoneSlabNew.EnumType.class);

    public BlockStoneSlabNew() {
        super(Material.rock);
        IBlockState iblockstate = this.blockState.getBaseState();

        if (this.isDouble()) {
            iblockstate = iblockstate.withProperty(SEAMLESS, Boolean.valueOf(false));
        } else {
            iblockstate = iblockstate.withProperty(HALF, BlockSlab.EnumBlockHalf.BOTTOM);
        }

        this.setDefaultState(iblockstate.withProperty(VARIANT, BlockStoneSlabNew.EnumType.RED_SANDSTONE));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Gets the localized name of this block. Used for the statistics page.
     */
    public String getLocalizedName() {
        return StatCollector.translateToLocal(this.getUnlocalizedName() + ".red_sandstone.name");
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemFromBlock(Blocks.stone_slab2);
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Item.getItemFromBlock(Blocks.stone_slab2);
    }

    /**
     * Returns the slab block name with the type associated with it
     */
    public String getUnlocalizedName(final int meta) {
        return super.getUnlocalizedName() + "." + BlockStoneSlabNew.EnumType.byMetadata(meta).getUnlocalizedName();
    }

    public IProperty<?> getVariantProperty() {
        return VARIANT;
    }

    public Object getVariant(final ItemStack stack) {
        return BlockStoneSlabNew.EnumType.byMetadata(stack.getMetadata() & 7);
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(final Item itemIn, final CreativeTabs tab, final List<ItemStack> list) {
        if (itemIn != Item.getItemFromBlock(Blocks.double_stone_slab2)) {
            for (final BlockStoneSlabNew.EnumType blockstoneslabnew$enumtype : BlockStoneSlabNew.EnumType.values()) {
                list.add(new ItemStack(itemIn, 1, blockstoneslabnew$enumtype.getMetadata()));
            }
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        IBlockState iblockstate = this.getDefaultState().withProperty(VARIANT, BlockStoneSlabNew.EnumType.byMetadata(meta & 7));

        if (this.isDouble()) {
            iblockstate = iblockstate.withProperty(SEAMLESS, Boolean.valueOf((meta & 8) != 0));
        } else {
            iblockstate = iblockstate.withProperty(HALF, (meta & 8) == 0 ? BlockSlab.EnumBlockHalf.BOTTOM : BlockSlab.EnumBlockHalf.TOP);
        }

        return iblockstate;
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(VARIANT).getMetadata();

        if (this.isDouble()) {
            if (state.getValue(SEAMLESS).booleanValue()) {
                i |= 8;
            }
        } else if (state.getValue(HALF) == BlockSlab.EnumBlockHalf.TOP) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return this.isDouble() ? new BlockState(this, SEAMLESS, VARIANT) : new BlockState(this, HALF, VARIANT);
    }

    /**
     * Get the MapColor for this Block and the given BlockState
     */
    public MapColor getMapColor(final IBlockState state) {
        return state.getValue(VARIANT).func_181068_c();
    }

    /**
     * Gets the metadata of the item this Block can drop. This method is called when the block gets destroyed. It
     * returns the metadata of the dropped item based on the old metadata of the block.
     */
    public int damageDropped(final IBlockState state) {
        return state.getValue(VARIANT).getMetadata();
    }

    public enum EnumType implements IStringSerializable {
        RED_SANDSTONE(0, "red_sandstone", BlockSand.EnumType.RED_SAND.getMapColor());

        private static final BlockStoneSlabNew.EnumType[] META_LOOKUP = new BlockStoneSlabNew.EnumType[values().length];
        private final int meta;
        private final String name;
        private final MapColor field_181069_e;

        EnumType(final int p_i46391_3_, final String p_i46391_4_, final MapColor p_i46391_5_) {
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

        public String toString() {
            return this.name;
        }

        public static BlockStoneSlabNew.EnumType byMetadata(int meta) {
            if (meta < 0 || meta >= META_LOOKUP.length) {
                meta = 0;
            }

            return META_LOOKUP[meta];
        }

        public String getName() {
            return this.name;
        }

        public String getUnlocalizedName() {
            return this.name;
        }

        static {
            for (final BlockStoneSlabNew.EnumType blockstoneslabnew$enumtype : values()) {
                META_LOOKUP[blockstoneslabnew$enumtype.getMetadata()] = blockstoneslabnew$enumtype;
            }
        }
    }
}
