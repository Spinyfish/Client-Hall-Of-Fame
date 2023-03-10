/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_8to1_9.packets;

import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.minecraft.BlockChangeRecord;
import com.viaversion.viaversion.api.minecraft.Environment;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.chunks.BaseChunk;
import com.viaversion.viaversion.api.minecraft.chunks.Chunk;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSection;
import com.viaversion.viaversion.api.minecraft.chunks.ChunkSectionImpl;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.Tag;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.types.Chunk1_9_1_2Type;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.types.Chunk1_8Type;
import de.gerrygames.viarewind.ViaRewind;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.items.ReplacementRegistry1_8to1_9;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.sound.Effect;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.sound.SoundRemapper;
import de.gerrygames.viarewind.utils.PacketUtil;
import java.util.ArrayList;

public class WorldPackets {
    public static void register(Protocol<ClientboundPackets1_9, ClientboundPackets1_8, ServerboundPackets1_9, ServerboundPackets1_8> protocol) {
        protocol.registerClientbound(ClientboundPackets1_9.BLOCK_ENTITY_DATA, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.NBT);
                this.handler(packetWrapper -> {
                    CompoundTag tag = packetWrapper.get(Type.NBT, 0);
                    if (tag != null && tag.contains("SpawnData")) {
                        String entity = (String)((Tag)((CompoundTag)tag.get("SpawnData")).get("id")).getValue();
                        tag.remove("SpawnData");
                        tag.put("entityId", new StringTag(entity));
                    }
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.BLOCK_ACTION, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.VAR_INT);
                this.handler(packetWrapper -> {
                    int block = packetWrapper.get(Type.VAR_INT, 0);
                    if (block >= 219 && block <= 234) {
                        block = 130;
                        packetWrapper.set(Type.VAR_INT, 0, 130);
                    }
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.BLOCK_CHANGE, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.POSITION);
                this.map(Type.VAR_INT);
                this.handler(packetWrapper -> {
                    int combined = packetWrapper.get(Type.VAR_INT, 0);
                    int replacedCombined = ReplacementRegistry1_8to1_9.replace(combined);
                    packetWrapper.set(Type.VAR_INT, 0, replacedCombined);
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.MULTI_BLOCK_CHANGE, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.BLOCK_CHANGE_RECORD_ARRAY);
                this.handler(packetWrapper -> {
                    for (BlockChangeRecord record : packetWrapper.get(Type.BLOCK_CHANGE_RECORD_ARRAY, 0)) {
                        int replacedCombined = ReplacementRegistry1_8to1_9.replace(record.getBlockId());
                        record.setBlockId(replacedCombined);
                    }
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.NAMED_SOUND, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(packetWrapper -> {
                    String name = packetWrapper.get(Type.STRING, 0);
                    if ((name = SoundRemapper.getOldName(name)) == null) {
                        packetWrapper.cancel();
                    } else {
                        packetWrapper.set(Type.STRING, 0, name);
                    }
                });
                this.map((Type)Type.VAR_INT, Type.NOTHING);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.FLOAT);
                this.map(Type.UNSIGNED_BYTE);
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.EXPLOSION, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.handler(packetWrapper -> {
                    int count = packetWrapper.read(Type.INT);
                    packetWrapper.write(Type.INT, count);
                    for (int i = 0; i < count; ++i) {
                        packetWrapper.passthrough(Type.UNSIGNED_BYTE);
                        packetWrapper.passthrough(Type.UNSIGNED_BYTE);
                        packetWrapper.passthrough(Type.UNSIGNED_BYTE);
                    }
                });
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.UNLOAD_CHUNK, ClientboundPackets1_8.CHUNK_DATA, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    int chunkX = packetWrapper.read(Type.INT);
                    int chunkZ = packetWrapper.read(Type.INT);
                    ClientWorld world = packetWrapper.user().get(ClientWorld.class);
                    packetWrapper.write(new Chunk1_8Type(world), new BaseChunk(chunkX, chunkZ, true, false, 0, new ChunkSection[16], null, new ArrayList<CompoundTag>()));
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_9.CHUNK_DATA, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    ClientWorld world = packetWrapper.user().get(ClientWorld.class);
                    Chunk chunk = packetWrapper.read(new Chunk1_9_1_2Type(world));
                    for (ChunkSection section : chunk.getSections()) {
                        if (section == null) continue;
                        for (int i = 0; i < section.getPaletteSize(); ++i) {
                            int block = section.getPaletteEntry(i);
                            int replacedBlock = ReplacementRegistry1_8to1_9.replace(block);
                            section.setPaletteEntry(i, replacedBlock);
                        }
                    }
                    if (chunk.isFullChunk() && chunk.getBitmask() == 0) {
                        boolean skylight = world.getEnvironment() == Environment.NORMAL;
                        ChunkSection[] sections = new ChunkSection[16];
                        ChunkSectionImpl section = new ChunkSectionImpl(true);
                        sections[0] = section;
                        section.addPaletteEntry(0);
                        if (skylight) {
                            section.getLight().setSkyLight(new byte[2048]);
                        }
                        chunk = new BaseChunk(chunk.getX(), chunk.getZ(), true, false, 1, sections, chunk.getBiomeData(), chunk.getBlockEntities());
                    }
                    packetWrapper.write(new Chunk1_8Type(world), chunk);
                    UserConnection user = packetWrapper.user();
                    chunk.getBlockEntities().forEach(nbt -> {
                        short action;
                        String id;
                        if (!(nbt.contains("x") && nbt.contains("y") && nbt.contains("z") && nbt.contains("id"))) {
                            return;
                        }
                        Position position = new Position((int)((Integer)((Tag)nbt.get("x")).getValue()), (short)((Integer)((Tag)nbt.get("y")).getValue()).intValue(), (int)((Integer)((Tag)nbt.get("z")).getValue()));
                        switch (id = (String)((Tag)nbt.get("id")).getValue()) {
                            case "minecraft:mob_spawner": {
                                action = 1;
                                break;
                            }
                            case "minecraft:command_block": {
                                action = 2;
                                break;
                            }
                            case "minecraft:beacon": {
                                action = 3;
                                break;
                            }
                            case "minecraft:skull": {
                                action = 4;
                                break;
                            }
                            case "minecraft:flower_pot": {
                                action = 5;
                                break;
                            }
                            case "minecraft:banner": {
                                action = 6;
                                break;
                            }
                            default: {
                                return;
                            }
                        }
                        PacketWrapper updateTileEntity = PacketWrapper.create(9, null, user);
                        updateTileEntity.write(Type.POSITION, position);
                        updateTileEntity.write(Type.UNSIGNED_BYTE, action);
                        updateTileEntity.write(Type.NBT, nbt);
                        PacketUtil.sendPacket(updateTileEntity, Protocol1_8TO1_9.class, false, false);
                    });
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.EFFECT, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.POSITION);
                this.map(Type.INT);
                this.map(Type.BOOLEAN);
                this.handler(packetWrapper -> {
                    int id = packetWrapper.get(Type.INT, 0);
                    if ((id = Effect.getOldId(id)) == -1) {
                        packetWrapper.cancel();
                        return;
                    }
                    packetWrapper.set(Type.INT, 0, id);
                    if (id == 2001) {
                        int replacedBlock = ReplacementRegistry1_8to1_9.replace(packetWrapper.get(Type.INT, 1));
                        packetWrapper.set(Type.INT, 1, replacedBlock);
                    }
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.SPAWN_PARTICLE, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.handler(packetWrapper -> {
                    int type = packetWrapper.get(Type.INT, 0);
                    if (type > 41 && !ViaRewind.getConfig().isReplaceParticles()) {
                        packetWrapper.cancel();
                        return;
                    }
                    if (type == 42) {
                        packetWrapper.set(Type.INT, 0, 24);
                    } else if (type == 43) {
                        packetWrapper.set(Type.INT, 0, 3);
                    } else if (type == 44) {
                        packetWrapper.set(Type.INT, 0, 34);
                    } else if (type == 45) {
                        packetWrapper.set(Type.INT, 0, 1);
                    }
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.MAP_DATA, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.BYTE);
                this.map((Type)Type.BOOLEAN, Type.NOTHING);
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.SOUND, ClientboundPackets1_8.NAMED_SOUND, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    int soundId = packetWrapper.read(Type.VAR_INT);
                    String sound = SoundRemapper.oldNameFromId(soundId);
                    if (sound == null) {
                        packetWrapper.cancel();
                    } else {
                        packetWrapper.write(Type.STRING, sound);
                    }
                });
                this.handler(packetWrapper -> packetWrapper.read(Type.VAR_INT));
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.INT);
                this.map(Type.FLOAT);
                this.map(Type.UNSIGNED_BYTE);
            }
        });
    }
}

