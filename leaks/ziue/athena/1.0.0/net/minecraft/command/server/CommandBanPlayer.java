package net.minecraft.command.server;

import net.minecraft.server.*;
import net.minecraft.command.*;
import com.mojang.authlib.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.*;
import java.util.*;
import net.minecraft.server.management.*;

public class CommandBanPlayer extends CommandBase
{
    @Override
    public String getCommandName() {
        return "ban";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 3;
    }
    
    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.ban.usage";
    }
    
    @Override
    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer() && super.canCommandSenderUseCommand(sender);
    }
    
    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 1 || args[0].length() <= 0) {
            throw new WrongUsageException("commands.ban.usage", new Object[0]);
        }
        final MinecraftServer minecraftserver = MinecraftServer.getServer();
        final GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[0]);
        if (gameprofile == null) {
            throw new CommandException("commands.ban.failed", new Object[] { args[0] });
        }
        String s = null;
        if (args.length >= 2) {
            s = CommandBase.getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
        }
        final UserListBansEntry userlistbansentry = new UserListBansEntry(gameprofile, null, sender.getName(), null, s);
        ((UserList<K, UserListBansEntry>)minecraftserver.getConfigurationManager().getBannedPlayers()).addEntry(userlistbansentry);
        final EntityPlayerMP entityplayermp = minecraftserver.getConfigurationManager().getPlayerByUsername(args[0]);
        if (entityplayermp != null) {
            entityplayermp.playerNetServerHandler.kickPlayerFromServer("You are banned from this server.");
        }
        CommandBase.notifyOperators(sender, this, "commands.ban.success", args[0]);
    }
    
    @Override
    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return (args.length >= 1) ? CommandBase.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
