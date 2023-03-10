/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.client.player.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.IChatComponent;
import net.minecraft.world.IInteractionObject;

public class LocalBlockIntercommunication
implements IInteractionObject {
    private String guiID;
    private IChatComponent displayName;

    public LocalBlockIntercommunication(String guiIdIn, IChatComponent displayNameIn) {
        this.guiID = guiIdIn;
        this.displayName = displayNameIn;
    }

    @Override
    public Container createContainer(InventoryPlayer playerInventory, EntityPlayer playerIn) {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getName() {
        return this.displayName.getUnformattedText();
    }

    @Override
    public boolean hasCustomName() {
        return true;
    }

    @Override
    public String getGuiID() {
        return this.guiID;
    }

    @Override
    public IChatComponent getDisplayName() {
        return this.displayName;
    }
}

