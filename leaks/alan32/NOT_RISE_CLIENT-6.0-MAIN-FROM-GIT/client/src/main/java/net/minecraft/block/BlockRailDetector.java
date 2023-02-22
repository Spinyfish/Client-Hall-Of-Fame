package net.minecraft.block;

import com.google.common.base.Predicate;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityMinecartCommandBlock;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class BlockRailDetector extends BlockRailBase {
    public static final PropertyEnum<BlockRailBase.EnumRailDirection> SHAPE = PropertyEnum.create("shape", BlockRailBase.EnumRailDirection.class, new Predicate<BlockRailBase.EnumRailDirection>() {
        public boolean apply(final BlockRailBase.EnumRailDirection p_apply_1_) {
            return p_apply_1_ != BlockRailBase.EnumRailDirection.NORTH_EAST && p_apply_1_ != BlockRailBase.EnumRailDirection.NORTH_WEST && p_apply_1_ != BlockRailBase.EnumRailDirection.SOUTH_EAST && p_apply_1_ != BlockRailBase.EnumRailDirection.SOUTH_WEST;
        }
    });
    public static final PropertyBool POWERED = PropertyBool.create("powered");

    public BlockRailDetector() {
        super(true);
        this.setDefaultState(this.blockState.getBaseState().withProperty(POWERED, Boolean.valueOf(false)).withProperty(SHAPE, BlockRailBase.EnumRailDirection.NORTH_SOUTH));
        this.setTickRandomly(true);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World worldIn) {
        return 20;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower() {
        return true;
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(final World worldIn, final BlockPos pos, final IBlockState state, final Entity entityIn) {
        if (!worldIn.isRemote) {
            if (!state.getValue(POWERED).booleanValue()) {
                this.updatePoweredState(worldIn, pos, state);
            }
        }
    }

    /**
     * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
     */
    public void randomTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random random) {
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!worldIn.isRemote && state.getValue(POWERED).booleanValue()) {
            this.updatePoweredState(worldIn, pos, state);
        }
    }

    public int isProvidingWeakPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return state.getValue(POWERED).booleanValue() ? 15 : 0;
    }

    public int isProvidingStrongPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return !state.getValue(POWERED).booleanValue() ? 0 : (side == EnumFacing.UP ? 15 : 0);
    }

    private void updatePoweredState(final World worldIn, final BlockPos pos, final IBlockState state) {
        final boolean flag = state.getValue(POWERED).booleanValue();
        boolean flag1 = false;
        final List<EntityMinecart> list = this.findMinecarts(worldIn, pos, EntityMinecart.class);

        if (!list.isEmpty()) {
            flag1 = true;
        }

        if (flag1 && !flag) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(true)), 3);
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        }

        if (!flag1 && flag) {
            worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(false)), 3);
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.down(), this);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        }

        if (flag1) {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
        }

        worldIn.updateComparatorOutputLevel(pos, this);
    }

    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.onBlockAdded(worldIn, pos, state);
        this.updatePoweredState(worldIn, pos, state);
    }

    public IProperty<BlockRailBase.EnumRailDirection> getShapeProperty() {
        return SHAPE;
    }

    public boolean hasComparatorInputOverride() {
        return true;
    }

    public int getComparatorInputOverride(final World worldIn, final BlockPos pos) {
        if (worldIn.getBlockState(pos).getValue(POWERED).booleanValue()) {
            final List<EntityMinecartCommandBlock> list = this.findMinecarts(worldIn, pos, EntityMinecartCommandBlock.class);

            if (!list.isEmpty()) {
                return list.get(0).getCommandBlockLogic().getSuccessCount();
            }

            final List<EntityMinecart> list1 = this.findMinecarts(worldIn, pos, EntityMinecart.class, EntitySelectors.selectInventories);

            if (!list1.isEmpty()) {
                return Container.calcRedstoneFromInventory((IInventory) list1.get(0));
            }
        }

        return 0;
    }

    protected <T extends EntityMinecart> List<T> findMinecarts(final World worldIn, final BlockPos pos, final Class<T> clazz, final Predicate<Entity>... filter) {
        final AxisAlignedBB axisalignedbb = this.getDectectionBox(pos);
        return filter.length != 1 ? worldIn.getEntitiesWithinAABB(clazz, axisalignedbb) : worldIn.getEntitiesWithinAABB(clazz, axisalignedbb, filter[0]);
    }

    private AxisAlignedBB getDectectionBox(final BlockPos pos) {
        final float f = 0.2F;
        return new AxisAlignedBB((float) pos.getX() + 0.2F, pos.getY(), (float) pos.getZ() + 0.2F, (float) (pos.getX() + 1) - 0.2F, (float) (pos.getY() + 1) - 0.2F, (float) (pos.getZ() + 1) - 0.2F);
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(SHAPE, BlockRailBase.EnumRailDirection.byMetadata(meta & 7)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(SHAPE).getMetadata();

        if (state.getValue(POWERED).booleanValue()) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, SHAPE, POWERED);
    }
}
