package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandSetSpawnpoint extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "spawnpoint";
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
        return "commands.spawnpoint.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length > 1 && args.length < 4) {
            throw new WrongUsageException("commands.spawnpoint.usage");
        } else {
            final EntityPlayerMP entityplayermp = args.length > 0 ? getPlayer(sender, args[0]) : getCommandSenderAsPlayer(sender);
            final BlockPos blockpos = args.length > 3 ? parseBlockPos(sender, args, 1, true) : entityplayermp.getPosition();

            if (entityplayermp.worldObj != null) {
                entityplayermp.setSpawnPoint(blockpos, true);
                notifyOperators(sender, this, "commands.spawnpoint.success", entityplayermp.getCommandSenderName(), Integer.valueOf(blockpos.getX()), Integer.valueOf(blockpos.getY()), Integer.valueOf(blockpos.getZ()));
            }
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : (args.length > 1 && args.length <= 4 ? func_175771_a(args, 1, pos) : null);
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
}
