package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockTrapDoor extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);
    public static final PropertyBool OPEN = PropertyBool.create("open");
    public static final PropertyEnum<BlockTrapDoor.DoorHalf> HALF = PropertyEnum.create("half", BlockTrapDoor.DoorHalf.class);

    protected BlockTrapDoor(final Material materialIn) {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(OPEN, Boolean.valueOf(false)).withProperty(HALF, BlockTrapDoor.DoorHalf.BOTTOM));
        final float f = 0.5F;
        final float f1 = 1.0F;
        this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
        this.setCreativeTab(CreativeTabs.tabRedstone);
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
        return !worldIn.getBlockState(pos).getValue(OPEN).booleanValue();
    }

    public AxisAlignedBB getSelectedBoundingBox(final World worldIn, final BlockPos pos) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getSelectedBoundingBox(worldIn, pos);
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.getCollisionBoundingBox(worldIn, pos, state);
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        this.setBounds(worldIn.getBlockState(pos));
    }

    /**
     * Sets the block's bounds for rendering it as an item
     */
    public void setBlockBoundsForItemRender() {
        final float f = 0.1875F;
        this.setBlockBounds(0.0F, 0.40625F, 0.0F, 1.0F, 0.59375F, 1.0F);
    }

    public void setBounds(final IBlockState state) {
        if (state.getBlock() == this) {
            final boolean flag = state.getValue(HALF) == BlockTrapDoor.DoorHalf.TOP;
            final Boolean obool = state.getValue(OPEN);
            final EnumFacing enumfacing = state.getValue(FACING);
            final float f = 0.1875F;

            if (flag) {
                this.setBlockBounds(0.0F, 0.8125F, 0.0F, 1.0F, 1.0F, 1.0F);
            } else {
                this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 0.1875F, 1.0F);
            }

            if (obool.booleanValue()) {
                if (enumfacing == EnumFacing.NORTH) {
                    this.setBlockBounds(0.0F, 0.0F, 0.8125F, 1.0F, 1.0F, 1.0F);
                }

                if (enumfacing == EnumFacing.SOUTH) {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, 0.1875F);
                }

                if (enumfacing == EnumFacing.WEST) {
                    this.setBlockBounds(0.8125F, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }

                if (enumfacing == EnumFacing.EAST) {
                    this.setBlockBounds(0.0F, 0.0F, 0.0F, 0.1875F, 1.0F, 1.0F);
                }
            }
        }
    }

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (this.blockMaterial == Material.iron) {
            return true;
        } else {
            state = state.cycleProperty(OPEN);
            worldIn.setBlockState(pos, state, 2);
            worldIn.playAuxSFXAtEntity(playerIn, state.getValue(OPEN).booleanValue() ? 1003 : 1006, pos, 0);
            return true;
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!worldIn.isRemote) {
            final BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());

            if (!isValidSupportBlock(worldIn.getBlockState(blockpos).getBlock())) {
                worldIn.setBlockToAir(pos);
                this.dropBlockAsItem(worldIn, pos, state, 0);
            } else {
                final boolean flag = worldIn.isBlockPowered(pos);

                if (flag || neighborBlock.canProvidePower()) {
                    final boolean flag1 = state.getValue(OPEN).booleanValue();

                    if (flag1 != flag) {
                        worldIn.setBlockState(pos, state.withProperty(OPEN, Boolean.valueOf(flag)), 2);
                        worldIn.playAuxSFXAtEntity(null, flag ? 1003 : 1006, pos, 0);
                    }
                }
            }
        }
    }

    /**
     * Ray traces through the blocks collision from start vector to end vector returning a ray trace hit.
     *
     * @param start The start vector
     * @param end   The end vector
     */
    public MovingObjectPosition collisionRayTrace(final World worldIn, final BlockPos pos, final Vec3 start, final Vec3 end) {
        this.setBlockBoundsBasedOnState(worldIn, pos);
        return super.collisionRayTrace(worldIn, pos, start, end);
    }

    /**
     * Called by ItemBlocks just before a block is actually set in the world, to allow for adjustments to the
     * IBlockstate
     */
    public IBlockState onBlockPlaced(final World worldIn, final BlockPos pos, final EnumFacing facing, final float hitX, final float hitY, final float hitZ, final int meta, final EntityLivingBase placer) {
        IBlockState iblockstate = this.getDefaultState();

        if (facing.getAxis().isHorizontal()) {
            iblockstate = iblockstate.withProperty(FACING, facing).withProperty(OPEN, Boolean.valueOf(false));
            iblockstate = iblockstate.withProperty(HALF, hitY > 0.5F ? BlockTrapDoor.DoorHalf.TOP : BlockTrapDoor.DoorHalf.BOTTOM);
        }

        return iblockstate;
    }

    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
        return !side.getAxis().isVertical() && isValidSupportBlock(worldIn.getBlockState(pos.offset(side.getOpposite())).getBlock());
    }

    protected static EnumFacing getFacing(final int meta) {
        switch (meta & 3) {
            case 0:
                return EnumFacing.NORTH;

            case 1:
                return EnumFacing.SOUTH;

            case 2:
                return EnumFacing.WEST;

            case 3:
            default:
                return EnumFacing.EAST;
        }
    }

    protected static int getMetaForFacing(final EnumFacing facing) {
        switch (facing) {
            case NORTH:
                return 0;

            case SOUTH:
                return 1;

            case WEST:
                return 2;

            case EAST:
            default:
                return 3;
        }
    }

    private static boolean isValidSupportBlock(final Block blockIn) {
        return blockIn.blockMaterial.isOpaque() && blockIn.isFullCube() || blockIn == Blocks.glowstone || blockIn instanceof BlockSlab || blockIn instanceof BlockStairs;
    }

    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(FACING, getFacing(meta)).withProperty(OPEN, Boolean.valueOf((meta & 4) != 0)).withProperty(HALF, (meta & 8) == 0 ? BlockTrapDoor.DoorHalf.BOTTOM : BlockTrapDoor.DoorHalf.TOP);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | getMetaForFacing(state.getValue(FACING));

        if (state.getValue(OPEN).booleanValue()) {
            i |= 4;
        }

        if (state.getValue(HALF) == BlockTrapDoor.DoorHalf.TOP) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, OPEN, HALF);
    }

    public enum DoorHalf implements IStringSerializable {
        TOP("top"),
        BOTTOM("bottom");

        private final String name;

        DoorHalf(final String name) {
            this.name = name;
        }

        public String toString() {
            return this.name;
        }

        public String getName() {
            return this.name;
        }
    }
}
