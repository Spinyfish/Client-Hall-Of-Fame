package net.minecraft.block.state.pattern;

import com.google.common.base.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;

public class BlockHelper implements Predicate<IBlockState>
{
    private final Block block;
    
    private BlockHelper(final Block blockType) {
        this.block = blockType;
    }
    
    public static BlockHelper forBlock(final Block blockType) {
        return new BlockHelper(blockType);
    }
    
    public boolean apply(final IBlockState p_apply_1_) {
        return p_apply_1_ != null && p_apply_1_.getBlock() == this.block;
    }
}
