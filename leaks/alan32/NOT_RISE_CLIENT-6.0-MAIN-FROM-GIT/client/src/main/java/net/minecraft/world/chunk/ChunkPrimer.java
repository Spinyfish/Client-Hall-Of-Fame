package net.minecraft.world.chunk;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class ChunkPrimer {
    private final short[] data = new short[65536];
    private final IBlockState defaultState = Blocks.air.getDefaultState();

    public IBlockState getBlockState(final int x, final int y, final int z) {
        final int i = x << 12 | z << 8 | y;
        return this.getBlockState(i);
    }

    public IBlockState getBlockState(final int index) {
        if (index >= 0 && index < this.data.length) {
            final IBlockState iblockstate = Block.BLOCK_STATE_IDS.getByValue(this.data[index]);
            return iblockstate != null ? iblockstate : this.defaultState;
        } else {
            throw new IndexOutOfBoundsException("The coordinate is out of range");
        }
    }

    public void setBlockState(final int x, final int y, final int z, final IBlockState state) {
        final int i = x << 12 | z << 8 | y;
        this.setBlockState(i, state);
    }

    public void setBlockState(final int index, final IBlockState state) {
        if (index >= 0 && index < this.data.length) {
            this.data[index] = (short) Block.BLOCK_STATE_IDS.get(state);
        } else {
            throw new IndexOutOfBoundsException("The coordinate is out of range");
        }
    }
}
