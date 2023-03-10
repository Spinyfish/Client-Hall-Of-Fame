/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_18to1_17_1.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntity;
import com.viaversion.viaversion.api.minecraft.blockentity.BlockEntityImpl;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk1_18;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionImpl;
import com.viaversion.viaversion.api.minecraft.chunks.DataPaletteImpl;
import com.viaversion.viaversion.api.minecraft.chunks.PaletteType;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_17_1to1_17.ClientboundPackets1_17_1;
import com.viaversion.viaversion.protocols.protocol1_17to1_16_4.types.Chunk1_17Type;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.BlockEntityIds;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.Protocol1_18To1_17_1;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.storage.ChunkLightStorage;
import com.viaversion.viaversion.protocols.protocol1_18to1_17_1.types.Chunk1_18Type;
import com.viaversion.viaversion.util.MathUtil;
import java.util.ArrayList;
import java.util.BitSet;

public final class WorldPackets {
    public static void register(final Protocol1_18To1_17_1 protocol) {
        protocol.registerClientbound(ClientboundPackets1_17_1.BLOCK_ENTITY_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION1_14);
                this.handler(wrapper -> {
                    short id = wrapper.read(Type.UNSIGNED_BYTE);
                    int newId = BlockEntityIds.newId(id);
                    wrapper.write(Type.VAR_INT, newId);
                    WorldPackets.handleSpawners(newId, wrapper.passthrough(Type.NBT));
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_17_1.UPDATE_LIGHT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int chunkX = wrapper.passthrough(Type.VAR_INT);
                    int chunkZ = wrapper.passthrough(Type.VAR_INT);
                    if (wrapper.user().get(ChunkLightStorage.class).isLoaded(chunkX, chunkZ)) {
                        if (!Via.getConfig().cache1_17Light()) {
                            return;
                        }
                    } else {
                        wrapper.cancel();
                    }
                    boolean trustEdges = wrapper.passthrough(Type.BOOLEAN);
                    long[] skyLightMask = wrapper.passthrough(Type.LONG_ARRAY_PRIMITIVE);
                    long[] blockLightMask = wrapper.passthrough(Type.LONG_ARRAY_PRIMITIVE);
                    long[] emptySkyLightMask = wrapper.passthrough(Type.LONG_ARRAY_PRIMITIVE);
                    long[] emptyBlockLightMask = wrapper.passthrough(Type.LONG_ARRAY_PRIMITIVE);
                    int skyLightLenght = wrapper.passthrough(Type.VAR_INT);
                    byte[][] skyLight = new byte[skyLightLenght][];
                    for (int i = 0; i < skyLightLenght; ++i) {
                        skyLight[i] = wrapper.passthrough(Type.BYTE_ARRAY_PRIMITIVE);
                    }
                    int blockLightLength = wrapper.passthrough(Type.VAR_INT);
                    byte[][] blockLight = new byte[blockLightLength][];
                    for (int i = 0; i < blockLightLength; ++i) {
                        blockLight[i] = wrapper.passthrough(Type.BYTE_ARRAY_PRIMITIVE);
                    }
                    ChunkLightStorage lightStorage = wrapper.user().get(ChunkLightStorage.class);
                    lightStorage.storeLight(chunkX, chunkZ, new ChunkLightStorage.ChunkLight(trustEdges, skyLightMask, blockLightMask, emptySkyLightMask, emptyBlockLightMask, skyLight, blockLight));
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_17_1.CHUNK_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    ChunkLightStorage.ChunkLight light;
                    Object tracker = protocol.getEntityRewriter().tracker(wrapper.user());
                    Chunk oldChunk = wrapper.read(new Chunk1_17Type(tracker.currentWorldSectionHeight()));
                    ArrayList<BlockEntity> blockEntities = new ArrayList<BlockEntity>(oldChunk.getBlockEntities().size());
                    for (CompoundTag tag : oldChunk.getBlockEntities()) {
                        String id;
                        NumberTag xTag = (NumberTag)tag.get("x");
                        NumberTag yTag = (NumberTag)tag.get("y");
                        NumberTag zTag = (NumberTag)tag.get("z");
                        StringTag idTag = (StringTag)tag.get("id");
                        if (xTag == null || yTag == null || zTag == null) continue;
                        if (idTag == null) {
                            if (tag.get("Chest") instanceof StringTag) {
                                id = "minecraft:chest";
                            } else {
                                if (!(tag.get("EnderChest") instanceof StringTag)) continue;
                                id = "minecraft:ender_chest";
                            }
                        } else {
                            id = idTag.getValue();
                        }
                        int typeId = protocol.getMappingData().blockEntityIds().getInt(id.replace("minecraft:", ""));
                        if (typeId == -1) {
                            Via.getPlatform().getLogger().warning("Unknown block entity: " + id);
                        }
                        WorldPackets.handleSpawners(typeId, tag);
                        byte packedXZ = (byte)((xTag.asInt() & 0xF) << 4 | zTag.asInt() & 0xF);
                        blockEntities.add(new BlockEntityImpl(packedXZ, yTag.asShort(), typeId, tag));
                    }
                    int[] biomeData = oldChunk.getBiomeData();
                    ChunkSection[] sections = oldChunk.getSections();
                    for (int i = 0; i < sections.length; ++i) {
                        ChunkSection section = sections[i];
                        if (section == null) {
                            sections[i] = section = new ChunkSectionImpl();
                            section.setNonAirBlocksCount(0);
                            DataPaletteImpl blockPalette = new DataPaletteImpl(4096);
                            blockPalette.addId(0);
                            section.addPalette(PaletteType.BLOCKS, blockPalette);
                        }
                        DataPaletteImpl biomePalette = new DataPaletteImpl(64);
                        section.addPalette(PaletteType.BIOMES, biomePalette);
                        int offset = i * 64;
                        int biomeIndex = 0;
                        int biomeArrayIndex = offset;
                        while (biomeIndex < 64) {
                            int biome = biomeData[biomeArrayIndex];
                            biomePalette.setIdAt(biomeIndex, biome != -1 ? biome : 0);
                            ++biomeIndex;
                            ++biomeArrayIndex;
                        }
                    }
                    Chunk1_18 chunk = new Chunk1_18(oldChunk.getX(), oldChunk.getZ(), sections, oldChunk.getHeightMap(), blockEntities);
                    wrapper.write(new Chunk1_18Type(tracker.currentWorldSectionHeight(), MathUtil.ceilLog2(protocol.getMappingData().getBlockStateMappings().mappedSize()), MathUtil.ceilLog2(tracker.biomesSent())), chunk);
                    ChunkLightStorage lightStorage = wrapper.user().get(ChunkLightStorage.class);
                    boolean alreadyLoaded = !lightStorage.addLoadedChunk(chunk.getX(), chunk.getZ());
                    ChunkLightStorage.ChunkLight chunkLight = light = Via.getConfig().cache1_17Light() ? lightStorage.getLight(chunk.getX(), chunk.getZ()) : lightStorage.removeLight(chunk.getX(), chunk.getZ());
                    if (light == null) {
                        Via.getPlatform().getLogger().warning("No light data found for chunk at " + chunk.getX() + ", " + chunk.getZ() + ". Chunk was already loaded: " + alreadyLoaded);
                        BitSet emptyLightMask = new BitSet();
                        emptyLightMask.set(0, tracker.currentWorldSectionHeight() + 2);
                        wrapper.write(Type.BOOLEAN, false);
                        wrapper.write(Type.LONG_ARRAY_PRIMITIVE, new long[0]);
                        wrapper.write(Type.LONG_ARRAY_PRIMITIVE, new long[0]);
                        wrapper.write(Type.LONG_ARRAY_PRIMITIVE, emptyLightMask.toLongArray());
                        wrapper.write(Type.LONG_ARRAY_PRIMITIVE, emptyLightMask.toLongArray());
                        wrapper.write(Type.VAR_INT, 0);
                        wrapper.write(Type.VAR_INT, 0);
                    } else {
                        wrapper.write(Type.BOOLEAN, light.trustEdges());
                        wrapper.write(Type.LONG_ARRAY_PRIMITIVE, light.skyLightMask());
                        wrapper.write(Type.LONG_ARRAY_PRIMITIVE, light.blockLightMask());
                        wrapper.write(Type.LONG_ARRAY_PRIMITIVE, light.emptySkyLightMask());
                        wrapper.write(Type.LONG_ARRAY_PRIMITIVE, light.emptyBlockLightMask());
                        wrapper.write(Type.VAR_INT, light.skyLight().length);
                        for (byte[] skyLight : light.skyLight()) {
                            wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, skyLight);
                        }
                        wrapper.write(Type.VAR_INT, light.blockLight().length);
                        for (byte[] blockLight : light.blockLight()) {
                            wrapper.write(Type.BYTE_ARRAY_PRIMITIVE, blockLight);
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_17_1.UNLOAD_CHUNK, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    int chunkX = wrapper.passthrough(Type.INT);
                    int chunkZ = wrapper.passthrough(Type.INT);
                    wrapper.user().get(ChunkLightStorage.class).clear(chunkX, chunkZ);
                });
            }
        });
    }

    private static void handleSpawners(int typeId, CompoundTag tag) {
        CompoundTag entity;
        if (typeId == 8 && (entity = (CompoundTag)tag.get("SpawnData")) != null) {
            CompoundTag spawnData = new CompoundTag();
            tag.put("SpawnData", spawnData);
            spawnData.put("entity", entity);
        }
    }
}

