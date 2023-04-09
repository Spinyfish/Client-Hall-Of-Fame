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

public class BlockPlanks extends Block {
    public static final PropertyEnum VARIANT_PROP = PropertyEnum.create("variant", BlockPlanks.EnumType.class);
    // private static final String __OBFID = "CL_00002082";

    public BlockPlanks() {
        super(Material.wood);
        this.setDefaultState(this.blockState.getBaseState().withProperty(VARIANT_PROP, BlockPlanks.EnumType.OAK));
        this.setCreativeTab(CreativeTabs.tabBlock);
    }

    /**
     * Get the damage value that this Block should drop
     */
    public int damageDropped(IBlockState state) {
        return ((BlockPlanks.EnumType) state.getValue(VARIANT_PROP)).func_176839_a();
    }

    /**
     * returns a list of blocks with the same ID, but different meta (eg: wood returns 4 blocks)
     */
    public void getSubBlocks(Item itemIn, CreativeTabs tab, List list) {
        BlockPlanks.EnumType[] var4 = BlockPlanks.EnumType.values();

        for (EnumType var7 : var4) {
            list.add(new ItemStack(itemIn, 1, var7.func_176839_a()));
        }
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(VARIANT_PROP, BlockPlanks.EnumType.func_176837_a(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return ((BlockPlanks.EnumType) state.getValue(VARIANT_PROP)).func_176839_a();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, VARIANT_PROP);
    }

    public enum EnumType implements IStringSerializable {
        OAK(0, "oak"),
        SPRUCE(1, "spruce"),
        BIRCH(2, "birch"),
        JUNGLE(3, "jungle"),
        ACACIA(4, "acacia"),
        DARK_OAK(5, "dark_oak", "big_oak");
        private static final BlockPlanks.EnumType[] field_176842_g = new BlockPlanks.EnumType[values().length];
        private final int field_176850_h;
        private final String field_176851_i;
        private final String field_176848_j;

        // private static final String __OBFID = "CL_00002081";

        EnumType(int p_i45695_3_, String p_i45695_4_) {
            this(p_i45695_3_, p_i45695_4_, p_i45695_4_);
        }

        EnumType(int p_i45696_3_, String p_i45696_4_, String p_i45696_5_) {
            this.field_176850_h = p_i45696_3_;
            this.field_176851_i = p_i45696_4_;
            this.field_176848_j = p_i45696_5_;
        }

        public int func_176839_a() {
            return this.field_176850_h;
        }

        public String toString() {
            return this.field_176851_i;
        }

        public static BlockPlanks.EnumType func_176837_a(int p_176837_0_) {
            if (p_176837_0_ < 0 || p_176837_0_ >= field_176842_g.length) {
                p_176837_0_ = 0;
            }

            return field_176842_g[p_176837_0_];
        }

        public String getName() {
            return this.field_176851_i;
        }

        public String func_176840_c() {
            return this.field_176848_j;
        }

        static {
            BlockPlanks.EnumType[] var0 = values();

            for (EnumType var3 : var0) {
                field_176842_g[var3.func_176839_a()] = var3;
            }
        }
    }
}
