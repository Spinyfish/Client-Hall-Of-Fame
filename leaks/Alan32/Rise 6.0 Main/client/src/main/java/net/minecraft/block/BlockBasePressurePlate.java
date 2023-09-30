package net.minecraft.block;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public abstract class BlockBasePressurePlate extends Block {
    protected BlockBasePressurePlate(final Material materialIn) {
        this(materialIn, materialIn.getMaterialMapColor());
    }

    protected BlockBasePressurePlate(final Material p_i46401_1_, final MapColor p_i46401_2_) {
        super(p_i46401_1_, p_i46401_2_);
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        this.setBlockBoundsBasedOnState0(worldIn.getBlockState(pos));
    }

    protected void setBlockBoundsBasedOnState0(final IBlockState state) {
        final boolean flag = this.getRedstoneStrength(state) > 0;
        final float f = 0.0625F;

        if (flag) {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.03125F, 0.9375F);
        } else {
            this.setBlockBounds(0.0625F, 0.0F, 0.0625F, 0.9375F, 0.0625F, 0.9375F);
        }
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World worldIn) {
        return 20;
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        return null;
    }

    /**
     * Used to determine ambient occlusion and culling when rebuilding chunks for render
     */
    public boolean isOpaqueCube() {
        return false;
    }

    public boolean isFullCube() {
        return false;
    }

    public boolean isPassable(final IBlockAccess worldIn, final BlockPos pos) {
        return true;
    }

    public boolean func_181623_g() {
        return true;
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return this.canBePlacedOn(worldIn, pos.down());
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!this.canBePlacedOn(worldIn, pos.down())) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
    }

    private boolean canBePlacedOn(final World worldIn, final BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(worldIn, pos) || worldIn.getBlockState(pos).getBlock() instanceof BlockFence;
    }

    /**
     * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
     */
    public void randomTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random random) {
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (!worldIn.isRemote) {
            final int i = this.getRedstoneStrength(state);

            if (i > 0) {
                this.updateState(worldIn, pos, state, i);
            }
        }
    }

    /**
     * Called When an Entity Collided with the Block
     */
    public void onEntityCollidedWithBlock(final World worldIn, final BlockPos pos, final IBlockState state, final Entity entityIn) {
        if (!worldIn.isRemote) {
            final int i = this.getRedstoneStrength(state);

            if (i == 0) {
                this.updateState(worldIn, pos, state, i);
            }
        }
    }

    /**
     * Updates the pressure plate when stepped on
     */
    protected void updateState(final World worldIn, final BlockPos pos, IBlockState state, final int oldRedstoneStrength) {
        final int i = this.computeRedstoneStrength(worldIn, pos);
        final boolean flag = oldRedstoneStrength > 0;
        final boolean flag1 = i > 0;

        if (oldRedstoneStrength != i) {
            state = this.setRedstoneStrength(state, i);
            worldIn.setBlockState(pos, state, 2);
            this.updateNeighbors(worldIn, pos);
            worldIn.markBlockRangeForRenderUpdate(pos, pos);
        }

        if (!flag1 && flag) {
            worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D, "random.click", 0.3F, 0.5F);
        } else if (flag1 && !flag) {
            worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D, "random.click", 0.3F, 0.6F);
        }

        if (flag1) {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
        }
    }

    /**
     * Returns the cubic AABB inset by 1/8 on all sides
     */
    protected AxisAlignedBB getSensitiveAABB(final BlockPos pos) {
        final float f = 0.125F;
        return new AxisAlignedBB((float) pos.getX() + 0.125F, pos.getY(), (float) pos.getZ() + 0.125F, (float) (pos.getX() + 1) - 0.125F, (double) pos.getY() + 0.25D, (float) (pos.getZ() + 1) - 0.125F);
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (this.getRedstoneStrength(state) > 0) {
            this.updateNeighbors(worldIn, pos);
        }

        super.breakBlock(worldIn, pos, state);
    }

    /**
     * Notify block and block below of changes
     */
    protected void updateNeighbors(final World worldIn, final BlockPos pos) {
        worldIn.notifyNeighborsOfStateChange(pos, this);
        worldIn.notifyNeighborsOfStateChange(pos.down(), this);
    }

    public int isProvidingWeakPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return this.getRedstoneStrength(state);
    }

    public int isProvidingStrongPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return side == EnumFacing.UP ? this.getRedstoneStrength(state) : 0;
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower() {
        return true;
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        final float f = 0.5F;
        final float f1 = 0.125F;
        final float f2 = 0.5F;
        this.setBlockBounds(0.0F, 0.375F, 0.0F, 1.0F, 0.625F, 1.0F);
    }

    public int getMobilityFlag() {
        return 1;
    }

    protected abstract int computeRedstoneStrength(World worldIn, BlockPos pos);

    protected abstract int getRedstoneStrength(IBlockState state);

    protected abstract IBlockState setRedstoneStrength(IBlockState state, int strength);
}
