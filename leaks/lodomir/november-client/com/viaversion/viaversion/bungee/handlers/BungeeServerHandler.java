/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 *  net.md_5.bungee.api.event.ServerConnectEvent
 *  net.md_5.bungee.api.event.ServerConnectedEvent
 *  net.md_5.bungee.api.event.ServerSwitchEvent
 *  net.md_5.bungee.api.plugin.Listener
 *  net.md_5.bungee.api.score.Team
 *  net.md_5.bungee.event.EventHandler
 *  net.md_5.bungee.protocol.packet.PluginMessage
 */
package com.viaversion.viaversion.bungee.handlers;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ProtocolInfo;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.data.entity.ClientEntityIdChangeListener;
import com.viaversion.viaversion.api.data.entity.EntityTracker;
import com.viaversion.viaversion.api.protocol.Protocol;
import com.viaversion.viaversion.api.protocol.ProtocolPathEntry;
import com.viaversion.viaversion.api.protocol.ProtocolPipeline;
import com.viaversion.viaversion.api.protocol.packet.PacketWrapper;
import com.viaversion.viaversion.api.protocol.version.ProtocolVersion;
import com.viaversion.viaversion.api.type.Type;
import com.viaversion.viaversion.bungee.service.ProtocolDetectorService;
import com.viaversion.viaversion.bungee.storage.BungeeStorage;
import com.viaversion.viaversion.protocols.protocol1_13to1_12_2.packets.InventoryPackets;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.ClientboundPackets1_9;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.Protocol1_9To1_8;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.providers.EntityIdProvider;
import com.viaversion.viaversion.protocols.protocol1_9to1_8.storage.EntityTracker1_9;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.event.ServerSwitchEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.score.Team;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.protocol.packet.PluginMessage;

public class BungeeServerHandler
implements Listener {
    private static Method getHandshake;
    private static Method getRegisteredChannels;
    private static Method getBrandMessage;
    private static Method setProtocol;
    private static Method getEntityMap;
    private static Method setVersion;
    private static Field entityRewrite;
    private static Field channelWrapper;

    @EventHandler(priority=120)
    public void onServerConnect(ServerConnectEvent e) {
        if (e.isCancelled()) {
            return;
        }
        UserConnection user = Via.getManager().getConnectionManager().getConnectedClient(e.getPlayer().getUniqueId());
        if (user == null) {
            return;
        }
        if (!user.has(BungeeStorage.class)) {
            user.put(new BungeeStorage(e.getPlayer()));
        }
        int protocolId = ProtocolDetectorService.getProtocolId(e.getTarget().getName());
        List<ProtocolPathEntry> protocols = Via.getManager().getProtocolManager().getProtocolPath(user.getProtocolInfo().getProtocolVersion(), protocolId);
        try {
            Object handshake = getHandshake.invoke((Object)e.getPlayer().getPendingConnection(), new Object[0]);
            setProtocol.invoke(handshake, protocols == null ? user.getProtocolInfo().getProtocolVersion() : protocolId);
        }
        catch (IllegalAccessException | InvocationTargetException e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler(priority=-120)
    public void onServerConnected(ServerConnectedEvent e) {
        try {
            this.checkServerChange(e, Via.getManager().getConnectionManager().getConnectedClient(e.getPlayer().getUniqueId()));
        }
        catch (Exception e1) {
            e1.printStackTrace();
        }
    }

    @EventHandler(priority=-120)
    public void onServerSwitch(ServerSwitchEvent e) {
        int playerId;
        UserConnection userConnection = Via.getManager().getConnectionManager().getConnectedClient(e.getPlayer().getUniqueId());
        if (userConnection == null) {
            return;
        }
        try {
            playerId = Via.getManager().getProviders().get(EntityIdProvider.class).getEntityId(userConnection);
        }
        catch (Exception ex) {
            return;
        }
        for (EntityTracker tracker : userConnection.getEntityTrackers()) {
            tracker.setClientEntityId(playerId);
        }
        for (StorableObject object : userConnection.getStoredObjects().values()) {
            if (!(object instanceof ClientEntityIdChangeListener)) continue;
            ((ClientEntityIdChangeListener)((Object)object)).setClientEntityId(playerId);
        }
    }

    public void checkServerChange(ServerConnectedEvent e, UserConnection user) throws Exception {
        if (user == null) {
            return;
        }
        if (user.has(BungeeStorage.class)) {
            BungeeStorage storage = user.get(BungeeStorage.class);
            ProxiedPlayer player = storage.getPlayer();
            if (e.getServer() != null && !e.getServer().getInfo().getName().equals(storage.getCurrentServer())) {
                boolean toOldId;
                EntityTracker1_9 oldEntityTracker = (EntityTracker1_9)user.getEntityTracker(Protocol1_9To1_8.class);
                if (oldEntityTracker != null && oldEntityTracker.isAutoTeam() && oldEntityTracker.isTeamExists()) {
                    oldEntityTracker.sendTeamPacket(false, true);
                }
                String serverName = e.getServer().getInfo().getName();
                storage.setCurrentServer(serverName);
                int protocolId = ProtocolDetectorService.getProtocolId(serverName);
                if (protocolId <= ProtocolVersion.v1_8.getVersion() && storage.getBossbar() != null) {
                    if (user.getProtocolInfo().getPipeline().contains(Protocol1_9To1_8.class)) {
                        for (UUID uuid : storage.getBossbar()) {
                            PacketWrapper wrapper = PacketWrapper.create(ClientboundPackets1_9.BOSSBAR, null, user);
                            wrapper.write(Type.UUID, uuid);
                            wrapper.write(Type.VAR_INT, 1);
                            wrapper.send(Protocol1_9To1_8.class);
                        }
                    }
                    storage.getBossbar().clear();
                }
                ProtocolInfo info = user.getProtocolInfo();
                int previousServerProtocol = info.getServerProtocolVersion();
                List<ProtocolPathEntry> protocolPath = Via.getManager().getProtocolManager().getProtocolPath(info.getProtocolVersion(), protocolId);
                ProtocolPipeline pipeline = user.getProtocolInfo().getPipeline();
                user.clearStoredObjects();
                pipeline.cleanPipes();
                if (protocolPath == null) {
                    protocolId = info.getProtocolVersion();
                } else {
                    ArrayList<Protocol> protocols = new ArrayList<Protocol>(protocolPath.size());
                    for (ProtocolPathEntry entry : protocolPath) {
                        protocols.add(entry.protocol());
                    }
                    pipeline.add(protocols);
                }
                info.setServerProtocolVersion(protocolId);
                pipeline.add(Via.getManager().getProtocolManager().getBaseProtocol(protocolId));
                int id1_13 = ProtocolVersion.v1_13.getVersion();
                boolean toNewId = previousServerProtocol < id1_13 && protocolId >= id1_13;
                boolean bl = toOldId = previousServerProtocol >= id1_13 && protocolId < id1_13;
                if (previousServerProtocol != -1 && (toNewId || toOldId)) {
                    PluginMessage brandMessage;
                    Collection registeredChannels = (Collection)getRegisteredChannels.invoke((Object)e.getPlayer().getPendingConnection(), new Object[0]);
                    if (!registeredChannels.isEmpty()) {
                        HashSet<String> newChannels = new HashSet<String>();
                        Iterator iterator = registeredChannels.iterator();
                        while (iterator.hasNext()) {
                            String channel;
                            String oldChannel = channel = (String)iterator.next();
                            channel = toNewId ? InventoryPackets.getNewPluginChannelId(channel) : InventoryPackets.getOldPluginChannelId(channel);
                            if (channel == null) {
                                iterator.remove();
                                continue;
                            }
                            if (oldChannel.equals(channel)) continue;
                            iterator.remove();
                            newChannels.add(channel);
                        }
                        registeredChannels.addAll(newChannels);
                    }
                    if ((brandMessage = (PluginMessage)getBrandMessage.invoke((Object)e.getPlayer().getPendingConnection(), new Object[0])) != null) {
                        Object channel = brandMessage.getTag();
                        channel = toNewId ? InventoryPackets.getNewPluginChannelId((String)channel) : InventoryPackets.getOldPluginChannelId((String)channel);
                        if (channel != null) {
                            brandMessage.setTag((String)channel);
                        }
                    }
                }
                user.put(storage);
                user.setActive(protocolPath != null);
                for (Protocol protocol : pipeline.pipes()) {
                    protocol.init(user);
                }
                EntityTracker1_9 newTracker = (EntityTracker1_9)user.getEntityTracker(Protocol1_9To1_8.class);
                if (newTracker != null && Via.getConfig().isAutoTeam()) {
                    String currentTeam = null;
                    for (Team team : player.getScoreboard().getTeams()) {
                        if (!team.getPlayers().contains(info.getUsername())) continue;
                        currentTeam = team.getName();
                    }
                    newTracker.setAutoTeam(true);
                    if (currentTeam == null) {
                        newTracker.sendTeamPacket(true, true);
                        newTracker.setCurrentTeam("viaversion");
                    } else {
                        newTracker.setAutoTeam(Via.getConfig().isAutoTeam());
                        newTracker.setCurrentTeam(currentTeam);
                    }
                }
                Object wrapper = channelWrapper.get(player);
                setVersion.invoke(wrapper, protocolId);
                Object entityMap = getEntityMap.invoke(null, protocolId);
                entityRewrite.set(player, entityMap);
            }
        }
    }

    static {
        getEntityMap = null;
        setVersion = null;
        entityRewrite = null;
        channelWrapper = null;
        try {
            getHandshake = Class.forName("net.md_5.bungee.connection.InitialHandler").getDeclaredMethod("getHandshake", new Class[0]);
            getRegisteredChannels = Class.forName("net.md_5.bungee.connection.InitialHandler").getDeclaredMethod("getRegisteredChannels", new Class[0]);
            getBrandMessage = Class.forName("net.md_5.bungee.connection.InitialHandler").getDeclaredMethod("getBrandMessage", new Class[0]);
            setProtocol = Class.forName("net.md_5.bungee.protocol.packet.Handshake").getDeclaredMethod("setProtocolVersion", Integer.TYPE);
            getEntityMap = Class.forName("net.md_5.bungee.entitymap.EntityMap").getDeclaredMethod("getEntityMap", Integer.TYPE);
            setVersion = Class.forName("net.md_5.bungee.netty.ChannelWrapper").getDeclaredMethod("setVersion", Integer.TYPE);
            channelWrapper = Class.forName("net.md_5.bungee.UserConnection").getDeclaredField("ch");
            channelWrapper.setAccessible(true);
            entityRewrite = Class.forName("net.md_5.bungee.UserConnection").getDeclaredField("entityRewrite");
            entityRewrite.setAccessible(true);
        }
        catch (Exception e) {
            Via.getPlatform().getLogger().severe("Error initializing BungeeServerHandler, try updating BungeeCord or ViaVersion!");
            e.printStackTrace();
        }
    }
}

