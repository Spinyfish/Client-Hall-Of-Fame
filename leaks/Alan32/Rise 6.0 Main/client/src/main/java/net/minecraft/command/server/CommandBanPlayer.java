package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.UserListBansEntry;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandBanPlayer extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "ban";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 3;
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender The {@link ICommandSender} who is requesting usage details.
     */
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.ban.usage";
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     *
     * @param sender The CommandSender
     */
    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return MinecraftServer.getServer().getConfigurationManager().getBannedPlayers().isLanServer() && super.canCommandSenderUseCommand(sender);
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length >= 1 && args[0].length() > 0) {
            final MinecraftServer minecraftserver = MinecraftServer.getServer();
            final GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[0]);

            if (gameprofile == null) {
                throw new CommandException("commands.ban.failed", args[0]);
            } else {
                String s = null;

                if (args.length >= 2) {
                    s = getChatComponentFromNthArg(sender, args, 1).getUnformattedText();
                }

                final UserListBansEntry userlistbansentry = new UserListBansEntry(gameprofile, null, sender.getCommandSenderName(), null, s);
                minecraftserver.getConfigurationManager().getBannedPlayers().addEntry(userlistbansentry);
                final EntityPlayerMP entityplayermp = minecraftserver.getConfigurationManager().getPlayerByUsername(args[0]);

                if (entityplayermp != null) {
                    entityplayermp.playerNetServerHandler.kickPlayerFromServer("You are banned from this server.");
                }

                notifyOperators(sender, this, "commands.ban.success", args[0]);
            }
        } else {
            throw new WrongUsageException("commands.ban.usage");
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length >= 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
