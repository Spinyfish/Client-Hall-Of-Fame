package net.minecraft.item;

import java.util.*;
import net.minecraft.block.*;
import net.minecraft.block.material.*;
import net.minecraft.init.*;
import com.google.common.collect.*;

public class ItemAxe extends ItemTool
{
    private static final Set<Block> EFFECTIVE_ON;
    
    protected ItemAxe(final ToolMaterial material) {
        super(3.0f, material, ItemAxe.EFFECTIVE_ON);
    }
    
    @Override
    public float getStrVsBlock(final ItemStack stack, final Block block) {
        return (block.getMaterial() != Material.wood && block.getMaterial() != Material.plants && block.getMaterial() != Material.vine) ? super.getStrVsBlock(stack, block) : this.efficiencyOnProperMaterial;
    }
    
    static {
        EFFECTIVE_ON = Sets.newHashSet((Object[])new Block[] { Blocks.planks, Blocks.bookshelf, Blocks.log, Blocks.log2, Blocks.chest, Blocks.pumpkin, Blocks.lit_pumpkin, Blocks.melon_block, Blocks.ladder });
    }
}
