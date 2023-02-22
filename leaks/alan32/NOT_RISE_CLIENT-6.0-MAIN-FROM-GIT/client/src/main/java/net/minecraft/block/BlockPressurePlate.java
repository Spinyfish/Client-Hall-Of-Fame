package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class BlockPressurePlate extends BlockBasePressurePlate {
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    private final BlockPressurePlate.Sensitivity sensitivity;

    protected BlockPressurePlate(final Material materialIn, final BlockPressurePlate.Sensitivity sensitivityIn) {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, Boolean.valueOf(false)));
        this.sensitivity = sensitivityIn;
    }

    protected int getRedstoneStrength(final IBlockState state) {
        return state.getValue(POWERED).booleanValue() ? 15 : 0;
    }

    protected IBlockState setRedstoneStrength(final IBlockState state, final int strength) {
        return state.withProperty(POWERED, Boolean.valueOf(strength > 0));
    }

    protected int computeRedstoneStrength(final World worldIn, final BlockPos pos) {
        final AxisAlignedBB axisalignedbb = this.getSensitiveAABB(pos);
        final List<? extends Entity> list;

        switch (this.sensitivity) {
            case EVERYTHING:
                list = worldIn.getEntitiesWithinAABBExcludingEntity(null, axisalignedbb);
                break;

            case MOBS:
                list = worldIn.<Entity>getEntitiesWithinAABB(EntityLivingBase.class, axisalignedbb);
                break;

            default:
                return 0;
        }

        if (!list.isEmpty()) {
            for (final Entity entity : list) {
                if (!entity.doesEntityNotTriggerPressurePlate()) {
                    return 15;
                }
            }
        }

        return 0;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(POWERED, Boolean.valueOf(meta == 1));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        return state.getValue(POWERED).booleanValue() ? 1 : 0;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, POWERED);
    }

    public enum Sensitivity {
        EVERYTHING,
        MOBS
    }
}
