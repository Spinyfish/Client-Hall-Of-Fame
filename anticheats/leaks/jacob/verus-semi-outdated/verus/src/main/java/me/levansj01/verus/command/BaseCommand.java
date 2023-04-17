package me.levansj01.verus.command;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.util.BukkitUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

@Getter
@Setter
public abstract class BaseCommand extends Command {

    private boolean database;
    private String usageMessage;

    public abstract void execute(CommandSender sender, String[] args);

    public BaseCommand(String name, String description, String usageMessage, List<String> aliases) {
        super(name, description, usageMessage, aliases);
    }

    public BaseCommand(String name) {
        super(name);
    }

    public boolean execute(CommandSender sender, String command, String[] args) {
        if (this.getPermission() != null && !BukkitUtil.hasPermission(sender, this.getPermission())) {
            sender.sendMessage(ChatColor.RED + this.getPermissionMessage());
            return false;
        } else if (this.database && !StorageEngine.getInstance().isConnected()) {
            sender.sendMessage(ChatColor.RED + "Please connect to a database to use this command.");
            return false;
        } else {
            this.execute(sender, args);
            return false;
        }
    }
}