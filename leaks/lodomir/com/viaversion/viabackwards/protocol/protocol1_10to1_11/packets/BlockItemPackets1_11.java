/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viabackwards.protocol.protocol1_10to1_11.packets;

import com.viaversion.viabackwards.api.data.MappedLegacyBlockItem;
import com.viaversion.viabackwards.api.rewriters.LegacyBlockItemRewriter;
import com.viaversion.viabackwards.api.rewriters.LegacyEnchantmentRewriter;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.Protocol1_10To1_11;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.storage.ChestedHorseStorage;
import com.viaversion.viabackwards.protocol.protocol1_10to1_11.storage.WindowTracker;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.StoredEntityData;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_11Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_11to1_10.EntityIdRewriter;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ClientboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.ServerboundPackets1_9_3;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import java.util.Arrays;
import java.util.Optional;

public class BlockItemPackets1_11
extends LegacyBlockItemRewriter<Protocol1_10To1_11> {
    private LegacyEnchantmentRewriter enchantmentRewriter;

    public BlockItemPackets1_11(Protocol1_10To1_11 protocol) {
        super(protocol);
    }

    @Override
    protected void registerPackets() {
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.SET_SLOT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.ITEM);
                this.handler(BlockItemPackets1_11.this.itemToClientHandler(Type.ITEM));
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                            Optional horse = BlockItemPackets1_11.this.getChestedHorse(wrapper.user());
                            if (!horse.isPresent()) {
                                return;
                            }
                            ChestedHorseStorage storage = (ChestedHorseStorage)horse.get();
                            int currentSlot = wrapper.get(Type.SHORT, 0).shortValue();
                            currentSlot = BlockItemPackets1_11.this.getNewSlotId(storage, currentSlot);
                            wrapper.set(Type.SHORT, 0, Integer.valueOf(currentSlot).shortValue());
                            wrapper.set(Type.ITEM, 0, BlockItemPackets1_11.this.getNewItem(storage, currentSlot, wrapper.get(Type.ITEM, 0)));
                        }
                    }
                });
            }
        });
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.WINDOW_ITEMS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.ITEM_ARRAY);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Item[] stacks = wrapper.get(Type.ITEM_ARRAY, 0);
                        for (int i = 0; i < stacks.length; ++i) {
                            stacks[i] = BlockItemPackets1_11.this.handleItemToClient(stacks[i]);
                        }
                        if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                            Optional horse = BlockItemPackets1_11.this.getChestedHorse(wrapper.user());
                            if (!horse.isPresent()) {
                                return;
                            }
                            ChestedHorseStorage storage = (ChestedHorseStorage)horse.get();
                            stacks = Arrays.copyOf(stacks, !storage.isChested() ? 38 : 53);
                            for (int i = stacks.length - 1; i >= 0; --i) {
                                stacks[((BlockItemPackets1_11)BlockItemPackets1_11.this).getNewSlotId((ChestedHorseStorage)storage, (int)i)] = stacks[i];
                                stacks[i] = BlockItemPackets1_11.this.getNewItem(storage, i, stacks[i]);
                            }
                            wrapper.set(Type.ITEM_ARRAY, 0, stacks);
                        }
                    }
                });
            }
        });
        this.registerEntityEquipment(ClientboundPackets1_9_3.ENTITY_EQUIPMENT, Type.ITEM);
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.PLUGIN_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (wrapper.get(Type.STRING, 0).equalsIgnoreCase("MC|TrList")) {
                            wrapper.passthrough(Type.INT);
                            int size = wrapper.passthrough(Type.UNSIGNED_BYTE).shortValue();
                            for (int i = 0; i < size; ++i) {
                                wrapper.write(Type.ITEM, BlockItemPackets1_11.this.handleItemToClient(wrapper.read(Type.ITEM)));
                                wrapper.write(Type.ITEM, BlockItemPackets1_11.this.handleItemToClient(wrapper.read(Type.ITEM)));
                                boolean secondItem = wrapper.passthrough(Type.BOOLEAN);
                                if (secondItem) {
                                    wrapper.write(Type.ITEM, BlockItemPackets1_11.this.handleItemToClient(wrapper.read(Type.ITEM)));
                                }
                                wrapper.passthrough(Type.BOOLEAN);
                                wrapper.passthrough(Type.INT);
                                wrapper.passthrough(Type.INT);
                            }
                        }
                    }
                });
            }
        });
        ((Protocol1_10To1_11)this.protocol).registerServerbound(ServerboundPackets1_9_3.CLICK_WINDOW, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Type.VAR_INT);
                this.map(Type.ITEM);
                this.handler(BlockItemPackets1_11.this.itemToServerHandler(Type.ITEM));
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                            Optional horse = BlockItemPackets1_11.this.getChestedHorse(wrapper.user());
                            if (!horse.isPresent()) {
                                return;
                            }
                            ChestedHorseStorage storage = (ChestedHorseStorage)horse.get();
                            short clickSlot = wrapper.get(Type.SHORT, 0);
                            int correctSlot = BlockItemPackets1_11.this.getOldSlotId(storage, clickSlot);
                            wrapper.set(Type.SHORT, 0, Integer.valueOf(correctSlot).shortValue());
                        }
                    }
                });
            }
        });
        this.registerCreativeInvAction(ServerboundPackets1_9_3.CREATIVE_INVENTORY_ACTION, Type.ITEM);
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.CHUNK_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                        Chunk1_9_3_4Type type = new Chunk1_9_3_4Type(clientWorld);
                        Chunk chunk = wrapper.passthrough(type);
                        BlockItemPackets1_11.this.handleChunk(chunk);
                        for (CompoundTag tag : chunk.getBlockEntities()) {
                            String id;
                            Object idTag = tag.get("id");
                            if (!(idTag instanceof StringTag) || !(id = (String)((Tag)idTag).getValue()).equals("minecraft:sign")) continue;
                            ((StringTag)idTag).setValue("Sign");
                        }
                    }
                });
            }
        });
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.BLOCK_CHANGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int idx = wrapper.get(Type.VAR_INT, 0);
                        wrapper.set(Type.VAR_INT, 0, BlockItemPackets1_11.this.handleBlockID(idx));
                    }
                });
            }
        });
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.MULTI_BLOCK_CHANGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.BLOCK_CHANGE_RECORD_ARRAY);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        for (BlockChangeRecord record : wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0)) {
                            record.setBlockId(BlockItemPackets1_11.this.handleBlockID(record.getBlockId()));
                        }
                    }
                });
            }
        });
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.BLOCK_ENTITY_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.NBT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        if (wrapper.get(Type.UNSIGNED_BYTE, 0) == 10) {
                            wrapper.cancel();
                        }
                        if (wrapper.get(Type.UNSIGNED_BYTE, 0) == 1) {
                            CompoundTag tag = wrapper.get(Type.NBT, 0);
                            EntityIdRewriter.toClientSpawner(tag, true);
                        }
                    }
                });
            }
        });
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.OPEN_WINDOW, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.STRING);
                this.map(Type.COMPONENT);
                this.map(Type.UNSIGNED_BYTE);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int entityId = -1;
                        if (wrapper.get(Type.STRING, 0).equals("EntityHorse")) {
                            entityId = wrapper.passthrough(Type.INT);
                        }
                        String inventory = wrapper.get(Type.STRING, 0);
                        WindowTracker windowTracker = wrapper.user().get(WindowTracker.class);
                        windowTracker.setInventory(inventory);
                        windowTracker.setEntityId(entityId);
                        if (BlockItemPackets1_11.this.isLlama(wrapper.user())) {
                            wrapper.set(Type.UNSIGNED_BYTE, 1, (short)17);
                        }
                    }
                });
            }
        });
        ((Protocol1_10To1_11)this.protocol).registerClientbound(ClientboundPackets1_9_3.CLOSE_WINDOW, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        WindowTracker windowTracker = wrapper.user().get(WindowTracker.class);
                        windowTracker.setInventory(null);
                        windowTracker.setEntityId(-1);
                    }
                });
            }
        });
        ((Protocol1_10To1_11)this.protocol).registerServerbound(ServerboundPackets1_9_3.CLOSE_WINDOW, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        WindowTracker windowTracker = wrapper.user().get(WindowTracker.class);
                        windowTracker.setInventory(null);
                        windowTracker.setEntityId(-1);
                    }
                });
            }
        });
        ((Protocol1_10To1_11)this.protocol).getEntityRewriter().filter().handler((event, meta) -> {
            if (meta.metaType().type().equals(Type.ITEM)) {
                meta.setValue(this.handleItemToClient((Item)meta.getValue()));
            }
        });
    }

    @Override
    protected void registerRewrites() {
        MappedLegacyBlockItem data = this.replacementData.computeIfAbsent(52, s -> new MappedLegacyBlockItem(52, -1, null, false));
        data.setBlockEntityHandler((b, tag) -> {
            EntityIdRewriter.toClientSpawner(tag, true);
            return tag;
        });
        this.enchantmentRewriter = new LegacyEnchantmentRewriter(this.nbtTagName);
        this.enchantmentRewriter.registerEnchantment(71, "\u00a7cCurse of Vanishing");
        this.enchantmentRewriter.registerEnchantment(10, "\u00a7cCurse of Binding");
        this.enchantmentRewriter.setHideLevelForEnchants(71, 10);
    }

    @Override
    public Item handleItemToClient(Item item) {
        if (item == null) {
            return null;
        }
        super.handleItemToClient(item);
        CompoundTag tag = item.tag();
        if (tag == null) {
            return item;
        }
        EntityIdRewriter.toClientItem(item, true);
        if (tag.get("ench") instanceof ListTag) {
            this.enchantmentRewriter.rewriteEnchantmentsToClient(tag, false);
        }
        if (tag.get("StoredEnchantments") instanceof ListTag) {
            this.enchantmentRewriter.rewriteEnchantmentsToClient(tag, true);
        }
        return item;
    }

    @Override
    public Item handleItemToServer(Item item) {
        if (item == null) {
            return null;
        }
        super.handleItemToServer(item);
        CompoundTag tag = item.tag();
        if (tag == null) {
            return item;
        }
        EntityIdRewriter.toServerItem(item, true);
        if (tag.contains(this.nbtTagName + "|ench")) {
            this.enchantmentRewriter.rewriteEnchantmentsToServer(tag, false);
        }
        if (tag.contains(this.nbtTagName + "|StoredEnchantments")) {
            this.enchantmentRewriter.rewriteEnchantmentsToServer(tag, true);
        }
        return item;
    }

    private boolean isLlama(UserConnection user) {
        WindowTracker tracker = user.get(WindowTracker.class);
        if (tracker.getInventory() != null && tracker.getInventory().equals("EntityHorse")) {
            Object entTracker = user.getEntityTracker(Protocol1_10To1_11.class);
            StoredEntityData entityData = entTracker.entityData(tracker.getEntityId());
            return entityData != null && entityData.type().is((EntityType)Entity1_11Types.EntityType.LIAMA);
        }
        return false;
    }

    private Optional<ChestedHorseStorage> getChestedHorse(UserConnection user) {
        Object entTracker;
        StoredEntityData entityData;
        WindowTracker tracker = user.get(WindowTracker.class);
        if (tracker.getInventory() != null && tracker.getInventory().equals("EntityHorse") && (entityData = (entTracker = user.getEntityTracker(Protocol1_10To1_11.class)).entityData(tracker.getEntityId())) != null) {
            return Optional.of(entityData.get(ChestedHorseStorage.class));
        }
        return Optional.empty();
    }

    private int getNewSlotId(ChestedHorseStorage storage, int slotId) {
        int totalSlots = !storage.isChested() ? 38 : 53;
        int strength = storage.isChested() ? storage.getLiamaStrength() : 0;
        int startNonExistingFormula = 2 + 3 * strength;
        int offsetForm = 15 - 3 * strength;
        if (slotId >= startNonExistingFormula && totalSlots > slotId + offsetForm) {
            return offsetForm + slotId;
        }
        if (slotId == 1) {
            return 0;
        }
        return slotId;
    }

    private int getOldSlotId(ChestedHorseStorage storage, int slotId) {
        int strength = storage.isChested() ? storage.getLiamaStrength() : 0;
        int startNonExistingFormula = 2 + 3 * strength;
        int endNonExistingFormula = 2 + 3 * (storage.isChested() ? 5 : 0);
        int offsetForm = endNonExistingFormula - startNonExistingFormula;
        if (slotId == 1 || slotId >= startNonExistingFormula && slotId < endNonExistingFormula) {
            return 0;
        }
        if (slotId >= endNonExistingFormula) {
            return slotId - offsetForm;
        }
        if (slotId == 0) {
            return 1;
        }
        return slotId;
    }

    private Item getNewItem(ChestedHorseStorage storage, int slotId, Item current) {
        int strength = storage.isChested() ? storage.getLiamaStrength() : 0;
        int startNonExistingFormula = 2 + 3 * strength;
        int endNonExistingFormula = 2 + 3 * (storage.isChested() ? 5 : 0);
        if (slotId >= startNonExistingFormula && slotId < endNonExistingFormula) {
            return new DataItem(166, 1, 0, this.getNamedTag("\u00a74SLOT DISABLED"));
        }
        if (slotId == 1) {
            return null;
        }
        return current;
    }
}

