package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockFenceGate extends BlockDirectional {
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyBool POWERED = PropertyBool.create("powered");
    public static final PropertyBool IN_WALL = PropertyBool.create("in_wall");

    public BlockFenceGate(final BlockPlanks.EnumType p_i46394_1_) {
        super(Material.wood, p_i46394_1_.func_181070_c());
        this.setDefaultState(this.blockState.getBaseState().withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)).withProperty(IN_WALL, Boolean.valueOf(false)));
        this.setCreativeTab(CreativeTabs.tabRedstone);
    }

    /**
     * Get the actual Block state of this Block at the given position. This applies properties not visible in the
     * metadata, such as fence connections.
     */
    public IBlockState getActualState(IBlockState state, final IBlockAccess worldIn, final BlockPos pos) {
        final EnumFacing.Axis enumfacing$axis = state.getValue(FACING).getAxis();

        if (enumfacing$axis == EnumFacing.Axis.Z && (worldIn.getBlockState(pos.west()).getBlock() == Blocks.cobblestone_wall || worldIn.getBlockState(pos.east()).getBlock() == Blocks.cobblestone_wall) || enumfacing$axis == EnumFacing.Axis.X && (worldIn.getBlockState(pos.north()).getBlock() == Blocks.cobblestone_wall || worldIn.getBlockState(pos.south()).getBlock() == Blocks.cobblestone_wall)) {
            state = state.withProperty(IN_WALL, Boolean.valueOf(true));
        }

        return state;
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return worldIn.getBlockState(pos.down()).getBlock().getMaterial().isSolid() && super.canPlaceBlockAt(worldIn, pos);
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (state.getValue(OPEN).booleanValue()) {
            return null;
        } else {
            final EnumFacing.Axis enumfacing$axis = state.getValue(FACING).getAxis();
            return enumfacing$axis == EnumFacing.Axis.Z ? new AxisAlignedBB(pos.getX(), pos.getY(), (float) pos.getZ() + 0.375F, pos.getX() + 1, (float) pos.getY() + 1.5F, (float) pos.getZ() + 0.625F) : new AxisAlignedBB((float) pos.getX() + 0.375F, pos.getY(), pos.getZ(), (float) pos.getX() + 0.625F, (float) pos.getY() + 1.5F, pos.getZ() + 1);
        }
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        final EnumFacing.Axis enumfacing$axis = worldIn.getBlockState(pos).getValue(FACING).getAxis();

        if (enumfacing$axis == EnumFacing.Axis.Z) {
            this.setBlockBounds(0.0F, 0.0F, 0.375F, 1.0F, 1.0F, 0.625F);
        } else {
            this.setBlockBounds(0.375F, 0.0F, 0.0F, 0.625F, 1.0F, 1.0F);
        }
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
        return worldIn.getBlockState(pos).getValue(OPEN).booleanValue();
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        return this.getDefaultState().withProperty(FACING, placer.getHorizontalFacing()).withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)).withProperty(IN_WALL, Boolean.valueOf(false));
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (state.getValue(OPEN).booleanValue()) {
            state = state.withProperty(OPEN, Boolean.valueOf(false));
            worldIn.setBlockState(pos, state, 2);
        } else {
            final EnumFacing enumfacing = EnumFacing.fromAngle(playerIn.rotationYaw);

            if (state.getValue(FACING) == enumfacing.getOpposite()) {
                state = state.withProperty(FACING, enumfacing);
            }

            state = state.withProperty(OPEN, Boolean.valueOf(true));
            worldIn.setBlockState(pos, state, 2);
        }

        worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN).booleanValue() ? 1003 : 1006, pos, 0);
        return true;
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!worldIn.isRemote) {
            final boolean flag = worldIn.isBlockPowered(pos);

            if (flag || neighborBlock.canProvidePower()) {
                if (flag && !state.getValue(OPEN).booleanValue() && !state.getValue(POWERED).booleanValue()) {
                    worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(true)).withProperty(POWERED, Boolean.valueOf(true)), 2);
                    worldIn.playAuxSFXAtEntity(null, 1003, pos, 0);
                } else if (!flag && state.getValue(OPEN).booleanValue() && state.getValue(POWERED).booleanValue()) {
                    worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(false)).withProperty(POWERED, Boolean.valueOf(false)), 2);
                    worldIn.playAuxSFXAtEntity(null, 1006, pos, 0);
                } else if (flag != state.getValue(POWERED).booleanValue()) {
                    worldIn.setBlockState(pos, state.withProperty(POWERED, Boolean.valueOf(flag)), 2);
                }
            }
        }
    }

    public boolean shouldSideBeRendered(final IBlockAccess worldIn, final BlockPos pos, final EnumFacing side) {
        return true;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(FACING, EnumFacing.getHorizontal(meta)).withProperty(OPEN, Boolean.valueOf((meta & 4) != 0)).withProperty(POWERED, Boolean.valueOf((meta & 8) != 0));
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

        if (state.getValue(OPEN).booleanValue()) {
            i |= 4;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, OPEN, POWERED, IN_WALL);
    }
}
