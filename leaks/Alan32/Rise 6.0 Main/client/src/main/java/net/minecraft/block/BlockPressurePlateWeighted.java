package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;

public class BlockPressurePlateWeighted extends BlockBasePressurePlate {
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    private final int field_150068_a;

    protected BlockPressurePlateWeighted(final Material p_i46379_1_, final int p_i46379_2_) {
        this(p_i46379_1_, p_i46379_2_, p_i46379_1_.getMaterialMapColor());
    }

    protected BlockPressurePlateWeighted(final Material p_i46380_1_, final int p_i46380_2_, final MapColor p_i46380_3_) {
        super(p_i46380_1_, p_i46380_3_);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWER, Integer.valueOf(0)));
        this.field_150068_a = p_i46380_2_;
    }

    protected int computeRedstoneStrength(final World worldIn, final BlockPos pos) {
        final int i = Math.min(worldIn.getEntitiesWithinAABB(Entity.class, this.getSensitiveAABB(pos)).size(), this.field_150068_a);

        if (i > 0) {
            final float f = (float) Math.min(this.field_150068_a, i) / (float) this.field_150068_a;
            return MathHelper.ceiling_float_int(f * 15.0F);
        } else {
            return 0;
        }
    }

    protected int getRedstoneStrength(final IBlockState state) {
        return state.getValue(POWER).intValue();
    }

    protected IBlockState setRedstoneStrength(final IBlockState state, final int strength) {
        return state.withProperty(POWER, Integer.valueOf(strength));
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World worldIn) {
        return 10;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(POWER, Integer.valueOf(meta));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(POWER).intValue();
    }

    protected BlockState createBlockState() {
        return new BlockState(this, POWER);
    }
}
