package net.minecraft.block;

import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.*;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.Random;

public class BlockPistonMoving extends BlockContainer {
    public static final PropertyDirection FACING = BlockPistonExtension.FACING;
    public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockPistonExtension.TYPE;

    public BlockPistonMoving() {
        super(Material.piston);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT));
        this.setHardness(-1.0F);
    }

    /**
     * Returns a new instance of a block's tile entity class. Called on placing the block.
     */
    public TileEntity createNewTileEntity(final World worldIn, final int meta) {
        return null;
    }

    public static TileEntity newTileEntity(final IBlockState state, final EnumFacing facing, final boolean extending, final boolean renderHead) {
        return new TileEntityPiston(state, facing, extending, renderHead);
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);

        if (tileentity instanceof TileEntityPiston) {
            ((TileEntityPiston) tileentity).clearPistonTileEntity();
        } else {
            super.breakBlock(worldIn, pos, state);
        }
    }

    public boolean canPlaceBlockAt(final World worldIn, final BlockPos pos) {
        return false;
    }

    /**
     * Check whether this Block can be placed on the given side
     */
    public boolean canPlaceBlockOnSide(final World worldIn, final BlockPos pos, final EnumFacing side) {
        return false;
    }

    /**
     * Called when a player destroys this Block
     */
    public void onBlockDestroyedByPlayer(final World worldIn, final BlockPos pos, final IBlockState state) {
        final BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());
        final IBlockState iblockstate = worldIn.getBlockState(blockpos);

        if (iblockstate.getBlock() instanceof BlockPistonBase && iblockstate.getValue(BlockPistonBase.EXTENDED).booleanValue()) {
            worldIn.setBlockToAir(blockpos);
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

    public boolean onBlockActivated(final World worldIn, final BlockPos pos, final IBlockState state, final EntityPlayer playerIn, final EnumFacing side, final float hitX, final float hitY, final float hitZ) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
            worldIn.setBlockToAir(pos);
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return null;
    }

    /**
     * Spawns this Block's drops into the World as EntityItems.
     *
     * @param chance  The chance that each Item is actually spawned (1.0 = always, 0.0 = never)
     * @param fortune The player's fortune level
     */
    public void dropBlockAsItemWithChance(final World worldIn, final BlockPos pos, final IBlockState state, final float chance, final int fortune) {
        if (!worldIn.isRemote) {
            final TileEntityPiston tileentitypiston = this.getTileEntity(worldIn, pos);

            if (tileentitypiston != null) {
                final IBlockState iblockstate = tileentitypiston.getPistonState();
                iblockstate.getBlock().dropBlockAsItem(worldIn, pos, iblockstate, 0);
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
        return null;
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!worldIn.isRemote) {
            worldIn.getTileEntity(pos);
        }
    }

    public AxisAlignedBB getCollisionBoundingBox(final World worldIn, final BlockPos pos, final IBlockState state) {
        final TileEntityPiston tileentitypiston = this.getTileEntity(worldIn, pos);

        if (tileentitypiston == null) {
            return null;
        } else {
            float f = tileentitypiston.getProgress(0.0F);

            if (tileentitypiston.isExtending()) {
                f = 1.0F - f;
            }

            return this.getBoundingBox(worldIn, pos, tileentitypiston.getPistonState(), f, tileentitypiston.getFacing());
        }
    }

    public void setBlockBoundsBasedOnState(final IBlockAccess worldIn, final BlockPos pos) {
        final TileEntityPiston tileentitypiston = this.getTileEntity(worldIn, pos);

        if (tileentitypiston != null) {
            final IBlockState iblockstate = tileentitypiston.getPistonState();
            final Block block = iblockstate.getBlock();

            if (block == this || block.getMaterial() == Material.air) {
                return;
            }

            float f = tileentitypiston.getProgress(0.0F);

            if (tileentitypiston.isExtending()) {
                f = 1.0F - f;
            }

            block.setBlockBoundsBasedOnState(worldIn, pos);

            if (block == Blocks.piston || block == Blocks.sticky_piston) {
                f = 0.0F;
            }

            final EnumFacing enumfacing = tileentitypiston.getFacing();
            this.minX = block.getBlockBoundsMinX() - (double) ((float) enumfacing.getFrontOffsetX() * f);
            this.minY = block.getBlockBoundsMinY() - (double) ((float) enumfacing.getFrontOffsetY() * f);
            this.minZ = block.getBlockBoundsMinZ() - (double) ((float) enumfacing.getFrontOffsetZ() * f);
            this.maxX = block.getBlockBoundsMaxX() - (double) ((float) enumfacing.getFrontOffsetX() * f);
            this.maxY = block.getBlockBoundsMaxY() - (double) ((float) enumfacing.getFrontOffsetY() * f);
            this.maxZ = block.getBlockBoundsMaxZ() - (double) ((float) enumfacing.getFrontOffsetZ() * f);
        }
    }

    public AxisAlignedBB getBoundingBox(final World worldIn, final BlockPos pos, final IBlockState extendingBlock, final float progress, final EnumFacing direction) {
        if (extendingBlock.getBlock() != this && extendingBlock.getBlock().getMaterial() != Material.air) {
            final AxisAlignedBB axisalignedbb = extendingBlock.getBlock().getCollisionBoundingBox(worldIn, pos, extendingBlock);

            if (axisalignedbb == null) {
                return null;
            } else {
                double d0 = axisalignedbb.minX;
                double d1 = axisalignedbb.minY;
                double d2 = axisalignedbb.minZ;
                double d3 = axisalignedbb.maxX;
                double d4 = axisalignedbb.maxY;
                double d5 = axisalignedbb.maxZ;

                if (direction.getFrontOffsetX() < 0) {
                    d0 -= (float) direction.getFrontOffsetX() * progress;
                } else {
                    d3 -= (float) direction.getFrontOffsetX() * progress;
                }

                if (direction.getFrontOffsetY() < 0) {
                    d1 -= (float) direction.getFrontOffsetY() * progress;
                } else {
                    d4 -= (float) direction.getFrontOffsetY() * progress;
                }

                if (direction.getFrontOffsetZ() < 0) {
                    d2 -= (float) direction.getFrontOffsetZ() * progress;
                } else {
                    d5 -= (float) direction.getFrontOffsetZ() * progress;
                }

                return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
            }
        } else {
            return null;
        }
    }

    private TileEntityPiston getTileEntity(final IBlockAccess worldIn, final BlockPos pos) {
        final TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityPiston ? (TileEntityPiston) tileentity : null;
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return null;
    }

    /**
     * Convert the given metadata into a BlockState for this Block
     */
    public IBlockState getStateFromMeta(final int meta) {
        return this.getDefaultState().withProperty(FACING, BlockPistonExtension.getFacing(meta)).withProperty(TYPE, (meta & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
    }

    /**
     * Convert the BlockState into the correct metadata value
     */
    public int getMetaFromState(final IBlockState state) {
        int i = 0;
        i = i | state.getValue(FACING).getIndex();

        if (state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
            i |= 8;
        }

        return i;
    }

    protected BlockState createBlockState() {
        return new BlockState(this, FACING, TYPE);
    }
}
