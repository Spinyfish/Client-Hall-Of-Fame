package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandDeOp extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "deop";
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
        return "commands.deop.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length == 1 && args[0].length() > 0) {
            final MinecraftServer minecraftserver = MinecraftServer.getServer();
            final GameProfile gameprofile = minecraftserver.getConfigurationManager().getOppedPlayers().getGameProfileFromName(args[0]);

            if (gameprofile == null) {
                throw new CommandException("commands.deop.failed", args[0]);
            } else {
                minecraftserver.getConfigurationManager().removeOp(gameprofile);
                notifyOperators(sender, this, "commands.deop.success", args[0]);
            }
        } else {
            throw new WrongUsageException("commands.deop.usage");
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getOppedPlayerNames()) : null;
    }
}
