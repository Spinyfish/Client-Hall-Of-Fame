/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.Bukkit
 *  org.bukkit.entity.Player
 *  org.bukkit.inventory.ItemStack
 *  org.bukkit.scheduler.BukkitRunnable
 */
package com.viaversion.viaversion.bukkit.listeners.protocol1_9to1_8;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class HandItemCache
extends BukkitRunnable {
    private final Map<UUID, Item> handCache = new ConcurrentHashMap<UUID, Item>();

    public void run() {
        ArrayList<UUID> players = new ArrayList<UUID>(this.handCache.keySet());
        for (Player p : Bukkit.getOnlinePlayers()) {
            this.handCache.put(p.getUniqueId(), HandItemCache.convert(p.getItemInHand()));
            players.remove(p.getUniqueId());
        }
        for (UUID uuid : players) {
            this.handCache.remove(uuid);
        }
    }

    public Item getHandItem(UUID player) {
        return this.handCache.get(player);
    }

    public static Item convert(ItemStack itemInHand) {
        if (itemInHand == null) {
            return new DataItem(0, 0, 0, null);
        }
        return new DataItem(itemInHand.getTypeId(), (byte)itemInHand.getAmount(), itemInHand.getDurability(), null);
    }
}

