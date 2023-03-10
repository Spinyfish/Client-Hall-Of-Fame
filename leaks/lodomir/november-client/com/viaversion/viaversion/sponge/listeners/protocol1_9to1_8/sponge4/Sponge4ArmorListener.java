/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.spongepowered.api.Sponge
 *  org.spongepowered.api.entity.living.player.Player
 *  org.spongepowered.api.event.Listener
 *  org.spongepowered.api.event.action.InteractEvent
 *  org.spongepowered.api.event.entity.DisplaceEntityEvent$Teleport
 *  org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent
 *  org.spongepowered.api.event.filter.cause.Root
 *  org.spongepowered.api.event.item.inventory.ClickInventoryEvent
 *  org.spongepowered.api.event.network.ClientConnectionEvent$Join
 *  org.spongepowered.api.item.inventory.ItemStack
 *  org.spongepowered.api.item.inventory.ItemStackSnapshot
 *  org.spongepowered.api.item.inventory.transaction.SlotTransaction
 *  org.spongepowered.api.world.World
 */
package com.viaversion.viaversion.sponge.listeners.protocol1_9to1_8.sponge4;

import com.viaversion.viaversion.ViaListener;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ArmorType;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import java.lang.reflect.Field;
import java.util.Optional;
import java.util.UUID;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.action.InteractEvent;
import org.spongepowered.api.event.entity.DisplaceEntityEvent;
import org.spongepowered.api.event.entity.living.humanoid.player.RespawnPlayerEvent;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.event.network.ClientConnectionEvent;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.world.World;

public class Sponge4ArmorListener
extends ViaListener {
    private static Field entityIdField;
    private static final UUID ARMOR_ATTRIBUTE;

    public Sponge4ArmorListener() {
        super(Protocol1_9To1_8.class);
    }

    public void sendArmorUpdate(Player player) {
        if (!this.isOnPipe(player.getUniqueId())) {
            return;
        }
        int armor = 0;
        armor += this.calculate(player.getHelmet());
        armor += this.calculate(player.getChestplate());
        armor += this.calculate(player.getLeggings());
        armor += this.calculate(player.getBoots());
        PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.ENTITY_PROPERTIES, null, this.getUserConnection(player.getUniqueId()));
        try {
            wrapper.write(Type.VAR_INT, this.getEntityId(player));
            wrapper.write(Type.INT, 1);
            wrapper.write(Type.STRING, "generic.armor");
            wrapper.write(Type.DOUBLE, 0.0);
            wrapper.write(Type.VAR_INT, 1);
            wrapper.write(Type.UUID, ARMOR_ATTRIBUTE);
            wrapper.write(Type.DOUBLE, Double.valueOf(armor));
            wrapper.write(Type.BYTE, (byte)0);
            wrapper.scheduleSend(Protocol1_9To1_8.class);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private int calculate(Optional<ItemStack> itemStack) {
        if (itemStack.isPresent()) {
            return ArmorType.findByType(itemStack.get().getItem().getType().getId()).getArmorPoints();
        }
        return 0;
    }

    @Listener
    public void onInventoryClick(ClickInventoryEvent e, @Root Player player) {
        for (SlotTransaction transaction : e.getTransactions()) {
            if (!ArmorType.isArmor(((ItemStackSnapshot)transaction.getFinal()).getType().getId()) && !ArmorType.isArmor(((ItemStackSnapshot)e.getCursorTransaction().getFinal()).getType().getId())) continue;
            this.sendDelayedArmorUpdate(player);
            break;
        }
    }

    @Listener
    public void onInteract(InteractEvent event, @Root Player player) {
        if (player.getItemInHand().isPresent() && ArmorType.isArmor(((ItemStack)player.getItemInHand().get()).getItem().getId())) {
            this.sendDelayedArmorUpdate(player);
        }
    }

    @Listener
    public void onJoin(ClientConnectionEvent.Join e) {
        this.sendArmorUpdate(e.getTargetEntity());
    }

    @Listener
    public void onRespawn(RespawnPlayerEvent e) {
        this.sendDelayedArmorUpdate(e.getTargetEntity());
    }

    @Listener
    public void onWorldChange(DisplaceEntityEvent.Teleport e) {
        if (!(e.getTargetEntity() instanceof Player)) {
            return;
        }
        if (!((World)e.getFromTransform().getExtent()).getUniqueId().equals(((World)e.getToTransform().getExtent()).getUniqueId())) {
            this.sendArmorUpdate((Player)e.getTargetEntity());
        }
    }

    public void sendDelayedArmorUpdate(final Player player) {
        if (!this.isOnPipe(player.getUniqueId())) {
            return;
        }
        Via.getPlatform().runSync(new Runnable(){

            @Override
            public void run() {
                Sponge4ArmorListener.this.sendArmorUpdate(player);
            }
        });
    }

    @Override
    public void register() {
        if (this.isRegistered()) {
            return;
        }
        Sponge.getEventManager().registerListeners((Object)Via.getPlatform(), (Object)this);
        this.setRegistered(true);
    }

    protected int getEntityId(Player p) {
        try {
            if (entityIdField == null) {
                entityIdField = p.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredField("field_145783_c");
                entityIdField.setAccessible(true);
            }
            return entityIdField.getInt(p);
        }
        catch (Exception e) {
            Via.getPlatform().getLogger().severe("Could not get the entity id, please report this on our Github");
            e.printStackTrace();
            Via.getPlatform().getLogger().severe("Could not get the entity id, please report this on our Github");
            return -1;
        }
    }

    static {
        ARMOR_ATTRIBUTE = UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150");
    }
}

