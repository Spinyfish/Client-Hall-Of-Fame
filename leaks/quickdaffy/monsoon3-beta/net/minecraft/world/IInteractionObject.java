/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.world;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.world.IWorldNameable;

public interface IInteractionObject
extends IWorldNameable {
    public Container createContainer(InventoryPlayer var1, EntityPlayer var2);

    public String getGuiID();
}

