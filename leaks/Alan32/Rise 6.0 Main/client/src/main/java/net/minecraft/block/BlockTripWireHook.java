package net.minecraft.block;

import com.google.common.base.Objects;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockTripWireHook extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool ATTACHED = PropertyBool.create("attached");
    public static final PropertyBool SUSPENDED = PropertyBool.create("suspended");

    public BlockTripWireHook() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(POWERED, Boolean.valueOf(false)).withProperty(ATTACHED, Boolean.valueOf(false)).withProperty(SUSPENDED, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabRedstone);
        this.setTickRandomly(true);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(final IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        return state.withProperty(SUSPENDED, Boolean.valueOf(!World.doesBlockHaveSolidTopSurface(worldIn, pos.down())));
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

    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
        return side.getAxis().isHorizontal() && worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock().isNormalCube();
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock().isNormalCube()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState iblockstate = this.getDefaultState().withProperty(POWERED, Boolean.valueOf(false)).withProperty(ATTACHED, Boolean.valueOf(false)).withProperty(SUSPENDED, Boolean.valueOf(false));

        if (facing.getAxis().isHorizontal()) {
            iblockstate = iblockstate.withProperty(FACING, facing);
        }

        return iblockstate;
    }

    /**
     * Called by ItemBlocks after a block is set in the world, to allow post-place logic
     */
    public void onBlockPlacedBy(final World worldIn, final BlockPos pos, final IBlockState state, final EntityLivingBase placer, final ItemStack stack) {
        this.func_176260_a(worldIn, pos, state, false, false, -1, null);
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (neighborBlock != this) {
            if (this.checkForDrop(worldIn, pos, state)) {
                final EnumFacing enumfacing = state.getValue(FACING);

                if (!worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock().isNormalCube()) {
                    this.dropBlockAsItem(worldIn, pos, state, 0);
                    worldIn.setBlockToAir(pos);
                }
            }
        }
    }

    public void func_176260_a(final World worldIn, final BlockPos pos, final IBlockState hookState, final boolean p_176260_4_, final boolean p_176260_5_, final int p_176260_6_, final IBlockState p_176260_7_) {
        final EnumFacing enumfacing = hookState.getValue(FACING);
        final boolean flag = hookState.getValue(ATTACHED).booleanValue();
        final boolean flag1 = hookState.getValue(POWERED).booleanValue();
        final boolean flag2 = !World.doesBlockHaveSolidTopSurface(worldIn, pos.down());
        boolean flag3 = !p_176260_4_;
        boolean flag4 = false;
        int i = 0;
        final IBlockState[] aiblockstate = new IBlockState[42];

        for (int j = 1; j < 42; ++j) {
            final BlockPos blockpos = pos.offset(enumfacing, j);
            IBlockState iblockstate = worldIn.getBlockState(blockpos);

            if (iblockstate.getBlock() == Blocks.tripwire_hook) {
                if (iblockstate.getValue(FACING) == enumfacing.getOpposite()) {
                    i = j;
                }

                break;
            }

            if (iblockstate.getBlock() != Blocks.tripwire && j != p_176260_6_) {
                aiblockstate[j] = null;
                flag3 = false;
            } else {
                if (j == p_176260_6_) {
                    iblockstate = Objects.firstNonNull(p_176260_7_, iblockstate);
                }

                final boolean flag5 = !iblockstate.getValue(BlockTripWire.DISARMED).booleanValue();
                final boolean flag6 = iblockstate.getValue(BlockTripWire.POWERED).booleanValue();
                final boolean flag7 = iblockstate.getValue(BlockTripWire.SUSPENDED).booleanValue();
                flag3 &= flag7 == flag2;
                flag4 |= flag5 && flag6;
                aiblockstate[j] = iblockstate;

                if (j == p_176260_6_) {
                    worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
                    flag3 &= flag5;
                }
            }
        }

        flag3 = flag3 & i > 1;
        flag4 = flag4 & flag3;
        final IBlockState iblockstate1 = this.getDefaultState().withProperty(ATTACHED, Boolean.valueOf(flag3)).withProperty(POWERED, Boolean.valueOf(flag4));

        if (i > 0) {
            final BlockPos blockpos1 = pos.offset(enumfacing, i);
            final EnumFacing enumfacing1 = enumfacing.getOpposite();
            worldIn.setBlockState(blockpos1, iblockstate1.withProperty(FACING, enumfacing1), 3);
            this.func_176262_b(worldIn, blockpos1, enumfacing1);
            this.func_180694_a(worldIn, blockpos1, flag3, flag4, flag, flag1);
        }

        this.func_180694_a(worldIn, pos, flag3, flag4, flag, flag1);

        if (!p_176260_4_) {
            worldIn.setBlockState(pos, iblockstate1.withProperty(FACING, enumfacing), 3);

            if (p_176260_5_) {
                this.func_176262_b(worldIn, pos, enumfacing);
            }
        }

        if (flag != flag3) {
            for (int k = 1; k < i; ++k) {
                final BlockPos blockpos2 = pos.offset(enumfacing, k);
                final IBlockState iblockstate2 = aiblockstate[k];

                if (iblockstate2 != null && worldIn.getBlockState(blockpos2).getBlock() != Blocks.air) {
                    worldIn.setBlockState(blockpos2, iblockstate2.withProperty(ATTACHED, Boolean.valueOf(flag3)), 3);
                }
            }
        }
    }

    /**
     * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
     */
    public void randomTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random random) {
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        this.func_176260_a(worldIn, pos, state, false, true, -1, null);
    }

    private void func_180694_a(final World worldIn, final BlockPos pos, final boolean p_180694_3_, final boolean p_180694_4_, final boolean p_180694_5_, final boolean p_180694_6_) {
        if (p_180694_4_ && !p_180694_6_) {
            worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D, "random.click", 0.4F, 0.6F);
        } else if (!p_180694_4_ && p_180694_6_) {
            worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D, "random.click", 0.4F, 0.5F);
        } else if (p_180694_3_ && !p_180694_5_) {
            worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D, "random.click", 0.4F, 0.7F);
        } else if (!p_180694_3_ && p_180694_5_) {
            worldIn.playSoundEffect((double) pos.getX() + 0.5D, (double) pos.getY() + 0.1D, (double) pos.getZ() + 0.5D, "random.bowhit", 0.4F, 1.2F / (worldIn.rand.nextFloat() * 0.2F + 0.9F));
        }
    }

    private void func_176262_b(final World worldIn, final BlockPos p_176262_2_, final EnumFacing p_176262_3_) {
        worldIn.notifyNeighborsOfStateChange(p_176262_2_, this);
        worldIn.notifyNeighborsOfStateChange(p_176262_2_.offset(p_176262_3_.getOpposite()), this);
    }

    private boolean checkForDrop(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!this.canPlaceBlockAt(worldIn, pos)) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return false;
        } else {
            return true;
        }
    }

    @SuppressWarnings("incomplete-switch")
    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        final float f = 0.1875F;

        switch (worldIn.getBlockState(pos).getValue(FACING)) {
            case EAST:
                this.setBlockBounds(0.0F, 0.2F, 0.5F - f, f * 2.0F, 0.8F, 0.5F + f);
                break;

            case WEST:
                this.setBlockBounds(1.0F - f * 2.0F, 0.2F, 0.5F - f, 1.0F, 0.8F, 0.5F + f);
                break;

            case SOUTH:
                this.setBlockBounds(0.5F - f, 0.2F, 0.0F, 0.5F + f, 0.8F, f * 2.0F);
                break;

            case NORTH:
                this.setBlockBounds(0.5F - f, 0.2F, 1.0F - f * 2.0F, 0.5F + f, 0.8F, 1.0F);
        }
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final boolean flag = state.getValue(ATTACHED).booleanValue();
        final boolean flag1 = state.getValue(POWERED).booleanValue();

        if (flag || flag1) {
            this.func_176260_a(worldIn, pos, state, true, false, -1, null);
        }

        if (flag1) {
            worldIn.notifyNeighborsOfStateChange(pos, this);
            worldIn.notifyNeighborsOfStateChange(pos.offset(state.getValue(FACING).getOpposite()), this);
        }

        super.breakBlock(worldIn, pos, state);
    }

    public int isProvidingWeakPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return state.getValue(POWERED).booleanValue() ? 15 : 0;
    }

    public int isProvidingStrongPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return !state.getValue(POWERED).booleanValue() ? 0 : (state.getValue(FACING) == side ? 15 : 0);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower() {
        return true;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta & 3)).withProperty(POWERED, Boolean.valueOf((meta & 8) > 0)).withProperty(ATTACHED, Boolean.valueOf((meta & 4) > 0));
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getHorizontalIndex();

        if (state.getValue(POWERED).booleanValue()) {
            i |= 8;
        }

        if (state.getValue(ATTACHED).booleanValue()) {
            i |= 4;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, POWERED, ATTACHED, SUSPENDED);
    }
}
