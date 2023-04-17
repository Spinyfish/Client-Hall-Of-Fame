package me.levansj01.verus.command.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.levansj01.verus.command.BaseCommand;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.manager.DataManager;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class PingCommand extends BaseCommand {

    public void execute(CommandSender commandSender, String[] stringArray) {
        if (!BukkitUtil.hasPermission(commandSender, "verus.ping")) {
            commandSender.sendMessage(ChatColor.RED + this.getPermissionMessage());
            return;
        }
        if (commandSender instanceof Player) {
            Player player = (Player)commandSender;
            if (stringArray.length == 1) {
                if (!BukkitUtil.hasPermission(commandSender, "verus.ping.other")) {
                    commandSender.sendMessage(ChatColor.RED + this.getPermissionMessage());
                    return;
                }
                Player player2 = Bukkit.getPlayer(stringArray[0]);
                if (player2 != null) {
                    PlayerData playerData;
                    boolean bl = true;
                    if (StorageEngine.getInstance().getVerusConfig().isVanishPing()) {
                        bl = player.canSee(player2);
                    }
                    if ((playerData = DataManager.getInstance().getPlayer(player2)) != null && bl) {
                        String string = EnumMessage.PING_OTHER.get().replace((CharSequence)"{player}", (CharSequence)playerData.getName()).replace((CharSequence)"{ping}", (CharSequence)(playerData.getAveragePing() + "ms (" + playerData.getAverageTransactionPing() + "ms)"));
                        player.sendMessage(string);
                    }
                } else {
                    player.sendMessage(ChatColor.RED + EnumMessage.COMMAND_PLAYER_NOT_FOUND.get());
                }
            } else {
                PlayerData playerData = DataManager.getInstance().getPlayer(player);
                if (playerData != null) {
                    String string = EnumMessage.PING_SELF.get().replace((CharSequence)"{ping}", (CharSequence)(playerData.getAveragePing() + "ms (" + playerData.getAverageTransactionPing() + "ms)"));
                    player.sendMessage(string);
                }
            }
        }
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String s, String[] args) throws IllegalArgumentException {
        Player player = sender instanceof Player ? (Player) sender : null;
        List<String> completed = new ArrayList<>();
        for (Player online : sender.getServer().getOnlinePlayers()) {
            String name = online.getName();
            if (!online.canSee(player))
                continue;
            String lastArg = args[args.length - 1];
            completed.add(name);
        }
        return completed;
    }

    public PingCommand() {
        super(EnumMessage.PING_COMMAND.get());
    }
}