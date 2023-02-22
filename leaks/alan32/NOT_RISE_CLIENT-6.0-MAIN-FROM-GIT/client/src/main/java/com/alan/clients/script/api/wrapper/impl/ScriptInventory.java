package com.alan.clients.script.api.wrapper.impl;

import lombok.Getter;
import net.minecraft.item.ItemStack;

/**
 * @author Strikeless
 * @since 20.06.2022
 */
public class ScriptInventory {

    @Getter
    private final ScriptItemStack[] itemStacks;

    public ScriptInventory(final ItemStack[] itemStacks) {
        this.itemStacks = new ScriptItemStack[itemStacks.length];

        for (int i = 0; i < this.itemStacks.length; ++i) {
            this.itemStacks[i] = new ScriptItemStack(itemStacks[i]);
        }
    }

    public ScriptItemStack getItemInSlot(final int slot) {
        return itemStacks[slot];
    }
}
