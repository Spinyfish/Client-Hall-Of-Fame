/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.WorldSettings;

public class CommandPublishLocalServer
extends CommandBase {
    @Override
    public String getCommandName() {
        return "publish";
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.publish.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        String s = MinecraftServer.getServer().shareToLAN(WorldSettings.GameType.SURVIVAL, false);
        if (s != null) {
            CommandPublishLocalServer.notifyOperators(sender, (ICommand)this, "commands.publish.started", s);
        } else {
            CommandPublishLocalServer.notifyOperators(sender, (ICommand)this, "commands.publish.failed", new Object[0]);
        }
    }
}

