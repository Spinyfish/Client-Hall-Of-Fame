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

public class ExemptCommand implements CommandExecutor {

    private CrystalAC plugin;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;

        if (sender.hasPermission("ac.command.exempt") ) {

            if (args.length == 0) {
                player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /exempt <player>"));
                return true;
            } else {
                final Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(CC.translate("&4Player not found"));
                    return false;
                }
                final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(target);
                //playerData.setExempted(!playerData.isExempted());
                if (playerData.isExempted()) {
                    playerData.setExempted(false);
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &b" + target.getDisplayName() + " &cis no longer exempted by the anticheat"));
                    return true;
                } else {
                    playerData.setExempted(true);
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &b" + target.getDisplayName() + " &ais now exempted by the anticheat"));
                    return true;
                }
            }
        } else {
            player.sendMessage(CC.translate(ChatColor.RED + "You do not have permission to execute this command."));
        }
        return true;
    }
}
