package net.minecraft.command.server;

import net.minecraft.command.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.List;
import java.util.regex.Matcher;

public class CommandPardonIp extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "pardon-ip";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 3;
    }

    /**
     * Returns true if the given command sender is allowed to use this command.
     *
     * @param sender The CommandSender
     */
    public boolean canCommandSenderUseCommand(final ICommandSender sender) {
        return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isLanServer() && super.canCommandSenderUseCommand(sender);
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender The {@link ICommandSender} who is requesting usage details.
     */
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.unbanip.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length == 1 && args[0].length() > 1) {
            final Matcher matcher = CommandBanIp.field_147211_a.matcher(args[0]);

            if (matcher.matches()) {
                MinecraftServer.getServer().getConfigurationManager().getBannedIPs().removeEntry(args[0]);
                notifyOperators(sender, this, "commands.unbanip.success", args[0]);
            } else {
                throw new SyntaxErrorException("commands.unbanip.invalid");
            }
        } else {
            throw new WrongUsageException("commands.unbanip.usage");
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getBannedIPs().getKeys()) : null;
    }
}
