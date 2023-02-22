package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandKill extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "kill";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender The {@link ICommandSender} who is requesting usage details.
     */
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.kill.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length == 0) {
            final EntityPlayer entityplayer = getCommandSenderAsPlayer(sender);
            entityplayer.onKillCommand();
            notifyOperators(sender, this, "commands.kill.successful", entityplayer.getDisplayName());
        } else {
            final Entity entity = func_175768_b(sender, args[0]);
            entity.onKillCommand();
            notifyOperators(sender, this, "commands.kill.successful", entity.getDisplayName());
        }
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     *
     * @param args  The arguments that were given
     * @param index The argument index that we are checking
     */
    public boolean isUsernameIndex(final String[] args, final int index) {
        return index == 0;
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
