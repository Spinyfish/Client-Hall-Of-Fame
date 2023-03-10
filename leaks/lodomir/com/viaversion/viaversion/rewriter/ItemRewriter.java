/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.rewriter;

import com.viaversion.viaversion.api.data.ParticleMappings;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.ClientboundPacketType;
import com.viaversion.viaversion.api.protocol.packet.ServerboundPacketType;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.rewriter.RewriterBase;
import com.viaversion.viaversion.api.type.Type;
import org.checkerframework.checker.nullness.qual.Nullable;

public abstract class ItemRewriter<T extends Protocol>
extends RewriterBase<T>
implements com.viaversion.viaversion.api.rewriter.ItemRewriter<T> {
    protected ItemRewriter(T protocol) {
        super(protocol);
    }

    @Override
    public @Nullable Item handleItemToClient(@Nullable Item item) {
        if (item == null) {
            return null;
        }
        if (this.protocol.getMappingData() != null && this.protocol.getMappingData().getItemMappings() != null) {
            item.setIdentifier(this.protocol.getMappingData().getNewItemId(item.identifier()));
        }
        return item;
    }

    @Override
    public @Nullable Item handleItemToServer(@Nullable Item item) {
        if (item == null) {
            return null;
        }
        if (this.protocol.getMappingData() != null && this.protocol.getMappingData().getItemMappings() != null) {
            item.setIdentifier(this.protocol.getMappingData().getOldItemId(item.identifier()));
        }
        return item;
    }

    public void registerWindowItems(ClientboundPacketType packetType, final Type<Item[]> type) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(type);
                this.handler(ItemRewriter.this.itemArrayHandler(type));
            }
        });
    }

    public void registerWindowItems1_17_1(ClientboundPacketType packetType, final Type<Item[]> itemsType, final Type<Item> carriedItemType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.map(itemsType);
                this.map(carriedItemType);
                this.handler(wrapper -> {
                    Item[] items;
                    for (Item item : items = wrapper.get(Type.FLAT_VAR_INT_ITEM_ARRAY_VAR_INT, 0)) {
                        ItemRewriter.this.handleItemToClient(item);
                    }
                    ItemRewriter.this.handleItemToClient(wrapper.get(Type.FLAT_VAR_INT_ITEM, 0));
                });
            }
        });
    }

    public void registerSetSlot(ClientboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(type);
                this.handler(ItemRewriter.this.itemToClientHandler(type));
            }
        });
    }

    public void registerSetSlot1_17_1(ClientboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.map(Type.SHORT);
                this.map(type);
                this.handler(ItemRewriter.this.itemToClientHandler(type));
            }
        });
    }

    public void registerEntityEquipment(ClientboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.VAR_INT);
                this.map(type);
                this.handler(ItemRewriter.this.itemToClientHandler(type));
            }
        });
    }

    public void registerEntityEquipmentArray(ClientboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    byte slot;
                    do {
                        slot = wrapper.passthrough(Type.BYTE);
                        ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type));
                    } while ((slot & 0xFFFFFF80) != 0);
                });
            }
        });
    }

    public void registerCreativeInvAction(ServerboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerServerbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.SHORT);
                this.map(type);
                this.handler(ItemRewriter.this.itemToServerHandler(type));
            }
        });
    }

    public void registerClickWindow(ServerboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerServerbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Type.VAR_INT);
                this.map(type);
                this.handler(ItemRewriter.this.itemToServerHandler(type));
            }
        });
    }

    public void registerClickWindow1_17(ServerboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerServerbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.BYTE);
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    int length = wrapper.passthrough(Type.VAR_INT);
                    for (int i = 0; i < length; ++i) {
                        wrapper.passthrough(Type.SHORT);
                        ItemRewriter.this.handleItemToServer((Item)wrapper.passthrough(type));
                    }
                    ItemRewriter.this.handleItemToServer((Item)wrapper.passthrough(type));
                });
            }
        });
    }

    public void registerClickWindow1_17_1(ServerboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerServerbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.map(Type.SHORT);
                this.map(Type.BYTE);
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    int length = wrapper.passthrough(Type.VAR_INT);
                    for (int i = 0; i < length; ++i) {
                        wrapper.passthrough(Type.SHORT);
                        ItemRewriter.this.handleItemToServer((Item)wrapper.passthrough(type));
                    }
                    ItemRewriter.this.handleItemToServer((Item)wrapper.passthrough(type));
                });
            }
        });
    }

    public void registerSetCooldown(ClientboundPacketType packetType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int itemId = wrapper.read(Type.VAR_INT);
                    wrapper.write(Type.VAR_INT, ItemRewriter.this.protocol.getMappingData().getNewItemId(itemId));
                });
            }
        });
    }

    public void registerTradeList(ClientboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    wrapper.passthrough(Type.VAR_INT);
                    int size = wrapper.passthrough(Type.UNSIGNED_BYTE).shortValue();
                    for (int i = 0; i < size; ++i) {
                        ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type));
                        ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type));
                        if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                            ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type));
                        }
                        wrapper.passthrough(Type.BOOLEAN);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.FLOAT);
                        wrapper.passthrough(Type.INT);
                    }
                });
            }
        });
    }

    public void registerAdvancements(ClientboundPacketType packetType, final Type<Item> type) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    wrapper.passthrough(Type.BOOLEAN);
                    int size = wrapper.passthrough(Type.VAR_INT);
                    for (int i = 0; i < size; ++i) {
                        wrapper.passthrough(Type.STRING);
                        if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                            wrapper.passthrough(Type.STRING);
                        }
                        if (wrapper.passthrough(Type.BOOLEAN).booleanValue()) {
                            wrapper.passthrough(Type.COMPONENT);
                            wrapper.passthrough(Type.COMPONENT);
                            ItemRewriter.this.handleItemToClient((Item)wrapper.passthrough(type));
                            wrapper.passthrough(Type.VAR_INT);
                            int flags = wrapper.passthrough(Type.INT);
                            if ((flags & 1) != 0) {
                                wrapper.passthrough(Type.STRING);
                            }
                            wrapper.passthrough(Type.FLOAT);
                            wrapper.passthrough(Type.FLOAT);
                        }
                        wrapper.passthrough(Type.STRING_ARRAY);
                        int arrayLength = wrapper.passthrough(Type.VAR_INT);
                        for (int array = 0; array < arrayLength; ++array) {
                            wrapper.passthrough(Type.STRING_ARRAY);
                        }
                    }
                });
            }
        });
    }

    public void registerSpawnParticle(ClientboundPacketType packetType, final Type<Item> itemType, final Type<?> coordType) {
        this.protocol.registerClientbound(packetType, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.BOOLEAN);
                this.map(coordType);
                this.map(coordType);
                this.map(coordType);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.INT);
                this.handler(ItemRewriter.this.getSpawnParticleHandler(itemType));
            }
        });
    }

    public PacketHandler getSpawnParticleHandler(Type<Item> itemType) {
        return wrapper -> {
            int id = wrapper.get(Type.INT, 0);
            if (id == -1) {
                return;
            }
            ParticleMappings mappings = this.protocol.getMappingData().getParticleMappings();
            if (mappings.isBlockParticle(id)) {
                int data = wrapper.passthrough(Type.VAR_INT);
                wrapper.set(Type.VAR_INT, 0, this.protocol.getMappingData().getNewBlockStateId(data));
            } else if (mappings.isItemParticle(id)) {
                this.handleItemToClient((Item)wrapper.passthrough(itemType));
            }
            int newId = this.protocol.getMappingData().getNewParticleId(id);
            if (newId != id) {
                wrapper.set(Type.INT, 0, newId);
            }
        };
    }

    public PacketHandler itemArrayHandler(Type<Item[]> type) {
        return wrapper -> {
            Item[] items;
            for (Item item : items = (Item[])wrapper.get(type, 0)) {
                this.handleItemToClient(item);
            }
        };
    }

    public PacketHandler itemToClientHandler(Type<Item> type) {
        return wrapper -> this.handleItemToClient((Item)wrapper.get(type, 0));
    }

    public PacketHandler itemToServerHandler(Type<Item> type) {
        return wrapper -> this.handleItemToServer((Item)wrapper.get(type, 0));
    }
}

