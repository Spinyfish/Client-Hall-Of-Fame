package me.levansj01.verus.command.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.command.BaseCommand;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.database.Log;
import me.levansj01.verus.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class RecentLogsCommand extends BaseCommand {

    private static final DateFormat FORMAT = new SimpleDateFormat("dd/MM HH:mm");

    public void execute(CommandSender commandSender, String[] stringArray) {
        if (stringArray.length == 1) {
            if (BukkitUtil.hasPermission(commandSender, "verus.staff") || BukkitUtil.hasPermission(commandSender, "verus.staff.logs.recent")) {
                StorageEngine.getInstance().getDatabase().getUUID(stringArray[0], uUID -> {
                    if (uUID == null) {
                        commandSender.sendMessage(ChatColor.RED + EnumMessage.COMMAND_PLAYER_NEVER_LOGGED_ON.get());
                        return;
                    }
                    StorageEngine.getInstance().getDatabase().getLogs(uUID, 10, iterable -> {
                        if (!iterable.iterator().hasNext()) {
                            commandSender.sendMessage(ChatColor.RED + "This player has no logs");
                        } else {
                            String string = ChatColor.GRAY + "(Gathering logs for " + stringArray[0] + "/" + uUID + "...)\n";
                            long l = System.currentTimeMillis();
                            for (Log log : iterable) {
                                string = string + this.getFormattedRecentLogs(log, l) + "\n";
                            }
                            commandSender.sendMessage(string);
                        }
                    });
                });
            } else {
                commandSender.sendMessage(ChatColor.RED + this.getPermissionMessage());
            }
        } else {
            commandSender.sendMessage(this.getUsageMessage());
        }
    }

    public String getFormattedRecentLogs(Log log, long l) {
        return ChatColor.translateAlternateColorCodes('&',
                StorageEngine.getInstance().getVerusConfig().getRecentlogsMessage()
                        .replace("{date}", (ChatColor.GRAY + "[" + ChatColor.WHITE +
                                FORMAT.format(l) + ChatColor.GRAY + "]")).replace("{time}",
                                (ChatColor.GRAY + "(" + ChatColor.WHITE + me.levansj01.verus.util.java.StringUtil.differenceAsTime((l - log.getTimestamp()))
                                        + ChatColor.GRAY + ")")).replace("{name}",
                                (VerusPlugin.COLOR + log.getName())).replace("{type}",
                                log.getType()).replace("{subType}", log.getSubType()).replace("{vl}",
                                String.valueOf(log.getViolations())).replace("{ping}",
                                String.valueOf(log.getPing())).replace("{lag}", String.valueOf(log.getLag())));
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

    public RecentLogsCommand() {
        super(EnumMessage.RECENT_LOGS_COMMAND.get());
        this.setDatabase(true);
        this.setUsageMessage(ChatColor.RED + "Usage: /" + this.getName() + " <name>");
    }
}