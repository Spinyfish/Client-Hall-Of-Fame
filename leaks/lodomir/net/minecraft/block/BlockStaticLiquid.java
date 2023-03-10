/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockDynamicLiquid;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class BlockStaticLiquid
extends BlockLiquid {
    protected BlockStaticLiquid(Material materialIn) {
        super(materialIn);
        this.setTickRandomly(false);
        if (materialIn == Material.lava) {
            this.setTickRandomly(true);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!this.checkForMixing(worldIn, pos, state)) {
            this.updateLiquid(worldIn, pos, state);
        }
    }

    private void updateLiquid(World worldIn, BlockPos pos, IBlockState state) {
        BlockDynamicLiquid blockdynamicliquid = BlockStaticLiquid.getFlowingBlock(this.blockMaterial);
        worldIn.setBlockState(pos, blockdynamicliquid.getDefaultState().withProperty(LEVEL, state.getValue(LEVEL)), 2);
        worldIn.scheduleUpdate(pos, blockdynamicliquid, this.tickRate(worldIn));
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        block5: {
            if (this.blockMaterial != Material.lava || !worldIn.getGameRules().getBoolean("doFireTick")) break block5;
            int i = rand.nextInt(3);
            if (i > 0) {
                BlockPos blockpos = pos;
                for (int j = 0; j < i; ++j) {
                    blockpos = blockpos.add(rand.nextInt(3) - 1, 1, rand.nextInt(3) - 1);
                    Block block = worldIn.getBlockState(blockpos).getBlock();
                    if (block.blockMaterial == Material.air) {
                        if (!this.isSurroundingBlockFlammable(worldIn, blockpos)) continue;
                        worldIn.setBlockState(blockpos, Blocks.fire.getDefaultState());
                        return;
                    }
                    if (!block.blockMaterial.blocksMovement()) continue;
                    return;
                }
            } else {
                for (int k = 0; k < 3; ++k) {
                    BlockPos blockpos1 = pos.add(rand.nextInt(3) - 1, 0, rand.nextInt(3) - 1);
                    if (!worldIn.isAirBlock(blockpos1.up()) || !this.getCanBlockBurn(worldIn, blockpos1)) continue;
                    worldIn.setBlockState(blockpos1.up(), Blocks.fire.getDefaultState());
                }
            }
        }
    }

    protected boolean isSurroundingBlockFlammable(World worldIn, BlockPos pos) {
        for (EnumFacing enumfacing : EnumFacing.values()) {
            if (!this.getCanBlockBurn(worldIn, pos.offset(enumfacing))) continue;
            return true;
        }
        return false;
    }

    private boolean getCanBlockBurn(World worldIn, BlockPos pos) {
        return worldIn.getBlockState(pos).getBlock().getMaterial().getCanBurn();
    }
}

