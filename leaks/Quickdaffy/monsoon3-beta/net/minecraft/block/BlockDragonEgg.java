/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockFalling;
import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityFallingBlock;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockDragonEgg
extends Block {
    public BlockDragonEgg() {
        super(Material.dragonEgg, MapColor.blackColor);
        this.setBlockBounds(0.0625f, 0.0f, 0.0625f, 0.9375f, 1.0f, 0.9375f);
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        this.checkFall(worldIn, pos);
    }

    private void checkFall(World worldIn, BlockPos pos) {
        if (BlockFalling.canFallInto(worldIn, pos.down()) && pos.getY() >= 0) {
            int i = 32;
            if (!BlockFalling.fallInstantly && worldIn.isAreaLoaded(pos.add(-i, -i, -i), pos.add(i, i, i))) {
                worldIn.spawnEntityInWorld(new EntityFallingBlock(worldIn, (float)pos.getX() + 0.5f, pos.getY(), (float)pos.getZ() + 0.5f, this.getDefaultState()));
            } else {
                worldIn.setBlockToAir(pos);
                BlockPos blockpos = pos;
                while (BlockFalling.canFallInto(worldIn, blockpos) && blockpos.getY() > 0) {
                    blockpos = blockpos.down();
                }
                if (blockpos.getY() > 0) {
                    worldIn.setBlockState(blockpos, this.getDefaultState(), 2);
                }
            }
        }
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumFacing side, float hitX, float hitY, float hitZ) {
        this.teleport(worldIn, pos);
        return true;
    }

    @Override
    public void onBlockClicked(World worldIn, BlockPos pos, EntityPlayer playerIn) {
        this.teleport(worldIn, pos);
    }

    private void teleport(World worldIn, BlockPos pos) {
        IBlockState iblockstate = worldIn.getBlockState(pos);
        if (iblockstate.getBlock() == this) {
            for (int i = 0; i < 1000; ++i) {
                BlockPos blockpos = pos.add(worldIn.rand.nextInt(16) - worldIn.rand.nextInt(16), worldIn.rand.nextInt(8) - worldIn.rand.nextInt(8), worldIn.rand.nextInt(16) - worldIn.rand.nextInt(16));
                if (worldIn.getBlockState((BlockPos)blockpos).getBlock().blockMaterial != Material.air) continue;
                if (worldIn.isRemote) {
                    for (int j = 0; j < 128; ++j) {
                        double d0 = worldIn.rand.nextDouble();
                        float f = (worldIn.rand.nextFloat() - 0.5f) * 0.2f;
                        float f1 = (worldIn.rand.nextFloat() - 0.5f) * 0.2f;
                        float f2 = (worldIn.rand.nextFloat() - 0.5f) * 0.2f;
                        double d1 = (double)blockpos.getX() + (double)(pos.getX() - blockpos.getX()) * d0 + (worldIn.rand.nextDouble() - 0.5) * 1.0 + 0.5;
                        double d2 = (double)blockpos.getY() + (double)(pos.getY() - blockpos.getY()) * d0 + worldIn.rand.nextDouble() * 1.0 - 0.5;
                        double d3 = (double)blockpos.getZ() + (double)(pos.getZ() - blockpos.getZ()) * d0 + (worldIn.rand.nextDouble() - 0.5) * 1.0 + 0.5;
                        worldIn.spawnParticle(EnumParticleTypes.PORTAL, d1, d2, d3, (double)f, (double)f1, (double)f2, new int[0]);
                    }
                } else {
                    worldIn.setBlockState(blockpos, iblockstate, 2);
                    worldIn.setBlockToAir(pos);
                }
                return;
            }
        }
    }

    @Override
    public int tickRate(World worldIn) {
        return 5;
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
        return true;
    }

    @Override
    public Item getItem(World worldIn, BlockPos pos) {
        return null;
    }
}

