/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.item.inventory.ItemStack
 */
package com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8;

import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.ItemGrabber;
import com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.sponge4.Sponge4ItemGrabber;
import com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.sponge5.Sponge5ItemGrabber;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.inventory.ItemStack;

public class HandItemCache
implements Runnable {
    public static boolean CACHE = false;
    private static Map<UUID, Item> handCache = new ConcurrentHashMap<UUID, Item>();
    private static Field GET_DAMAGE;
    private static Method GET_ID;
    private static ItemGrabber grabber;

    public static Item getHandItem(UUID player) {
        return handCache.get(player);
    }

    @Override
    public void run() {
        ArrayList<UUID> players = new ArrayList<UUID>(handCache.keySet());
        for (Player p : Sponge.getServer().getOnlinePlayers()) {
            handCache.put(p.getUniqueId(), HandItemCache.convert(grabber.getItem(p)));
            players.remove(p.getUniqueId());
        }
        for (UUID uuid : players) {
            handCache.remove(uuid);
        }
    }

    public static Item convert(ItemStack itemInHand) {
        if (itemInHand == null) {
            return new DataItem(0, 0, 0, null);
        }
        if (GET_DAMAGE == null) {
            try {
                GET_DAMAGE = itemInHand.getClass().getDeclaredField("field_77991_e");
                GET_DAMAGE.setAccessible(true);
            }
            catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if (GET_ID == null) {
            try {
                GET_ID = Class.forName("net.minecraft.item.Item").getDeclaredMethod("func_150891_b", Class.forName("net.minecraft.item.Item"));
            }
            catch (NoSuchMethodException e) {
                e.printStackTrace();
            }
            catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        int id = 0;
        if (GET_ID != null) {
            try {
                id = (Integer)GET_ID.invoke(null, itemInHand.getItem());
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        int damage = 0;
        if (GET_DAMAGE != null) {
            try {
                damage = (Integer)GET_DAMAGE.get(itemInHand);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        return new DataItem(id, (byte)itemInHand.getQuantity(), (short)damage, null);
    }

    static {
        try {
            Class.forName("org.spongepowered.api.event.entity.DisplaceEntityEvent");
            grabber = new Sponge4ItemGrabber();
        }
        catch (ClassNotFoundException e) {
            grabber = new Sponge5ItemGrabber();
        }
    }
}

