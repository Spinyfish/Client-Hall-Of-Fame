/*
 * Decompiled with CFR 0.152.
 */
package com.viaversion.viaversion.protocols.protocol1_9to1_8.packets;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.minecraft.entities.Entity1_10Types;
import com.viaversion.viaversion.api.minecraft.item.Item;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.protocols.protocol1_8.ClientboundPackets1_8;
import com.viaversion.viaversion.protocols.protocol1_9_3to1_9_1_2.storage.ClientWorld;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ItemRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.PlayerMovementMapper;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ServerboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.chat.ChatRewriter;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.chat.GameMode;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.CommandBlockProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.CompressionProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.MainHandProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.ClientChunks;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;

public class PlayerPackets {
    public static void register(Protocol1_9To1_8 protocol) {
        protocol.registerClientbound(ClientboundPackets1_8.CHAT_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.BYTE);
                this.handler(wrapper -> {
                    try {
                        JsonObject obj = (JsonObject)wrapper.get(Type.COMPONENT, 0);
                        ChatRewriter.toClient(obj, wrapper.user());
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.TAB_LIST, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.DISCONNECT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING, Protocol1_9To1_8.FIX_JSON);
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.TITLE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    int action = wrapper.get(Type.VAR_INT, 0);
                    if (action == 0 || action == 1) {
                        Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING));
                    }
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
                this.map(Type.BYTE);
                this.handler(wrapper -> wrapper.write(Type.VAR_INT, 0));
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.TEAMS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.map(Type.BYTE);
                this.handler(wrapper -> {
                    byte mode = wrapper.get(Type.BYTE, 0);
                    if (mode == 0 || mode == 2) {
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough(Type.BYTE);
                        wrapper.passthrough(Type.STRING);
                        wrapper.write(Type.STRING, Via.getConfig().isPreventCollision() ? "never" : "");
                        wrapper.passthrough(Type.BYTE);
                    }
                    if (mode == 0 || mode == 3 || mode == 4) {
                        String[] players = wrapper.passthrough(Type.STRING_ARRAY);
                        EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        String myName = wrapper.user().getProtocolInfo().getUsername();
                        String teamName = wrapper.get(Type.STRING, 0);
                        for (String player : players) {
                            if (!entityTracker.isAutoTeam() || !player.equalsIgnoreCase(myName)) continue;
                            if (mode == 4) {
                                wrapper.send(Protocol1_9To1_8.class);
                                wrapper.cancel();
                                entityTracker.sendTeamPacket(true, true);
                                entityTracker.setCurrentTeam("viaversion");
                                continue;
                            }
                            entityTracker.sendTeamPacket(false, true);
                            entityTracker.setCurrentTeam(teamName);
                        }
                    }
                    if (mode == 1) {
                        EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        String teamName = wrapper.get(Type.STRING, 0);
                        if (entityTracker.isAutoTeam() && teamName.equals(entityTracker.getCurrentTeam())) {
                            wrapper.send(Protocol1_9To1_8.class);
                            wrapper.cancel();
                            entityTracker.sendTeamPacket(true, true);
                            entityTracker.setCurrentTeam("viaversion");
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.JOIN_GAME, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.handler(wrapper -> {
                    int entityId = wrapper.get(Type.INT, 0);
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.addEntity(entityId, Entity1_10Types.EntityType.PLAYER);
                    tracker.setClientEntityId(entityId);
                });
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.STRING);
                this.map(Type.BOOLEAN);
                this.handler(wrapper -> {
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.setGameMode(GameMode.getById(wrapper.get(Type.UNSIGNED_BYTE, 0).shortValue()));
                });
                this.handler(wrapper -> {
                    ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                    byte dimensionId = wrapper.get(Type.BYTE, 0);
                    clientWorld.setEnvironment(dimensionId);
                });
                this.handler(wrapper -> {
                    CommandBlockProvider provider = Via.getManager().getProviders().get(CommandBlockProvider.class);
                    provider.sendPermission(wrapper.user());
                });
                this.handler(wrapper -> {
                    EntityTracker1_9 entityTracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    if (Via.getConfig().isAutoTeam()) {
                        entityTracker.setAutoTeam(true);
                        wrapper.send(Protocol1_9To1_8.class);
                        wrapper.cancel();
                        entityTracker.sendTeamPacket(true, true);
                        entityTracker.setCurrentTeam("viaversion");
                    } else {
                        entityTracker.setAutoTeam(false);
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.PLAYER_INFO, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    int action = wrapper.get(Type.VAR_INT, 0);
                    int count = wrapper.get(Type.VAR_INT, 1);
                    for (int i = 0; i < count; ++i) {
                        wrapper.passthrough(Type.UUID);
                        if (action == 0) {
                            wrapper.passthrough(Type.STRING);
                            int properties = wrapper.passthrough(Type.VAR_INT);
                            for (int j = 0; j < properties; ++j) {
                                wrapper.passthrough(Type.STRING);
                                wrapper.passthrough(Type.STRING);
                                boolean isSigned = wrapper.passthrough(Type.BOOLEAN);
                                if (!isSigned) continue;
                                wrapper.passthrough(Type.STRING);
                            }
                            wrapper.passthrough(Type.VAR_INT);
                            wrapper.passthrough(Type.VAR_INT);
                            boolean hasDisplayName = wrapper.passthrough(Type.BOOLEAN);
                            if (!hasDisplayName) continue;
                            Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING));
                            continue;
                        }
                        if (action == 1 || action == 2) {
                            wrapper.passthrough(Type.VAR_INT);
                            continue;
                        }
                        if (action == 3) {
                            boolean hasDisplayName = wrapper.passthrough(Type.BOOLEAN);
                            if (!hasDisplayName) continue;
                            Protocol1_9To1_8.FIX_JSON.write(wrapper, wrapper.read(Type.STRING));
                            continue;
                        }
                        if (action != 4) continue;
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.PLUGIN_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    String name = wrapper.get(Type.STRING, 0);
                    if (name.equalsIgnoreCase("MC|BOpen")) {
                        wrapper.read(Type.REMAINING_BYTES);
                        wrapper.write(Type.VAR_INT, 0);
                    }
                    if (name.equalsIgnoreCase("MC|TrList")) {
                        wrapper.passthrough(Type.INT);
                        Short size = wrapper.passthrough(Type.UNSIGNED_BYTE);
                        for (int i = 0; i < size; ++i) {
                            Item item1 = wrapper.passthrough(Type.ITEM);
                            ItemRewriter.toClient(item1);
                            Item item2 = wrapper.passthrough(Type.ITEM);
                            ItemRewriter.toClient(item2);
                            boolean present = wrapper.passthrough(Type.BOOLEAN);
                            if (present) {
                                Item item3 = wrapper.passthrough(Type.ITEM);
                                ItemRewriter.toClient(item3);
                            }
                            wrapper.passthrough(Type.BOOLEAN);
                            wrapper.passthrough(Type.INT);
                            wrapper.passthrough(Type.INT);
                        }
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.RESPAWN, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.INT);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.STRING);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ClientWorld clientWorld = wrapper.user().get(ClientWorld.class);
                        int dimensionId = wrapper.get(Type.INT, 0);
                        clientWorld.setEnvironment(dimensionId);
                    }
                });
                this.handler(wrapper -> {
                    wrapper.user().get(ClientChunks.class).getLoadedChunks().clear();
                    short gamemode = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                    tracker.setGameMode(GameMode.getById(gamemode));
                });
                this.handler(wrapper -> {
                    CommandBlockProvider provider = Via.getManager().getProviders().get(CommandBlockProvider.class);
                    provider.sendPermission(wrapper.user());
                    provider.unloadChunks(wrapper.user());
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.GAME_EVENT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.UNSIGNED_BYTE);
                this.map(Type.FLOAT);
                this.handler(wrapper -> {
                    short reason = wrapper.get(Type.UNSIGNED_BYTE, 0);
                    if (reason == 3) {
                        int gamemode = wrapper.get(Type.FLOAT, 0).intValue();
                        EntityTracker1_9 tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class);
                        tracker.setGameMode(GameMode.getById(gamemode));
                    } else if (reason == 4) {
                        wrapper.set(Type.FLOAT, 0, Float.valueOf(1.0f));
                    }
                });
            }
        });
        protocol.registerClientbound(ClientboundPackets1_8.SET_COMPRESSION, null, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(wrapper -> {
                    wrapper.cancel();
                    CompressionProvider provider = Via.getManager().getProviders().get(CompressionProvider.class);
                    provider.handlePlayCompression(wrapper.user(), wrapper.read(Type.VAR_INT));
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_9.TAB_COMPLETE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.map((Type)Type.BOOLEAN, Type.NOTHING);
            }
        });
        protocol.registerServerbound(ServerboundPackets1_9.CLIENT_SETTINGS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.map(Type.BYTE);
                this.map((Type)Type.VAR_INT, Type.BYTE);
                this.map(Type.BOOLEAN);
                this.map(Type.UNSIGNED_BYTE);
                this.handler(wrapper -> {
                    int hand = wrapper.read(Type.VAR_INT);
                    if (Via.getConfig().isLeftHandedHandling() && hand == 0) {
                        wrapper.set(Type.UNSIGNED_BYTE, 0, (short)(wrapper.get(Type.UNSIGNED_BYTE, 0).intValue() | 0x80));
                    }
                    wrapper.sendToServer(Protocol1_9To1_8.class);
                    wrapper.cancel();
                    Via.getManager().getProviders().get(MainHandProvider.class).setMainHand(wrapper.user(), hand);
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_9.ANIMATION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map((Type)Type.VAR_INT, Type.NOTHING);
            }
        });
        protocol.cancelServerbound(ServerboundPackets1_9.TELEPORT_CONFIRM);
        protocol.cancelServerbound(ServerboundPackets1_9.VEHICLE_MOVE);
        protocol.cancelServerbound(ServerboundPackets1_9.STEER_BOAT);
        protocol.registerServerbound(ServerboundPackets1_9.PLUGIN_MESSAGE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(wrapper -> {
                    Item item;
                    String name = wrapper.get(Type.STRING, 0);
                    if (name.equalsIgnoreCase("MC|BSign") && (item = wrapper.passthrough(Type.ITEM)) != null) {
                        item.setIdentifier(387);
                        ItemRewriter.rewriteBookToServer(item);
                    }
                    if (name.equalsIgnoreCase("MC|AutoCmd")) {
                        wrapper.set(Type.STRING, 0, "MC|AdvCdm");
                        wrapper.write(Type.BYTE, (byte)0);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.INT);
                        wrapper.passthrough(Type.STRING);
                        wrapper.passthrough(Type.BOOLEAN);
                        wrapper.clearInputBuffer();
                    }
                    if (name.equalsIgnoreCase("MC|AdvCmd")) {
                        wrapper.set(Type.STRING, 0, "MC|AdvCdm");
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_9.CLIENT_STATUS, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.VAR_INT);
                this.handler(wrapper -> {
                    EntityTracker1_9 tracker;
                    int action = wrapper.get(Type.VAR_INT, 0);
                    if (action == 2 && (tracker = (EntityTracker1_9)wrapper.user().getEntityTracker(Protocol1_9To1_8.class)).isBlocking()) {
                        if (!Via.getConfig().isShowShieldWhenSwordInHand()) {
                            tracker.setSecondHand(null);
                        }
                        tracker.setBlocking(false);
                    }
                });
            }
        });
        protocol.registerServerbound(ServerboundPackets1_9.PLAYER_POSITION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.BOOLEAN);
                this.handler(new PlayerMovementMapper());
            }
        });
        protocol.registerServerbound(ServerboundPackets1_9.PLAYER_POSITION_AND_ROTATION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.DOUBLE);
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.BOOLEAN);
                this.handler(new PlayerMovementMapper());
            }
        });
        protocol.registerServerbound(ServerboundPackets1_9.PLAYER_ROTATION, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.FLOAT);
                this.map(Type.FLOAT);
                this.map(Type.BOOLEAN);
                this.handler(new PlayerMovementMapper());
            }
        });
        protocol.registerServerbound(ServerboundPackets1_9.PLAYER_MOVEMENT, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.BOOLEAN);
                this.handler(new PlayerMovementMapper());
            }
        });
    }
}

