/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.md_5.bungee.api.CommandSender
 *  net.md_5.bungee.api.connection.ProxiedPlayer
 */
package com.viaversion.viaversion.bungee.commands;

import com.viaversion.viaversion.api.command.ViaCommandSender;
import java.util.UUID;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCommandSender
implements ViaCommandSender {
    private final CommandSender sender;

    public BungeeCommandSender(CommandSender sender) {
        this.sender = sender;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.sender.hasPermission(permission);
    }

    @Override
    public void sendMessage(String msg) {
        this.sender.sendMessage(msg);
    }

    @Override
    public UUID getUUID() {
        if (this.sender instanceof ProxiedPlayer) {
            return ((ProxiedPlayer)this.sender).getUniqueId();
        }
        return UUID.fromString(this.getName());
    }

    @Override
    public String getName() {
        return this.sender.getName();
    }
}

