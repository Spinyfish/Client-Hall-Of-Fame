/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandOp
extends CommandBase {
    @Override
    public String getCommandName() {
        return "op";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.op.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        GameProfile gameprofile;
        MinecraftServer minecraftserver;
        if (args.length == 1 && args[0].length() > 0) {
            minecraftserver = MinecraftServer.getServer();
            gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[0]);
            if (gameprofile == null) {
                throw new CommandException("commands.op.failed", args[0]);
            }
        } else {
            throw new WrongUsageException("commands.op.usage", new Object[0]);
        }
        minecraftserver.getConfigurationManager().addOp(gameprofile);
        CommandOp.notifyOperators(sender, (ICommand)this, "commands.op.success", args[0]);
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            String s = args[args.length - 1];
            ArrayList list = Lists.newArrayList();
            for (GameProfile gameprofile : MinecraftServer.getServer().getGameProfiles()) {
                if (MinecraftServer.getServer().getConfigurationManager().canSendCommands(gameprofile) || !CommandOp.doesStringStartWith(s, gameprofile.getName())) continue;
                list.add(gameprofile.getName());
            }
            return list;
        }
        return null;
    }
}

