package net.minecraft.item;

import net.minecraft.block.BlockLeaves;

public class ItemLeaves extends ItemBlock {
    private final BlockLeaves leaves;

    public ItemLeaves(final BlockLeaves block) {
        super(block);
        this.leaves = block;
        this.setMaxDamage(0);
        this.setHasSubtypes(true);
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(final int damage) {
        return damage | 4;
    }

    public int getColorFromItemStack(final ItemStack stack, final int renderPass) {
        return this.leaves.getRenderColor(this.leaves.getStateFromMeta(stack.getMetadata()));
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(final ItemStack stack) {
        return super.getUnlocalizedName() + "." + this.leaves.getWoodType(stack.getMetadata()).getUnlocalizedName();
    }
}
