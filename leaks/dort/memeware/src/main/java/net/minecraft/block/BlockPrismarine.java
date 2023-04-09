package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IStringSerializable;

import java.util.List;

public class BlockPrismarine extends Block {
    public static final PropertyEnum VARIANTS = PropertyEnum.create("variant", BlockPrismarine.EnumType.class);
    public static final int ROUGHMETA = BlockPrismarine.EnumType.ROUGH.getMetadata();
    public static final int BRICKSMETA = BlockPrismarine.EnumType.BRICKS.getMetadata();
    public static final int DARKMETA = BlockPrismarine.EnumType.DARK.getMetadata();
    // private static final String __OBFID = "CL_00002077";

    public BlockPrismarine() {
        super(Material.rock);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANTS, BlockPrismarine.EnumType.ROUGH));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Get the damage value that this Block should drop
     */
    public int damageDropped(IBlockState state) {
        return ((BlockPrismarine.EnumType) state.getValue(VARIANTS)).getMetadata();
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return ((BlockPrismarine.EnumType) state.getValue(VARIANTS)).getMetadata();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, VARIANTS);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANTS, BlockPrismarine.EnumType.func_176810_a(meta));
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        list.add(new ItemStack(itemIn, 1, ROUGHMETA));
        list.add(new ItemStack(itemIn, 1, BRICKSMETA));
        list.add(new ItemStack(itemIn, 1, DARKMETA));
    }

    public enum EnumType implements IStringSerializable {
        ROUGH(0, "prismarine", "rough"),
        BRICKS(1, "prismarine_bricks", "bricks"),
        DARK(2, "dark_prismarine", "dark");
        private static final BlockPrismarine.EnumType[] field_176813_d = new BlockPrismarine.EnumType[values().length];
        private final int meta;
        private final String field_176811_f;
        private final String field_176812_g;

        // private static final String __OBFID = "CL_00002076";

        EnumType(int p_i45692_3_, String p_i45692_4_, String p_i45692_5_) {
            this.meta = p_i45692_3_;
            this.field_176811_f = p_i45692_4_;
            this.field_176812_g = p_i45692_5_;
        }

        public int getMetadata() {
            return this.meta;
        }

        public String toString() {
            return this.field_176811_f;
        }

        public static BlockPrismarine.EnumType func_176810_a(int p_176810_0_) {
            if (p_176810_0_ < 0 || p_176810_0_ >= field_176813_d.length) {
                p_176810_0_ = 0;
            }

            return field_176813_d[p_176810_0_];
        }

        public String getName() {
            return this.field_176811_f;
        }

        public String func_176809_c() {
            return this.field_176812_g;
        }

        static {
            BlockPrismarine.EnumType[] var0 = values();

            for (EnumType var3 : var0) {
                field_176813_d[var3.getMetadata()] = var3;
            }
        }
    }
}
