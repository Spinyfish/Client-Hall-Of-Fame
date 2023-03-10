/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.inventory.InventoryType
 *  org.bukkit.inventory.Inventory
 *  org.bukkit.inventory.InventoryView
 *  org.bukkit.inventory.ItemStack
 */
package com.viaversion.viaversion.bukkit.providers;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.bukkit.tasks.protocol1_12to1_11_1.BukkitInventoryUpdateTask;
import com.viaversion.viaversion.bukkit.util.NMSUtil;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.providers.InventoryQuickMoveProvider;
import com.viaversion.viaversion.protocols.protocol1_12to1_11_1.storage.ItemTransaction;
import com.viaversion.viaversion.util.ReflectionUtil;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;

public class BukkitInventoryQuickMoveProvider
extends InventoryQuickMoveProvider {
    private final Map<UUID, BukkitInventoryUpdateTask> updateTasks = new ConcurrentHashMap<UUID, BukkitInventoryUpdateTask>();
    private final boolean supported = this.isSupported();
    private Class<?> windowClickPacketClass;
    private Object clickTypeEnum;
    private Method nmsItemMethod;
    private Method craftPlayerHandle;
    private Field connection;
    private Method packetMethod;

    public BukkitInventoryQuickMoveProvider() {
        this.setupReflection();
    }

    @Override
    public boolean registerQuickMoveAction(short windowId, short slotId, short actionId, UserConnection userConnection) {
        boolean registered;
        int protocolId;
        if (!this.supported) {
            return false;
        }
        if (slotId < 0) {
            return false;
        }
        if (windowId == 0 && slotId >= 36 && slotId <= 45 && (protocolId = Via.getAPI().getServerVersion().lowestSupportedVersion()) == ProtocolVersion.v1_8.getVersion()) {
            return false;
        }
        ProtocolInfo info = userConnection.getProtocolInfo();
        UUID uuid = info.getUuid();
        BukkitInventoryUpdateTask updateTask = this.updateTasks.get(uuid);
        boolean bl = registered = updateTask != null;
        if (!registered) {
            updateTask = new BukkitInventoryUpdateTask(this, uuid);
            this.updateTasks.put(uuid, updateTask);
        }
        updateTask.addItem(windowId, slotId, actionId);
        if (!registered && Via.getPlatform().isPluginEnabled()) {
            Via.getPlatform().runSync(updateTask);
        }
        return true;
    }

    public Object buildWindowClickPacket(Player p, ItemTransaction storage) {
        int protocolId;
        InventoryType tinvtype;
        if (!this.supported) {
            return null;
        }
        InventoryView inv = p.getOpenInventory();
        short slotId = storage.getSlotId();
        Inventory tinv = inv.getTopInventory();
        InventoryType inventoryType = tinvtype = tinv == null ? null : tinv.getType();
        if (tinvtype != null && (protocolId = Via.getAPI().getServerVersion().lowestSupportedVersion()) == ProtocolVersion.v1_8.getVersion() && tinvtype == InventoryType.BREWING && slotId >= 5 && slotId <= 40) {
            slotId = (short)(slotId - 1);
        }
        ItemStack itemstack = null;
        if (slotId <= inv.countSlots()) {
            itemstack = inv.getItem((int)slotId);
        } else {
            String cause = "Too many inventory slots: slotId: " + slotId + " invSlotCount: " + inv.countSlots() + " invType: " + inv.getType() + " topInvType: " + tinvtype;
            Via.getPlatform().getLogger().severe("Failed to get an item to create a window click packet. Please report this issue to the ViaVersion Github: " + cause);
        }
        Object packet = null;
        try {
            packet = this.windowClickPacketClass.getDeclaredConstructor(new Class[0]).newInstance(new Object[0]);
            Object nmsItem = itemstack == null ? null : this.nmsItemMethod.invoke(null, itemstack);
            ReflectionUtil.set(packet, "a", storage.getWindowId());
            ReflectionUtil.set(packet, "slot", slotId);
            ReflectionUtil.set(packet, "button", 0);
            ReflectionUtil.set(packet, "d", storage.getActionId());
            ReflectionUtil.set(packet, "item", nmsItem);
            int protocolId2 = Via.getAPI().getServerVersion().lowestSupportedVersion();
            if (protocolId2 == ProtocolVersion.v1_8.getVersion()) {
                ReflectionUtil.set(packet, "shift", 1);
            } else if (protocolId2 >= ProtocolVersion.v1_9.getVersion()) {
                ReflectionUtil.set(packet, "shift", this.clickTypeEnum);
            }
        }
        catch (Exception e) {
            Via.getPlatform().getLogger().log(Level.SEVERE, "Failed to create a window click packet. Please report this issue to the ViaVersion Github: " + e.getMessage(), e);
        }
        return packet;
    }

    public boolean sendPacketToServer(Player p, Object packet) {
        if (packet == null) {
            return true;
        }
        try {
            Object entityPlayer = this.craftPlayerHandle.invoke((Object)p, new Object[0]);
            Object playerConnection = this.connection.get(entityPlayer);
            this.packetMethod.invoke(playerConnection, packet);
        }
        catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void onTaskExecuted(UUID uuid) {
        this.updateTasks.remove(uuid);
    }

    private void setupReflection() {
        if (!this.supported) {
            return;
        }
        try {
            this.windowClickPacketClass = NMSUtil.nms("PacketPlayInWindowClick");
            int protocolId = Via.getAPI().getServerVersion().lowestSupportedVersion();
            if (protocolId >= ProtocolVersion.v1_9.getVersion()) {
                Class<?> eclassz = NMSUtil.nms("InventoryClickType");
                ?[] constants = eclassz.getEnumConstants();
                this.clickTypeEnum = constants[1];
            }
            Class<?> craftItemStack = NMSUtil.obc("inventory.CraftItemStack");
            this.nmsItemMethod = craftItemStack.getDeclaredMethod("asNMSCopy", ItemStack.class);
        }
        catch (Exception e) {
            throw new RuntimeException("Couldn't find required inventory classes", e);
        }
        try {
            this.craftPlayerHandle = NMSUtil.obc("entity.CraftPlayer").getDeclaredMethod("getHandle", new Class[0]);
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Couldn't find CraftPlayer", e);
        }
        try {
            this.connection = NMSUtil.nms("EntityPlayer").getDeclaredField("playerConnection");
        }
        catch (ClassNotFoundException | NoSuchFieldException e) {
            throw new RuntimeException("Couldn't find Player Connection", e);
        }
        try {
            this.packetMethod = NMSUtil.nms("PlayerConnection").getDeclaredMethod("a", this.windowClickPacketClass);
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            throw new RuntimeException("Couldn't find CraftPlayer", e);
        }
    }

    private boolean isSupported() {
        int protocolId = Via.getAPI().getServerVersion().lowestSupportedVersion();
        return protocolId >= ProtocolVersion.v1_8.getVersion() && protocolId <= ProtocolVersion.v1_11_1.getVersion();
    }
}

