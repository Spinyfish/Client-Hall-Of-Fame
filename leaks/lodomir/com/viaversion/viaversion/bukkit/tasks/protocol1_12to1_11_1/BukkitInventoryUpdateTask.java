/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 */
package com.viaversion.viaversion.bukkit.tasks.protocol1_12to1_11_1;

import com.viaversion.viaversion.bukkit.providers.BukkitInventoryQuickMoveProvider;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.storage.ItemTransaction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitInventoryUpdateTask
implements Runnable {
    private final BukkitInventoryQuickMoveProvider provider;
    private final UUID uuid;
    private final List<ItemTransaction> items;

    public BukkitInventoryUpdateTask(BukkitInventoryQuickMoveProvider provider, UUID uuid) {
        this.provider = provider;
        this.uuid = uuid;
        this.items = Collections.synchronizedList(new ArrayList());
    }

    public void addItem(short windowId, short slotId, short actionId) {
        ItemTransaction storage = new ItemTransaction(windowId, slotId, actionId);
        this.items.add(storage);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void run() {
        Player p = Bukkit.getServer().getPlayer(this.uuid);
        if (p == null) {
            this.provider.onTaskExecuted(this.uuid);
            return;
        }
        try {
            List<ItemTransaction> list = this.items;
            synchronized (list) {
                ItemTransaction storage;
                Object packet;
                boolean result;
                Iterator<ItemTransaction> iterator = this.items.iterator();
                while (iterator.hasNext() && (result = this.provider.sendPacketToServer(p, packet = this.provider.buildWindowClickPacket(p, storage = iterator.next())))) {
                }
                this.items.clear();
            }
        }
        finally {
            this.provider.onTaskExecuted(this.uuid);
        }
    }
}

