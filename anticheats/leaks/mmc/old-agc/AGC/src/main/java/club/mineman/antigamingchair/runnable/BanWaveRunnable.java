package club.mineman.antigamingchair.runnable;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.manager.BanWaveManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

public class BanWaveRunnable extends BukkitRunnable {
    private static final String REASON = "[AntiGamingChair] Ban Wave v1";
    private final AntiGamingChair plugin;
    private int countdown;

    public BanWaveRunnable(final AntiGamingChair plugin) {
        this.countdown = 6;
        this.plugin = plugin;
    }

    public void run() {
        final BanWaveManager manager = this.plugin.getBanWaveManager();
        if (manager.isBanWaveStarted()) {
            if (--this.countdown > 0) {
                this.plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE + "The ban wave will commence in " + ChatColor.DARK_PURPLE + this.countdown + " second(s)...");
            } else if (this.countdown == 0) {
                this.plugin.getServer().broadcastMessage(ChatColor.GREEN + "The ban wave has commenced.");
                this.countdown = -1;
            } else if (manager.getCheaters().size() > 0) {
                final long id = manager.queueCheater();
                final String name = manager.getCheaterName(id);
                this.plugin.getServer().getScheduler().runTask(this.plugin, () -> this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "ban " + name + " " + "[AntiGamingChair] Ban Wave v1" + " -s"));
                this.plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE + name + ChatColor.LIGHT_PURPLE + " was banned for " + ChatColor.DARK_PURPLE + "[AntiGamingChair] Ban Wave v1");
            } else if (manager.getCheaters().size() == 0) {
                manager.setBanWaveStarted(false);
            }
        } else {
            this.plugin.getServer().broadcastMessage(ChatColor.STRIKETHROUGH + "--------------------------------------------------\n" + ChatColor.RED + "\u2718 " + ChatColor.DARK_PURPLE + "AntiGamingChair " + ChatColor.LIGHT_PURPLE + "has finished the ban wave. A total of " + ChatColor.DARK_PURPLE + manager.getCheaterNames().size() + " players " + ChatColor.LIGHT_PURPLE + "were banned. Stay legit minemen...\n" + ChatColor.RED + ChatColor.STRIKETHROUGH + "--------------------------------------------------\n");
            this.plugin.getBanWaveManager().clearCheaters();
            this.cancel();
        }
    }
}
