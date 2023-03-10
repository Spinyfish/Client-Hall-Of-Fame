/*
 * Decompiled with CFR 0.152.
 */
package de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.Position;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonParser;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.CompoundTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.ListTag;
import com.viaversion.viaversion.libs.opennbt.tag.builtin.StringTag;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import de.gerrygames.viarewind.ViaRewind;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.ClientboundPackets1_7;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.Protocol1_7_6_10TO1_8;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.ServerboundPackets1_7;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.entityreplacements.ArmorStandReplacement;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.items.ItemRewriter;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.provider.TitleRenderProvider;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.EntityTracker;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.GameProfileStorage;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.PlayerAbilities;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.PlayerPosition;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.Scoreboard;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.storage.Windows;
import de.gerrygames.viarewind.protocol.protocol1_7_6_10to1_8.types.Types1_7_6_10;
import de.gerrygames.viarewind.replacement.EntityReplacement;
import de.gerrygames.viarewind.utils.ChatUtil;
import de.gerrygames.viarewind.utils.PacketUtil;
import de.gerrygames.viarewind.utils.Utils;
import de.gerrygames.viarewind.utils.math.AABB;
import de.gerrygames.viarewind.utils.math.Ray3d;
import de.gerrygames.viarewind.utils.math.RayTracing;
import de.gerrygames.viarewind.utils.math.Vector3d;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

public class PlayerPackets {
    public static void register(Protocol1_7_6_10TO1_8 protocol) {
        protocol.registerClientbound(ClientboundPackets1_8.JOIN_GAME, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.STRING);
                this.map((Type)Type.BOOLEAN, Type.NOTHING);
                this.handler(packetWrapper -> {
                    if (!ViaRewind.getConfig().isReplaceAdventureMode()) {
                        return;
                    }
                    if (packetWrapper.get(Type.UNSIGNED_BYTE, 0) == 2) {
                        packetWrapper.set(Type.UNSIGNED_BYTE, 0, (short)0);
                    }
                });
                this.handler(packetWrapper -> {
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.setGamemode(packetWrapper.get(Type.UNSIGNED_BYTE, 0).shortValue());
                    tracker.setPlayerId(packetWrapper.get(Type.INT, 0));
                    tracker.getClientEntityTypes().put(tracker.getPlayerId(), Entity1_10Types.EntityType.ENTITY_HUMAN);
                    tracker.setDimension(packetWrapper.get(Type.BYTE, 0).byteValue());
                });
                this.handler(packetWrapper -> {
                    ClientWorld world = packetWrapper.user().get(ClientWorld.class);
                    world.setEnvironment(packetWrapper.get(Type.BYTE, 0).byteValue());
                });
                this.handler(wrapper -> wrapper.user().put(new Scoreboard(wrapper.user())));
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.CHAT_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.COMPONENT);
                this.handler(packetWrapper -> {
                    byte position = packetWrapper.read(Type.BYTE);
                    if (position == 2) {
                        packetWrapper.cancel();
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.SPAWN_POSITION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    Position position = packetWrapper.read(Type.POSITION);
                    packetWrapper.write(Type.INT, position.getX());
                    packetWrapper.write(Type.INT, position.getY());
                    packetWrapper.write(Type.INT, position.getZ());
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.UPDATE_HEALTH, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.FLOAT);
                this.map((Type)Type.VAR_INT, Type.SHORT);
                this.map(Type.FLOAT);
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.RESPAWN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.STRING);
                this.handler(packetWrapper -> {
                    if (!ViaRewind.getConfig().isReplaceAdventureMode()) {
                        return;
                    }
                    if (packetWrapper.get(Type.UNSIGNED_BYTE, 1) == 2) {
                        packetWrapper.set(Type.UNSIGNED_BYTE, 1, (short)0);
                    }
                });
                this.handler(packetWrapper -> {
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    tracker.setGamemode(packetWrapper.get(Type.UNSIGNED_BYTE, 1).shortValue());
                    if (tracker.getDimension() != packetWrapper.get(Type.INT, 0).intValue()) {
                        tracker.setDimension(packetWrapper.get(Type.INT, 0));
                        tracker.clearEntities();
                        tracker.getClientEntityTypes().put(tracker.getPlayerId(), Entity1_10Types.EntityType.ENTITY_HUMAN);
                    }
                });
                this.handler(packetWrapper -> {
                    ClientWorld world = packetWrapper.user().get(ClientWorld.class);
                    world.setEnvironment(packetWrapper.get(Type.INT, 0));
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.PLAYER_POSITION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.handler(packetWrapper -> {
                    PlayerPosition playerPosition = packetWrapper.user().get(PlayerPosition.class);
                    playerPosition.setPositionPacketReceived(true);
                    byte flags = packetWrapper.read(Type.BYTE);
                    if ((flags & 1) == 1) {
                        double x = packetWrapper.get(Type.DOUBLE, 0);
                        packetWrapper.set(Type.DOUBLE, 0, x += playerPosition.getPosX());
                    }
                    double y = packetWrapper.get(Type.DOUBLE, 1);
                    if ((flags & 2) == 2) {
                        y += playerPosition.getPosY();
                    }
                    playerPosition.setReceivedPosY(y);
                    packetWrapper.set(Type.DOUBLE, 1, y += (double)1.62f);
                    if ((flags & 4) == 4) {
                        double z = packetWrapper.get(Type.DOUBLE, 2);
                        packetWrapper.set(Type.DOUBLE, 2, z += playerPosition.getPosZ());
                    }
                    if ((flags & 8) == 8) {
                        float yaw = packetWrapper.get(Type.FLOAT, 0).floatValue();
                        packetWrapper.set(Type.FLOAT, 0, Float.valueOf(yaw += playerPosition.getYaw()));
                    }
                    if ((flags & 0x10) == 16) {
                        float pitch = packetWrapper.get(Type.FLOAT, 1).floatValue();
                        packetWrapper.set(Type.FLOAT, 1, Float.valueOf(pitch += playerPosition.getPitch()));
                    }
                });
                this.handler(packetWrapper -> {
                    PlayerPosition playerPosition = packetWrapper.user().get(PlayerPosition.class);
                    packetWrapper.write(Type.BOOLEAN, playerPosition.isOnGround());
                });
                this.handler(packetWrapper -> {
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    if (tracker.getSpectating() != tracker.getPlayerId()) {
                        packetWrapper.cancel();
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.SET_EXPERIENCE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.FLOAT);
                this.map((Type)Type.VAR_INT, Type.SHORT);
                this.map((Type)Type.VAR_INT, Type.SHORT);
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.GAME_EVENT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.FLOAT);
                this.handler(packetWrapper -> {
                    short mode = packetWrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (mode != 3) {
                        return;
                    }
                    int gamemode = packetWrapper.get(Type.FLOAT, 0).intValue();
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    if (gamemode == 3 || tracker.getGamemode() == 3) {
                        Item[] equipment;
                        UUID uuid = packetWrapper.user().getProtocolInfo().getUuid();
                        if (gamemode == 3) {
                            GameProfileStorage.GameProfile profile = packetWrapper.user().get(GameProfileStorage.class).get(uuid);
                            equipment = new Item[5];
                            equipment[4] = profile.getSkull();
                        } else {
                            equipment = tracker.getPlayerEquipment(uuid);
                            if (equipment == null) {
                                equipment = new Item[5];
                            }
                        }
                        for (int i = 1; i < 5; ++i) {
                            PacketWrapper setSlot = PacketWrapper.create(47, null, packetWrapper.user());
                            setSlot.write(Type.BYTE, (byte)0);
                            setSlot.write(Type.SHORT, (short)(9 - i));
                            setSlot.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, equipment[i]);
                            PacketUtil.sendPacket(setSlot, Protocol1_7_6_10TO1_8.class);
                        }
                    }
                });
                this.handler(packetWrapper -> {
                    short mode = packetWrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (mode == 3) {
                        int gamemode = packetWrapper.get(Type.FLOAT, 0).intValue();
                        if (gamemode == 2 && ViaRewind.getConfig().isReplaceAdventureMode()) {
                            gamemode = 0;
                            packetWrapper.set(Type.FLOAT, 0, Float.valueOf(0.0f));
                        }
                        packetWrapper.user().get(EntityTracker.class).setGamemode(gamemode);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.OPEN_SIGN_EDITOR, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    Position position = packetWrapper.read(Type.POSITION);
                    packetWrapper.write(Type.INT, position.getX());
                    packetWrapper.write(Type.INT, position.getY());
                    packetWrapper.write(Type.INT, position.getZ());
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.PLAYER_INFO, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    packetWrapper.cancel();
                    int action = packetWrapper.read(Type.VAR_INT);
                    int count = packetWrapper.read(Type.VAR_INT);
                    GameProfileStorage gameProfileStorage = packetWrapper.user().get(GameProfileStorage.class);
                    for (int i = 0; i < count; ++i) {
                        GameProfileStorage.GameProfile gameProfile;
                        GameProfileStorage.GameProfile gameProfile2;
                        UUID uuid = packetWrapper.read(Type.UUID);
                        if (action == 0) {
                            int ping;
                            String name = packetWrapper.read(Type.STRING);
                            gameProfile2 = gameProfileStorage.get(uuid);
                            if (gameProfile2 == null) {
                                gameProfile2 = gameProfileStorage.put(uuid, name);
                            }
                            int propertyCount = packetWrapper.read(Type.VAR_INT);
                            while (propertyCount-- > 0) {
                                gameProfile2.properties.add(new GameProfileStorage.Property(packetWrapper.read(Type.STRING), packetWrapper.read(Type.STRING), packetWrapper.read(Type.BOOLEAN) != false ? packetWrapper.read(Type.STRING) : null));
                            }
                            int gamemode = packetWrapper.read(Type.VAR_INT);
                            gameProfile2.ping = ping = packetWrapper.read(Type.VAR_INT).intValue();
                            gameProfile2.gamemode = gamemode;
                            if (packetWrapper.read(Type.BOOLEAN).booleanValue()) {
                                gameProfile2.setDisplayName(ChatUtil.jsonToLegacy(packetWrapper.read(Type.COMPONENT)));
                            }
                            PacketWrapper packet = PacketWrapper.create(56, null, packetWrapper.user());
                            packet.write(Type.STRING, gameProfile2.name);
                            packet.write(Type.BOOLEAN, true);
                            packet.write(Type.SHORT, (short)ping);
                            PacketUtil.sendPacket(packet, Protocol1_7_6_10TO1_8.class);
                            continue;
                        }
                        if (action == 1) {
                            EntityTracker tracker;
                            int entityId;
                            int gamemode = packetWrapper.read(Type.VAR_INT);
                            gameProfile2 = gameProfileStorage.get(uuid);
                            if (gameProfile2 == null || gameProfile2.gamemode == gamemode) continue;
                            if ((gamemode == 3 || gameProfile2.gamemode == 3) && (entityId = (tracker = packetWrapper.user().get(EntityTracker.class)).getPlayerEntityId(uuid)) != -1) {
                                Item[] equipment;
                                if (gamemode == 3) {
                                    equipment = new Item[5];
                                    equipment[4] = gameProfile2.getSkull();
                                } else {
                                    equipment = tracker.getPlayerEquipment(uuid);
                                    if (equipment == null) {
                                        equipment = new Item[5];
                                    }
                                }
                                for (short slot = 0; slot < 5; slot = (short)(slot + 1)) {
                                    PacketWrapper equipmentPacket = PacketWrapper.create(4, null, packetWrapper.user());
                                    equipmentPacket.write(Type.INT, entityId);
                                    equipmentPacket.write(Type.SHORT, slot);
                                    equipmentPacket.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, equipment[slot]);
                                    PacketUtil.sendPacket(equipmentPacket, Protocol1_7_6_10TO1_8.class);
                                }
                            }
                            gameProfile2.gamemode = gamemode;
                            continue;
                        }
                        if (action == 2) {
                            int ping = packetWrapper.read(Type.VAR_INT);
                            gameProfile2 = gameProfileStorage.get(uuid);
                            if (gameProfile2 == null) continue;
                            gameProfile2.ping = ping;
                            PacketWrapper packet = PacketWrapper.create(56, null, packetWrapper.user());
                            packet.write(Type.STRING, gameProfile2.name);
                            packet.write(Type.BOOLEAN, true);
                            packet.write(Type.SHORT, (short)ping);
                            PacketUtil.sendPacket(packet, Protocol1_7_6_10TO1_8.class);
                            continue;
                        }
                        if (action == 3) {
                            String displayName = packetWrapper.read(Type.BOOLEAN) != false ? ChatUtil.jsonToLegacy(packetWrapper.read(Type.COMPONENT)) : null;
                            gameProfile2 = gameProfileStorage.get(uuid);
                            if (gameProfile2 == null || gameProfile2.displayName == null && displayName == null || !(gameProfile2.displayName == null && displayName != null || gameProfile2.displayName != null && displayName == null) && gameProfile2.displayName.equals(displayName)) continue;
                            gameProfile2.setDisplayName(displayName);
                            continue;
                        }
                        if (action != 4 || (gameProfile = gameProfileStorage.remove(uuid)) == null) continue;
                        PacketWrapper packet = PacketWrapper.create(56, null, packetWrapper.user());
                        packet.write(Type.STRING, gameProfile.name);
                        packet.write(Type.BOOLEAN, false);
                        packet.write(Type.SHORT, (short)gameProfile.ping);
                        PacketUtil.sendPacket(packet, Protocol1_7_6_10TO1_8.class);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.PLAYER_ABILITIES, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.BYTE);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.handler(packetWrapper -> {
                    byte flags = packetWrapper.get(Type.BYTE, 0);
                    float flySpeed = packetWrapper.get(Type.FLOAT, 0).floatValue();
                    float walkSpeed = packetWrapper.get(Type.FLOAT, 1).floatValue();
                    PlayerAbilities abilities = packetWrapper.user().get(PlayerAbilities.class);
                    abilities.setInvincible((flags & 8) == 8);
                    abilities.setAllowFly((flags & 4) == 4);
                    abilities.setFlying((flags & 2) == 2);
                    abilities.setCreative((flags & 1) == 1);
                    abilities.setFlySpeed(flySpeed);
                    abilities.setWalkSpeed(walkSpeed);
                    if (abilities.isSprinting() && abilities.isFlying()) {
                        packetWrapper.set(Type.FLOAT, 0, Float.valueOf(abilities.getFlySpeed() * 2.0f));
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.PLUGIN_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(packetWrapper -> {
                    String channel = packetWrapper.get(Type.STRING, 0);
                    if (channel.equalsIgnoreCase("MC|TrList")) {
                        packetWrapper.passthrough(Type.INT);
                        int size = packetWrapper.isReadable(Type.BYTE, 0) ? packetWrapper.passthrough(Type.BYTE).byteValue() : packetWrapper.passthrough(Type.UNSIGNED_BYTE).shortValue();
                        for (int i = 0; i < size; ++i) {
                            Item item = ItemRewriter.toClient(packetWrapper.read(Type.ITEM));
                            packetWrapper.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, item);
                            item = ItemRewriter.toClient(packetWrapper.read(Type.ITEM));
                            packetWrapper.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, item);
                            boolean has3Items = packetWrapper.passthrough(Type.BOOLEAN);
                            if (has3Items) {
                                item = ItemRewriter.toClient(packetWrapper.read(Type.ITEM));
                                packetWrapper.write(Types1_7_6_10.COMPRESSED_NBT_ITEM, item);
                            }
                            packetWrapper.passthrough(Type.BOOLEAN);
                            packetWrapper.read(Type.INT);
                            packetWrapper.read(Type.INT);
                        }
                    } else if (channel.equalsIgnoreCase("MC|Brand")) {
                        packetWrapper.write(Type.REMAINING_BYTES, packetWrapper.read(Type.STRING).getBytes(StandardCharsets.UTF_8));
                    }
                    packetWrapper.cancel();
                    packetWrapper.setId(-1);
                    ByteBuf newPacketBuf = Unpooled.buffer();
                    packetWrapper.writeToBuffer(newPacketBuf);
                    PacketWrapper newWrapper = PacketWrapper.create(63, newPacketBuf, packetWrapper.user());
                    newWrapper.passthrough(Type.STRING);
                    if (newPacketBuf.readableBytes() <= Short.MAX_VALUE) {
                        newWrapper.write(Type.SHORT, (short)newPacketBuf.readableBytes());
                        newWrapper.send(Protocol1_7_6_10TO1_8.class);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.CAMERA, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    packetWrapper.cancel();
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    int entityId = packetWrapper.read(Type.VAR_INT);
                    int spectating = tracker.getSpectating();
                    if (spectating != entityId) {
                        tracker.setSpectating(entityId);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.TITLE, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    packetWrapper.cancel();
                    TitleRenderProvider titleRenderProvider = Via.getManager().getProviders().get(TitleRenderProvider.class);
                    if (titleRenderProvider == null) {
                        return;
                    }
                    int action = packetWrapper.read(Type.VAR_INT);
                    UUID uuid = Utils.getUUID(packetWrapper.user());
                    switch (action) {
                        case 0: {
                            titleRenderProvider.setTitle(uuid, packetWrapper.read(Type.STRING));
                            break;
                        }
                        case 1: {
                            titleRenderProvider.setSubTitle(uuid, packetWrapper.read(Type.STRING));
                            break;
                        }
                        case 2: {
                            titleRenderProvider.setTimings(uuid, packetWrapper.read(Type.INT), packetWrapper.read(Type.INT), packetWrapper.read(Type.INT));
                            break;
                        }
                        case 3: {
                            titleRenderProvider.clear(uuid);
                            break;
                        }
                        case 4: {
                            titleRenderProvider.reset(uuid);
                        }
                    }
                });
            }
        });
        protocol.cancelClientbound(ClientboundPackets1_8.TAB_LIST);
        protocol.registerClientbound(ClientboundPackets1_8.RESOURCE_PACK, ClientboundPackets1_7.PLUGIN_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.create(Type.STRING, "MC|RPack");
                this.handler(packetWrapper -> {
                    ByteBuf buf = ByteBufAllocator.DEFAULT.buffer();
                    try {
                        Type.STRING.write(buf, packetWrapper.read(Type.STRING));
                        packetWrapper.write(Type.SHORT_BYTE_ARRAY, (byte[])Type.REMAINING_BYTES.read(buf));
                    }
                    finally {
                        buf.release();
                    }
                });
                this.map(Type.STRING, Type.NOTHING);
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.CHAT_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(packetWrapper -> {
                    String msg = packetWrapper.get(Type.STRING, 0);
                    int gamemode = packetWrapper.user().get(EntityTracker.class).getGamemode();
                    if (gamemode == 3 && msg.toLowerCase().startsWith("/stp ")) {
                        String username = msg.split(" ")[1];
                        GameProfileStorage storage = packetWrapper.user().get(GameProfileStorage.class);
                        GameProfileStorage.GameProfile profile = storage.get(username, true);
                        if (profile != null && profile.uuid != null) {
                            packetWrapper.cancel();
                            PacketWrapper teleportPacket = PacketWrapper.create(24, null, packetWrapper.user());
                            teleportPacket.write(Type.UUID, profile.uuid);
                            PacketUtil.sendToServer(teleportPacket, Protocol1_7_6_10TO1_8.class, true, true);
                        }
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.INTERACT_ENTITY, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map((Type)Type.INT, Type.VAR_INT);
                this.map((Type)Type.BYTE, Type.VAR_INT);
                this.handler(packetWrapper -> {
                    int mode = packetWrapper.get(Type.VAR_INT, 1);
                    if (mode != 0) {
                        return;
                    }
                    int entityId = packetWrapper.get(Type.VAR_INT, 0);
                    EntityTracker tracker = packetWrapper.user().get(EntityTracker.class);
                    EntityReplacement replacement = tracker.getEntityReplacement(entityId);
                    if (!(replacement instanceof ArmorStandReplacement)) {
                        return;
                    }
                    ArmorStandReplacement armorStand = (ArmorStandReplacement)replacement;
                    AABB boundingBox = armorStand.getBoundingBox();
                    PlayerPosition playerPosition = packetWrapper.user().get(PlayerPosition.class);
                    Vector3d pos = new Vector3d(playerPosition.getPosX(), playerPosition.getPosY() + 1.8, playerPosition.getPosZ());
                    double yaw = Math.toRadians(playerPosition.getYaw());
                    double pitch = Math.toRadians(playerPosition.getPitch());
                    Vector3d dir = new Vector3d(-Math.cos(pitch) * Math.sin(yaw), -Math.sin(pitch), Math.cos(pitch) * Math.cos(yaw));
                    Ray3d ray = new Ray3d(pos, dir);
                    Vector3d intersection = RayTracing.trace(ray, boundingBox, 5.0);
                    if (intersection == null) {
                        return;
                    }
                    intersection.substract(boundingBox.getMin());
                    mode = 2;
                    packetWrapper.set(Type.VAR_INT, 1, mode);
                    packetWrapper.write(Type.FLOAT, Float.valueOf((float)intersection.getX()));
                    packetWrapper.write(Type.FLOAT, Float.valueOf((float)intersection.getY()));
                    packetWrapper.write(Type.FLOAT, Float.valueOf((float)intersection.getZ()));
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.PLAYER_MOVEMENT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.BOOLEAN);
                this.handler(packetWrapper -> {
                    PlayerPosition playerPosition = packetWrapper.user().get(PlayerPosition.class);
                    playerPosition.setOnGround(packetWrapper.get(Type.BOOLEAN, 0));
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.PLAYER_POSITION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map((Type)Type.DOUBLE, Type.NOTHING);
                this.map(Type.DOUBLE);
                this.map(Type.BOOLEAN);
                this.handler(packetWrapper -> {
                    double x = packetWrapper.get(Type.DOUBLE, 0);
                    double feetY = packetWrapper.get(Type.DOUBLE, 1);
                    double z = packetWrapper.get(Type.DOUBLE, 2);
                    PlayerPosition playerPosition = packetWrapper.user().get(PlayerPosition.class);
                    if (playerPosition.isPositionPacketReceived()) {
                        playerPosition.setPositionPacketReceived(false);
                        packetWrapper.set(Type.DOUBLE, 1, feetY -= 0.01);
                    }
                    playerPosition.setOnGround(packetWrapper.get(Type.BOOLEAN, 0));
                    playerPosition.setPos(x, feetY, z);
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.PLAYER_ROTATION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.BOOLEAN);
                this.handler(packetWrapper -> {
                    PlayerPosition playerPosition = packetWrapper.user().get(PlayerPosition.class);
                    playerPosition.setYaw(packetWrapper.get(Type.FLOAT, 0).floatValue());
                    playerPosition.setPitch(packetWrapper.get(Type.FLOAT, 1).floatValue());
                    playerPosition.setOnGround(packetWrapper.get(Type.BOOLEAN, 0));
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.PLAYER_POSITION_AND_ROTATION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map((Type)Type.DOUBLE, Type.NOTHING);
                this.map(Type.DOUBLE);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.BOOLEAN);
                this.handler(packetWrapper -> {
                    double x = packetWrapper.get(Type.DOUBLE, 0);
                    double feetY = packetWrapper.get(Type.DOUBLE, 1);
                    double z = packetWrapper.get(Type.DOUBLE, 2);
                    float yaw = packetWrapper.get(Type.FLOAT, 0).floatValue();
                    float pitch = packetWrapper.get(Type.FLOAT, 1).floatValue();
                    PlayerPosition playerPosition = packetWrapper.user().get(PlayerPosition.class);
                    if (playerPosition.isPositionPacketReceived()) {
                        playerPosition.setPositionPacketReceived(false);
                        feetY = playerPosition.getReceivedPosY();
                        packetWrapper.set(Type.DOUBLE, 1, feetY);
                    }
                    playerPosition.setOnGround(packetWrapper.get(Type.BOOLEAN, 0));
                    playerPosition.setPos(x, feetY, z);
                    playerPosition.setYaw(yaw);
                    playerPosition.setPitch(pitch);
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.PLAYER_DIGGING, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.handler(packetWrapper -> {
                    int x = packetWrapper.read(Type.INT);
                    short y = packetWrapper.read(Type.UNSIGNED_BYTE);
                    int z = packetWrapper.read(Type.INT);
                    packetWrapper.write(Type.POSITION, new Position(x, y, z));
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.PLAYER_BLOCK_PLACEMENT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    int x = packetWrapper.read(Type.INT);
                    short y = packetWrapper.read(Type.UNSIGNED_BYTE);
                    int z = packetWrapper.read(Type.INT);
                    packetWrapper.write(Type.POSITION, new Position(x, y, z));
                    packetWrapper.passthrough(Type.BYTE);
                    Item item = packetWrapper.read(Types1_7_6_10.COMPRESSED_NBT_ITEM);
                    item = ItemRewriter.toServer(item);
                    packetWrapper.write(Type.ITEM, item);
                    for (int i = 0; i < 3; ++i) {
                        packetWrapper.passthrough(Type.BYTE);
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.ANIMATION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    int entityId = packetWrapper.read(Type.INT);
                    int animation = packetWrapper.read(Type.BYTE).byteValue();
                    if (animation == 1) {
                        return;
                    }
                    packetWrapper.cancel();
                    switch (animation) {
                        case 104: {
                            animation = 0;
                            break;
                        }
                        case 105: {
                            animation = 1;
                            break;
                        }
                        case 3: {
                            animation = 2;
                            break;
                        }
                        default: {
                            return;
                        }
                    }
                    PacketWrapper entityAction = PacketWrapper.create(11, null, packetWrapper.user());
                    entityAction.write(Type.VAR_INT, entityId);
                    entityAction.write(Type.VAR_INT, animation);
                    entityAction.write(Type.VAR_INT, 0);
                    PacketUtil.sendPacket(entityAction, Protocol1_7_6_10TO1_8.class, true, true);
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.ENTITY_ACTION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map((Type)Type.INT, Type.VAR_INT);
                this.handler(packetWrapper -> packetWrapper.write(Type.VAR_INT, packetWrapper.read(Type.BYTE) - 1));
                this.map((Type)Type.INT, Type.VAR_INT);
                this.handler(packetWrapper -> {
                    int action = packetWrapper.get(Type.VAR_INT, 1);
                    if (action == 3 || action == 4) {
                        PlayerAbilities abilities = packetWrapper.user().get(PlayerAbilities.class);
                        abilities.setSprinting(action == 3);
                        PacketWrapper abilitiesPacket = PacketWrapper.create(57, null, packetWrapper.user());
                        abilitiesPacket.write(Type.BYTE, abilities.getFlags());
                        abilitiesPacket.write(Type.FLOAT, Float.valueOf(abilities.isSprinting() ? abilities.getFlySpeed() * 2.0f : abilities.getFlySpeed()));
                        abilitiesPacket.write(Type.FLOAT, Float.valueOf(abilities.getWalkSpeed()));
                        PacketUtil.sendPacket(abilitiesPacket, Protocol1_7_6_10TO1_8.class);
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.STEER_VEHICLE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.handler(packetWrapper -> {
                    EntityTracker tracker;
                    boolean jump = packetWrapper.read(Type.BOOLEAN);
                    boolean unmount = packetWrapper.read(Type.BOOLEAN);
                    short flags = 0;
                    if (jump) {
                        flags = (short)(flags + 1);
                    }
                    if (unmount) {
                        flags = (short)(flags + 2);
                    }
                    packetWrapper.write(Type.UNSIGNED_BYTE, flags);
                    if (unmount && (tracker = packetWrapper.user().get(EntityTracker.class)).getSpectating() != tracker.getPlayerId()) {
                        PacketWrapper sneakPacket = PacketWrapper.create(11, null, packetWrapper.user());
                        sneakPacket.write(Type.VAR_INT, tracker.getPlayerId());
                        sneakPacket.write(Type.VAR_INT, 0);
                        sneakPacket.write(Type.VAR_INT, 0);
                        PacketWrapper unsneakPacket = PacketWrapper.create(11, null, packetWrapper.user());
                        unsneakPacket.write(Type.VAR_INT, tracker.getPlayerId());
                        unsneakPacket.write(Type.VAR_INT, 1);
                        unsneakPacket.write(Type.VAR_INT, 0);
                        PacketUtil.sendToServer(sneakPacket, Protocol1_7_6_10TO1_8.class);
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.UPDATE_SIGN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(packetWrapper -> {
                    int x = packetWrapper.read(Type.INT);
                    short y = packetWrapper.read(Type.SHORT);
                    int z = packetWrapper.read(Type.INT);
                    packetWrapper.write(Type.POSITION, new Position(x, y, z));
                    for (int i = 0; i < 4; ++i) {
                        String line = packetWrapper.read(Type.STRING);
                        line = ChatUtil.legacyToJson(line);
                        packetWrapper.write(Type.COMPONENT, JsonParser.parseString(line));
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.PLAYER_ABILITIES, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.BYTE);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.handler(packetWrapper -> {
                    PlayerAbilities abilities = packetWrapper.user().get(PlayerAbilities.class);
                    if (abilities.isAllowFly()) {
                        byte flags = packetWrapper.get(Type.BYTE, 0);
                        abilities.setFlying((flags & 2) == 2);
                    }
                    packetWrapper.set(Type.FLOAT, 0, Float.valueOf(abilities.getFlySpeed()));
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.TAB_COMPLETE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.create(Type.OPTIONAL_POSITION, null);
                this.handler(packetWrapper -> {
                    String msg = packetWrapper.get(Type.STRING, 0);
                    if (msg.toLowerCase().startsWith("/stp ")) {
                        packetWrapper.cancel();
                        String[] args = msg.split(" ");
                        if (args.length <= 2) {
                            String prefix = args.length == 1 ? "" : args[1];
                            GameProfileStorage storage = packetWrapper.user().get(GameProfileStorage.class);
                            List<GameProfileStorage.GameProfile> profiles = storage.getAllWithPrefix(prefix, true);
                            PacketWrapper tabComplete = PacketWrapper.create(58, null, packetWrapper.user());
                            tabComplete.write(Type.VAR_INT, profiles.size());
                            for (GameProfileStorage.GameProfile profile : profiles) {
                                tabComplete.write(Type.STRING, profile.name);
                            }
                            PacketUtil.sendPacket(tabComplete, Protocol1_7_6_10TO1_8.class);
                        }
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.CLIENT_SETTINGS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.map(Type.BYTE);
                this.map(Type.BYTE);
                this.map(Type.BOOLEAN);
                this.map((Type)Type.BYTE, Type.NOTHING);
                this.handler(packetWrapper -> {
                    boolean cape = packetWrapper.read(Type.BOOLEAN);
                    packetWrapper.write(Type.UNSIGNED_BYTE, (short)(cape ? 127 : 126));
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_7.PLUGIN_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.map((Type)Type.SHORT, Type.NOTHING);
                this.handler(packetWrapper -> {
                    String channel;
                    switch (channel = packetWrapper.get(Type.STRING, 0)) {
                        case "MC|TrSel": {
                            packetWrapper.passthrough(Type.INT);
                            packetWrapper.read(Type.REMAINING_BYTES);
                            break;
                        }
                        case "MC|ItemName": {
                            byte[] data = packetWrapper.read(Type.REMAINING_BYTES);
                            String name = new String(data, StandardCharsets.UTF_8);
                            packetWrapper.write(Type.STRING, name);
                            Windows windows = packetWrapper.user().get(Windows.class);
                            PacketWrapper updateCost = PacketWrapper.create(49, null, packetWrapper.user());
                            updateCost.write(Type.UNSIGNED_BYTE, windows.anvilId);
                            updateCost.write(Type.SHORT, (short)0);
                            updateCost.write(Type.SHORT, windows.levelCost);
                            PacketUtil.sendPacket(updateCost, Protocol1_7_6_10TO1_8.class, true, true);
                            break;
                        }
                        case "MC|BEdit": 
                        case "MC|BSign": {
                            Item book = packetWrapper.read(Types1_7_6_10.COMPRESSED_NBT_ITEM);
                            CompoundTag tag = book.tag();
                            if (tag != null && tag.contains("pages")) {
                                ListTag pages = (ListTag)tag.get("pages");
                                for (int i = 0; i < pages.size(); ++i) {
                                    StringTag page = (StringTag)pages.get(i);
                                    String value = page.getValue();
                                    value = ChatUtil.legacyToJson(value);
                                    page.setValue(value);
                                }
                            }
                            packetWrapper.write(Type.ITEM, book);
                            break;
                        }
                        case "MC|Brand": {
                            packetWrapper.write(Type.STRING, new String(packetWrapper.read(Type.REMAINING_BYTES), StandardCharsets.UTF_8));
                        }
                    }
                });
            }
        });
    }
}

