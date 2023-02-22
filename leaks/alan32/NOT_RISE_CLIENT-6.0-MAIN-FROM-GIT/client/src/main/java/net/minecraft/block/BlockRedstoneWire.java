package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class BlockRedstoneWire extends Block {
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> NORTH = PropertyEnum.create("north", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> EAST = PropertyEnum.create("east", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> SOUTH = PropertyEnum.create("south", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyEnum<BlockRedstoneWire.EnumAttachPosition> WEST = PropertyEnum.create("west", BlockRedstoneWire.EnumAttachPosition.class);
    public static final PropertyInteger POWER = PropertyInteger.create("power", 0, 15);
    private boolean canProvidePower = true;
    private final Set<BlockPos> blocksNeedingUpdate = Sets.newHashSet();

    public BlockRedstoneWire() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(EAST, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(SOUTH, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(WEST, BlockRedstoneWire.EnumAttachPosition.NONE).withProperty(POWER, Integer.valueOf(0)));
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.0625F, 1.0F);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        state = state.withProperty(WEST, this.getAttachPosition(worldIn, pos, EnumFacing.WEST));
        state = state.withProperty(EAST, this.getAttachPosition(worldIn, pos, EnumFacing.EAST));
        state = state.withProperty(NORTH, this.getAttachPosition(worldIn, pos, EnumFacing.NORTH));
        state = state.withProperty(SOUTH, this.getAttachPosition(worldIn, pos, EnumFacing.SOUTH));
        return state;
    }

    private BlockRedstoneWire.EnumAttachPosition getAttachPosition(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing direction) {
        final BlockPos blockpos = pos.offset(direction);
        final Block block = worldIn.getBlockState(pos.offset(direction)).getBlock();

        if (!canConnectTo(worldIn.getBlockState(blockpos), direction) && (block.isBlockNormalCube() || !canConnectUpwardsTo(worldIn.getBlockState(blockpos.down())))) {
            final Block block1 = worldIn.getBlockState(pos.up()).getBlock();
            return !block1.isBlockNormalCube() && block.isBlockNormalCube() && canConnectUpwardsTo(worldIn.getBlockState(blockpos.up())) ? BlockRedstoneWire.EnumAttachPosition.UP : BlockRedstoneWire.EnumAttachPosition.NONE;
        } else {
            return BlockRedstoneWire.EnumAttachPosition.SIDE;
        }
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

    public int colorMultiplier(final IBlockAccess worldIn, final BlockPos pos, final int renderPass) {
        final IBlockState iblockstate = worldIn.getBlockState(pos);
        return iblockstate.getBlock() != this ? super.colorMultiplier(worldIn, pos, renderPass) : this.colorMultiplier(iblockstate.getValue(POWER).intValue());
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return World.doesBlockHaveSolidTopSurface(worldIn, pos.down()) || worldIn.getBlockState(pos.down()).getBlock() == Blocks.glowstone;
    }

    private IBlockState updateSurroundingRedstone(final World worldIn, final BlockPos pos, IBlockState state) {
        state = this.calculateCurrentChanges(worldIn, pos, pos, state);
        final List<BlockPos> list = Lists.newArrayList(this.blocksNeedingUpdate);
        this.blocksNeedingUpdate.clear();

        for (final BlockPos blockpos : list) {
            worldIn.notifyNeighborsOfStateChange(blockpos, this);
        }

        return state;
    }

    private IBlockState calculateCurrentChanges(final World worldIn, final BlockPos pos1, final BlockPos pos2, IBlockState state) {
        final IBlockState iblockstate = state;
        final int i = state.getValue(POWER).intValue();
        int j = 0;
        j = this.getMaxCurrentStrength(worldIn, pos2, j);
        this.canProvidePower = false;
        final int k = worldIn.isBlockIndirectlyGettingPowered(pos1);
        this.canProvidePower = true;

        if (k > 0 && k > j - 1) {
            j = k;
        }

        int l = 0;

        for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            final BlockPos blockpos = pos1.offset(enumfacing);
            final boolean flag = blockpos.getX() != pos2.getX() || blockpos.getZ() != pos2.getZ();

            if (flag) {
                l = this.getMaxCurrentStrength(worldIn, blockpos, l);
            }

            if (worldIn.getBlockState(blockpos).getBlock().isNormalCube() && !worldIn.getBlockState(pos1.up()).getBlock().isNormalCube()) {
                if (flag && pos1.getY() >= pos2.getY()) {
                    l = this.getMaxCurrentStrength(worldIn, blockpos.up(), l);
                }
            } else if (!worldIn.getBlockState(blockpos).getBlock().isNormalCube() && flag && pos1.getY() <= pos2.getY()) {
                l = this.getMaxCurrentStrength(worldIn, blockpos.down(), l);
            }
        }

        if (l > j) {
            j = l - 1;
        } else if (j > 0) {
            --j;
        } else {
            j = 0;
        }

        if (k > j - 1) {
            j = k;
        }

        if (i != j) {
            state = state.withProperty(POWER, Integer.valueOf(j));

            if (worldIn.getBlockState(pos1) == iblockstate) {
                worldIn.setBlockState(pos1, state, 2);
            }

            this.blocksNeedingUpdate.add(pos1);

            for (final EnumFacing enumfacing1 : EnumFacing.values()) {
                this.blocksNeedingUpdate.add(pos1.offset(enumfacing1));
            }
        }

        return state;
    }

    /**
     * Calls World.notifyNeighborsOfStateChange() for all neighboring blocks, but only if the given block is a redstone
     * wire.
     */
    private void notifyWireNeighborsOfStateChange(final World worldIn, final BlockPos pos) {
        if (worldIn.getBlockState(pos).getBlock() == this) {
            worldIn.notifyNeighborsOfStateChange(pos, this);

            for (final EnumFacing enumfacing : EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }
        }
    }

    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (!worldIn.isRemote) {
            this.updateSurroundingRedstone(worldIn, pos, state);

            for (final EnumFacing enumfacing : EnumFacing.Plane.VERTICAL) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }

            for (final EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumfacing1));
            }

            for (final EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
                final BlockPos blockpos = pos.offset(enumfacing2);

                if (worldIn.getBlockState(blockpos).getBlock().isNormalCube()) {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
                } else {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
                }
            }
        }
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        super.breakBlock(worldIn, pos, state);

        if (!worldIn.isRemote) {
            for (final EnumFacing enumfacing : EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }

            this.updateSurroundingRedstone(worldIn, pos, state);

            for (final EnumFacing enumfacing1 : EnumFacing.Plane.HORIZONTAL) {
                this.notifyWireNeighborsOfStateChange(worldIn, pos.offset(enumfacing1));
            }

            for (final EnumFacing enumfacing2 : EnumFacing.Plane.HORIZONTAL) {
                final BlockPos blockpos = pos.offset(enumfacing2);

                if (worldIn.getBlockState(blockpos).getBlock().isNormalCube()) {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.up());
                } else {
                    this.notifyWireNeighborsOfStateChange(worldIn, blockpos.down());
                }
            }
        }
    }

    private int getMaxCurrentStrength(final World worldIn, final BlockPos pos, final int strength) {
        if (worldIn.getBlockState(pos).getBlock() != this) {
            return strength;
        } else {
            final int i = worldIn.getBlockState(pos).getValue(POWER).intValue();
            return i > strength ? i : strength;
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!worldIn.isRemote) {
            if (this.canPlaceBlockAt(worldIn, pos)) {
                this.updateSurroundingRedstone(worldIn, pos, state);
            } else {
                this.dropBlockAsItem(worldIn, pos, state, 0);
                worldIn.setBlockToAir(pos);
            }
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Items.redstone;
    }

    public int isProvidingStrongPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return !this.canProvidePower ? 0 : this.isProvidingWeakPower(worldIn, pos, state, side);
    }

    public int isProvidingWeakPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        if (!this.canProvidePower) {
            return 0;
        } else {
            final int i = state.getValue(POWER).intValue();

            if (i == 0) {
                return 0;
            } else if (side == EnumFacing.UP) {
                return i;
            } else {
                final EnumSet<EnumFacing> enumset = EnumSet.noneOf(EnumFacing.class);

                for (final EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
                    if (this.func_176339_d(worldIn, pos, enumfacing)) {
                        enumset.add(enumfacing);
                    }
                }

                if (side.getAxis().isHorizontal() && enumset.isEmpty()) {
                    return i;
                } else if (enumset.contains(side) && !enumset.contains(side.rotateYCCW()) && !enumset.contains(side.rotateY())) {
                    return i;
                } else {
                    return 0;
                }
            }
        }
    }

    private boolean func_176339_d(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        final BlockPos blockpos = pos.offset(side);
        final IBlockState iblockstate = worldIn.getBlockState(blockpos);
        final Block block = iblockstate.getBlock();
        final boolean flag = block.isNormalCube();
        final boolean flag1 = worldIn.getBlockState(pos.up()).getBlock().isNormalCube();
        return !flag1 && flag && canConnectUpwardsTo(worldIn, blockpos.up()) || (canConnectTo(iblockstate, side) || (block == Blocks.powered_repeater && iblockstate.getValue(BlockRedstoneDiode.FACING) == side || !flag && canConnectUpwardsTo(worldIn, blockpos.down())));
    }

    protected static boolean canConnectUpwardsTo(final IBlockAccess worldIn, final BlockPos pos) {
        return canConnectUpwardsTo(worldIn.getBlockState(pos));
    }

    protected static boolean canConnectUpwardsTo(final IBlockState state) {
        return canConnectTo(state, null);
    }

    protected static boolean canConnectTo(final IBlockState blockState, final EnumFacing side) {
        final Block block = blockState.getBlock();

        if (block == Blocks.redstone_wire) {
            return true;
        } else if (Blocks.unpowered_repeater.isAssociated(block)) {
            final EnumFacing enumfacing = blockState.getValue(BlockRedstoneRepeater.FACING);
            return enumfacing == side || enumfacing.getOpposite() == side;
        } else {
            return block.canProvidePower() && side != null;
        }
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower() {
        return this.canProvidePower;
    }

    private int colorMultiplier(final int powerLevel) {
        final float f = (float) powerLevel / 15.0F;
        float f1 = f * 0.6F + 0.4F;

        if (powerLevel == 0) {
            f1 = 0.3F;
        }

        float f2 = f * f * 0.7F - 0.5F;
        float f3 = f * f * 0.6F - 0.7F;

        if (f2 < 0.0F) {
            f2 = 0.0F;
        }

        if (f3 < 0.0F) {
            f3 = 0.0F;
        }

        final int i = MathHelper.clamp_int((int) (f1 * 255.0F), 0, 255);
        final int j = MathHelper.clamp_int((int) (f2 * 255.0F), 0, 255);
        final int k = MathHelper.clamp_int((int) (f3 * 255.0F), 0, 255);
        return -16777216 | i << 16 | j << 8 | k;
    }

    public void randomDisplayTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        final int i = state.getValue(POWER).intValue();

        if (i != 0) {
            final double d0 = (double) pos.getX() + 0.5D + ((double) rand.nextFloat() - 0.5D) * 0.2D;
            final double d1 = (float) pos.getY() + 0.0625F;
            final double d2 = (double) pos.getZ() + 0.5D + ((double) rand.nextFloat() - 0.5D) * 0.2D;
            final float f = (float) i / 15.0F;
            final float f1 = f * 0.6F + 0.4F;
            final float f2 = Math.max(0.0F, f * f * 0.7F - 0.5F);
            final float f3 = Math.max(0.0F, f * f * 0.6F - 0.7F);
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, f1, f2, f3);
        }
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Items.redstone;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
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
        return new BlockState(this, NORTH, EAST, SOUTH, WEST, POWER);
    }

    enum EnumAttachPosition implements IStringSerializable {
        UP("up"),
        SIDE("side"),
        NONE("none");

        private final String name;

        EnumAttachPosition(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.getName();
        }

        public String getName() {
            return this.name;
        }
    }
}
