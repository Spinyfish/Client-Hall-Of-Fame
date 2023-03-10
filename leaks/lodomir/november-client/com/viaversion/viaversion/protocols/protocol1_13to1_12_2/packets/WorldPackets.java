/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.Particle;
import com.viaversion.viaversion.libs.fastutil.ints.IntOpenHashSet;
import com.viaversion.viaversion.libs.fastutil.ints.IntSet;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.NumberTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_12_1to1_12.ClientboundPackets1_12_1;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.Protocol1_13To1_12_2;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionData;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.blockconnections.ConnectionHandler;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.NamedSoundRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.data.ParticleRewriter;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.BlockEntityProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.providers.PaintingProvider;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.storage.BlockStorage;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.types.Chunk1_13Type;
import com.viaversion.viaversion.protocols.protocol1_9_1_2to1_9_3_4.types.Chunk1_9_3_4Type;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class WorldPackets {
    private static final IntSet VALID_BIOMES;

    public static void register(Protocol protocol) {
        protocol.registerClientbound(ClientboundPackets1_12_1.SPAWN_PAINTING, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        String motive;
                        PaintingProvider provider = Via.getManager().getProviders().get(PaintingProvider.class);
                        Optional<Integer> id = provider.getIntByIdentifier(motive = wrapper.read(Type.STRING));
                        if (!(id.isPresent() || Via.getConfig().isSuppressConversionWarnings() && !Via.getManager().isDebug())) {
                            Via.getPlatform().getLogger().warning("Could not find painting motive: " + motive + " falling back to default (0)");
                        }
                        wrapper.write(Type.VAR_INT, id.orElse(0));
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.BLOCK_ENTITY_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.NBT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        BlockStorage storage;
                        BlockStorage.ReplacementData replacementData;
                        Position position = wrapper.get(Type.POSITION, 0);
                        short action = wrapper.get(Type.UNSIGNED_BYTE, 0);
                        CompoundTag tag = wrapper.get(Type.NBT, 0);
                        BlockEntityProvider provider = Via.getManager().getProviders().get(BlockEntityProvider.class);
                        int newId = provider.transform(wrapper.user(), position, tag, true);
                        if (newId != -1 && (replacementData = (storage = wrapper.user().get(BlockStorage.class)).get(position)) != null) {
                            replacementData.setReplacement(newId);
                        }
                        if (action == 5) {
                            wrapper.cancel();
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.BLOCK_ACTION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Position pos = wrapper.get(Type.POSITION, 0);
                        short action = wrapper.get(Type.UNSIGNED_BYTE, 0);
                        short param = wrapper.get(Type.UNSIGNED_BYTE, 1);
                        int blockId = wrapper.get(Type.VAR_INT, 0);
                        if (blockId == 25) {
                            blockId = 73;
                        } else if (blockId == 33) {
                            blockId = 99;
                        } else if (blockId == 29) {
                            blockId = 92;
                        } else if (blockId == 54) {
                            blockId = 142;
                        } else if (blockId == 146) {
                            blockId = 305;
                        } else if (blockId == 130) {
                            blockId = 249;
                        } else if (blockId == 138) {
                            blockId = 257;
                        } else if (blockId == 52) {
                            blockId = 140;
                        } else if (blockId == 209) {
                            blockId = 472;
                        } else if (blockId >= 219 && blockId <= 234) {
                            blockId = blockId - 219 + 483;
                        }
                        if (blockId == 73) {
                            PacketWrapper blockChange = wrapper.create(11);
                            blockChange.write(Type.POSITION, pos);
                            blockChange.write(Type.VAR_INT, 249 + action * 24 * 2 + param * 2);
                            blockChange.send(Protocol1_13To1_12_2.class);
                        }
                        wrapper.set(Type.VAR_INT, 0, blockId);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.BLOCK_CHANGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.VAR_INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Position position = wrapper.get(Type.POSITION, 0);
                        int newId = WorldPackets.toNewId(wrapper.get(Type.VAR_INT, 0));
                        UserConnection userConnection = wrapper.user();
                        if (Via.getConfig().isServersideBlockConnections()) {
                            ConnectionData.updateBlockStorage(userConnection, position.x(), position.y(), position.z(), newId);
                            newId = ConnectionData.connect(userConnection, position, newId);
                        }
                        wrapper.set(Type.VAR_INT, 0, WorldPackets.checkStorage(wrapper.user(), position, newId));
                        if (Via.getConfig().isServersideBlockConnections()) {
                            wrapper.send(Protocol1_13To1_12_2.class);
                            wrapper.cancel();
                            ConnectionData.update(userConnection, position);
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.MULTI_BLOCK_CHANGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.BLOCK_CHANGE_RECORD_ARRAY);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        Position position;
                        BlockChangeRecord[] records;
                        int chunkX = wrapper.get(Type.INT, 0);
                        int chunkZ = wrapper.get(Type.INT, 1);
                        UserConnection userConnection = wrapper.user();
                        for (BlockChangeRecord record : records = wrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0)) {
                            int newBlock = WorldPackets.toNewId(record.getBlockId());
                            position = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                            if (Via.getConfig().isServersideBlockConnections()) {
                                ConnectionData.updateBlockStorage(userConnection, position.x(), position.y(), position.z(), newBlock);
                            }
                            record.setBlockId(WorldPackets.checkStorage(wrapper.user(), position, newBlock));
                        }
                        if (Via.getConfig().isServersideBlockConnections()) {
                            for (BlockChangeRecord record : records) {
                                int blockState = record.getBlockId();
                                position = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                                ConnectionHandler handler = ConnectionData.getConnectionHandler(blockState);
                                if (handler == null) continue;
                                blockState = handler.connect(userConnection, position, blockState);
                                record.setBlockId(blockState);
                            }
                            wrapper.send(Protocol1_13To1_12_2.class);
                            wrapper.cancel();
                            for (BlockChangeRecord record : records) {
                                Position position2 = new Position(record.getSectionX() + chunkX * 16, record.getY(), record.getSectionZ() + chunkZ * 16);
                                ConnectionData.update(userConnection, position2);
                            }
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.EXPLOSION, new PacketRemapper(){

            @Override
            public void registerMap() {
                if (!Via.getConfig().isServersideBlockConnections()) {
                    return;
                }
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int i;
                        UserConnection userConnection = wrapper.user();
                        int x = (int)Math.floor(wrapper.get(Type.FLOAT, 0).floatValue());
                        int y = (int)Math.floor(wrapper.get(Type.FLOAT, 1).floatValue());
                        int z = (int)Math.floor(wrapper.get(Type.FLOAT, 2).floatValue());
                        int recordCount = wrapper.get(Type.INT, 0);
                        Position[] records = new Position[recordCount];
                        for (i = 0; i < recordCount; ++i) {
                            Position position;
                            records[i] = position = new Position(x + wrapper.passthrough(Type.BYTE), (short)(y + wrapper.passthrough(Type.BYTE)), z + wrapper.passthrough(Type.BYTE));
                            ConnectionData.updateBlockStorage(userConnection, position.x(), position.y(), position.z(), 0);
                        }
                        wrapper.send(Protocol1_13To1_12_2.class);
                        wrapper.cancel();
                        for (i = 0; i < recordCount; ++i) {
                            ConnectionData.update(userConnection, records[i]);
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.UNLOAD_CHUNK, new PacketRemapper(){

            @Override
            public void registerMap() {
                if (Via.getConfig().isServersideBlockConnections()) {
                    this.handler(new PacketHandler(){

                        @Override
                        public void handle(PacketWrapper wrapper) throws Exception {
                            int x = wrapper.passthrough(Type.INT);
                            int z = wrapper.passthrough(Type.INT);
                            ConnectionData.blockConnectionProvider.unloadChunk(wrapper.user(), x, z);
                        }
                    });
                }
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.NAMED_SOUND, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        String sound = wrapper.get(Type.STRING, 0).replace("minecraft:", "");
                        String newSoundId = NamedSoundRewriter.getNewId(sound);
                        wrapper.set(Type.STRING, 0, newSoundId);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.CHUNK_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                        BlockStorage storage = wrapper.user().get(BlockStorage.class);
                        Chunk1_9_3_4Type type = new Chunk1_9_3_4Type(clientWorld);
                        Chunk1_13Type type1_13 = new Chunk1_13Type(clientWorld);
                        Chunk chunk = wrapper.read(type);
                        wrapper.write(type1_13, chunk);
                        for (int i = 0; i < chunk.getSections().length; ++i) {
                            int block;
                            int x;
                            int z;
                            int y;
                            int newId;
                            ChunkSection section = chunk.getSections()[i];
                            if (section == null) continue;
                            for (int p = 0; p < section.getPaletteSize(); ++p) {
                                int old = section.getPaletteEntry(p);
                                newId = WorldPackets.toNewId(old);
                                section.setPaletteEntry(p, newId);
                            }
                            boolean willSaveToStorage = false;
                            for (int p = 0; p < section.getPaletteSize(); ++p) {
                                newId = section.getPaletteEntry(p);
                                if (!storage.isWelcome(newId)) continue;
                                willSaveToStorage = true;
                                break;
                            }
                            boolean willSaveConnection = false;
                            if (Via.getConfig().isServersideBlockConnections() && ConnectionData.needStoreBlocks()) {
                                for (int p = 0; p < section.getPaletteSize(); ++p) {
                                    int newId2 = section.getPaletteEntry(p);
                                    if (!ConnectionData.isWelcome(newId2)) continue;
                                    willSaveConnection = true;
                                    break;
                                }
                            }
                            if (willSaveToStorage) {
                                for (y = 0; y < 16; ++y) {
                                    for (z = 0; z < 16; ++z) {
                                        for (x = 0; x < 16; ++x) {
                                            block = section.getFlatBlock(x, y, z);
                                            if (!storage.isWelcome(block)) continue;
                                            storage.store(new Position(x + (chunk.getX() << 4), (short)(y + (i << 4)), z + (chunk.getZ() << 4)), block);
                                        }
                                    }
                                }
                            }
                            if (!willSaveConnection) continue;
                            for (y = 0; y < 16; ++y) {
                                for (z = 0; z < 16; ++z) {
                                    for (x = 0; x < 16; ++x) {
                                        block = section.getFlatBlock(x, y, z);
                                        if (!ConnectionData.isWelcome(block)) continue;
                                        ConnectionData.blockConnectionProvider.storeBlock(wrapper.user(), x + (chunk.getX() << 4), y + (i << 4), z + (chunk.getZ() << 4), block);
                                    }
                                }
                            }
                        }
                        if (chunk.isBiomeData()) {
                            int latestBiomeWarn = Integer.MIN_VALUE;
                            for (int i = 0; i < 256; ++i) {
                                int biome = chunk.getBiomeData()[i];
                                if (VALID_BIOMES.contains(biome)) continue;
                                if (biome != 255 && latestBiomeWarn != biome) {
                                    if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                                        Via.getPlatform().getLogger().warning("Received invalid biome id " + biome);
                                    }
                                    latestBiomeWarn = biome;
                                }
                                chunk.getBiomeData()[i] = 1;
                            }
                        }
                        BlockEntityProvider provider = Via.getManager().getProviders().get(BlockEntityProvider.class);
                        Iterator<CompoundTag> iterator = chunk.getBlockEntities().iterator();
                        while (iterator.hasNext()) {
                            String id;
                            Object idTag;
                            CompoundTag tag = iterator.next();
                            int newId = provider.transform(wrapper.user(), null, tag, false);
                            if (newId != -1) {
                                int z;
                                int y;
                                int x = ((NumberTag)tag.get("x")).asInt();
                                Position position = new Position(x, (short)(y = ((NumberTag)tag.get("y")).asInt()), z = ((NumberTag)tag.get("z")).asInt());
                                BlockStorage.ReplacementData replacementData = storage.get(position);
                                if (replacementData != null) {
                                    replacementData.setReplacement(newId);
                                }
                                chunk.getSections()[y >> 4].setFlatBlock(x & 0xF, y & 0xF, z & 0xF, newId);
                            }
                            if (!((idTag = tag.get("id")) instanceof StringTag) || !(id = ((StringTag)idTag).getValue()).equals("minecraft:noteblock") && !id.equals("minecraft:flower_pot")) continue;
                            iterator.remove();
                        }
                        if (Via.getConfig().isServersideBlockConnections()) {
                            ConnectionData.connectBlocks(wrapper.user(), chunk);
                            wrapper.send(Protocol1_13To1_12_2.class);
                            wrapper.cancel();
                            for (int i = 0; i < chunk.getSections().length; ++i) {
                                ChunkSection section = chunk.getSections()[i];
                                if (section == null) continue;
                                ConnectionData.updateChunkSectionNeighbours(wrapper.user(), chunk.getX(), chunk.getZ(), i);
                            }
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_12_1.SPAWN_PARTICLE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.BOOLEAN);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.INT);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int particleId = wrapper.get(Type.INT, 0);
                        int dataCount = 0;
                        if (particleId == 37 || particleId == 38 || particleId == 46) {
                            dataCount = 1;
                        } else if (particleId == 36) {
                            dataCount = 2;
                        }
                        Integer[] data = new Integer[dataCount];
                        for (int i = 0; i < data.length; ++i) {
                            data[i] = wrapper.read(Type.VAR_INT);
                        }
                        Particle particle = ParticleRewriter.rewriteParticle(particleId, data);
                        if (particle == null || particle.getId() == -1) {
                            wrapper.cancel();
                            return;
                        }
                        if (particle.getId() == 11) {
                            int count = wrapper.get(Type.INT, 1);
                            float speed = wrapper.get(Type.FLOAT, 6).floatValue();
                            if (count == 0) {
                                wrapper.set(Type.INT, 1, 1);
                                wrapper.set(Type.FLOAT, 6, Float.valueOf(0.0f));
                                List<Particle.ParticleData> arguments = particle.getArguments();
                                for (int i = 0; i < 3; ++i) {
                                    float colorValue = wrapper.get(Type.FLOAT, i + 3).floatValue() * speed;
                                    if (colorValue == 0.0f && i == 0) {
                                        colorValue = 1.0f;
                                    }
                                    arguments.get(i).setValue(Float.valueOf(colorValue));
                                    wrapper.set(Type.FLOAT, i + 3, Float.valueOf(0.0f));
                                }
                            }
                        }
                        wrapper.set(Type.INT, 0, particle.getId());
                        for (Particle.ParticleData particleData : particle.getArguments()) {
                            wrapper.write(particleData.getType(), particleData.getValue());
                        }
                    }
                });
            }
        });
    }

    public static int toNewId(int oldId) {
        int newId;
        if (oldId < 0) {
            oldId = 0;
        }
        if ((newId = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId)) != -1) {
            return newId;
        }
        newId = Protocol1_13To1_12_2.MAPPINGS.getBlockMappings().getNewId(oldId & 0xFFFFFFF0);
        if (newId != -1) {
            if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
                Via.getPlatform().getLogger().warning("Missing block " + oldId);
            }
            return newId;
        }
        if (!Via.getConfig().isSuppressConversionWarnings() || Via.getManager().isDebug()) {
            Via.getPlatform().getLogger().warning("Missing block completely " + oldId);
        }
        return 1;
    }

    private static int checkStorage(UserConnection user, Position position, int newId) {
        BlockStorage storage = user.get(BlockStorage.class);
        if (storage.contains(position)) {
            BlockStorage.ReplacementData data = storage.get(position);
            if (data.getOriginal() == newId) {
                if (data.getReplacement() != -1) {
                    return data.getReplacement();
                }
            } else {
                storage.remove(position);
                if (storage.isWelcome(newId)) {
                    storage.store(position, newId);
                }
            }
        } else if (storage.isWelcome(newId)) {
            storage.store(position, newId);
        }
        return newId;
    }

    static {
        int i;
        VALID_BIOMES = new IntOpenHashSet(70, 0.99f);
        for (i = 0; i < 50; ++i) {
            VALID_BIOMES.add(i);
        }
        VALID_BIOMES.add(127);
        for (i = 129; i <= 134; ++i) {
            VALID_BIOMES.add(i);
        }
        VALID_BIOMES.add(140);
        VALID_BIOMES.add(149);
        VALID_BIOMES.add(151);
        for (i = 155; i <= 158; ++i) {
            VALID_BIOMES.add(i);
        }
        for (i = 160; i <= 167; ++i) {
            VALID_BIOMES.add(i);
        }
    }
}

