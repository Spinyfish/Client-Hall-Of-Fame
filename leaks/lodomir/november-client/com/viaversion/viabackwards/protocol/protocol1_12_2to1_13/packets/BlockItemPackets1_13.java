/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.primitives.Ints
 */
package com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.packets;

import com.google.common.primitives.Ints;
import com.viaversion.viabackwards.ViaBackwards;
import com.viaversion.viabackwards.api.rewriters.EnchantmentRewriter;
import com.viaversion.viabackwards.api.rewriters.ItemRewriter;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.Protocol1_12_2To1_13;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.block_entity_handlers.FlowerPotHandler;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.providers.BackwardsBlockEntityProvider;
import com.viaversion.viabackwards.protocol.protocol1_12_2to1_13.storage.BackwardsBlockStorage;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.conversion.ConverterRegistry;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ByteTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.IntTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ShortTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ServerboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.BlockIdData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.SpawnEggRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class BlockItemPackets1_13
extends ItemRewriter<Protocol1_12_2To1_13> {
    private final Map<String, String> enchantmentMappings = new HashMap<String, String>();
    private final String extraNbtTag;

    public BlockItemPackets1_13(Protocol1_12_2To1_13 protocol) {
        super(protocol, null);
        this.extraNbtTag = "VB|" + protocol.getClass().getSimpleName() + "|2";
    }

    public static boolean isDamageable(int id) {
        return id >= 256 && id <= 259 || id == 261 || id >= 267 && id <= 279 || id >= 283 && id <= 286 || id >= 290 && id <= 294 || id >= 298 && id <= 317 || id == 346 || id == 359 || id == 398 || id == 442 || id == 443;
    }

    @Override
    protected void registerPackets() {
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.COOLDOWN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int itemId = wrapper.read(Type.VAR_INT);
                        int oldId = ((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getItemMappings().get(itemId);
                        if (oldId != -1) {
                            Optional<String> eggEntityId = SpawnEggRewriter.getEntityId(oldId);
                            itemId = eggEntityId.isPresent() ? 25100288 : oldId >> 4 << 16 | oldId & 0xF;
                        }
                        wrapper.write(Type.VAR_INT, itemId);
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.BLOCK_ACTION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int blockId = wrapper.get(Type.VAR_INT, 0);
                        if (blockId == 73) {
                            blockId = 25;
                        } else if (blockId == 99) {
                            blockId = 33;
                        } else if (blockId == 92) {
                            blockId = 29;
                        } else if (blockId == 142) {
                            blockId = 54;
                        } else if (blockId == 305) {
                            blockId = 146;
                        } else if (blockId == 249) {
                            blockId = 130;
                        } else if (blockId == 257) {
                            blockId = 138;
                        } else if (blockId == 140) {
                            blockId = 52;
                        } else if (blockId == 472) {
                            blockId = 209;
                        } else if (blockId >= 483 && blockId <= 498) {
                            blockId = blockId - 483 + 219;
                        }
                        wrapper.set(Type.VAR_INT, 0, blockId);
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.BLOCK_ENTITY_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.NBT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        BackwardsBlockEntityProvider provider = Via.getManager().getProviders().get(BackwardsBlockEntityProvider.class);
                        if (wrapper.get(Type.UNSIGNED_BYTE, 0) == 5) {
                            wrapper.cancel();
                        }
                        wrapper.set(Type.NBT, 0, provider.transform(wrapper.user(), wrapper.get(Type.POSITION, 0), wrapper.get(Type.NBT, 0)));
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.UNLOAD_CHUNK, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int chunkMinX = wrapper.passthrough(Type.INT) << 4;
                        int chunkMinZ = wrapper.passthrough(Type.INT) << 4;
                        int chunkMaxX = chunkMinX + 15;
                        int chunkMaxZ = chunkMinZ + 15;
                        BackwardsBlockStorage blockStorage = wrapper.user().get(BackwardsBlockStorage.class);
                        blockStorage.getBlocks().entrySet().removeIf(entry -> {
                            Position position = (Position)entry.getKey();
                            return position.getX() >= chunkMinX && position.getZ() >= chunkMinZ && position.getX() <= chunkMaxX && position.getZ() <= chunkMaxZ;
                        });
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.BLOCK_CHANGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int blockState = wrapper.read(Type.VAR_INT);
                        Position position = wrapper.get(Type.POSITION, 0);
                        BackwardsBlockStorage storage = wrapper.user().get(BackwardsBlockStorage.class);
                        storage.checkAndStore(position, blockState);
                        wrapper.write(Type.VAR_INT, ((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getNewBlockStateId(blockState));
                        BlockItemPackets1_13.flowerPotSpecialTreatment(wrapper.user(), blockState, position);
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.MULTI_BLOCK_CHANGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.BLOCK_CHANGE_RECORD_ARRAY);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        BackwardsBlockStorage storage = wrapper.user().get(BackwardsBlockStorage.class);
                        for (BlockChangeRecord record : wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0)) {
                            int chunkX = wrapper.get(Type.INT, 0);
                            int chunkZ = wrapper.get(Type.INT, 1);
                            int block = record.getBlockId();
                            Position position = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                            storage.checkAndStore(position, block);
                            BlockItemPackets1_13.flowerPotSpecialTreatment(wrapper.user(), block, position);
                            record.setBlockId(((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getNewBlockStateId(block));
                        }
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.WINDOW_ITEMS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.FLAT_ITEM_ARRAY, Type.ITEM_ARRAY);
                this.handler(BlockItemPackets1_13.this.itemArrayHandler(Type.ITEM_ARRAY));
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.SET_SLOT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.FLAT_ITEM, Type.ITEM);
                this.handler(BlockItemPackets1_13.this.itemToClientHandler(Type.ITEM));
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.CHUNK_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int i;
                    ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    Chunk1_9_3_4Type type_old = new Chunk1_9_3_4Type(clientWorld);
                    Chunk1_13Type type = new Chunk1_13Type(clientWorld);
                    Chunk chunk = wrapper.read(type);
                    BackwardsBlockEntityProvider provider = Via.getManager().getProviders().get(BackwardsBlockEntityProvider.class);
                    BackwardsBlockStorage storage = wrapper.user().get(BackwardsBlockStorage.class);
                    for (CompoundTag tag : chunk.getBlockEntities()) {
                        int sectionIndex;
                        String id;
                        Object idTag = tag.get("id");
                        if (idTag == null || !provider.isHandled(id = (String)((Tag)idTag).getValue()) || (sectionIndex = ((NumberTag)tag.get("y")).asInt() >> 4) < 0 || sectionIndex > 15) continue;
                        ChunkSection section = chunk.getSections()[sectionIndex];
                        int x = ((NumberTag)tag.get("x")).asInt();
                        int y = ((NumberTag)tag.get("y")).asInt();
                        int z = ((NumberTag)tag.get("z")).asInt();
                        Position position = new Position(x, (short)y, z);
                        int block = section.getFlatBlock(x & 0xF, y & 0xF, z & 0xF);
                        storage.checkAndStore(position, block);
                        provider.transform(wrapper.user(), position, tag);
                    }
                    for (i = 0; i < chunk.getSections().length; ++i) {
                        ChunkSection section = chunk.getSections()[i];
                        if (section == null) continue;
                        for (int y = 0; y < 16; ++y) {
                            for (int z = 0; z < 16; ++z) {
                                for (int x = 0; x < 16; ++x) {
                                    int block = section.getFlatBlock(x, y, z);
                                    if (!FlowerPotHandler.isFlowah(block)) continue;
                                    Position pos = new Position(x + (chunk.getX() << 4), (short)(y + (i << 4)), z + (chunk.getZ() << 4));
                                    storage.checkAndStore(pos, block);
                                    CompoundTag nbt = provider.transform(wrapper.user(), pos, "minecraft:flower_pot");
                                    chunk.getBlockEntities().add(nbt);
                                }
                            }
                        }
                        for (int p = 0; p < section.getPaletteSize(); ++p) {
                            int old = section.getPaletteEntry(p);
                            if (old == 0) continue;
                            int oldId = ((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getNewBlockStateId(old);
                            section.setPaletteEntry(p, oldId);
                        }
                    }
                    if (chunk.isBiomeData()) {
                        for (i = 0; i < 256; ++i) {
                            int biome = chunk.getBiomeData()[i];
                            int newId = -1;
                            switch (biome) {
                                case 40: 
                                case 41: 
                                case 42: 
                                case 43: {
                                    newId = 9;
                                    break;
                                }
                                case 47: 
                                case 48: 
                                case 49: {
                                    newId = 24;
                                    break;
                                }
                                case 50: {
                                    newId = 10;
                                    break;
                                }
                                case 44: 
                                case 45: 
                                case 46: {
                                    newId = 0;
                                }
                            }
                            if (newId == -1) continue;
                            chunk.getBiomeData()[i] = newId;
                        }
                    }
                    wrapper.write(type_old, chunk);
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.EFFECT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.POSITION);
                this.map(Type.INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int id = wrapper.get(Type.INT, 0);
                        int data = wrapper.get(Type.INT, 1);
                        if (id == 1010) {
                            wrapper.set(Type.INT, 1, ((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getItemMappings().get(data) >> 4);
                        } else if (id == 2001) {
                            data = ((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getNewBlockStateId(data);
                            int blockId = data >> 4;
                            int blockData = data & 0xF;
                            wrapper.set(Type.INT, 1, blockId & 0xFFF | blockData << 12);
                        }
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.MAP_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.BYTE);
                this.map(Type.BOOLEAN);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int iconCount = wrapper.passthrough(Type.VAR_INT);
                        for (int i = 0; i < iconCount; ++i) {
                            int type = wrapper.read(Type.VAR_INT);
                            byte x = wrapper.read(Type.BYTE);
                            byte z = wrapper.read(Type.BYTE);
                            byte direction = wrapper.read(Type.BYTE);
                            if (wrapper.read(Type.BOOLEAN).booleanValue()) {
                                wrapper.read(Type.COMPONENT);
                            }
                            if (type > 9) {
                                wrapper.set(Type.VAR_INT, 1, wrapper.get(Type.VAR_INT, 1) - 1);
                                continue;
                            }
                            wrapper.write(Type.BYTE, (byte)(type << 4 | direction & 0xF));
                            wrapper.write(Type.BYTE, x);
                            wrapper.write(Type.BYTE, z);
                        }
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.ENTITY_EQUIPMENT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.VAR_INT);
                this.map(Type.FLAT_ITEM, Type.ITEM);
                this.handler(BlockItemPackets1_13.this.itemToClientHandler(Type.ITEM));
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerClientbound(ClientboundPackets1_13.WINDOW_PROPERTY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.handler(wrapper -> {
                    short property = wrapper.get(Type.SHORT, 0);
                    if (property >= 4 && property <= 6) {
                        short oldId = wrapper.get(Type.SHORT, 1);
                        wrapper.set(Type.SHORT, 1, (short)((Protocol1_12_2To1_13)BlockItemPackets1_13.this.protocol).getMappingData().getEnchantmentMappings().getNewId(oldId));
                    }
                });
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerServerbound(ServerboundPackets1_12_1.CREATIVE_INVENTORY_ACTION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.SHORT);
                this.map(Type.ITEM, Type.FLAT_ITEM);
                this.handler(BlockItemPackets1_13.this.itemToServerHandler(Type.FLAT_ITEM));
            }
        });
        ((Protocol1_12_2To1_13)this.protocol).registerServerbound(ServerboundPackets1_12_1.CLICK_WINDOW, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.SHORT);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Type.VAR_INT);
                this.map(Type.ITEM, Type.FLAT_ITEM);
                this.handler(BlockItemPackets1_13.this.itemToServerHandler(Type.FLAT_ITEM));
            }
        });
    }

    @Override
    protected void registerRewrites() {
        this.enchantmentMappings.put("minecraft:loyalty", "\u00a77Loyalty");
        this.enchantmentMappings.put("minecraft:impaling", "\u00a77Impaling");
        this.enchantmentMappings.put("minecraft:riptide", "\u00a77Riptide");
        this.enchantmentMappings.put("minecraft:channeling", "\u00a77Channeling");
    }

    @Override
    public Item handleItemToClient(Item item) {
        Object originalIdTag;
        if (item == null) {
            return null;
        }
        int originalId = item.identifier();
        Integer rawId = null;
        boolean gotRawIdFromTag = false;
        CompoundTag tag = item.tag();
        if (tag != null && (originalIdTag = tag.remove(this.extraNbtTag)) != null) {
            rawId = ((NumberTag)originalIdTag).asInt();
            gotRawIdFromTag = true;
        }
        if (rawId == null) {
            super.handleItemToClient(item);
            if (item.identifier() == -1) {
                if (originalId == 362) {
                    rawId = 0xE50000;
                } else {
                    if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                        ViaBackwards.getPlatform().getLogger().warning("Failed to get 1.12 item for " + originalId);
                    }
                    rawId = 65536;
                }
            } else {
                if (tag == null) {
                    tag = item.tag();
                }
                rawId = this.itemIdToRaw(item.identifier(), item, tag);
            }
        }
        item.setIdentifier(rawId >> 16);
        item.setData((short)(rawId & 0xFFFF));
        if (tag != null) {
            StringTag name;
            if (BlockItemPackets1_13.isDamageable(item.identifier())) {
                Object damageTag = tag.remove("Damage");
                if (!gotRawIdFromTag && damageTag instanceof IntTag) {
                    item.setData((short)((Integer)((Tag)damageTag).getValue()).intValue());
                }
            }
            if (item.identifier() == 358) {
                Object mapTag = tag.remove("map");
                if (!gotRawIdFromTag && mapTag instanceof IntTag) {
                    item.setData((short)((Integer)((Tag)mapTag).getValue()).intValue());
                }
            }
            this.invertShieldAndBannerId(item, tag);
            CompoundTag display = (CompoundTag)tag.get("display");
            if (display != null && (name = (StringTag)display.get("Name")) != null) {
                display.put(this.extraNbtTag + "|Name", new StringTag(name.getValue()));
                name.setValue(ChatRewriter.jsonToLegacyText(name.getValue()));
            }
            this.rewriteEnchantmentsToClient(tag, false);
            this.rewriteEnchantmentsToClient(tag, true);
            this.rewriteCanPlaceToClient(tag, "CanPlaceOn");
            this.rewriteCanPlaceToClient(tag, "CanDestroy");
        }
        return item;
    }

    private int itemIdToRaw(int oldId, Item item, CompoundTag tag) {
        Optional<String> eggEntityId = SpawnEggRewriter.getEntityId(oldId);
        if (eggEntityId.isPresent()) {
            if (tag == null) {
                tag = new CompoundTag();
                item.setTag(tag);
            }
            if (!tag.contains("EntityTag")) {
                CompoundTag entityTag = new CompoundTag();
                entityTag.put("id", new StringTag(eggEntityId.get()));
                tag.put("EntityTag", entityTag);
            }
            return 25100288;
        }
        return oldId >> 4 << 16 | oldId & 0xF;
    }

    private void rewriteCanPlaceToClient(CompoundTag tag, String tagName) {
        if (!(tag.get(tagName) instanceof ListTag)) {
            return;
        }
        ListTag blockTag = (ListTag)tag.get(tagName);
        if (blockTag == null) {
            return;
        }
        ListTag newCanPlaceOn = new ListTag(StringTag.class);
        tag.put(this.extraNbtTag + "|" + tagName, ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(blockTag)));
        for (Tag oldTag : blockTag) {
            String[] newValues;
            Object value = oldTag.getValue();
            String[] stringArray = newValues = value instanceof String ? BlockIdData.fallbackReverseMapping.get(((String)value).replace("minecraft:", "")) : null;
            if (newValues != null) {
                for (String newValue : newValues) {
                    newCanPlaceOn.add(new StringTag(newValue));
                }
                continue;
            }
            newCanPlaceOn.add(oldTag);
        }
        tag.put(tagName, newCanPlaceOn);
    }

    private void rewriteEnchantmentsToClient(CompoundTag tag, boolean storedEnch) {
        String key = storedEnch ? "StoredEnchantments" : "Enchantments";
        ListTag enchantments = (ListTag)tag.get(key);
        if (enchantments == null) {
            return;
        }
        ListTag noMapped = new ListTag(CompoundTag.class);
        ListTag newEnchantments = new ListTag(CompoundTag.class);
        ArrayList<Tag> lore = new ArrayList<Tag>();
        boolean hasValidEnchants = false;
        for (Tag enchantmentEntryTag : enchantments.clone()) {
            CompoundTag enchantmentEntry = (CompoundTag)enchantmentEntryTag;
            Object idTag = enchantmentEntry.get("id");
            if (!(idTag instanceof StringTag)) continue;
            String newId = (String)((Tag)idTag).getValue();
            int levelValue = ((NumberTag)enchantmentEntry.get("lvl")).asInt();
            int level = levelValue < Short.MAX_VALUE ? (int)levelValue : Short.MAX_VALUE;
            String mappedEnchantmentId = this.enchantmentMappings.get(newId);
            if (mappedEnchantmentId != null) {
                lore.add(new StringTag(mappedEnchantmentId + " " + EnchantmentRewriter.getRomanNumber(level)));
                noMapped.add(enchantmentEntry);
                continue;
            }
            if (newId.isEmpty()) continue;
            Short oldId = (Short)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().inverse().get((Object)newId);
            if (oldId == null) {
                if (!newId.startsWith("viaversion:legacy/")) {
                    noMapped.add(enchantmentEntry);
                    if (ViaBackwards.getConfig().addCustomEnchantsToLore()) {
                        String name = newId;
                        int index = name.indexOf(58) + 1;
                        if (index != 0 && index != name.length()) {
                            name = name.substring(index);
                        }
                        name = "\u00a77" + Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase(Locale.ENGLISH);
                        lore.add(new StringTag(name + " " + EnchantmentRewriter.getRomanNumber(level)));
                    }
                    if (!Via.getManager().isDebug()) continue;
                    ViaBackwards.getPlatform().getLogger().warning("Found unknown enchant: " + newId);
                    continue;
                }
                oldId = Short.valueOf(newId.substring(18));
            }
            if (level != 0) {
                hasValidEnchants = true;
            }
            CompoundTag newEntry = new CompoundTag();
            newEntry.put("id", new ShortTag(oldId));
            newEntry.put("lvl", new ShortTag((short)level));
            newEnchantments.add(newEntry);
        }
        if (!storedEnch && !hasValidEnchants) {
            IntTag hideFlags = (IntTag)tag.get("HideFlags");
            if (hideFlags == null) {
                hideFlags = new IntTag();
                tag.put(this.extraNbtTag + "|DummyEnchant", new ByteTag());
            } else {
                tag.put(this.extraNbtTag + "|OldHideFlags", new IntTag(hideFlags.asByte()));
            }
            if (newEnchantments.size() == 0) {
                CompoundTag enchEntry = new CompoundTag();
                enchEntry.put("id", new ShortTag(0));
                enchEntry.put("lvl", new ShortTag(0));
                newEnchantments.add(enchEntry);
            }
            int value = hideFlags.asByte() | 1;
            hideFlags.setValue(value);
            tag.put("HideFlags", hideFlags);
        }
        if (noMapped.size() != 0) {
            tag.put(this.extraNbtTag + "|" + key, noMapped);
            if (!lore.isEmpty()) {
                ListTag loreTag;
                CompoundTag display = (CompoundTag)tag.get("display");
                if (display == null) {
                    display = new CompoundTag();
                    tag.put("display", display);
                }
                if ((loreTag = (ListTag)display.get("Lore")) == null) {
                    loreTag = new ListTag(StringTag.class);
                    display.put("Lore", loreTag);
                    tag.put(this.extraNbtTag + "|DummyLore", new ByteTag());
                } else if (loreTag.size() != 0) {
                    ListTag oldLore = new ListTag(StringTag.class);
                    for (Tag value : loreTag) {
                        oldLore.add(value.clone());
                    }
                    tag.put(this.extraNbtTag + "|OldLore", oldLore);
                    lore.addAll((Collection<Tag>)loreTag.getValue());
                }
                loreTag.setValue(lore);
            }
        }
        tag.remove("Enchantments");
        tag.put(storedEnch ? key : "ench", newEnchantments);
    }

    @Override
    public Item handleItemToServer(Item item) {
        if (item == null) {
            return null;
        }
        CompoundTag tag = item.tag();
        int originalId = item.identifier() << 16 | item.data() & 0xFFFF;
        int rawId = item.identifier() << 4 | item.data() & 0xF;
        if (BlockItemPackets1_13.isDamageable(item.identifier())) {
            if (tag == null) {
                tag = new CompoundTag();
                item.setTag(tag);
            }
            tag.put("Damage", new IntTag(item.data()));
        }
        if (item.identifier() == 358) {
            if (tag == null) {
                tag = new CompoundTag();
                item.setTag(tag);
            }
            tag.put("map", new IntTag(item.data()));
        }
        if (tag != null) {
            CompoundTag displayTag;
            StringTag name;
            this.invertShieldAndBannerId(item, tag);
            Object display = tag.get("display");
            if (display instanceof CompoundTag && (name = (StringTag)(displayTag = (CompoundTag)display).get("Name")) != null) {
                StringTag via = (StringTag)displayTag.remove(this.extraNbtTag + "|Name");
                name.setValue(via != null ? via.getValue() : ChatRewriter.legacyTextToJsonString(name.getValue()));
            }
            this.rewriteEnchantmentsToServer(tag, false);
            this.rewriteEnchantmentsToServer(tag, true);
            this.rewriteCanPlaceToServer(tag, "CanPlaceOn");
            this.rewriteCanPlaceToServer(tag, "CanDestroy");
            if (item.identifier() == 383) {
                StringTag identifier;
                CompoundTag entityTag = (CompoundTag)tag.get("EntityTag");
                if (entityTag != null && (identifier = (StringTag)entityTag.get("id")) != null) {
                    rawId = SpawnEggRewriter.getSpawnEggId(identifier.getValue());
                    if (rawId == -1) {
                        rawId = 25100288;
                    } else {
                        entityTag.remove("id");
                        if (entityTag.isEmpty()) {
                            tag.remove("EntityTag");
                        }
                    }
                } else {
                    rawId = 25100288;
                }
            }
            if (tag.isEmpty()) {
                tag = null;
                item.setTag(null);
            }
        }
        int identifier = item.identifier();
        item.setIdentifier(rawId);
        super.handleItemToServer(item);
        if (item.identifier() != rawId && item.identifier() != -1) {
            return item;
        }
        item.setIdentifier(identifier);
        int newId = -1;
        if (!((Protocol1_12_2To1_13)this.protocol).getMappingData().getItemMappings().inverse().containsKey(rawId)) {
            if (!BlockItemPackets1_13.isDamageable(item.identifier()) && item.identifier() != 358) {
                if (tag == null) {
                    tag = new CompoundTag();
                    item.setTag(tag);
                }
                tag.put(this.extraNbtTag, new IntTag(originalId));
            }
            if (item.identifier() == 229) {
                newId = 362;
            } else if (item.identifier() == 31 && item.data() == 0) {
                rawId = 512;
            } else if (((Protocol1_12_2To1_13)this.protocol).getMappingData().getItemMappings().inverse().containsKey(rawId & 0xFFFFFFF0)) {
                rawId &= 0xFFFFFFF0;
            } else {
                if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                    ViaBackwards.getPlatform().getLogger().warning("Failed to get 1.13 item for " + item.identifier());
                }
                rawId = 16;
            }
        }
        if (newId == -1) {
            newId = ((Protocol1_12_2To1_13)this.protocol).getMappingData().getItemMappings().inverse().get(rawId);
        }
        item.setIdentifier(newId);
        item.setData((short)0);
        return item;
    }

    private void rewriteCanPlaceToServer(CompoundTag tag, String tagName) {
        if (!(tag.get(tagName) instanceof ListTag)) {
            return;
        }
        ListTag blockTag = (ListTag)tag.remove(this.extraNbtTag + "|" + tagName);
        if (blockTag != null) {
            tag.put(tagName, ConverterRegistry.convertToTag(ConverterRegistry.convertToValue(blockTag)));
        } else {
            blockTag = (ListTag)tag.get(tagName);
            if (blockTag != null) {
                ListTag newCanPlaceOn = new ListTag(StringTag.class);
                for (Tag oldTag : blockTag) {
                    String lowerCaseId;
                    String[] newValues;
                    Object value = oldTag.getValue();
                    String oldId = value.toString().replace("minecraft:", "");
                    int key = Ints.tryParse((String)oldId);
                    String numberConverted = (String)BlockIdData.numberIdToString.get(key);
                    if (numberConverted != null) {
                        oldId = numberConverted;
                    }
                    if ((newValues = BlockIdData.blockIdMapping.get(lowerCaseId = oldId.toLowerCase(Locale.ROOT))) != null) {
                        for (String newValue : newValues) {
                            newCanPlaceOn.add(new StringTag(newValue));
                        }
                        continue;
                    }
                    newCanPlaceOn.add(new StringTag(lowerCaseId));
                }
                tag.put(tagName, newCanPlaceOn);
            }
        }
    }

    private void rewriteEnchantmentsToServer(CompoundTag tag, boolean storedEnch) {
        ListTag oldLore;
        CompoundTag display;
        String key = storedEnch ? "StoredEnchantments" : "Enchantments";
        ListTag enchantments = (ListTag)tag.get(storedEnch ? key : "ench");
        if (enchantments == null) {
            return;
        }
        ListTag newEnchantments = new ListTag(CompoundTag.class);
        boolean dummyEnchant = false;
        if (!storedEnch) {
            IntTag hideFlags = (IntTag)tag.remove(this.extraNbtTag + "|OldHideFlags");
            if (hideFlags != null) {
                tag.put("HideFlags", new IntTag(hideFlags.asByte()));
                dummyEnchant = true;
            } else if (tag.remove(this.extraNbtTag + "|DummyEnchant") != null) {
                tag.remove("HideFlags");
                dummyEnchant = true;
            }
        }
        for (Object enchEntry : enchantments) {
            CompoundTag enchantmentEntry = new CompoundTag();
            short oldId = ((NumberTag)((CompoundTag)enchEntry).get("id")).asShort();
            short level = ((NumberTag)((CompoundTag)enchEntry).get("lvl")).asShort();
            if (dummyEnchant && oldId == 0 && level == 0) continue;
            String newId = (String)Protocol1_13To1_12_2.MAPPINGS.getOldEnchantmentsIds().get((Object)oldId);
            if (newId == null) {
                newId = "viaversion:legacy/" + oldId;
            }
            enchantmentEntry.put("id", new StringTag(newId));
            enchantmentEntry.put("lvl", new ShortTag(level));
            newEnchantments.add(enchantmentEntry);
        }
        ListTag noMapped = (ListTag)tag.remove(this.extraNbtTag + "|Enchantments");
        if (noMapped != null) {
            for (Tag value : noMapped) {
                newEnchantments.add(value);
            }
        }
        if ((display = (CompoundTag)tag.get("display")) == null) {
            display = new CompoundTag();
            tag.put("display", display);
        }
        if ((oldLore = (ListTag)tag.remove(this.extraNbtTag + "|OldLore")) != null) {
            ListTag lore = (ListTag)display.get("Lore");
            if (lore == null) {
                lore = new ListTag();
                tag.put("Lore", lore);
            }
            lore.setValue((List<Tag>)oldLore.getValue());
        } else if (tag.remove(this.extraNbtTag + "|DummyLore") != null) {
            display.remove("Lore");
            if (display.isEmpty()) {
                tag.remove("display");
            }
        }
        if (!storedEnch) {
            tag.remove("ench");
        }
        tag.put(key, newEnchantments);
    }

    private void invertShieldAndBannerId(Item item, CompoundTag tag) {
        Object patterns;
        if (item.identifier() != 442 && item.identifier() != 425) {
            return;
        }
        Object blockEntityTag = tag.get("BlockEntityTag");
        if (!(blockEntityTag instanceof CompoundTag)) {
            return;
        }
        CompoundTag blockEntityCompoundTag = (CompoundTag)blockEntityTag;
        Object base = blockEntityCompoundTag.get("Base");
        if (base instanceof IntTag) {
            IntTag baseTag = (IntTag)base;
            baseTag.setValue(15 - baseTag.asInt());
        }
        if ((patterns = blockEntityCompoundTag.get("Patterns")) instanceof ListTag) {
            ListTag patternsTag = (ListTag)patterns;
            for (Tag pattern : patternsTag) {
                if (!(pattern instanceof CompoundTag)) continue;
                IntTag colorTag = (IntTag)((CompoundTag)pattern).get("Color");
                colorTag.setValue(15 - colorTag.asInt());
            }
        }
    }

    private static void flowerPotSpecialTreatment(UserConnection user, int blockState, Position position) throws Exception {
        if (FlowerPotHandler.isFlowah(blockState)) {
            BackwardsBlockEntityProvider beProvider = Via.getManager().getProviders().get(BackwardsBlockEntityProvider.class);
            CompoundTag nbt = beProvider.transform(user, position, "minecraft:flower_pot");
            PacketWrapper blockUpdateRemove = PacketWrapper.create(11, null, user);
            blockUpdateRemove.write(Type.POSITION, position);
            blockUpdateRemove.write(Type.VAR_INT, 0);
            blockUpdateRemove.scheduleSend(Protocol1_12_2To1_13.class);
            PacketWrapper blockCreate = PacketWrapper.create(11, null, user);
            blockCreate.write(Type.POSITION, position);
            blockCreate.write(Type.VAR_INT, Protocol1_12_2To1_13.MAPPINGS.getNewBlockStateId(blockState));
            blockCreate.scheduleSend(Protocol1_12_2To1_13.class);
            PacketWrapper wrapper = PacketWrapper.create(9, null, user);
            wrapper.write(Type.POSITION, position);
            wrapper.write(Type.UNSIGNED_BYTE, (short)5);
            wrapper.write(Type.NBT, nbt);
            wrapper.scheduleSend(Protocol1_12_2To1_13.class);
        }
    }
}

