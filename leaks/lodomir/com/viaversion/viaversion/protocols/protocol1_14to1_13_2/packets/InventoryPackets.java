/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Sets
 */
package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets;

import com.google.common.collect.Sets;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.item.DataItem;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.DoubleTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.RecipeRewriter1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ServerboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import com.viaversion.viaversion.rewriter.ComponentRewriter;
import com.viaversion.viaversion.rewriter.ItemRewriter;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class InventoryPackets
extends ItemRewriter<Protocol1_14To1_13_2> {
    private static final String NBT_TAG_NAME = "ViaVersion|" + Protocol1_14To1_13_2.class.getSimpleName();
    private static final Set<String> REMOVED_RECIPE_TYPES = Sets.newHashSet((Object[])new String[]{"crafting_special_banneraddpattern", "crafting_special_repairitem"});
    private static final ComponentRewriter COMPONENT_REWRITER = new ComponentRewriter(){

        @Override
        protected void handleTranslate(JsonObject object, String translate) {
            super.handleTranslate(object, translate);
            if (translate.startsWith("block.") && translate.endsWith(".name")) {
                object.addProperty("translate", translate.substring(0, translate.length() - 5));
            }
        }
    };

    public InventoryPackets(Protocol1_14To1_13_2 protocol) {
        super(protocol);
    }

    @Override
    public void registerPackets() {
        this.registerSetCooldown(ClientboundPackets1_13.COOLDOWN);
        this.registerAdvancements(ClientboundPackets1_13.ADVANCEMENTS, Type.FLAT_VAR_INT_ITEM);
        ((Protocol1_14To1_13_2)this.protocol).registerClientbound(ClientboundPackets1_13.OPEN_WINDOW, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Short windowId = wrapper.read(Type.UNSIGNED_BYTE);
                        String type = wrapper.read(Type.STRING);
                        JsonElement title = wrapper.read(Type.COMPONENT);
                        COMPONENT_REWRITER.processText(title);
                        Short slots = wrapper.read(Type.UNSIGNED_BYTE);
                        if (type.equals("EntityHorse")) {
                            wrapper.setId(31);
                            int entityId = wrapper.read(Type.INT);
                            wrapper.write(Type.UNSIGNED_BYTE, windowId);
                            wrapper.write(Type.VAR_INT, slots.intValue());
                            wrapper.write(Type.INT, entityId);
                        } else {
                            wrapper.setId(46);
                            wrapper.write(Type.VAR_INT, windowId.intValue());
                            int typeId = -1;
                            switch (type) {
                                case "minecraft:crafting_table": {
                                    typeId = 11;
                                    break;
                                }
                                case "minecraft:furnace": {
                                    typeId = 13;
                                    break;
                                }
                                case "minecraft:dropper": 
                                case "minecraft:dispenser": {
                                    typeId = 6;
                                    break;
                                }
                                case "minecraft:enchanting_table": {
                                    typeId = 12;
                                    break;
                                }
                                case "minecraft:brewing_stand": {
                                    typeId = 10;
                                    break;
                                }
                                case "minecraft:villager": {
                                    typeId = 18;
                                    break;
                                }
                                case "minecraft:beacon": {
                                    typeId = 8;
                                    break;
                                }
                                case "minecraft:anvil": {
                                    typeId = 7;
                                    break;
                                }
                                case "minecraft:hopper": {
                                    typeId = 15;
                                    break;
                                }
                                case "minecraft:shulker_box": {
                                    typeId = 19;
                                    break;
                                }
                                default: {
                                    if (slots <= 0 || slots > 54) break;
                                    typeId = slots / 9 - 1;
                                }
                            }
                            if (typeId == -1) {
                                Via.getPlatform().getLogger().warning("Can't open inventory for 1.14 player! Type: " + type + " Size: " + slots);
                            }
                            wrapper.write(Type.VAR_INT, typeId);
                            wrapper.write(Type.COMPONENT, title);
                        }
                    }
                });
            }
        });
        this.registerWindowItems(ClientboundPackets1_13.WINDOW_ITEMS, Type.FLAT_VAR_INT_ITEM_ARRAY);
        this.registerSetSlot(ClientboundPackets1_13.SET_SLOT, Type.FLAT_VAR_INT_ITEM);
        ((Protocol1_14To1_13_2)this.protocol).registerClientbound(ClientboundPackets1_13.PLUGIN_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        String channel = wrapper.get(Type.STRING, 0);
                        if (channel.equals("minecraft:trader_list") || channel.equals("trader_list")) {
                            wrapper.setId(39);
                            wrapper.resetReader();
                            wrapper.read(Type.STRING);
                            int windowId = wrapper.read(Type.INT);
                            EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                            tracker.setLatestTradeWindowId(windowId);
                            wrapper.write(Type.VAR_INT, windowId);
                            int size = wrapper.passthrough(Type.UNSIGNED_BYTE).shortValue();
                            for (int i = 0; i < size; ++i) {
                                InventoryPackets.this.handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                                InventoryPackets.this.handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                                boolean secondItem = wrapper.passthrough(Type.BOOLEAN);
                                if (secondItem) {
                                    InventoryPackets.this.handleItemToClient(wrapper.passthrough(Type.FLAT_VAR_INT_ITEM));
                                }
                                wrapper.passthrough(Type.BOOLEAN);
                                wrapper.passthrough(Type.INT);
                                wrapper.passthrough(Type.INT);
                                wrapper.write(Type.INT, 0);
                                wrapper.write(Type.INT, 0);
                                wrapper.write(Type.FLOAT, Float.valueOf(0.0f));
                            }
                            wrapper.write(Type.VAR_INT, 0);
                            wrapper.write(Type.VAR_INT, 0);
                            wrapper.write(Type.BOOLEAN, false);
                        } else if (channel.equals("minecraft:book_open") || channel.equals("book_open")) {
                            int hand = wrapper.read(Type.VAR_INT);
                            wrapper.clearPacket();
                            wrapper.setId(45);
                            wrapper.write(Type.VAR_INT, hand);
                        }
                    }
                });
            }
        });
        this.registerEntityEquipment(ClientboundPackets1_13.ENTITY_EQUIPMENT, Type.FLAT_VAR_INT_ITEM);
        final RecipeRewriter1_13_2 recipeRewriter = new RecipeRewriter1_13_2(this.protocol);
        ((Protocol1_14To1_13_2)this.protocol).registerClientbound(ClientboundPackets1_13.DECLARE_RECIPES, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int size = wrapper.passthrough(Type.VAR_INT);
                    int deleted = 0;
                    for (int i = 0; i < size; ++i) {
                        String id = wrapper.read(Type.STRING);
                        String type = wrapper.read(Type.STRING);
                        if (REMOVED_RECIPE_TYPES.contains(type)) {
                            ++deleted;
                            continue;
                        }
                        wrapper.write(Type.STRING, type);
                        wrapper.write(Type.STRING, id);
                        recipeRewriter.handle(wrapper, type);
                    }
                    wrapper.set(Type.VAR_INT, 0, size - deleted);
                });
            }
        });
        this.registerClickWindow(ServerboundPackets1_14.CLICK_WINDOW, Type.FLAT_VAR_INT_ITEM);
        ((Protocol1_14To1_13_2)this.protocol).registerServerbound(ServerboundPackets1_14.SELECT_TRADE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        PacketWrapper resyncPacket = wrapper.create(8);
                        EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                        resyncPacket.write(Type.UNSIGNED_BYTE, (short)tracker.getLatestTradeWindowId());
                        resyncPacket.write(Type.SHORT, (short)-999);
                        resyncPacket.write(Type.BYTE, (byte)2);
                        resyncPacket.write(Type.SHORT, (short)ThreadLocalRandom.current().nextInt());
                        resyncPacket.write(Type.VAR_INT, 5);
                        CompoundTag tag = new CompoundTag();
                        tag.put("force_resync", new DoubleTag(Double.NaN));
                        resyncPacket.write(Type.FLAT_VAR_INT_ITEM, new DataItem(1, 1, 0, tag));
                        resyncPacket.scheduleSendToServer(Protocol1_14To1_13_2.class);
                    }
                });
            }
        });
        this.registerCreativeInvAction(ServerboundPackets1_14.CREATIVE_INVENTORY_ACTION, Type.FLAT_VAR_INT_ITEM);
        this.registerSpawnParticle(ClientboundPackets1_13.SPAWN_PARTICLE, Type.FLAT_VAR_INT_ITEM, Type.FLOAT);
    }

    @Override
    public Item handleItemToClient(Item item) {
        CompoundTag display;
        Object loreTag;
        if (item == null) {
            return null;
        }
        item.setIdentifier(Protocol1_14To1_13_2.MAPPINGS.getNewItemId(item.identifier()));
        if (item.tag() == null) {
            return item;
        }
        Object displayTag = item.tag().get("display");
        if (displayTag instanceof CompoundTag && (loreTag = (display = (CompoundTag)displayTag).get("Lore")) instanceof ListTag) {
            ListTag lore = (ListTag)loreTag;
            display.put(NBT_TAG_NAME + "|Lore", new ListTag((List<Tag>)lore.clone().getValue()));
            for (Tag loreEntry : lore) {
                if (!(loreEntry instanceof StringTag)) continue;
                String jsonText = ChatRewriter.legacyTextToJsonString(((StringTag)loreEntry).getValue(), true);
                ((StringTag)loreEntry).setValue(jsonText);
            }
        }
        return item;
    }

    @Override
    public Item handleItemToServer(Item item) {
        CompoundTag display;
        Object loreTag;
        if (item == null) {
            return null;
        }
        item.setIdentifier(Protocol1_14To1_13_2.MAPPINGS.getOldItemId(item.identifier()));
        if (item.tag() == null) {
            return item;
        }
        Object displayTag = item.tag().get("display");
        if (displayTag instanceof CompoundTag && (loreTag = (display = (CompoundTag)displayTag).get("Lore")) instanceof ListTag) {
            ListTag lore = (ListTag)loreTag;
            ListTag savedLore = (ListTag)display.remove(NBT_TAG_NAME + "|Lore");
            if (savedLore != null) {
                display.put("Lore", new ListTag((List<Tag>)savedLore.getValue()));
            } else {
                for (Tag loreEntry : lore) {
                    if (!(loreEntry instanceof StringTag)) continue;
                    ((StringTag)loreEntry).setValue(ChatRewriter.jsonToLegacyText(((StringTag)loreEntry).getValue()));
                }
            }
        }
        return item;
    }
}

