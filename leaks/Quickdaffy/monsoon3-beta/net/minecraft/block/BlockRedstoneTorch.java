/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 */
package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockTorch;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

public class BlockRedstoneTorch
extends BlockTorch {
    private static Map<World, List<Toggle>> toggles = Maps.newHashMap();
    private final boolean isOn;

    private boolean isBurnedOut(World worldIn, BlockPos pos, boolean turnOff) {
        if (!toggles.containsKey(worldIn)) {
            toggles.put(worldIn, Lists.newArrayList());
        }
        List<Toggle> list = toggles.get(worldIn);
        if (turnOff) {
            list.add(new Toggle(pos, worldIn.getTotalWorldTime()));
        }
        int i = 0;
        for (int j = 0; j < list.size(); ++j) {
            Toggle blockredstonetorch$toggle = list.get(j);
            if (!blockredstonetorch$toggle.pos.equals(pos) || ++i < 8) continue;
            return true;
        }
        return false;
    }

    protected BlockRedstoneTorch(boolean isOn) {
        this.isOn = isOn;
        this.setTickRandomly(true);
        this.setCreativeTab(null);
    }

    @Override
    public int tickRate(World worldIn) {
        return 2;
    }

    @Override
    public void onBlockAdded(World worldIn, BlockPos pos, IBlockState state) {
        if (this.isOn) {
            for (EnumFacing enumfacing : EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state) {
        if (this.isOn) {
            for (EnumFacing enumfacing : EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }
        }
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        return this.isOn && state.getValue(FACING) != side ? 15 : 0;
    }

    private boolean shouldBeOff(World worldIn, BlockPos pos, IBlockState state) {
        EnumFacing enumfacing = state.getValue(FACING).getOpposite();
        return worldIn.isSidePowered(pos.offset(enumfacing), enumfacing);
    }

    @Override
    public void randomTick(World worldIn, BlockPos pos, IBlockState state, Random random) {
    }

    @Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        boolean flag = this.shouldBeOff(worldIn, pos, state);
        List<Toggle> list = toggles.get(worldIn);
        while (list != null && !list.isEmpty() && worldIn.getTotalWorldTime() - list.get((int)0).time > 60L) {
            list.remove(0);
        }
        if (this.isOn) {
            if (flag) {
                worldIn.setBlockState(pos, Blocks.unlit_redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
                if (this.isBurnedOut(worldIn, pos, true)) {
                    worldIn.playSoundEffect((float)pos.getX() + 0.5f, (float)pos.getY() + 0.5f, (float)pos.getZ() + 0.5f, "random.fizz", 0.5f, 2.6f + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8f);
                    for (int i = 0; i < 5; ++i) {
                        double d0 = (double)pos.getX() + rand.nextDouble() * 0.6 + 0.2;
                        double d1 = (double)pos.getY() + rand.nextDouble() * 0.6 + 0.2;
                        double d2 = (double)pos.getZ() + rand.nextDouble() * 0.6 + 0.2;
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0, 0.0, 0.0, new int[0]);
                    }
                    worldIn.scheduleUpdate(pos, worldIn.getBlockState(pos).getBlock(), 160);
                }
            }
        } else if (!flag && !this.isBurnedOut(worldIn, pos, false)) {
            worldIn.setBlockState(pos, Blocks.redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
        }
    }

    @Override
    public void onNeighborBlockChange(World worldIn, BlockPos pos, IBlockState state, Block neighborBlock) {
        if (!this.onNeighborChangeInternal(worldIn, pos, state) && this.isOn == this.shouldBeOff(worldIn, pos, state)) {
            worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
        }
    }

    @Override
    public int isProvidingStrongPower(IBlockAccess worldIn, BlockPos pos, IBlockState state, EnumFacing side) {
        return side == EnumFacing.DOWN ? this.isProvidingWeakPower(worldIn, pos, state, side) : 0;
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune) {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public void randomDisplayTick(World worldIn, BlockPos pos, IBlockState state, Random rand) {
        if (this.isOn) {
            double d0 = (double)pos.getX() + 0.5 + (rand.nextDouble() - 0.5) * 0.2;
            double d1 = (double)pos.getY() + 0.7 + (rand.nextDouble() - 0.5) * 0.2;
            double d2 = (double)pos.getZ() + 0.5 + (rand.nextDouble() - 0.5) * 0.2;
            EnumFacing enumfacing = state.getValue(FACING);
            if (enumfacing.getAxis().isHorizontal()) {
                EnumFacing enumfacing1 = enumfacing.getOpposite();
                double d3 = 0.27;
                d0 += 0.27 * (double)enumfacing1.getFrontOffsetX();
                d1 += 0.22;
                d2 += 0.27 * (double)enumfacing1.getFrontOffsetZ();
            }
            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0.0, 0.0, 0.0, new int[0]);
        }
    }

    @Override
    public Item getItem(World worldIn, BlockPos pos) {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    @Override
    public boolean isAssociatedBlock(Block other) {
        return other == Blocks.unlit_redstone_torch || other == Blocks.redstone_torch;
    }

    static class Toggle {
        BlockPos pos;
        long time;

        public Toggle(BlockPos pos, long time) {
            this.pos = pos;
            this.time = time;
        }
    }
}

