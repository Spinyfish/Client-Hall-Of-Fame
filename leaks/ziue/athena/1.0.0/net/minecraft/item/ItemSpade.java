package net.minecraft.item;

import java.util.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import com.google.common.collect.*;

public class ItemSpade extends ItemTool
{
    private static final Set<Block> EFFECTIVE_ON;
    
    public ItemSpade(final ToolMaterial material) {
        super(1.0f, material, ItemSpade.EFFECTIVE_ON);
    }
    
    @Override
    public boolean canHarvestBlock(final Block blockIn) {
        return blockIn == Blocks.snow_layer || blockIn == Blocks.snow;
    }
    
    static {
        EFFECTIVE_ON = Sets.newHashSet((Object[])new Block[] { Blocks.clay, Blocks.dirt, Blocks.farmland, Blocks.grass, Blocks.gravel, Blocks.mycelium, Blocks.sand, Blocks.snow, Blocks.snow_layer, Blocks.soul_sand });
    }
}
