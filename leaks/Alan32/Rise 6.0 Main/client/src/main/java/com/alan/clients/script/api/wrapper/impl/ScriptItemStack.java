package com.alan.clients.script.api.wrapper.impl;

import com.alan.clients.script.api.wrapper.ScriptWrapper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

/**
 * @author Strikeless
 * @since 20.06.2022
 */
public class ScriptItemStack extends ScriptWrapper<ItemStack> {

    public ScriptItemStack(final ItemStack wrapped) {
        super(wrapped);
    }

    public int getAmount() {
        return this.wrapped.getStackSize();
    }

    public int getMaxAmount() {
        return this.wrapped.getMaxStackSize();
    }

    public int getItemId() {
        return Item.getIdFromItem(this.wrapped.getItem());
    }

    public ItemStack getWrapped() {
        return this.wrapped;
    }
}
