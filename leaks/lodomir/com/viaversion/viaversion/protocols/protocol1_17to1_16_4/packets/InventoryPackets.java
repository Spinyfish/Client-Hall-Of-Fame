/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_17to1_16_4.packets;

import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ClientboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16_2to1_16_1.ServerboundPackets1_16_2;
import com.viaversion.viaversion.protocols.protocol1_16to1_15_2.data.RecipeRewriter1_16;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ClientboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.Protocol1_17To1_16_4;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.ServerboundPackets1_17;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.storage.InventoryAcknowledgements;
import com.viaversion.viaversion.rewriter.ItemRewriter;

public final class InventoryPackets
extends ItemRewriter<Protocol1_17To1_16_4> {
    public InventoryPackets(Protocol1_17To1_16_4 protocol) {
        super(protocol);
    }

    @Override
    public void registerPackets() {
        this.registerSetCooldown(ClientboundPackets1_16_2.COOLDOWN);
        this.registerWindowItems(ClientboundPackets1_16_2.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
        this.registerTradeList(ClientboundPackets1_16_2.TRADE_LIST, Type.FLAT_VAR_INT_ITEM);
        this.registerSetSlot(ClientboundPackets1_16_2.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
        this.registerAdvancements(ClientboundPackets1_16_2.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
        this.registerEntityEquipmentArray(ClientboundPackets1_16_2.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
        this.registerSpawnParticle(ClientboundPackets1_16_2.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, Type.DOUBLE);
        new RecipeRewriter1_16(this.protocol).registerDefaultHandler(ClientboundPackets1_16_2.DECLARE_RECIPES);
        this.registerCreativeInvAction(ServerboundPackets1_17.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
        ((Protocol1_17To1_16_4)this.protocol).registerServerbound(ServerboundPackets1_17.EDIT_BOOK, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> InventoryPackets.this.handleItemToServer(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM)));
            }
        });
        ((Protocol1_17To1_16_4)this.protocol).registerServerbound(ServerboundPackets1_17.CLICK_WINDOW, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.BYTE);
                this.handler(wrapper -> wrapper.write(Type.SHORT, (short)0));
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    int length = wrapper.read(Type.VAR_INT);
                    for (int i = 0; i < length; ++i) {
                        wrapper.read(Type.SHORT);
                        wrapper.read(Type.FLAT_VAR_INT_ITEM);
                    }
                    Item item = wrapper.read(Type.FLAT_VAR_INT_ITEM);
                    int action = wrapper.get(Type.VAR_INT, 0);
                    if (action == 5 || action == 1) {
                        item = null;
                    } else {
                        InventoryPackets.this.handleItemToServer(item);
                    }
                    wrapper.write(Type.FLAT_VAR_INT_ITEM, item);
                });
            }
        });
        ((Protocol1_17To1_16_4)this.protocol).registerClientbound(ClientboundPackets1_16_2.WINDOW_CONFIRMATION, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    short inventoryId = wrapper.read(Type.UNSIGNED_BYTE);
                    short confirmationId = wrapper.read(Type.SHORT);
                    boolean accepted = wrapper.read(Type.BOOLEAN);
                    if (!accepted) {
                        int id = 0x40000000 | inventoryId << 16 | confirmationId & 0xFFFF;
                        wrapper.user().get(InventoryAcknowledgements.class).addId(id);
                        PacketWrapper pingPacket = wrapper.create(ClientboundPackets1_17.PING);
                        pingPacket.write(Type.INT, id);
                        pingPacket.send(Protocol1_17To1_16_4.class);
                    }
                    wrapper.cancel();
                });
            }
        });
        ((Protocol1_17To1_16_4)this.protocol).registerServerbound(ServerboundPackets1_17.PONG, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int id = wrapper.read(Type.INT);
                    if ((id & 0x40000000) != 0 && wrapper.user().get(InventoryAcknowledgements.class).removeId(id)) {
                        short inventoryId = (short)(id >> 16 & 0xFF);
                        short confirmationId = (short)(id & 0xFFFF);
                        PacketWrapper packet = wrapper.create(ServerboundPackets1_16_2.WINDOW_CONFIRMATION);
                        packet.write(Type.UNSIGNED_BYTE, inventoryId);
                        packet.write(Type.SHORT, confirmationId);
                        packet.write(Type.BOOLEAN, true);
                        packet.sendToServer(Protocol1_17To1_16_4.class);
                    }
                    wrapper.cancel();
                });
            }
        });
    }
}

