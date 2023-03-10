/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.entity.Player
 *  org.bukkit.event.Listener
 *  org.bukkit.plugin.Plugin
 */
package com.viaversion.viaversion.bukkit.listeners;

import com.viaversion.viaversion.ViaListener;
import com.viaversion.viaversion.api.connection.UserConnection;
import com.viaversion.viaversion.api.protocol.Protocol;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class ViaBukkitListener
extends ViaListener
implements Listener {
    private final Plugin plugin;

    public ViaBukkitListener(Plugin plugin, Class<? extends Protocol> requiredPipeline) {
        super(requiredPipeline);
        this.plugin = plugin;
    }

    protected UserConnection getUserConnection(Player player) {
        return this.getUserConnection(player.getUniqueId());
    }

    protected boolean isOnPipe(Player player) {
        return this.isOnPipe(player.getUniqueId());
    }

    @Override
    public void register() {
        if (this.isRegistered()) {
            return;
        }
        this.plugin.getServer().getPluginManager().registerEvents((Listener)this, this.plugin);
        this.setRegistered(true);
    }

    public Plugin getPlugin() {
        return this.plugin;
    }
}

