/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.bukkit.command.Command
 *  org.bukkit.command.CommandExecutor
 *  org.bukkit.command.CommandSender
 *  org.bukkit.command.TabExecutor
 */
package com.viaversion.viaversion.bukkit.commands;

import com.viaversion.viaversion.bukkit.commands.BukkitCommandSender;
import com.viaversion.viaversion.commands.ViaCommandHandler;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;

public class BukkitCommandHandler
extends ViaCommandHandler
implements CommandExecutor,
TabExecutor {
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return this.onCommand(new BukkitCommandSender(sender), args);
    }

    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        return this.onTabComplete(new BukkitCommandSender(sender), args);
    }
}

