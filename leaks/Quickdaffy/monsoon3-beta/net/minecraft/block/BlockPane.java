/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.List;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockState;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumWorldBlockLayer;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockPane
extends Block {
    public static final PropertyBool NORTH = PropertyBool.create("north");
    public static final PropertyBool EAST = PropertyBool.create("east");
    public static final PropertyBool SOUTH = PropertyBool.create("south");
    public static final PropertyBool WEST = PropertyBool.create("west");
    private final boolean canDrop;

    protected BlockPane(Material materialIn, boolean canDrop) {
        super(materialIn);
        this.setDefaultState(this.blockState.getBaseState().withProperty(NORTH, false).withProperty(EAST, false).withProperty(SOUTH, false).withProperty(WEST, false));
        this.canDrop = canDrop;
        this.setCreativeTab(CreativeTabs.tabDecorations);
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos) {
        return state.withProperty(NORTH, this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()).getBlock())).withProperty(SOUTH, this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()).getBlock())).withProperty(WEST, this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()).getBlock())).withProperty(EAST, this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()).getBlock()));
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return !this.canDrop ? null : super.getItemDropped(state, rand, fortune);
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
    public boolean shouldSideBeRendered(IBlockAccess worldIn, BlockPos pos, EnumFacing side) {
        return worldIn.getBlockState(pos).getBlock() == this ? false : super.shouldSideBeRendered(worldIn, pos, side);
    }

    @Override
    public void addCollisionBoxesToList(World worldIn, BlockPos pos, IBlockState state, AxisAlignedBB mask, List<AxisAlignedBB> list, Entity collidingEntity) {
        boolean flag = this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()).getBlock());
        boolean flag1 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()).getBlock());
        boolean flag2 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()).getBlock());
        boolean flag3 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()).getBlock());
        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
            if (flag2) {
                this.setBlockBounds(0.0f, 0.0f, 0.4375f, 0.5f, 1.0f, 0.5625f);
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            } else if (flag3) {
                this.setBlockBounds(0.5f, 0.0f, 0.4375f, 1.0f, 1.0f, 0.5625f);
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            }
        } else {
            this.setBlockBounds(0.0f, 0.0f, 0.4375f, 1.0f, 1.0f, 0.5625f);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }
        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
            if (flag) {
                this.setBlockBounds(0.4375f, 0.0f, 0.0f, 0.5625f, 1.0f, 0.5f);
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            } else if (flag1) {
                this.setBlockBounds(0.4375f, 0.0f, 0.5f, 0.5625f, 1.0f, 1.0f);
                super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
            }
        } else {
            this.setBlockBounds(0.4375f, 0.0f, 0.0f, 0.5625f, 1.0f, 1.0f);
            super.addCollisionBoxesToList(worldIn, pos, state, mask, list, collidingEntity);
        }
    }

    @Override
    public void setBlockBoundsForItemRender() {
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }

    @Override
    public void setBlockBoundsBasedOnState(IBlockAccess worldIn, BlockPos pos) {
        float f = 0.4375f;
        float f1 = 0.5625f;
        float f2 = 0.4375f;
        float f3 = 0.5625f;
        boolean flag = this.canPaneConnectToBlock(worldIn.getBlockState(pos.north()).getBlock());
        boolean flag1 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.south()).getBlock());
        boolean flag2 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.west()).getBlock());
        boolean flag3 = this.canPaneConnectToBlock(worldIn.getBlockState(pos.east()).getBlock());
        if ((!flag2 || !flag3) && (flag2 || flag3 || flag || flag1)) {
            if (flag2) {
                f = 0.0f;
            } else if (flag3) {
                f1 = 1.0f;
            }
        } else {
            f = 0.0f;
            f1 = 1.0f;
        }
        if ((!flag || !flag1) && (flag2 || flag3 || flag || flag1)) {
            if (flag) {
                f2 = 0.0f;
            } else if (flag1) {
                f3 = 1.0f;
            }
        } else {
            f2 = 0.0f;
            f3 = 1.0f;
        }
        this.setBlockBounds(f, 0.0f, f2, f1, 1.0f, f3);
    }

    public final boolean canPaneConnectToBlock(Block blockIn) {
        return blockIn.isFullBlock() || blockIn == this || blockIn == Blocks.glass || blockIn == Blocks.stained_glass || blockIn == Blocks.stained_glass_pane || blockIn instanceof BlockPane;
    }

    @Override
    protected boolean canSilkHarvest() {
        return true;
    }

    @Override
    public EnumWorldBlockLayer getBlockLayer() {
        return EnumWorldBlockLayer.CUTOUT_MIPPED;
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return 0;
    }

    @Override
    protected BlockState createBlockState() {
        return new BlockState(this, NORTH, EAST, WEST, SOUTH);
    }
}

