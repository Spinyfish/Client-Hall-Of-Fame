package net.minecraft.command.server;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandOp extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "op";
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
        return "commands.op.usage";
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
            final GameProfile gameprofile = minecraftserver.getPlayerProfileCache().getGameProfileForUsername(args[0]);

            if (gameprofile == null) {
                throw new CommandException("commands.op.failed", args[0]);
            } else {
                minecraftserver.getConfigurationManager().addOp(gameprofile);
                notifyOperators(sender, this, "commands.op.success", args[0]);
            }
        } else {
            throw new WrongUsageException("commands.op.usage");
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        if (args.length == 1) {
            final String s = args[args.length - 1];
            final List<String> list = Lists.newArrayList();

            for (final GameProfile gameprofile : MinecraftServer.getServer().getGameProfiles()) {
                if (!MinecraftServer.getServer().getConfigurationManager().canSendCommands(gameprofile) && doesStringStartWith(s, gameprofile.getName())) {
                    list.add(gameprofile.getName());
                }
            }

            return list;
        } else {
            return null;
        }
    }
}
