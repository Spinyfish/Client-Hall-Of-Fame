package net.minecraft.item;

import net.minecraft.creativetab.CreativeTabs;

import java.util.List;

public class ItemCoal extends Item {
    public ItemCoal() {
        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setCreativeTab(CreativeTabs.tabMaterials);
    }

    /**
     * Returns the unlocalized name of this item. This version accepts an ItemStack so different stacks can have
     * different names based on their damage or NBT.
     */
    public String getUnlocalizedName(final ItemStack stack) {
        return stack.getMetadata() == 1 ? "item.charcoal" : "item.coal";
    }

    /**
     * returns a list of items with the same ID, but different meta (eg: dye returns 16 items)
     *
     * @param subItems The List of sub-items. This is a List of ItemStacks.
     */
    public void getSubItems(final Item itemIn, final CreativeTabs tab, final List<ItemStack> subItems) {
        subItems.add(new ItemStack(itemIn, 1, 0));
        subItems.add(new ItemStack(itemIn, 1, 1));
    }
}
