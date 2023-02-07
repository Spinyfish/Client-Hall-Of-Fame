package org.crystalpvp.anticheat.command;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.api.util.CrystalUtil;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class AlertsCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player)sender;

        if (player == null) {
            return false;
        }


        if (sender.hasPermission("ac.command.alerts") ) {
            if (args.length == 0) {
                player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /alerts" + "&b <normal/verbose>"));
            } else {
                if (args[0].equalsIgnoreCase("normal") || (args[0].equalsIgnoreCase("n"))) {
                    CrystalAC.getInstance().toggleAlerts(player, false);
                } else if (args[0].equalsIgnoreCase("verbose") || (args[0].equalsIgnoreCase("v"))) {
                    CrystalAC.getInstance().toggleAlerts(player, true);
                } else {
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /alerts" + "&b <normal/verbose>"));
                }
            }
        }
        else{
            player.sendMessage(CC.translate(ChatColor.RED + "You do not have permission to execute this command."));
        }
        return true;
    }
}