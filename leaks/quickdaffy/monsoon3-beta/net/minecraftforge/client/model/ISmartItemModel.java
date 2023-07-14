/*
 * Decompiled with CFR 0.152.
 */
package net.minecraftforge.client.model;

import net.minecraft.client.resources.model.IBakedModel;
import net.minecraft.item.ItemStack;

public interface ISmartItemModel
extends IBakedModel {
    public IBakedModel handleItemState(ItemStack var1);
}

