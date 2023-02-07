package org.crystalpvp.anticheat.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.api.util.CrystalUtil;
import org.crystalpvp.anticheat.api.util.TaskUtil;


import java.util.List;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class ACBanCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        Player player = (Player)sender;

        if (player == null) {
            return false;
        }

        if (sender.hasPermission("ac.command.ban") ) {
            if(args.length == 0) {
                player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /acban <player>"));
            } else  {
                final Player target = Bukkit.getPlayer(args[0]);
                if (target == null) {
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Player not found"));
                    return true;
                }
                Ban(target);
            }

        } else {
            player.sendMessage(CC.translate(ChatColor.RED + "You do not have permission to execute this command."));
        }
        return true;
    }

    public void Ban(Player player) {
        final List<String> messages = ConfigurationManager.getBanBroadcast();

        for (String s : messages) {
            Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%player%", player.getDisplayName()));
        }

        TaskUtil.run(new Runnable() {
            @Override
            public void run() {
                CrystalAC.getInstance().getServer().dispatchCommand(
                        CrystalAC.getInstance().getServer().getConsoleSender(),
                        ConfigurationManager.getBanCommand().replaceAll("%player%", player.getDisplayName())
                );
            }
        });
    }

}