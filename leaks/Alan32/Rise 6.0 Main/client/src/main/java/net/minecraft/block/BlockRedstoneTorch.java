package net.minecraft.block;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.util.List;
import java.util.Map;
import java.util.Random;

public class BlockRedstoneTorch extends BlockTorch {
    private static final Map<World, List<BlockRedstoneTorch.Toggle>> toggles = Maps.newHashMap();
    private final boolean isOn;

    private boolean isBurnedOut(final World worldIn, final BlockPos pos, final boolean turnOff) {
        if (!toggles.containsKey(worldIn)) {
            toggles.put(worldIn, Lists.newArrayList());
        }

        final List<BlockRedstoneTorch.Toggle> list = toggles.get(worldIn);

        if (turnOff) {
            list.add(new BlockRedstoneTorch.Toggle(pos, worldIn.getTotalWorldTime()));
        }

        int i = 0;

        for (int j = 0; j < list.size(); ++j) {
            final BlockRedstoneTorch.Toggle blockredstonetorch$toggle = list.get(j);

            if (blockredstonetorch$toggle.pos.equals(pos)) {
                ++i;

                if (i >= 8) {
                    return true;
                }
            }
        }

        return false;
    }

    protected BlockRedstoneTorch(final boolean isOn) {
        this.isOn = isOn;
        this.setTickRandomly(true);
        this.setCreativeTab(null);
    }

    /**
     * How many world ticks before ticking
     */
    public int tickRate(final World worldIn) {
        return 2;
    }

    public void onBlockAdded(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (this.isOn) {
            for (final EnumFacing enumfacing : EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }
        }
    }

    public void breakBlock(final World worldIn, final BlockPos pos, final IBlockState state) {
        if (this.isOn) {
            for (final EnumFacing enumfacing : EnumFacing.values()) {
                worldIn.notifyNeighborsOfStateChange(pos.offset(enumfacing), this);
            }
        }
    }

    public int isProvidingWeakPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return this.isOn && state.getValue(FACING) != side ? 15 : 0;
    }

    private boolean shouldBeOff(final World worldIn, final BlockPos pos, final IBlockState state) {
        final EnumFacing enumfacing = state.getValue(FACING).getOpposite();
        return worldIn.isSidePowered(pos.offset(enumfacing), enumfacing);
    }

    /**
     * Called randomly when setTickRandomly is set to true (used by e.g. crops to grow, etc.)
     */
    public void randomTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random random) {
    }

    public void updateTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        final boolean flag = this.shouldBeOff(worldIn, pos, state);
        final List<BlockRedstoneTorch.Toggle> list = toggles.get(worldIn);

        while (list != null && !list.isEmpty() && worldIn.getTotalWorldTime() - list.get(0).time > 60L) {
            list.remove(0);
        }

        if (this.isOn) {
            if (flag) {
                worldIn.setBlockState(pos, Blocks.unlit_redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);

                if (this.isBurnedOut(worldIn, pos, true)) {
                    worldIn.playSoundEffect((float) pos.getX() + 0.5F, (float) pos.getY() + 0.5F, (float) pos.getZ() + 0.5F, "random.fizz", 0.5F, 2.6F + (worldIn.rand.nextFloat() - worldIn.rand.nextFloat()) * 0.8F);

                    for (int i = 0; i < 5; ++i) {
                        final double d0 = (double) pos.getX() + rand.nextDouble() * 0.6D + 0.2D;
                        final double d1 = (double) pos.getY() + rand.nextDouble() * 0.6D + 0.2D;
                        final double d2 = (double) pos.getZ() + rand.nextDouble() * 0.6D + 0.2D;
                        worldIn.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, d0, d1, d2, 0.0D, 0.0D, 0.0D);
                    }

                    worldIn.scheduleUpdate(pos, worldIn.getBlockState(pos).getBlock(), 160);
                }
            }
        } else if (!flag && !this.isBurnedOut(worldIn, pos, false)) {
            worldIn.setBlockState(pos, Blocks.redstone_torch.getDefaultState().withProperty(FACING, state.getValue(FACING)), 3);
        }
    }

    /**
     * Called when a neighboring block changes.
     */
    public void onNeighborBlockChange(final World worldIn, final BlockPos pos, final IBlockState state, final Block neighborBlock) {
        if (!this.onNeighborChangeInternal(worldIn, pos, state)) {
            if (this.isOn == this.shouldBeOff(worldIn, pos, state)) {
                worldIn.scheduleUpdate(pos, this, this.tickRate(worldIn));
            }
        }
    }

    public int isProvidingStrongPower(final IBlockAccess worldIn, final BlockPos pos, final IBlockState state, final EnumFacing side) {
        return side == EnumFacing.DOWN ? this.isProvidingWeakPower(worldIn, pos, state, side) : 0;
    }

    /**
     * Get the Item that this Block should drop when harvested.
     *
     * @param fortune the level of the Fortune enchantment on the player's tool
     */
    public Item getItemDropped(final IBlockState state, final Random rand, final int fortune) {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    /**
     * Can this block provide power. Only wire currently seems to have this change based on its state.
     */
    public boolean canProvidePower() {
        return true;
    }

    public void randomDisplayTick(final World worldIn, final BlockPos pos, final IBlockState state, final Random rand) {
        if (this.isOn) {
            double d0 = (double) pos.getX() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
            double d1 = (double) pos.getY() + 0.7D + (rand.nextDouble() - 0.5D) * 0.2D;
            double d2 = (double) pos.getZ() + 0.5D + (rand.nextDouble() - 0.5D) * 0.2D;
            final EnumFacing enumfacing = state.getValue(FACING);

            if (enumfacing.getAxis().isHorizontal()) {
                final EnumFacing enumfacing1 = enumfacing.getOpposite();
                final double d3 = 0.27D;
                d0 += 0.27D * (double) enumfacing1.getFrontOffsetX();
                d1 += 0.22D;
                d2 += 0.27D * (double) enumfacing1.getFrontOffsetZ();
            }

            worldIn.spawnParticle(EnumParticleTypes.REDSTONE, d0, d1, d2, 0.0D, 0.0D, 0.0D);
        }
    }

    /**
     * Used by pick block on the client to get a block's item form, if it exists.
     */
    public Item getItem(final World worldIn, final BlockPos pos) {
        return Item.getItemFromBlock(Blocks.redstone_torch);
    }

    public boolean isAssociatedBlock(final Block other) {
        return other == Blocks.unlit_redstone_torch || other == Blocks.redstone_torch;
    }

    static class Toggle {
        BlockPos pos;
        long time;

        public Toggle(final BlockPos pos, final long time) {
            this.pos = pos;
            this.time = time;
        }
    }
}
