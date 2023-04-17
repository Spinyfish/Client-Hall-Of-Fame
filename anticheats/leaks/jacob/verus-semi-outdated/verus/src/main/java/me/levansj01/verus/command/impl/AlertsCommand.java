package me.levansj01.verus.command.impl;

import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.command.BaseCommand;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.manager.DataManager;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AlertsCommand extends BaseCommand {

    public AlertsCommand() {
        super(EnumMessage.ALERTS_COMMAND.get());
    }

    private void update(PlayerData playerData) {
        StorageEngine storageEngine = StorageEngine.getInstance();
        if (storageEngine.isConnected()) {
            storageEngine.getDatabase().updateAlerts(playerData);
        }
    }

    public void execute(CommandSender commandSender, String[] stringArray) {
        if (BukkitUtil.hasPermission(commandSender, "verus.staff") || BukkitUtil.hasPermission(commandSender, "verus.staff.alerts")
                || BukkitUtil.hasPermission(commandSender, "verus.alerts")) {
            if (commandSender instanceof Player) {
                Player player = (Player)commandSender;
                PlayerData playerData = DataManager.getInstance().getPlayer(player);
                if (playerData != null) {
                    EnumMessage enumMessage;
                    boolean alerts;
                    if (BukkitUtil.isDev(player) && stringArray.length == 1 && stringArray[0].equalsIgnoreCase("debug")) {
                        String string;
                        boolean debug = playerData.isDebug();
                        playerData.setDebug(debug);
                        this.update(playerData);
                        BukkitUtil.setMeta(player, "verus.admin", playerData.isDebug());
                        StringBuilder stringBuilder = new StringBuilder().append(VerusPlugin.COLOR).append("You are ");
                        if (playerData.isDebug()) {
                            string = "now";
                        } else {
                            string = "no longer";
                        }
                        commandSender.sendMessage(stringBuilder.append(string).append(" in debug mode").toString());
                        return;
                    }
                    alerts = playerData.isAlerts();
                    playerData.setAlerts(alerts);
                    this.update(playerData);
                    BukkitUtil.setMeta(player, "verus.alerts", playerData.isAlerts());
                    if (playerData.isAlerts()) {
                        enumMessage = EnumMessage.ALERTS_ENABLED_COMMAND;
                    } else {
                        enumMessage = EnumMessage.ALERTS_DISABLED_COMMAND;
                    }
                    EnumMessage enumMessage2 = enumMessage;
                    commandSender.sendMessage(VerusPlugin.COLOR + enumMessage2.get());
                } else {
                    player.sendMessage(ChatColor.RED + "Your data is not currently loaded.");
                }
            }
        } else {
            commandSender.sendMessage(ChatColor.RED + this.getPermissionMessage());
        }
    }
}
