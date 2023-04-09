package net.minecraft.command.server;

import com.mojang.authlib.GameProfile;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;

import java.util.List;

public class CommandWhitelist extends CommandBase {
    // private static final String __OBFID = "CL_00001186";

    public String getCommandName() {
        return "whitelist";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 3;
    }

    public String getCommandUsage(ICommandSender sender) {
        return "commands.whitelist.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException("commands.whitelist.usage");
        } else {
            MinecraftServer var3 = MinecraftServer.getServer();

            switch (args[0]) {
                case "on":
                    var3.getConfigurationManager().setWhiteListEnabled(true);
                    notifyOperators(sender, this, "commands.whitelist.enabled");
                    break;
                case "off":
                    var3.getConfigurationManager().setWhiteListEnabled(false);
                    notifyOperators(sender, this, "commands.whitelist.disabled");
                    break;
                case "list":
                    sender.addChatMessage(new ChatComponentTranslation("commands.whitelist.list", var3.getConfigurationManager().getWhitelistedPlayerNames().length, var3.getConfigurationManager().getAvailablePlayerDat().length));
                    String[] var4 = var3.getConfigurationManager().getWhitelistedPlayerNames();
                    sender.addChatMessage(new ChatComponentText(joinNiceString(var4)));
                    break;
                default:
                    GameProfile var5;
                    switch (args[0]) {
                        case "add":
                            if (args.length < 2) {
                                throw new WrongUsageException("commands.whitelist.add.usage");
                            }

                            var5 = var3.getPlayerProfileCache().getGameProfileForUsername(args[1]);

                            if (var5 == null) {
                                throw new CommandException("commands.whitelist.add.failed", args[1]);
                            }

                            var3.getConfigurationManager().addWhitelistedPlayer(var5);
                            notifyOperators(sender, this, "commands.whitelist.add.success", args[1]);
                            break;
                        case "remove":
                            if (args.length < 2) {
                                throw new WrongUsageException("commands.whitelist.remove.usage");
                            }

                            var5 = var3.getConfigurationManager().getWhitelistedPlayers().func_152706_a(args[1]);

                            if (var5 == null) {
                                throw new CommandException("commands.whitelist.remove.failed", args[1]);
                            }

                            var3.getConfigurationManager().removePlayerFromWhitelist(var5);
                            notifyOperators(sender, this, "commands.whitelist.remove.success", args[1]);
                            break;
                        case "reload":
                            var3.getConfigurationManager().loadWhiteList();
                            notifyOperators(sender, this, "commands.whitelist.reloaded");
                            break;
                    }
                    break;
            }
        }
    }

    public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, "on", "off", "list", "add", "remove", "reload");
        } else {
            if (args.length == 2) {
                if (args[0].equals("remove")) {
                    return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getConfigurationManager().getWhitelistedPlayerNames());
                }

                if (args[0].equals("add")) {
                    return getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getPlayerProfileCache().func_152654_a());
                }
            }

            return null;
        }
    }
}
