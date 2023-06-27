package net.minecraft.block;

import net.minecraft.block.state.*;
import java.util.*;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.block.material.*;

public class BlockGravel extends BlockFalling
{
    @Override
    public Item getItemDropped(final IBlockState state, final Random rand, int fortune) {
        if (fortune > 3) {
            fortune = 3;
        }
        return (rand.nextInt(10 - fortune * 3) == 0) ? Items.flint : Item.getItemFromBlock(this);
    }
    
    @Override
    public MapColor getMapColor(final IBlockState state) {
        return MapColor.stoneColor;
    }
}
