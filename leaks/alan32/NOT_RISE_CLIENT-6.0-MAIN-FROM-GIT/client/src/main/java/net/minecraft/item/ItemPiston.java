package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemPiston extends ItemBlock {
    public ItemPiston(final Block block) {
        super(block);
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(final int damage) {
        return 7;
    }
}
