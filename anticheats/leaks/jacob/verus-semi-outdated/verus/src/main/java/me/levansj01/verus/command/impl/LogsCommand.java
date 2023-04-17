package me.levansj01.verus.command.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import me.levansj01.verus.alert.manager.AlertManager;
import me.levansj01.verus.command.BaseCommand;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.database.Database;
import me.levansj01.verus.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class LogsCommand extends BaseCommand {

    private void getLogs(CommandSender sender, String s, int i) {
        this._getLogs(sender, s, false, i);
    }

    private void _getLogs(CommandSender sender, String string, boolean b, int i) {
        Database database = StorageEngine.getInstance().getDatabase();
        database.getUUID(string, uuid -> {
            if (uuid == null) {
                sender.sendMessage(ChatColor.RED + EnumMessage.COMMAND_PLAYER_NEVER_LOGGED_ON.get());
            } else {
                if (i != 0) {
                    database.getLogs(uuid, i, log -> {
                        if (!log.iterator().hasNext()) {
                            sender.sendMessage(ChatColor.RED + EnumMessage.PLAYER_NO_LOGS.get());
                        } else {
                            AlertManager.getInstance().uploadLogs(sender, uuid, log, b);
                        }

                    });
                } else {
                    database.getLogs(uuid, log -> {
                        if (!log.iterator().hasNext()) {
                            sender.sendMessage(ChatColor.RED + EnumMessage.PLAYER_NO_LOGS.get());
                        } else {
                            AlertManager.getInstance().uploadLogs(sender, uuid, log, b);
                        }

                    });
                }

            }
        });
    }

    public LogsCommand() {
        super(EnumMessage.LOGS_COMMAND.get());
        this.setDatabase(true);
        this.setUsageMessage(ChatColor.RED + "Usage: /" + this.getName() + " <name> <amount/all>");
    }

    private void getLogs(CommandSender sender, String target) {
        this._getLogs(sender, target, false, 0);
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

    public void execute(CommandSender sender, String[] args) {
        if (!BukkitUtil.hasPermission(sender, "verus.staff") && !BukkitUtil.hasPermission(sender, "verus.staff.logs")) {
            sender.sendMessage(ChatColor.RED + this.getPermissionMessage());
        } else if (args.length == 2) {
            int i;
            try {
                i = Integer.parseInt(args[1]);
            } catch (Exception ignored) {
                String type = args[1];
                if (type.equalsIgnoreCase("all")) {
                    this.getLogs(sender, args[0]);
                } else if (type.startsWith("verbose") && BukkitUtil.hasPermission(sender, "verus.admin")) {
                    String name = args[0];
                    short amount = type.equals("verbose1") ? (short) 0 : 500;

                    this._getLogs(sender, name, true, amount);
                } else {
                    sender.sendMessage(this.getUsageMessage());
                }

                return;
            }

            this.getLogs(sender, args[0], i);
        } else if (args.length == 1) {
            this.getLogs(sender, args[0], 200);
        } else {
            sender.sendMessage(this.getUsageMessage());
        }

    }
}