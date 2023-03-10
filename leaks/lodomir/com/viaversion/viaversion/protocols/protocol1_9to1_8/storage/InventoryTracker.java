/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8.storage;

import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import java.util.HashMap;
import java.util.Map;

public class InventoryTracker
implements StorableObject {
    private String inventory;
    private final Map<Short, Map<Short, Integer>> windowItemCache = new HashMap<Short, Map<Short, Integer>>();
    private int itemIdInCursor = 0;
    private boolean dragging = false;

    public String getInventory() {
        return this.inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }

    public void resetInventory(short windowId) {
        if (this.inventory == null) {
            this.itemIdInCursor = 0;
            this.dragging = false;
            if (windowId != 0) {
                this.windowItemCache.remove(windowId);
            }
        }
    }

    public int getItemId(short windowId, short slot) {
        Map<Short, Integer> itemMap = this.windowItemCache.get(windowId);
        if (itemMap == null) {
            return 0;
        }
        return itemMap.getOrDefault(slot, 0);
    }

    public void setItemId(short windowId, short slot, int itemId) {
        if (windowId == -1 && slot == -1) {
            this.itemIdInCursor = itemId;
        } else {
            this.windowItemCache.computeIfAbsent(windowId, k -> new HashMap()).put(slot, itemId);
        }
    }

    public void handleWindowClick(UserConnection user, short windowId, byte mode, short hoverSlot, byte button) {
        EntityTracker1_9 entityTracker = (EntityTracker1_9)user.getEntityTracker(Protocol1_9To1_8.class);
        if (hoverSlot == -1) {
            return;
        }
        if (hoverSlot == 45) {
            entityTracker.setSecondHand(null);
            return;
        }
        boolean isArmorOrResultSlot = hoverSlot >= 5 && hoverSlot <= 8 || hoverSlot == 0;
        switch (mode) {
            case 0: {
                if (this.itemIdInCursor == 0) {
                    this.itemIdInCursor = this.getItemId(windowId, hoverSlot);
                    this.setItemId(windowId, hoverSlot, 0);
                    break;
                }
                if (hoverSlot == -999) {
                    this.itemIdInCursor = 0;
                    break;
                }
                if (isArmorOrResultSlot) break;
                int previousItem = this.getItemId(windowId, hoverSlot);
                this.setItemId(windowId, hoverSlot, this.itemIdInCursor);
                this.itemIdInCursor = previousItem;
                break;
            }
            case 2: {
                if (isArmorOrResultSlot) break;
                short hotkeySlot = (short)(button + 36);
                int sourceItem = this.getItemId(windowId, hoverSlot);
                int destinationItem = this.getItemId(windowId, hotkeySlot);
                this.setItemId(windowId, hotkeySlot, sourceItem);
                this.setItemId(windowId, hoverSlot, destinationItem);
                break;
            }
            case 4: {
                int hoverItem = this.getItemId(windowId, hoverSlot);
                if (hoverItem == 0) break;
                this.setItemId(windowId, hoverSlot, 0);
                break;
            }
            case 5: {
                switch (button) {
                    case 0: 
                    case 4: {
                        this.dragging = true;
                        break;
                    }
                    case 1: 
                    case 5: {
                        if (!this.dragging || this.itemIdInCursor == 0 || isArmorOrResultSlot) break;
                        int previousItem = this.getItemId(windowId, hoverSlot);
                        this.setItemId(windowId, hoverSlot, this.itemIdInCursor);
                        this.itemIdInCursor = previousItem;
                        break;
                    }
                    case 2: 
                    case 6: {
                        this.dragging = false;
                    }
                }
                break;
            }
        }
        entityTracker.syncShieldWithSword();
    }
}

