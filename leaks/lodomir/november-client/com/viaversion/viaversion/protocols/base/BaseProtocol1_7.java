/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 */
package com.viaversion.viaversion.protocols.base;

import com.google.common.base.Joiner;
import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.protocol.AbstractProtocol;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.packet.State;
import com.viaversion.viaversion.api.protocol.remapper.PacketHandler;
import com.viaversion.viaversion.api.protocol.remapper.PacketRemapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.protocol.version.VersionProvider;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.libs.gson.JsonElement;
import com.viaversion.viaversion.libs.gson.JsonObject;
import com.viaversion.viaversion.libs.gson.JsonParseException;
import com.viaversion.viaversion.protocol.ProtocolManagerImpl;
import com.viaversion.viaversion.protocol.ServerProtocolVersionSingleton;
import com.viaversion.viaversion.protocols.base.BaseProtocol;
import com.viaversion.viaversion.protocols.base.ClientboundLoginPackets;
import com.viaversion.viaversion.protocols.base.ClientboundStatusPackets;
import com.viaversion.viaversion.protocols.base.ServerboundLoginPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.util.ChatColorUtil;
import com.viaversion.viaversion.util.GsonUtil;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

public class BaseProtocol1_7
extends AbstractProtocol {
    @Override
    protected void registerPackets() {
        this.registerClientbound(ClientboundStatusPackets.STATUS_RESPONSE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.map(Type.STRING);
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ProtocolInfo info = wrapper.user().getProtocolInfo();
                        String originalStatus = wrapper.get(Type.STRING, 0);
                        try {
                            VersionProvider versionProvider;
                            JsonObject version;
                            JsonElement json = GsonUtil.getGson().fromJson(originalStatus, JsonElement.class);
                            int protocolVersion = 0;
                            if (json.isJsonObject()) {
                                if (json.getAsJsonObject().has("version")) {
                                    version = json.getAsJsonObject().get("version").getAsJsonObject();
                                    if (version.has("protocol")) {
                                        protocolVersion = Long.valueOf(version.get("protocol").getAsLong()).intValue();
                                    }
                                } else {
                                    version = new JsonObject();
                                    json.getAsJsonObject().add("version", version);
                                }
                            } else {
                                json = new JsonObject();
                                version = new JsonObject();
                                json.getAsJsonObject().add("version", version);
                            }
                            if (Via.getConfig().isSendSupportedVersions()) {
                                version.add("supportedVersions", GsonUtil.getGson().toJsonTree(Via.getAPI().getSupportedVersions()));
                            }
                            if (!Via.getAPI().getServerVersion().isKnown()) {
                                ProtocolManagerImpl protocolManager = (ProtocolManagerImpl)Via.getManager().getProtocolManager();
                                protocolManager.setServerProtocol(new ServerProtocolVersionSingleton(ProtocolVersion.getProtocol(protocolVersion).getVersion()));
                            }
                            if ((versionProvider = Via.getManager().getProviders().get(VersionProvider.class)) == null) {
                                wrapper.user().setActive(false);
                                return;
                            }
                            int closestServerProtocol = versionProvider.getClosestServerProtocol(wrapper.user());
                            List<ProtocolPathEntry> protocols = null;
                            if (info.getProtocolVersion() >= closestServerProtocol || Via.getPlatform().isOldClientsAllowed()) {
                                protocols = Via.getManager().getProtocolManager().getProtocolPath(info.getProtocolVersion(), closestServerProtocol);
                            }
                            if (protocols != null) {
                                if (protocolVersion == closestServerProtocol || protocolVersion == 0) {
                                    ProtocolVersion prot = ProtocolVersion.getProtocol(info.getProtocolVersion());
                                    version.addProperty("protocol", prot.getOriginalVersion());
                                }
                            } else {
                                wrapper.user().setActive(false);
                            }
                            if (Via.getConfig().blockedProtocolVersions().contains(info.getProtocolVersion())) {
                                version.addProperty("protocol", -1);
                            }
                            wrapper.set(Type.STRING, 0, GsonUtil.getGson().toJson(json));
                        }
                        catch (JsonParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });
        this.registerClientbound(ClientboundLoginPackets.GAME_PROFILE, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        ProtocolInfo info = wrapper.user().getProtocolInfo();
                        info.setState(State.PLAY);
                        UUID uuid = BaseProtocol1_7.this.passthroughLoginUUID(wrapper);
                        info.setUuid(uuid);
                        String username = wrapper.passthrough(Type.STRING);
                        info.setUsername(username);
                        Via.getManager().getConnectionManager().onLoginSuccess(wrapper.user());
                        if (!info.getPipeline().hasNonBaseProtocols()) {
                            wrapper.user().setActive(false);
                        }
                        if (Via.getManager().isDebug()) {
                            Via.getPlatform().getLogger().log(Level.INFO, "{0} logged in with protocol {1}, Route: {2}", new Object[]{username, info.getProtocolVersion(), Joiner.on((String)", ").join(info.getPipeline().pipes(), (Object)", ", new Object[0])});
                        }
                    }
                });
            }
        });
        this.registerServerbound(ServerboundLoginPackets.HELLO, new PacketRemapper(){

            @Override
            public void registerMap() {
                this.handler(new PacketHandler(){

                    @Override
                    public void handle(PacketWrapper wrapper) throws Exception {
                        int protocol = wrapper.user().getProtocolInfo().getProtocolVersion();
                        if (Via.getConfig().blockedProtocolVersions().contains(protocol)) {
                            if (!wrapper.user().getChannel().isOpen()) {
                                return;
                            }
                            if (!wrapper.user().shouldApplyBlockProtocol()) {
                                return;
                            }
                            PacketWrapper disconnectPacket = PacketWrapper.create(ClientboundLoginPackets.LOGIN_DISCONNECT, wrapper.user());
                            Protocol1_9To1_8.FIX_JSON.write(disconnectPacket, ChatColorUtil.translateAlternateColorCodes(Via.getConfig().getBlockedDisconnectMsg()));
                            wrapper.cancel();
                            ChannelFuture future = disconnectPacket.sendFuture(BaseProtocol.class);
                            future.addListener((GenericFutureListener<? extends Future<? super Void>>)((GenericFutureListener<Future>)f -> wrapper.user().getChannel().close()));
                        }
                    }
                });
            }
        });
    }

    @Override
    public boolean isBaseProtocol() {
        return true;
    }

    public static String addDashes(String trimmedUUID) {
        StringBuilder idBuff = new StringBuilder(trimmedUUID);
        idBuff.insert(20, '-');
        idBuff.insert(16, '-');
        idBuff.insert(12, '-');
        idBuff.insert(8, '-');
        return idBuff.toString();
    }

    protected UUID passthroughLoginUUID(PacketWrapper wrapper) throws Exception {
        String uuidString = wrapper.passthrough(Type.STRING);
        if (uuidString.length() == 32) {
            uuidString = BaseProtocol1_7.addDashes(uuidString);
        }
        return UUID.fromString(uuidString);
    }
}

