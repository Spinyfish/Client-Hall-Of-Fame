package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.world.World;

import java.util.Random;

public class BlockHugeMushroom extends Block {
    public static final PropertyEnum field_176380_a = PropertyEnum.create("variant", BlockHugeMushroom.EnumType.class);
    private final Block field_176379_b;
    // private static final String __OBFID = "CL_00000258";

    public BlockHugeMushroom(Material p_i45711_1_, Block p_i45711_2_) {
        super(p_i45711_1_);
        this.setDefaultState(this.blockState.getBaseState().withProperty(field_176380_a, BlockHugeMushroom.EnumType.ALL_OUTSIDE));
        this.field_176379_b = p_i45711_2_;
    }

    /**
     * Returns the quantity of items to drop on block destruction.
     */
    public int quantityDropped(Random random) {
        return Math.max(0, random.nextInt(10) - 7);
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(this.field_176379_b);
    }

    public Item getItem(World worldIn, BlockPos pos) {
        return Item.getItemFromBlock(this.field_176379_b);
    }

    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        return this.getDefaultState();
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(field_176380_a, BlockHugeMushroom.EnumType.func_176895_a(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(IBlockState state) {
        return ((BlockHugeMushroom.EnumType) state.getValue(field_176380_a)).func_176896_a();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, field_176380_a);
    }

    public enum EnumType implements IStringSerializable {
        NORTH_WEST(1, "north_west"),
        NORTH(2, "north"),
        NORTH_EAST(3, "north_east"),
        WEST(4, "west"),
        CENTER(5, "center"),
        EAST(6, "east"),
        SOUTH_WEST(7, "south_west"),
        SOUTH(8, "south"),
        SOUTH_EAST(9, "south_east"),
        STEM(10, "stem"),
        ALL_INSIDE(0, "all_inside"),
        ALL_OUTSIDE(14, "all_outside"),
        ALL_STEM(15, "all_stem");
        private static final BlockHugeMushroom.EnumType[] field_176905_n = new BlockHugeMushroom.EnumType[16];
        private final int field_176906_o;
        private final String field_176914_p;

        // private static final String __OBFID = "CL_00002105";

        EnumType(int p_i45710_3_, String p_i45710_4_) {
            this.field_176906_o = p_i45710_3_;
            this.field_176914_p = p_i45710_4_;
        }

        public int func_176896_a() {
            return this.field_176906_o;
        }

        public String toString() {
            return this.field_176914_p;
        }

        public static BlockHugeMushroom.EnumType func_176895_a(int p_176895_0_) {
            if (p_176895_0_ < 0 || p_176895_0_ >= field_176905_n.length) {
                p_176895_0_ = 0;
            }

            BlockHugeMushroom.EnumType var1 = field_176905_n[p_176895_0_];
            return var1 == null ? field_176905_n[0] : var1;
        }

        public String getName() {
            return this.field_176914_p;
        }

        static {
            BlockHugeMushroom.EnumType[] var0 = values();

            for (EnumType var3 : var0) {
                field_176905_n[var3.func_176896_a()] = var3;
            }
        }
    }
}
