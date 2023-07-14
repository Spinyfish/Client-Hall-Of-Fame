/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.block.BlockPistonExtension;
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
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPistonMoving
extends BlockContainer {
    public static final PropertyDirection FACING = BlockPistonExtension.FACING;
    public static final PropertyEnum<BlockPistonExtension.EnumPistonType> TYPE = BlockPistonExtension.TYPE;

    public BlockPistonMoving() {
        super(Material.piston);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.NORTH).withProperty(TYPE, BlockPistonExtension.EnumPistonType.DEFAULT));
        this.setHardness(-1.0f);
    }

    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta) {
        return null;
    }

    public static TileEntity newTileEntity(IBlockState state, EnumFacing facing, boolean extending, boolean renderHead) {
        return new TileEntityPiston(state, facing, extending, renderHead);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        if (tileentity instanceof TileEntityPiston) {
            ((TileEntityPiston)tileentity).clearPistonTileEntity();
        } else {
            super.breakBlock(worldIn, pos, state);
        }
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        return false;
    }

    @Override
    public boolean canPlaceBlockOnSide(World worldIn, BlockPos pos, EnumFacing side) {
        return false;
    }

    @Override
    public void onBlockDestroyedByPlayer(World worldIn, BlockPos pos, IBlockState state) {
        BlockPos blockpos = pos.offset(state.getValue(FACING).getOpposite());
        IBlockState iblockstate = worldIn.getBlockState(blockpos);
        if (iblockstate.getBlock() instanceof BlockPistonBase && iblockstate.getValue(BlockPistonBase.EXTENDED).booleanValue()) {
            worldIn.setBlockToAir(blockpos);
        }
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        if (!worldIn.isRemote && worldIn.getTileEntity(pos) == null) {
            worldIn.setBlockToAir(pos);
            return true;
        }
        return false;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return null;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        TileEntityPiston tileentitypiston;
        if (!worldIn.isRemote && (tileentitypiston = this.getTileEntity(worldIn, pos)) != null) {
            IBlockState iblockstate = tileentitypiston.getPistonState();
            iblockstate.getBlock().dropBlockAsItem(worldIn, pos, iblockstate, 0);
        }
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
        return null;
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!worldIn.isRemote) {
            worldIn.getTileEntity(pos);
        }
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        TileEntityPiston tileentitypiston = this.getTileEntity(worldIn, pos);
        if (tileentitypiston == null) {
            return null;
        }
        float f = tileentitypiston.getProgress(0.0f);
        if (tileentitypiston.isExtending()) {
            f = 1.0f - f;
        }
        return this.getBoundingBox(worldIn, pos, tileentitypiston.getPistonState(), f, tileentitypiston.getFacing());
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        TileEntityPiston tileentitypiston = this.getTileEntity(worldIn, pos);
        if (tileentitypiston != null) {
            IBlockState iblockstate = tileentitypiston.getPistonState();
            Block block = iblockstate.getBlock();
            if (block == this || block.getMaterial() == Material.air) {
                return;
            }
            float f = tileentitypiston.getProgress(0.0f);
            if (tileentitypiston.isExtending()) {
                f = 1.0f - f;
            }
            block.setBlockBoundsBasedOnState(worldIn, pos);
            if (block == Blocks.piston || block == Blocks.sticky_piston) {
                f = 0.0f;
            }
            EnumFacing enumfacing = tileentitypiston.getFacing();
            this.minX = block.getBlockBoundsMinX() - (double)((float)enumfacing.getFrontOffsetX() * f);
            this.minY = block.getBlockBoundsMinY() - (double)((float)enumfacing.getFrontOffsetY() * f);
            this.minZ = block.getBlockBoundsMinZ() - (double)((float)enumfacing.getFrontOffsetZ() * f);
            this.maxX = block.getBlockBoundsMaxX() - (double)((float)enumfacing.getFrontOffsetX() * f);
            this.maxY = block.getBlockBoundsMaxY() - (double)((float)enumfacing.getFrontOffsetY() * f);
            this.maxZ = block.getBlockBoundsMaxZ() - (double)((float)enumfacing.getFrontOffsetZ() * f);
        }
    }

    public AxisAlignedBB getBoundingBox(World worldIn, BlockPos pos, IBlockState extendingBlock, float progress, EnumFacing direction) {
        if (extendingBlock.getBlock() != this && extendingBlock.getBlock().getMaterial() != Material.air) {
            AxisAlignedBB axisalignedbb = extendingBlock.getBlock().getCollisionBoundingBox(worldIn, pos, extendingBlock);
            if (axisalignedbb == null) {
                return null;
            }
            double d0 = axisalignedbb.minX;
            double d1 = axisalignedbb.minY;
            double d2 = axisalignedbb.minZ;
            double d3 = axisalignedbb.maxX;
            double d4 = axisalignedbb.maxY;
            double d5 = axisalignedbb.maxZ;
            if (direction.getFrontOffsetX() < 0) {
                d0 -= (double)((float)direction.getFrontOffsetX() * progress);
            } else {
                d3 -= (double)((float)direction.getFrontOffsetX() * progress);
            }
            if (direction.getFrontOffsetY() < 0) {
                d1 -= (double)((float)direction.getFrontOffsetY() * progress);
            } else {
                d4 -= (double)((float)direction.getFrontOffsetY() * progress);
            }
            if (direction.getFrontOffsetZ() < 0) {
                d2 -= (double)((float)direction.getFrontOffsetZ() * progress);
            } else {
                d5 -= (double)((float)direction.getFrontOffsetZ() * progress);
            }
            return new AxisAlignedBB(d0, d1, d2, d3, d4, d5);
        }
        return null;
    }

    private TileEntityPiston getTileEntity(IBlockAccess worldIn, BlockPos pos) {
        TileEntity tileentity = worldIn.getTileEntity(pos);
        return tileentity instanceof TileEntityPiston ? (TileEntityPiston)tileentity : null;
    }

    @Override
    public Item getItem(World worldIn, BlockPos pos) {
        return null;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        return this.getDefaultState().withProperty(FACING, BlockPistonExtension.getFacing(meta)).withProperty(TYPE, (meta & 8) > 0 ? BlockPistonExtension.EnumPistonType.STICKY : BlockPistonExtension.EnumPistonType.DEFAULT);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        i |= state.getValue(FACING).getIndex();
        if (state.getValue(TYPE) == BlockPistonExtension.EnumPistonType.STICKY) {
            i |= 8;
        }
        return i;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING, TYPE);
    }
}

