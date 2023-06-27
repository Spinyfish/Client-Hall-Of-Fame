package net.minecraft.command.server;

import net.minecraft.server.*;
import net.minecraft.command.*;
import com.mojang.authlib.*;
import net.minecraft.util.*;
import java.util.*;
import com.google.common.collect.*;

public class CommandOp extends CommandBase
{
    @Override
    public String getCommandName() {
        return "op";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }
    
    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.op.usage";
    }
    
    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length != 1 || args[0].length() <= 0) {
            throw new WrongUsageException("commands.op.usage", new Object[0]);
        }
        final MinecraftServer minecraftserver = MinecraftServer.getServer();
        final GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[0]);
        if (gameprofile == null) {
            throw new CommandException("commands.op.failed", new Object[] { args[0] });
        }
        minecraftserver.getConfigurationManager().addOp(gameprofile);
        CommandBase.notifyOperators(sender, this, "commands.op.success", args[0]);
    }
    
    @Override
    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        if (args.length == 1) {
            final String s = args[args.length - 1];
            final List<String> list = (List<String>)Lists.newArrayList();
            for (final GameProfile gameprofile : MinecraftServer.getServer().getGameProfiles()) {
                if (!MinecraftServer.getServer().getConfigurationManager().canSendCommands(gameprofile) && CommandBase.doesStringStartWith(s, gameprofile.getName())) {
                    list.add(gameprofile.getName());
                }
            }
            return list;
        }
        return null;
    }
}
