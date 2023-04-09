package net.minecraft.command.server;

import net.minecraft.command.*;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.management.IPBanEntry;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;

import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommandBanIp extends CommandBase {
    public static final Pattern field_147211_a = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
    // private static final String __OBFID = "CL_00000139";

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
     */
    public boolean canCommandSenderUseCommand(ICommandSender sender) {
        return MinecraftServer.getServer().getConfigurationManager().getBannedIPs().isLanServer() && super.canCommandSenderUseCommand(sender);
    }

    public String getCommandUsage(ICommandSender sender) {
        return "commands.banip.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 1 && args[0].length() > 1) {
            IChatComponent var3 = args.length >= 2 ? getChatComponentFromNthArg(sender, args, 1) : null;
            Matcher var4 = field_147211_a.matcher(args[0]);

            if (var4.matches()) {
                this.func_147210_a(sender, args[0], var3 == null ? null : var3.getUnformattedText());
            } else {
                EntityPlayerMP var5 = MinecraftServer.getServer().getConfigurationManager().getPlayerByUsername(args[0]);

                if (var5 == null) {
                    throw new PlayerNotFoundException("commands.banip.invalid");
                }

                this.func_147210_a(sender, var5.getPlayerIP(), var3 == null ? null : var3.getUnformattedText());
            }
        } else {
            throw new WrongUsageException("commands.banip.usage");
        }
    }

    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }

    protected void func_147210_a(ICommandSender p_147210_1_, String p_147210_2_, String p_147210_3_) {
        IPBanEntry var4 = new IPBanEntry(p_147210_2_, null, p_147210_1_.getName(), null, p_147210_3_);
        MinecraftServer.getServer().getConfigurationManager().getBannedIPs().addEntry(var4);
        List var5 = MinecraftServer.getServer().getConfigurationManager().getPlayersMatchingAddress(p_147210_2_);
        String[] var6 = new String[var5.size()];
        int var7 = 0;
        EntityPlayerMP var9;

        for (Iterator var8 = var5.iterator(); var8.hasNext(); var6[var7++] = var9.getName()) {
            var9 = (EntityPlayerMP) var8.next();
            var9.playerNetServerHandler.kickPlayerFromServer("You have been IP banned.");
        }

        if (var5.isEmpty()) {
            notifyOperators(p_147210_1_, this, "commands.banip.success", p_147210_2_);
        } else {
            notifyOperators(p_147210_1_, this, "commands.banip.success.players", p_147210_2_, joinNiceString(var6));
        }
    }
}
