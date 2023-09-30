/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Predicate
 */
package net.minecraft.block;

import com.google.common.base.Predicate;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFence;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class BlockTorch
extends Block {
    public static final PropertyDirection FACING = PropertyDirection.create("facing", new Predicate<EnumFacing>(){

        public boolean apply(EnumFacing p_apply_1_) {
            return p_apply_1_ != EnumFacing.DOWN;
        }
    });

    protected BlockTorch() {
        super(Material.circuits);
        this.setDefaultState(this.blockState.getBaseState().withProperty(FACING, EnumFacing.UP));
        this.setTickRandomly(true);
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public AxisAlignedBB getCollisionBoundingBox(World worldIn, BlockPos pos, IBlockState state) {
        return null;
    }

    @Override
    public boolean isOpaqueCube() {
        return false;
    }

    @Override
    public boolean isFullCube() {
        return false;
    }

    private boolean canPlaceOn(World worldIn, BlockPos pos) {
        if (World.doesBlockHaveSolidTopSurface(worldIn, pos)) {
            return true;
        }
        Block block = worldIn.getBlockState(pos).getBlock();
        return block instanceof BlockFence || block == Blocks.glass || block == Blocks.cobblestone_wall || block == Blocks.stained_glass;
    }

    @Override
    public boolean canPlaceBlockAt(World worldIn, BlockPos pos) {
        for (EnumFacing enumfacing : FACING.getAllowedValues()) {
            if (!this.canPlaceAt(worldIn, pos, enumfacing)) continue;
            return true;
        }
        return false;
    }

    private boolean canPlaceAt(World worldIn, BlockPos pos, EnumFacing facing) {
        BlockPos blockpos = pos.offset(facing.getOpposite());
        boolean flag = facing.getAxis().isHorizontal();
        return flag && worldIn.isBlockNormalCube(blockpos, true) || facing.equals(EnumFacing.UP) && this.canPlaceOn(worldIn, blockpos);
    }

    @Override
    public IBlockState onBlockPlaced(World worldIn, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        if (this.canPlaceAt(worldIn, pos, facing)) {
            return this.getDefaultState().withProperty(FACING, facing);
        }
        for (EnumFacing enumfacing : EnumFacing.Plane.HORIZONTAL) {
            if (!worldIn.isBlockNormalCube(pos.offset(enumfacing.getOpposite()), true)) continue;
            return this.getDefaultState().withProperty(FACING, enumfacing);
        }
        return this.getDefaultState();
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        this.checkForDrop(worldIn, pos, state);
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        this.onNeighborChangeInternal(worldIn, pos, state);
    }

    protected boolean onNeighborChangeInternal(World worldIn, BlockPos pos, IBlockState state) {
        if (!this.checkForDrop(worldIn, pos, state)) {
            return true;
        }
        EnumFacing enumfacing = state.getValue(FACING);
        EnumFacing.Axis enumfacing$axis = enumfacing.getAxis();
        EnumFacing enumfacing1 = enumfacing.getOpposite();
        boolean flag = false;
        if (enumfacing$axis.isHorizontal() && !worldIn.isBlockNormalCube(pos.offset(enumfacing1), true)) {
            flag = true;
        } else if (enumfacing$axis.isVertical() && !this.canPlaceOn(worldIn, pos.offset(enumfacing1))) {
            flag = true;
        }
        if (flag) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
            return true;
        }
        return false;
    }

    protected boolean checkForDrop(World worldIn, BlockPos pos, IBlockState state) {
        if (state.getBlock() == this && this.canPlaceAt(worldIn, pos, state.getValue(FACING))) {
            return true;
        }
        if (worldIn.getBlockState(pos).getBlock() == this) {
            this.dropBlockAsItem(worldIn, pos, state, 0);
            worldIn.setBlockToAir(pos);
        }
        return false;
    }

    @Override
    public MovingObjectPosition collisionRayTrace(World worldIn, BlockPos pos, Vec3 start, Vec3 end) {
        EnumFacing enumfacing = worldIn.getBlockState(pos).getValue(FACING);
        float f = 0.15f;
        if (enumfacing == EnumFacing.EAST) {
            this.setBlockBounds(0.0f, 0.2f, 0.5f - f, f * 2.0f, 0.8f, 0.5f + f);
        } else if (enumfacing == EnumFacing.WEST) {
            this.setBlockBounds(1.0f - f * 2.0f, 0.2f, 0.5f - f, 1.0f, 0.8f, 0.5f + f);
        } else if (enumfacing == EnumFacing.SOUTH) {
            this.setBlockBounds(0.5f - f, 0.2f, 0.0f, 0.5f + f, 0.8f, f * 2.0f);
        } else if (enumfacing == EnumFacing.NORTH) {
            this.setBlockBounds(0.5f - f, 0.2f, 1.0f - f * 2.0f, 0.5f + f, 0.8f, 1.0f);
        } else {
            f = 0.1f;
            this.setBlockBounds(0.5f - f, 0.0f, 0.5f - f, 0.5f + f, 0.6f, 0.5f + f);
        }
        return super.collisionRayTrace(worldIn, pos, start, end);
    }

    @Override
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        EnumFacing enumfacing = state.getValue(FACING);
        double d0 = (double)pos.getX() + 0.5;
        double d1 = (double)pos.getY() + 0.7;
        double d2 = (double)pos.getZ() + 0.5;
        double d3 = 0.22;
        double d4 = 0.27;
        if (enumfacing.getAxis().isHorizontal()) {
            EnumFacing enumfacing1 = enumfacing.getOpposite();
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0 + d4 * (double)enumfacing1.getFrontOffsetX(), d1 + d3, d2 + d4 * (double)enumfacing1.getFrontOffsetZ(), 0.0, 0.0, 0.0, new int[0]);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0 + d4 * (double)enumfacing1.getFrontOffsetX(), d1 + d3, d2 + d4 * (double)enumfacing1.getFrontOffsetZ(), 0.0, 0.0, 0.0, new int[0]);
        } else {
            worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0, 0.0, 0.0, new int[0]);
            worldIn.spawnParticle(EnumParticleTypes.FLAME, d0, d1, d2, 0.0, 0.0, 0.0, new int[0]);
        }
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT;
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        IBlockState iblockstate = this.getDefaultState();
        switch (meta) {
            case 1: {
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.EAST);
                break;
            }
            case 2: {
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.WEST);
                break;
            }
            case 3: {
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.SOUTH);
                break;
            }
            case 4: {
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.NORTH);
                break;
            }
            default: {
                iblockstate = iblockstate.withProperty(FACING, EnumFacing.UP);
            }
        }
        return iblockstate;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        int i = 0;
        switch (state.getValue(FACING)) {
            case EAST: {
                i |= 1;
                break;
            }
            case WEST: {
                i |= 2;
                break;
            }
            case SOUTH: {
                i |= 3;
                break;
            }
            case NORTH: {
                i |= 4;
                break;
            }
            default: {
                i |= 5;
            }
        }
        return i;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, FACING);
    }
}

