package org.crystalpvp.anticheat.command;

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

public class BypassCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player) sender;

        if (sender.hasPermission("ac.command.bypass")) {
            final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player);
            if (playerData.isExempted()) {
                playerData.setExempted(false);
                player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &b" + "&cYou are no longer bypassing CrystalAC"));

            } else {
                playerData.setExempted(true);
                player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &b" + "&aYou are now bypassing CrystalAC"));
            }

        }else{
            player.sendMessage(CC.translate(ChatColor.RED + "You do not have permission to execute this command."));
        }
        return true;
    }
}