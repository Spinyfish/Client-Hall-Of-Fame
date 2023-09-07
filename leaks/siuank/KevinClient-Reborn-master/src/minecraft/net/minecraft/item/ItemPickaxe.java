package net.minecraft.item;

import com.google.common.collect.Sets;
import java.util.Set;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;

public class ItemPickaxe extends ItemTool
{
    private static final Set<Block> EFFECTIVE_ON = Sets.newHashSet(Blocks.activator_rail, Blocks.coal_ore, Blocks.cobblestone, Blocks.detector_rail, Blocks.diamond_block, Blocks.diamond_ore, Blocks.double_stone_slab, Blocks.golden_rail, Blocks.gold_block, Blocks.gold_ore, Blocks.ice, Blocks.iron_block, Blocks.iron_ore, Blocks.lapis_block, Blocks.lapis_ore, Blocks.lit_redstone_ore, Blocks.mossy_cobblestone, Blocks.netherrack, Blocks.packed_ice, Blocks.rail, Blocks.redstone_ore, Blocks.sandstone, Blocks.red_sandstone, Blocks.stone, Blocks.stone_slab);

    protected ItemPickaxe(Item.ToolMaterial material)
    {
        super(2.0F, material, EFFECTIVE_ON);
    }

    /**
     * Check whether this Item can harvest the given Block
     */
    public boolean canHarvestBlock(Block blockIn)
    {
        if (blockIn == Blocks.obsidian)
        {
            return this.toolMaterial.getHarvestLevel() == 3;
        }
        else if (blockIn != Blocks.diamond_block && blockIn != Blocks.diamond_ore)
        {
            if (blockIn != Blocks.emerald_ore && blockIn != Blocks.emerald_block)
            {
                if (blockIn != Blocks.gold_block && blockIn != Blocks.gold_ore)
                {
                    if (blockIn != Blocks.iron_block && blockIn != Blocks.iron_ore)
                    {
                        if (blockIn != Blocks.lapis_block && blockIn != Blocks.lapis_ore)
                        {
                            if (blockIn != Blocks.redstone_ore && blockIn != Blocks.lit_redstone_ore)
                            {
                                if (blockIn.getMaterial() == Material.rock)
                                {
                                    return true;
                                }
                                else if (blockIn.getMaterial() == Material.iron)
                                {
                                    return true;
                                }
                                else
                                {
                                    return blockIn.getMaterial() == Material.anvil;
                                }
                            }
                            else
                            {
                                return this.toolMaterial.getHarvestLevel() >= 2;
                            }
                        }
                        else
                        {
                            return this.toolMaterial.getHarvestLevel() >= 1;
                        }
                    }
                    else
                    {
                        return this.toolMaterial.getHarvestLevel() >= 1;
                    }
                }
                else
                {
                    return this.toolMaterial.getHarvestLevel() >= 2;
                }
            }
            else
            {
                return this.toolMaterial.getHarvestLevel() >= 2;
            }
        }
        else
        {
            return this.toolMaterial.getHarvestLevel() >= 2;
        }
    }

    public float getStrVsBlock(ItemStack stack, Block state)
    {
        return state.getMaterial() != Material.iron && state.getMaterial() != Material.anvil && state.getMaterial() != Material.rock ? super.getStrVsBlock(stack, state) : this.efficiencyOnProperMaterial;
    }
}
