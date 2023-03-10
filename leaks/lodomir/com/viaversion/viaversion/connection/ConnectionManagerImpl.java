/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.checkerframework.checker.nullness.qual.Nullable
 */
package com.viaversion.viaversion.connection;

import com.viaversion.viaversion.api.Via;
import com.viaversion.viaversion.api.connection.ConnectionManager;
import com.viaversion.viaversion.api.connection.UserConnection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ConnectionManagerImpl
implements ConnectionManager {
    protected final Map<UUID, UserConnection> clients = new ConcurrentHashMap<UUID, UserConnection>();
    protected final Set<UserConnection> connections = Collections.newSetFromMap(new ConcurrentHashMap());

    @Override
    public void onLoginSuccess(UserConnection connection) {
        UUID id;
        Objects.requireNonNull(connection, "connection is null!");
        this.connections.add(connection);
        if (this.isFrontEnd(connection) && this.clients.put(id = connection.getProtocolInfo().getUuid(), connection) != null) {
            Via.getPlatform().getLogger().warning("Duplicate UUID on frontend connection! (" + id + ")");
        }
        if (connection.getChannel() != null) {
            connection.getChannel().closeFuture().addListener(future -> this.onDisconnect(connection));
        }
    }

    @Override
    public void onDisconnect(UserConnection connection) {
        Objects.requireNonNull(connection, "connection is null!");
        this.connections.remove(connection);
        if (this.isFrontEnd(connection)) {
            UUID id = connection.getProtocolInfo().getUuid();
            this.clients.remove(id);
        }
    }

    @Override
    public Map<UUID, UserConnection> getConnectedClients() {
        return Collections.unmodifiableMap(this.clients);
    }

    @Override
    public @Nullable UserConnection getConnectedClient(UUID clientIdentifier) {
        return this.clients.get(clientIdentifier);
    }

    @Override
    public @Nullable UUID getConnectedClientId(UserConnection connection) {
        if (connection.getProtocolInfo() == null) {
            return null;
        }
        UUID uuid = connection.getProtocolInfo().getUuid();
        UserConnection client = this.clients.get(uuid);
        if (connection.equals(client)) {
            return uuid;
        }
        return null;
    }

    @Override
    public Set<UserConnection> getConnections() {
        return Collections.unmodifiableSet(this.connections);
    }

    @Override
    public boolean isClientConnected(UUID playerId) {
        return this.clients.containsKey(playerId);
    }
}

