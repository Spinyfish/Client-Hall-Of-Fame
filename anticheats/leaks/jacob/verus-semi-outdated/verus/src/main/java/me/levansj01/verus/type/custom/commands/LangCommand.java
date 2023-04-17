package me.levansj01.verus.type.custom.commands;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import me.levansj01.launcher.VerusLauncher;
import me.levansj01.verus.command.BaseCommand;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.util.BukkitUtil;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class LangCommand extends BaseCommand {
    public LangCommand() {
        super(EnumMessage.LANG_COMMAND.get());
        this.setDatabase(true);
        this.setUsageMessage(ChatColor.RED + "Usage: /" + this.getName());
    }

    public void execute(CommandSender sender, String[] args) {
        if (BukkitUtil.hasPermission(sender, "verus.admin") || BukkitUtil.hasPermission(sender, "verus.staff.lang")) {
            if (args.length == 0) {
                VerusLauncher launcher = VerusLauncher.getPlugin();
                YamlConfiguration lang = new YamlConfiguration();
                File file = new File(launcher.getDataFolder(), "lang.yml");
                if (!file.exists()) {
                    sender.sendMessage(ChatColor.RED + "Couldn't find lang.yml, check console for errors.");
                    return;
                }
                try {
                    lang.load(file);
                } catch (IOException | InvalidConfigurationException exception) {
                    launcher.getLogger().log(Level.SEVERE, "Failed to load " + file.getName() + ": ", exception);
                    sender.sendMessage(ChatColor.RED + "Failed to reload lang.yml, check console for errors.");
                    return;
                }
                for (EnumMessage message : EnumMessage.values()) {
                    message.setValue(ChatColor.translateAlternateColorCodes('&', lang.getString(message.getLocation(), message.getFallback())));
                }
                sender.sendMessage(ChatColor.GREEN + "Successfully reloaded lang.yml!");
                sender.sendMessage(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Please be aware that commands will not be affected by this change.");
            } else {
                sender.sendMessage(this.getUsageMessage());
            }
        }
    }
}
