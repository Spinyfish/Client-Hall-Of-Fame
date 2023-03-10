/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_8to1_9.packets;

import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.entities.EntityType;
import com.viaversion.viaversion.api.minecraft.metadata.Metadata;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.api.type.types.version.Types1_8;
import com.viaversion.viaversion.api.type.types.version.Types1_9;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_8.ServerboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import de.gerrygames.viarewind.ViaRewind;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.Protocol1_8TO1_9;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.entityreplacement.ShulkerBulletReplacement;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.entityreplacement.ShulkerReplacement;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.items.ReplacementRegistry1_8to1_9;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.metadata.MetadataRewriter;
import de.gerrygames.viarewind.protocol.protocol1_8to1_9.storage.EntityTracker;
import de.gerrygames.viarewind.replacement.EntityReplacement;
import de.gerrygames.viarewind.replacement.Replacement;
import de.gerrygames.viarewind.utils.PacketUtil;
import java.util.List;

public class SpawnPackets {
    public static void register(Protocol<ClientboundPackets1_9, ClientboundPackets1_8, ServerboundPackets1_9, ServerboundPackets1_8> protocol) {
        protocol.registerClientbound(ClientboundPackets1_9.SPAWN_ENTITY, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID, Type.NOTHING);
                this.map(Type.BYTE);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.INT);
                this.handler(packetWrapper -> {
                    int blockData;
                    int blockId;
                    Replacement replace;
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    byte typeId = packetWrapper.get(Type.BYTE, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    Entity1_10Types.EntityType type = Entity1_10Types.getTypeFromId(typeId, true);
                    if (typeId == 3 || typeId == 91 || typeId == 92 || typeId == 93) {
                        packetWrapper.cancel();
                        return;
                    }
                    if (type == null) {
                        ViaRewind.getPlatform().getLogger().warning("[ViaRewind] Unhandled Spawn Object Type: " + typeId);
                        packetWrapper.cancel();
                        return;
                    }
                    int x = packetWrapper.get(Type.INT, 0);
                    int y = packetWrapper.get(Type.INT, 1);
                    int z = packetWrapper.get(Type.INT, 2);
                    if (type.is((EntityType)Entity1_10Types.EntityType.BOAT)) {
                        byte yaw = packetWrapper.get(Type.BYTE, 1);
                        yaw = (byte)(yaw - 64);
                        packetWrapper.set(Type.BYTE, 1, yaw);
                        packetWrapper.set(Type.INT, 1, y += 10);
                    } else if (type.is((EntityType)Entity1_10Types.EntityType.SHULKER_BULLET)) {
                        packetWrapper.cancel();
                        ShulkerBulletReplacement shulkerBulletReplacement = new ShulkerBulletReplacement(entityId, packetWrapper.user());
                        shulkerBulletReplacement.setLocation((double)x / 32.0, (double)y / 32.0, (double)z / 32.0);
                        tracker.addEntityReplacement(shulkerBulletReplacement);
                        return;
                    }
                    int data = packetWrapper.get(Type.INT, 3);
                    if (type.isOrHasParent(Entity1_10Types.EntityType.ARROW) && data != 0) {
                        packetWrapper.set(Type.INT, 3, --data);
                    }
                    if (type.is((EntityType)Entity1_10Types.EntityType.FALLING_BLOCK) && (replace = ReplacementRegistry1_8to1_9.getReplacement(blockId = data & 0xFFF, blockData = data >> 12 & 0xF)) != null) {
                        packetWrapper.set(Type.INT, 3, replace.getId() | replace.replaceData(data) << 12);
                    }
                    if (data > 0) {
                        packetWrapper.passthrough(Type.SHORT);
                        packetWrapper.passthrough(Type.SHORT);
                        packetWrapper.passthrough(Type.SHORT);
                    } else {
                        short vX = packetWrapper.read(Type.SHORT);
                        short vY = packetWrapper.read(Type.SHORT);
                        short vZ = packetWrapper.read(Type.SHORT);
                        PacketWrapper velocityPacket = PacketWrapper.create(18, null, packetWrapper.user());
                        velocityPacket.write(Type.VAR_INT, entityId);
                        velocityPacket.write(Type.SHORT, vX);
                        velocityPacket.write(Type.SHORT, vY);
                        velocityPacket.write(Type.SHORT, vZ);
                        PacketUtil.sendPacket(velocityPacket, Protocol1_8TO1_9.class);
                    }
                    tracker.getClientEntityTypes().put(entityId, type);
                    tracker.sendMetadataBuffer(entityId);
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.SPAWN_EXPERIENCE_ORB, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.SHORT);
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityId, Entity1_10Types.EntityType.EXPERIENCE_ORB);
                    tracker.sendMetadataBuffer(entityId);
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.SPAWN_GLOBAL_ENTITY, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.BYTE);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityId, Entity1_10Types.EntityType.LIGHTNING);
                    tracker.sendMetadataBuffer(entityId);
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.SPAWN_MOB, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID, Type.NOTHING);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Type.SHORT);
                this.map(Types1_9.METADATA_LIST, Types1_8.METADATA_LIST);
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    short typeId = packetWrapper.get(Type.UNSIGNED_BYTE, 0);
                    int x = packetWrapper.get(Type.INT, 0);
                    int y = packetWrapper.get(Type.INT, 1);
                    int z = packetWrapper.get(Type.INT, 2);
                    byte pitch = packetWrapper.get(Type.BYTE, 1);
                    byte yaw = packetWrapper.get(Type.BYTE, 0);
                    byte headYaw = packetWrapper.get(Type.BYTE, 2);
                    if (typeId == 69) {
                        packetWrapper.cancel();
                        EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                        ShulkerReplacement shulkerReplacement = new ShulkerReplacement(entityId, packetWrapper.user());
                        shulkerReplacement.setLocation((double)x / 32.0, (double)y / 32.0, (double)z / 32.0);
                        shulkerReplacement.setYawPitch((float)yaw * 360.0f / 256.0f, (float)pitch * 360.0f / 256.0f);
                        shulkerReplacement.setHeadYaw((float)headYaw * 360.0f / 256.0f);
                        tracker.addEntityReplacement(shulkerReplacement);
                    } else if (typeId == -1 || typeId == 255) {
                        packetWrapper.cancel();
                    }
                });
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    short typeId = packetWrapper.get(Type.UNSIGNED_BYTE, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityId, Entity1_10Types.getTypeFromId(typeId, false));
                    tracker.sendMetadataBuffer(entityId);
                });
                this.handler(wrapper -> {
                    List<Metadata> metadataList = wrapper.get(Types1_8.METADATA_LIST, 0);
                    int entityId = wrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = wrapper.user().get(EntityTracker.class);
                    EntityReplacement replacement = tracker.getEntityReplacement(entityId);
                    if (replacement != null) {
                        replacement.updateMetadata(metadataList);
                    } else if (tracker.getClientEntityTypes().containsKey(entityId)) {
                        MetadataRewriter.transform(tracker.getClientEntityTypes().get(entityId), metadataList);
                    } else {
                        wrapper.cancel();
                    }
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.SPAWN_PAINTING, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID, Type.NOTHING);
                this.map(Type.STRING);
                this.map(Type.POSITION);
                this.map((Type)Type.BYTE, Type.UNSIGNED_BYTE);
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityId, Entity1_10Types.EntityType.PAINTING);
                    tracker.sendMetadataBuffer(entityId);
                });
            }
        }));
        protocol.registerClientbound(ClientboundPackets1_9.SPAWN_PLAYER, (ClientboundPackets1_8)((Object)new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.UUID);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.DOUBLE, Protocol1_8TO1_9.TO_OLD_INT);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.handler(packetWrapper -> packetWrapper.write(Type.SHORT, (short)0));
                this.map(Types1_9.METADATA_LIST, Types1_8.METADATA_LIST);
                this.handler(wrapper -> {
                    List<Metadata> metadataList = wrapper.get(Types1_8.METADATA_LIST, 0);
                    MetadataRewriter.transform(Entity1_10Types.EntityType.PLAYER, metadataList);
                });
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.getClientEntityTypes().put(entityId, Entity1_10Types.EntityType.PLAYER);
                    tracker.sendMetadataBuffer(entityId);
                });
            }
        }));
    }
}

