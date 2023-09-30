/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.inventory;

import net.minecraft.inventory.InventoryBasic;
import net.minecraft.util.IChatComponent;

public class AnimalChest
extends InventoryBasic {
    public AnimalChest(String inventoryName, int slotCount) {
        super(inventoryName, false, slotCount);
    }

    public AnimalChest(IChatComponent invTitle, int slotCount) {
        super(invTitle, slotCount);
    }
}

