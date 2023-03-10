/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.mojang.authlib.GameProfile
 */
package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

public class CommandWhitelist
extends CommandBase {
    @Override
    public String getCommandName() {
        return "whitelist";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.whitelist.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException("commands.whitelist.usage", new Object[0]);
        }
        MinecraftServer minecraftserver = MinecraftServer.getServer();
        if (args[0].equals("on")) {
            minecraftserver.getConfigurationManager().setWhiteListEnabled(true);
            CommandWhitelist.notifyOperators(sender, (ICommand)this, "commands.whitelist.enabled", new Object[0]);
        } else if (args[0].equals("off")) {
            minecraftserver.getConfigurationManager().setWhiteListEnabled(false);
            CommandWhitelist.notifyOperators(sender, (ICommand)this, "commands.whitelist.disabled", new Object[0]);
        } else if (args[0].equals("list")) {
            sender.addChatMessage(new ChatComponentTranslation("commands.whitelist.list", minecraftserver.getConfigurationManager().getWhitelistedPlayerNames().length, minecraftserver.getConfigurationManager().getAvailablePlayerDat().length));
            Object[] astring = minecraftserver.getConfigurationManager().getWhitelistedPlayerNames();
            sender.addChatMessage(new ChatComponentText(CommandWhitelist.joinNiceString(astring)));
        } else if (args[0].equals("add")) {
            if (args.length < 2) {
                throw new WrongUsageException("commands.whitelist.add.usage", new Object[0]);
            }
            GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[1]);
            if (gameprofile == null) {
                throw new CommandException("commands.whitelist.add.failed", args[1]);
            }
            minecraftserver.getConfigurationManager().addWhitelistedPlayer(gameprofile);
            CommandWhitelist.notifyOperators(sender, (ICommand)this, "commands.whitelist.add.success", args[1]);
        } else if (args[0].equals("remove")) {
            if (args.length < 2) {
                throw new WrongUsageException("commands.whitelist.remove.usage", new Object[0]);
            }
            GameProfile gameprofile1 = minecraftserver.getConfigurationManager().getWhitelistedPlayers().func_152706_a(args[1]);
            if (gameprofile1 == null) {
                throw new CommandException("commands.whitelist.remove.failed", args[1]);
            }
            minecraftserver.getConfigurationManager().removePlayerFromWhitelist(gameprofile1);
            CommandWhitelist.notifyOperators(sender, (ICommand)this, "commands.whitelist.remove.success", args[1]);
        } else if (args[0].equals("reload")) {
            minecraftserver.getConfigurationManager().loadWhiteList();
            CommandWhitelist.notifyOperators(sender, (ICommand)this, "commands.whitelist.reloaded", new Object[0]);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return CommandWhitelist.getListOfStringsMatchingLastWord(args, "on", "off", "list", "add", "remove", "reload");
        }
        if (args.length == 2) {
            if (args[0].equals("remove")) {
                return CommandWhitelist.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getWhitelistedPlayerNames());
            }
            if (args[0].equals("add")) {
                return CommandWhitelist.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getPlayerProfileCache().getUsernames());
            }
        }
        return null;
    }
}

