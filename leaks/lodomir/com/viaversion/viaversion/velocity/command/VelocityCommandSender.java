/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.velocitypowered.api.command.CommandSource
 *  com.velocitypowered.api.proxy.Player
 *  net.kyori.adventure.text.Component
 */
package com.viaversion.viaversion.velocity.command;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import com.viaversion.viaversion.VelocityPlugin;
import com.viaversion.viaversion.api.command.ViaCommandSender;
import java.util.UUID;
import net.kyori.adventure.text.Component;

public class VelocityCommandSender
implements ViaCommandSender {
    private final CommandSource source;

    public VelocityCommandSender(CommandSource source) {
        this.source = source;
    }

    @Override
    public boolean hasPermission(String permission) {
        return this.source.hasPermission(permission);
    }

    @Override
    public void sendMessage(String msg) {
        this.source.sendMessage((Component)VelocityPlugin.COMPONENT_SERIALIZER.deserialize(msg));
    }

    @Override
    public UUID getUUID() {
        if (this.source instanceof Player) {
            return ((Player)this.source).getUniqueId();
        }
        return UUID.fromString(this.getName());
    }

    @Override
    public String getName() {
        if (this.source instanceof Player) {
            return ((Player)this.source).getUsername();
        }
        return "?";
    }
}

