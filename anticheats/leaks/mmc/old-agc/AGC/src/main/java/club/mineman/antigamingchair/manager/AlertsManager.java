package club.mineman.antigamingchair.manager;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.data.PlayerData;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class AlertsManager {
    private final Set<UUID> alertsToggled;
    private final AntiGamingChair plugin;

    public AlertsManager(final AntiGamingChair plugin) {
        this.alertsToggled = new HashSet<>();
        this.plugin = plugin;
    }

    public boolean hasAlertsToggled(final Player player) {
        return this.alertsToggled.contains(player.getUniqueId());
    }

    public void toggleAlerts(final Player player) {
        if (!this.alertsToggled.remove(player.getUniqueId())) {
            this.alertsToggled.add(player.getUniqueId());
        }
    }

    public void forceAlert(final String message) {
        this.forceAlertWithData(message, null);
    }

    private void forceAlertWithData(final String message, final PlayerData playerData) {
        final Set<UUID> playerUUIDs = new HashSet<>(this.plugin.getAlertsManager().getAlertsToggled());
        if (playerData != null) {
            playerUUIDs.addAll(playerData.getPlayersWatching());
        }
        playerUUIDs.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull).forEach(p -> p.sendMessage(message));
    }

    public void forceAlert(final String message, final Player player) {
        final double tps = MinecraftServer.getServer().tps1.getAverage();
        String fixedTPS = new DecimalFormat(".##").format(tps);
        if (tps > 20.0) {
            fixedTPS = "20.0";
        }
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        final String alert = String.valueOf(message) + ChatColor.LIGHT_PURPLE + " Ping " + playerData.getPing() + " ms. TPS " + fixedTPS + ".";
        this.forceAlertWithData(ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.DARK_PURPLE + " " + alert, playerData);
    }

    public Set<UUID> getAlertsToggled() {
        return this.alertsToggled;
    }
}
