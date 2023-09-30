package net.minecraft.item;

import net.minecraft.block.Block;

public class ItemColored extends ItemBlock {
    private final Block coloredBlock;
    private String[] subtypeNames;

    public ItemColored(final Block block, final boolean hasSubtypes) {
        super(block);
        this.coloredBlock = block;

        if (hasSubtypes) {
            this.setMaxDamage(0);
            this.setHasSubtypes(true);
        }
    }

    public int getColorFromItemStack(final ItemStack stack, final int renderPass) {
        return this.coloredBlock.getRenderColor(this.coloredBlock.getStateFromMeta(stack.getMetadata()));
    }

    /**
     * Converts the given ItemStack damage value into a metadata value to be placed in the world when this Item is
     * placed as a Block (mostly used with ItemBlocks).
     */
    public int getMetadata(final int damage) {
        return damage;
    }

    public ItemColored setSubtypeNames(final String[] names) {
        this.subtypeNames = names;
        return this;
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(final ItemStack stack) {
        if (this.subtypeNames == null) {
            return super.getUnlocalizedName(stack);
        } else {
            final int i = stack.getMetadata();
            return i >= 0 && i < this.subtypeNames.length ? super.getUnlocalizedName(stack) + "." + this.subtypeNames[i] : super.getUnlocalizedName(stack);
        }
    }
}
