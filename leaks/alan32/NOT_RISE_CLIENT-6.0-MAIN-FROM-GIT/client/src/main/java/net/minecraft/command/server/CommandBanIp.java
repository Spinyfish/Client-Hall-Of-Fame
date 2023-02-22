package net.minecraft.command.server;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandBanIp extends CommandBase {
    public static final Pattern field_147211_a = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");

    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "ban-ip";
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
        return "commands.banip.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length >= 1 && args[0].length() > 1) {
            final IChatComponent ichatcomponent = args.length >= 2 ? getChatComponentFromNthArg(sender, args, 1) : null;
            final Matcher matcher = field_147211_a.matcher(args[0]);

            if (matcher.matches()) {
                this.func_147210_a(sender, args[0], ichatcomponent == null ? null : ichatcomponent.getUnformattedText());
            } else {
                final EntityPlayerMP entityplayermp = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(args[0]);

                if (entityplayermp == null) {
                    throw new PlayerNotFoundException("commands.banip.invalid");
                }

                this.func_147210_a(sender, entityplayermp.getPlayerIP(), ichatcomponent == null ? null : ichatcomponent.getUnformattedText());
            }
        } else {
            throw new WrongUsageException("commands.banip.usage");
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }

    protected void func_147210_a(final ICommandSender p_147210_1_, final String p_147210_2_, final String p_147210_3_) {
        final IPBanEntry ipbanentry = new IPBanEntry(p_147210_2_, null, p_147210_1_.getCommandSenderName(), null, p_147210_3_);
        MinecraftServer.getServer().getConfigurationManager().getBannedIPs().addEntry(ipbanentry);
        final List<EntityPlayerMP> list = MinecraftServer.getServer().getConfigurationManager().getPlayersMatchingAddress(p_147210_2_);
        final String[] astring = new String[list.size()];
        int i = 0;

        for (final EntityPlayerMP entityplayermp : list) {
            entityplayermp.playerNetServerHandler.kickPlayerFromServer("You have been IP banned.");
            astring[i++] = entityplayermp.getCommandSenderName();
        }

        if (list.isEmpty()) {
            notifyOperators(p_147210_1_, this, "commands.banip.success", p_147210_2_);
        } else {
            notifyOperators(p_147210_1_, this, "commands.banip.success.players", p_147210_2_, joinNiceString(astring));
        }
    }
}
