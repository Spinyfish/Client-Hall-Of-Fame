package org.crystalpvp.anticheat.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.api.util.CrystalUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class WatchCommand implements CommandExecutor {

    private CrystalAC plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;

        if (sender.hasPermission("ac.command.watch")) {
            if (args.length == 0) {
                player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /watch <player>"));
                return true;
            }

            final Player target = Bukkit.getPlayer(args[0]);

            if (target == null) {
                player.sendMessage(ChatColor.RED + "That player is not online.");
                return true;
            }

            final PlayerData targetData = this.plugin.getPlayerDataManager().getPlayerData(target);

            targetData.togglePlayerWatching(player);
            player.sendMessage(ChatColor.GREEN + "You are " + (targetData.isPlayerWatching(player) ? "now" : "no longer") + " focusing on " + target.getName() + " anticheat alerts.");
        } else {
            player.sendMessage(CC.translate(ChatColor.RED + "You do not have permission to execute this command."));
        }
        return true;
    }
}
