/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.proxy.Player
 */
package com.viaversion.viaversion.velocity.storage;

import com.velocitypowered.api.proxy.Player;
import com.viaversion.viaversion.api.connection.StorableObject;
import com.viaversion.viaversion.util.ReflectionUtil;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class VelocityStorage
implements StorableObject {
    private final Player player;
    private String currentServer;
    private List<UUID> cachedBossbar;
    private static Method getServerBossBars;
    private static Class<?> clientPlaySessionHandler;
    private static Method getMinecraftConnection;

    public VelocityStorage(Player player) {
        this.player = player;
        this.currentServer = "";
    }

    public List<UUID> getBossbar() {
        if (this.cachedBossbar == null) {
            if (clientPlaySessionHandler == null) {
                return null;
            }
            if (getServerBossBars == null) {
                return null;
            }
            if (getMinecraftConnection == null) {
                return null;
            }
            try {
                Object connection = getMinecraftConnection.invoke((Object)this.player, new Object[0]);
                Object sessionHandler = ReflectionUtil.invoke(connection, "getSessionHandler");
                if (clientPlaySessionHandler.isInstance(sessionHandler)) {
                    this.cachedBossbar = (List)getServerBossBars.invoke(sessionHandler, new Object[0]);
                }
            }
            catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return this.cachedBossbar;
    }

    public Player getPlayer() {
        return this.player;
    }

    public String getCurrentServer() {
        return this.currentServer;
    }

    public void setCurrentServer(String currentServer) {
        this.currentServer = currentServer;
    }

    public List<UUID> getCachedBossbar() {
        return this.cachedBossbar;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        VelocityStorage that = (VelocityStorage)o;
        if (!Objects.equals(this.player, that.player)) {
            return false;
        }
        if (!Objects.equals(this.currentServer, that.currentServer)) {
            return false;
        }
        return Objects.equals(this.cachedBossbar, that.cachedBossbar);
    }

    public int hashCode() {
        int result = this.player != null ? this.player.hashCode() : 0;
        result = 31 * result + (this.currentServer != null ? this.currentServer.hashCode() : 0);
        result = 31 * result + (this.cachedBossbar != null ? this.cachedBossbar.hashCode() : 0);
        return result;
    }

    static {
        try {
            clientPlaySessionHandler = Class.forName("com.velocitypowered.proxy.connection.client.ClientPlaySessionHandler");
            getServerBossBars = clientPlaySessionHandler.getDeclaredMethod("getServerBossBars", new Class[0]);
            getMinecraftConnection = Class.forName("com.velocitypowered.proxy.connection.client.ConnectedPlayer").getDeclaredMethod("getMinecraftConnection", new Class[0]);
        }
        catch (ClassNotFoundException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}

