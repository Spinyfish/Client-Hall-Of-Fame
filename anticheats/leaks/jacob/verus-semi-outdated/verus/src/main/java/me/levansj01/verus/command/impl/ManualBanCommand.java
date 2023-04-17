package me.levansj01.verus.command.impl;

import java.util.ArrayList;
import java.util.List;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.alert.manager.AlertManager;
import me.levansj01.verus.check.checks.manual.ManualA;
import me.levansj01.verus.command.BaseCommand;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.manager.DataManager;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.database.Database;
import me.levansj01.verus.storage.database.Log;
import me.levansj01.verus.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;


public class ManualBanCommand extends BaseCommand {

    public List<String> tabComplete(CommandSender commandSender, String string, String[] stringArray) throws IllegalArgumentException {
        if (BukkitUtil.hasPermission(commandSender, this.getPermission()) && (stringArray.length == 1
                || stringArray.length == 2 && stringArray[0].equalsIgnoreCase("check"))) {
            Player player;
            if (commandSender instanceof Player) {
                player = (Player)commandSender;
            } else {
                player = null;
            }
            Player player2 = player;
            List<String> arrayList = new ArrayList<>();
            for (Player online : commandSender.getServer().getOnlinePlayers()) {
                String name = online.getName();
                if (player != null && !player.canSee(online))
                    continue;
                String last = stringArray[stringArray.length - 1];
                if (StringUtil.startsWithIgnoreCase(name, last)) {
                    arrayList.add(last);
                }
            }
            arrayList.sort(String.CASE_INSENSITIVE_ORDER);
            return arrayList;
        }
        return super.tabComplete(commandSender, string, stringArray);
    }

    public ManualBanCommand() {
        super(EnumMessage.MANUAL_BAN_COMMAND.get());
        this.setDatabase(true);
        this.setPermission("verus.admin");
        this.setUsageMessage(ChatColor.RED + "Usage: /" + this.getName() + " <name> OR /" + this.getName() + " check <name>");
    }

    public void execute(CommandSender commandSender, String[] stringArray) {
        if (stringArray.length == 1) {
            Player player;
            String name = commandSender instanceof Player ? commandSender.getName() : "Console";
            if ((player = Bukkit.getPlayer(stringArray[0])) != null) {
                PlayerData playerData = DataManager.getInstance().getPlayer(player);
                ManualA manual = playerData.getCheckData().getManualBanCheck();
                if (manual != null) {
                    manual.setViolations(1.0);
                    manual.handleViolation(name);
                    AlertManager alertManager = AlertManager.getInstance();
                    if (alertManager.insertBan(playerData, manual)) {
                        playerData.setEnabled(false);
                    }
                }
            } else {
                commandSender.sendMessage(ChatColor.RED + EnumMessage.COMMAND_PLAYER_NOT_FOUND.get());
            }
        } else if (stringArray.length == 2 && stringArray[0].equalsIgnoreCase("check")) {
            String string = stringArray[1];
            Database database = StorageEngine.getInstance().getDatabase();
            database.getUUID(string, uUID -> {
                if (uUID == null) {
                    commandSender.sendMessage(ChatColor.RED + EnumMessage.COMMAND_PLAYER_NEVER_LOGGED_ON.get());
                    return;
                }
                database.getManualLogs(uUID, 10, iterable -> {
                    if (!iterable.iterator().hasNext()) {
                        commandSender.sendMessage(ChatColor.RED + EnumMessage.PLAYER_NO_LOGS.get());
                    } else {
                        StringBuilder msg = new StringBuilder(ChatColor.GRAY + "(Gathering logs for " + stringArray[0] + "/" + uUID + "...)\n");
                        long l = System.currentTimeMillis();
                        for (Log log : iterable) {
                            String string2 = log.getData();
                            if (string2 != null) {
                                msg.append(ChatColor.GRAY).append("(").append(ChatColor.WHITE)
                                        .append(me.levansj01.verus.util.java.StringUtil.differenceAsTime(l - log.getTimestamp()))
                                        .append(ChatColor.GRAY).append(") ").append(VerusPlugin.COLOR).append(log.getName())
                                        .append(ChatColor.GRAY).append(" was manual banned by ").append(ChatColor.WHITE).append(log.getData()).append("\n");
                            }
                        }
                        commandSender.sendMessage(msg.toString());
                    }
                });
            });
        } else {
            commandSender.sendMessage(this.getUsageMessage());
        }
    }
}
