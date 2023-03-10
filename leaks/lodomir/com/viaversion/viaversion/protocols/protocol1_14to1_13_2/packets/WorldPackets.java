/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_14to1_13_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockFace;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.NibbleArray;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_14Types;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.LongArrayTag;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.ClientboundPackets1_13;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.ClientboundPackets1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.Protocol1_14To1_13_2;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.storage.EntityTracker1_14;
import com.viaversion.viaversion.protocols.protocol1_14to1_13_2.types.Chunk1_14Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.rewriter.BlockRewriter;
import com.viaversion.viaversion.util.CompactArrayUtil;
import java.util.Arrays;

public class WorldPackets {
    public static final int SERVERSIDE_VIEW_DISTANCE = 64;
    private static final byte[] FULL_LIGHT = new byte[2048];
    public static int air;
    public static int voidAir;
    public static int caveAir;

    public static void register(final Protocol1_14To1_13_2 protocol) {
        BlockRewriter blockRewriter = new BlockRewriter(protocol, null);
        protocol.registerClientbound(ClientboundPackets1_13.BLOCK_BREAK_ANIMATION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.POSITION, Type.POSITION1_14);
                this.map(Type.BYTE);
            }
        });
        protocol.registerClientbound(ClientboundPackets1_13.BLOCK_ENTITY_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION, Type.POSITION1_14);
            }
        });
        protocol.registerClientbound(ClientboundPackets1_13.BLOCK_ACTION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION, Type.POSITION1_14);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.set(Type.VAR_INT, 0, protocol.getMappingData().getNewBlockId(wrapper.get(Type.VAR_INT, 0)));
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_13.BLOCK_CHANGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION, Type.POSITION1_14);
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int id = wrapper.get(Type.VAR_INT, 0);
                        wrapper.set(Type.VAR_INT, 0, protocol.getMappingData().getNewBlockStateId(id));
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_13.SERVER_DIFFICULTY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.write(Type.BOOLEAN, false);
                    }
                });
            }
        });
        blockRewriter.registerMultiBlockChange(ClientboundPackets1_13.MULTI_BLOCK_CHANGE);
        protocol.registerClientbound(ClientboundPackets1_13.EXPLOSION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        for (int i = 0; i < 3; ++i) {
                            float coord = wrapper.get(Type.FLOAT, i).floatValue();
                            if (!(coord < 0.0f)) continue;
                            coord = (int)coord;
                            wrapper.set(Type.FLOAT, i, Float.valueOf(coord));
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_13.CHUNK_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                        Chunk chunk = wrapper.read(new Chunk1_13Type(clientWorld));
                        wrapper.write(new Chunk1_14Type(), chunk);
                        int[] motionBlocking = new int[256];
                        int[] worldSurface = new int[256];
                        for (int s = 0; s < chunk.getSections().length; ++s) {
                            ChunkSection section = chunk.getSections()[s];
                            if (section == null) continue;
                            boolean hasBlock = false;
                            for (int i = 0; i < section.getPaletteSize(); ++i) {
                                int old = section.getPaletteEntry(i);
                                int newId = protocol.getMappingData().getNewBlockStateId(old);
                                if (!hasBlock && newId != air && newId != voidAir && newId != caveAir) {
                                    hasBlock = true;
                                }
                                section.setPaletteEntry(i, newId);
                            }
                            if (!hasBlock) {
                                section.setNonAirBlocksCount(0);
                                continue;
                            }
                            int nonAirBlockCount = 0;
                            for (int x = 0; x < 16; ++x) {
                                for (int y = 0; y < 16; ++y) {
                                    for (int z = 0; z < 16; ++z) {
                                        int id = section.getFlatBlock(x, y, z);
                                        if (id != air && id != voidAir && id != caveAir) {
                                            ++nonAirBlockCount;
                                            worldSurface[x + z * 16] = y + s * 16 + 1;
                                        }
                                        if (protocol.getMappingData().getMotionBlocking().contains(id)) {
                                            motionBlocking[x + z * 16] = y + s * 16 + 1;
                                        }
                                        if (!Via.getConfig().isNonFullBlockLightFix() || !protocol.getMappingData().getNonFullBlocks().contains(id)) continue;
                                        WorldPackets.setNonFullLight(chunk, section, s, x, y, z);
                                    }
                                }
                            }
                            section.setNonAirBlocksCount(nonAirBlockCount);
                        }
                        CompoundTag heightMap = new CompoundTag();
                        heightMap.put("MOTION_BLOCKING", new LongArrayTag(WorldPackets.encodeHeightMap(motionBlocking)));
                        heightMap.put("WORLD_SURFACE", new LongArrayTag(WorldPackets.encodeHeightMap(worldSurface)));
                        chunk.setHeightMap(heightMap);
                        PacketWrapper lightPacket = wrapper.create(ClientboundPackets1_14.UPDATE_LIGHT);
                        lightPacket.write(Type.VAR_INT, chunk.getX());
                        lightPacket.write(Type.VAR_INT, chunk.getZ());
                        int skyLightMask = chunk.isFullChunk() ? 262143 : 0;
                        int blockLightMask = 0;
                        for (int i = 0; i < chunk.getSections().length; ++i) {
                            ChunkSection sec = chunk.getSections()[i];
                            if (sec == null) continue;
                            if (!chunk.isFullChunk() && sec.getLight().hasSkyLight()) {
                                skyLightMask |= 1 << i + 1;
                            }
                            blockLightMask |= 1 << i + 1;
                        }
                        lightPacket.write(Type.VAR_INT, skyLightMask);
                        lightPacket.write(Type.VAR_INT, blockLightMask);
                        lightPacket.write(Type.VAR_INT, 0);
                        lightPacket.write(Type.VAR_INT, 0);
                        if (chunk.isFullChunk()) {
                            lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, FULL_LIGHT);
                        }
                        for (ChunkSection section : chunk.getSections()) {
                            if (section == null || !section.getLight().hasSkyLight()) {
                                if (!chunk.isFullChunk()) continue;
                                lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, FULL_LIGHT);
                                continue;
                            }
                            lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, section.getLight().getSkyLight());
                        }
                        if (chunk.isFullChunk()) {
                            lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, FULL_LIGHT);
                        }
                        for (ChunkSection section : chunk.getSections()) {
                            if (section == null) continue;
                            lightPacket.write(Type.BYTE_ARRAY_PRIMITIVE, section.getLight().getBlockLight());
                        }
                        EntityTracker1_14 entityTracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                        int diffX = Math.abs(entityTracker.getChunkCenterX() - chunk.getX());
                        int diffZ = Math.abs(entityTracker.getChunkCenterZ() - chunk.getZ());
                        if (entityTracker.isForceSendCenterChunk() || diffX >= 64 || diffZ >= 64) {
                            PacketWrapper fakePosLook = wrapper.create(ClientboundPackets1_14.UPDATE_VIEW_POSITION);
                            fakePosLook.write(Type.VAR_INT, chunk.getX());
                            fakePosLook.write(Type.VAR_INT, chunk.getZ());
                            fakePosLook.send(Protocol1_14To1_13_2.class);
                            entityTracker.setChunkCenterX(chunk.getX());
                            entityTracker.setChunkCenterZ(chunk.getZ());
                        }
                        lightPacket.send(Protocol1_14To1_13_2.class);
                        for (ChunkSection section : chunk.getSections()) {
                            if (section == null) continue;
                            section.setLight(null);
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_13.EFFECT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.POSITION, Type.POSITION1_14);
                this.map(Type.INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int id = wrapper.get(Type.INT, 0);
                        int data = wrapper.get(Type.INT, 1);
                        if (id == 1010) {
                            wrapper.set(Type.INT, 1, protocol.getMappingData().getNewItemId(data));
                        } else if (id == 2001) {
                            wrapper.set(Type.INT, 1, protocol.getMappingData().getNewBlockStateId(data));
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_13.JOIN_GAME, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientChunks = wrapper.user().get(ClientWorld.class);
                        int dimensionId = wrapper.get(Type.INT, 1);
                        clientChunks.setEnvironment(dimensionId);
                        int entityId = wrapper.get(Type.INT, 0);
                        Entity1_14Types entType = Entity1_14Types.PLAYER;
                        EntityTracker1_14 tracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                        tracker.addEntity(entityId, entType);
                        tracker.setClientEntityId(entityId);
                    }
                });
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        short difficulty = wrapper.read(Type.UNSIGNED_BYTE);
                        PacketWrapper difficultyPacket = wrapper.create(ClientboundPackets1_14.SERVER_DIFFICULTY);
                        difficultyPacket.write(Type.UNSIGNED_BYTE, difficulty);
                        difficultyPacket.write(Type.BOOLEAN, false);
                        difficultyPacket.scheduleSend(protocol.getClass());
                        wrapper.passthrough(Type.UNSIGNED_BYTE);
                        wrapper.passthrough(Type.STRING);
                        wrapper.write(Type.VAR_INT, 64);
                    }
                });
                this.handler(wrapper -> {
                    wrapper.send(Protocol1_14To1_13_2.class);
                    wrapper.cancel();
                    WorldPackets.sendViewDistancePacket(wrapper.user());
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_13.MAP_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.BYTE);
                this.map(Type.BOOLEAN);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        wrapper.write(Type.BOOLEAN, false);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_13.RESPAWN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                        int dimensionId = wrapper.get(Type.INT, 0);
                        clientWorld.setEnvironment(dimensionId);
                        EntityTracker1_14 entityTracker = (EntityTracker1_14)wrapper.user().getEntityTracker(Protocol1_14To1_13_2.class);
                        entityTracker.setForceSendCenterChunk(true);
                    }
                });
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        short difficulty = wrapper.read(Type.UNSIGNED_BYTE);
                        PacketWrapper difficultyPacket = wrapper.create(ClientboundPackets1_14.SERVER_DIFFICULTY);
                        difficultyPacket.write(Type.UNSIGNED_BYTE, difficulty);
                        difficultyPacket.write(Type.BOOLEAN, false);
                        difficultyPacket.scheduleSend(protocol.getClass());
                    }
                });
                this.handler(wrapper -> {
                    wrapper.send(Protocol1_14To1_13_2.class);
                    wrapper.cancel();
                    WorldPackets.sendViewDistancePacket(wrapper.user());
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_13.SPAWN_POSITION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION, Type.POSITION1_14);
            }
        });
    }

    private static void sendViewDistancePacket(UserConnection connection) throws Exception {
        PacketWrapper setViewDistance = PacketWrapper.create(ClientboundPackets1_14.UPDATE_VIEW_DISTANCE, null, connection);
        setViewDistance.write(Type.VAR_INT, 64);
        setViewDistance.send(Protocol1_14To1_13_2.class);
    }

    private static long[] encodeHeightMap(int[] heightMap) {
        return CompactArrayUtil.createCompactArray(9, heightMap.length, i -> heightMap[i]);
    }

    private static void setNonFullLight(Chunk chunk, ChunkSection section, int ySection, int x, int y, int z) {
        int skyLight = 0;
        int blockLight = 0;
        for (BlockFace blockFace : BlockFace.values()) {
            NibbleArray skyLightArray = section.getLight().getSkyLightNibbleArray();
            NibbleArray blockLightArray = section.getLight().getBlockLightNibbleArray();
            int neighbourX = x + blockFace.modX();
            int neighbourY = y + blockFace.modY();
            int neighbourZ = z + blockFace.modZ();
            if (blockFace.modX() != 0) {
                if (neighbourX == 16 || neighbourX == -1) {
                    continue;
                }
            } else if (blockFace.modY() != 0) {
                if (neighbourY == 16 || neighbourY == -1) {
                    ChunkSection newSection;
                    if (neighbourY == 16) {
                        ++ySection;
                        neighbourY = 0;
                    } else {
                        --ySection;
                        neighbourY = 15;
                    }
                    if (ySection == 16 || ySection == -1 || (newSection = chunk.getSections()[ySection]) == null) continue;
                    skyLightArray = newSection.getLight().getSkyLightNibbleArray();
                    blockLightArray = newSection.getLight().getBlockLightNibbleArray();
                }
            } else if (blockFace.modZ() != 0 && (neighbourZ == 16 || neighbourZ == -1)) continue;
            if (blockLightArray != null && blockLight != 15) {
                int neighbourBlockLight = blockLightArray.get(neighbourX, neighbourY, neighbourZ);
                if (neighbourBlockLight == 15) {
                    blockLight = 14;
                } else if (neighbourBlockLight > blockLight) {
                    blockLight = neighbourBlockLight - 1;
                }
            }
            if (skyLightArray == null || skyLight == 15) continue;
            int neighbourSkyLight = skyLightArray.get(neighbourX, neighbourY, neighbourZ);
            if (neighbourSkyLight == 15) {
                if (blockFace.modY() == 1) {
                    skyLight = 15;
                    continue;
                }
                skyLight = 14;
                continue;
            }
            if (neighbourSkyLight <= skyLight) continue;
            skyLight = neighbourSkyLight - 1;
        }
        if (skyLight != 0) {
            if (!section.getLight().hasSkyLight()) {
                byte[] newSkyLight = new byte[2028];
                section.getLight().setSkyLight(newSkyLight);
            }
            section.getLight().getSkyLightNibbleArray().set(x, y, z, skyLight);
        }
        if (blockLight != 0) {
            section.getLight().getBlockLightNibbleArray().set(x, y, z, blockLight);
        }
    }

    private static long getChunkIndex(int x, int z) {
        return ((long)x & 0x3FFFFFFL) << 38 | (long)z & 0x3FFFFFFL;
    }

    static {
        Arrays.fill(FULL_LIGHT, (byte)-1);
    }
}

