package club.mineman.antigamingchair.listener;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.check.ICheck;
import club.mineman.antigamingchair.data.PlayerData;
import club.mineman.antigamingchair.event.PlayerUpdatePositionEvent;
import club.mineman.antigamingchair.event.PlayerUpdateRotationEvent;
import club.mineman.antigamingchair.event.player.PlayerAlertEvent;
import club.mineman.antigamingchair.event.player.PlayerBanEvent;
import club.mineman.antigamingchair.event.player.PlayerBanWaveEvent;
import club.mineman.antigamingchair.log.Log;
import club.mineman.antigamingchair.util.BlockUtil;
import club.mineman.antigamingchair.util.CC;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutCustomPayload;
import net.minecraft.server.v1_8_R3.PlayerConnection;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.text.DecimalFormat;
import java.util.*;

public class PlayerListener implements Listener {
    private final AntiGamingChair plugin;

    public PlayerListener(final AntiGamingChair plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        this.plugin.getPlayerDataManager().addPlayerData(event.getPlayer());
        PlayerConnection playerConnection = ((CraftPlayer) event.getPlayer()).getHandle().playerConnection;
        PacketPlayOutCustomPayload packetPlayOutCustomPayload = new PacketPlayOutCustomPayload("REGISTER", new PacketDataSerializer(Unpooled.wrappedBuffer("CB-Client" .getBytes())));
        PacketPlayOutCustomPayload packetPlayOutCustomPayload2 = new PacketPlayOutCustomPayload("REGISTER", new PacketDataSerializer(Unpooled.wrappedBuffer("CC" .getBytes())));
        this.plugin.getServer().getScheduler().runTaskLater(this.plugin, () -> {
            playerConnection.sendPacket(packetPlayOutCustomPayload);
            new PacketPlayOutCustomPayload("REGISTER", new PacketDataSerializer(Unpooled.wrappedBuffer("CC" .getBytes())));
            playerConnection.sendPacket(packetPlayOutCustomPayload2);
        }, 10L);

    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (this.plugin.getAlertsManager().hasAlertsToggled(event.getPlayer())) {
            this.plugin.getAlertsManager().toggleAlerts(event.getPlayer());
        }
        this.plugin.getPlayerDataManager().removePlayerData(event.getPlayer());
    }

    @EventHandler
    public void onTeleport(final PlayerTeleportEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData != null) {
            playerData.setLastTeleportTime(System.currentTimeMillis());
            playerData.setSendingVape(true);
        }
    }

    @EventHandler
    public void onDeath(final PlayerDeathEvent event) {
        final Player player = event.getEntity();
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData != null) {
            playerData.setInventoryOpen(false);
        }
    }

    @EventHandler
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        final Player player = event.getPlayer();
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData != null) {
            playerData.setInventoryOpen(false);
        }
    }

    @EventHandler
    public void onPlayerUpdatePosition(final PlayerUpdatePositionEvent event) {
        final Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        playerData.setOnGround(BlockUtil.isOnGround(event.getTo(), 0) || BlockUtil.isOnGround(event.getTo(), 1));
        if (playerData.isOnGround()) {
            playerData.setLastGroundY(event.getTo().getY());
        }
        playerData.setInLiquid(BlockUtil.isOnLiquid(event.getTo(), 0) || BlockUtil.isOnLiquid(event.getTo(), 1));
        playerData.setInWeb(BlockUtil.isOnWeb(event.getTo(), 0));
        playerData.setOnIce(BlockUtil.isOnIce(event.getTo(), 1) || BlockUtil.isOnIce(event.getTo(), 2));
        playerData.setOnStairs(BlockUtil.isOnStairs(event.getTo(), 0) || BlockUtil.isOnStairs(event.getTo(), 1));
        playerData.setUnderBlock(BlockUtil.isOnGround(event.getTo(), -2));
        if (event.getTo().getY() != event.getFrom().getY() && playerData.getVelocityV() > 0) {
            playerData.setVelocityV(playerData.getVelocityV() - 1);
        }
        if (Math.hypot(event.getTo().getX() - event.getFrom().getX(), event.getTo().getZ() - event.getFrom().getZ()) > 0.0 && playerData.getVelocityH() > 0) {
            playerData.setVelocityH(playerData.getVelocityH() - 1);
        }
        Class[] checks;
        for (int length = (checks = PlayerData.CHECKS).length, i = 0; i < length; ++i) {
            final Class checkClass = checks[i];
            final ICheck check = playerData.getCheck(checkClass);
            if (check.getType() == PlayerUpdatePositionEvent.class) {
                check.handleCheck(player, event);
            }
        }
        if (playerData.getVelocityY() > 0.0 && event.getTo().getY() > event.getFrom().getY()) {
            playerData.setVelocityY(0.0);
        }
    }

    @EventHandler
    public void onPlayerUpdateRotation(final PlayerUpdateRotationEvent event) {
        final Player player = event.getPlayer();
        if (player.getAllowFlight()) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        for (final Class checkClass : PlayerData.CHECKS) {
            final ICheck check = playerData.getCheck(checkClass);
            if (check.getType() == PlayerUpdateRotationEvent.class) {
                check.handleCheck(player, event);
            }
        }
    }

    @EventHandler
    public void onPlayerBanWave(final PlayerBanWaveEvent event) {
        if (!this.plugin.isAntiCheatEnabled() && !event.getReason().equals("Manual")) {
            return;
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        this.plugin.getLogger().info("Added " + player.getName() + " to the ban wave.");
    }

    @EventHandler
    public void onPlayerAlert(final PlayerAlertEvent event) {
        if (!this.plugin.isAntiCheatEnabled()) {
            event.setCancelled(true);
            return;
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        final PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
        if (playerData == null) {
            return;
        }
        final double tps = MinecraftServer.getServer().tps1.getAverage();
        String fixedTPS = new DecimalFormat(".##").format(tps);
        if (tps > 20.0) {
            fixedTPS = "20.0";
        }
        final String alert = String.valueOf(event.getAlert()) + ChatColor.LIGHT_PURPLE + " Ping " + playerData.getPing() + " ms. TPS " + fixedTPS + ".";
        final Set<UUID> playerUUIDs = new HashSet<>(this.plugin.getAlertsManager().getAlertsToggled());
        playerUUIDs.addAll(playerData.getPlayersWatching());
        final PlayerAlertEvent.AlertType type = event.getAlertType();
        playerUUIDs.stream().map(this.plugin.getServer()::getPlayer).filter(Objects::nonNull).forEach(p -> {
            if (player.hasPermission("anticheat.alerts")) {
                PlayerData alertedData = this.plugin.getPlayerDataManager().getPlayerData(p);
                boolean sendAlert = true;
                for (String phrase : alertedData.getFilteredPhrases()) {
                    if (!alert.toLowerCase().contains(phrase)) continue;
                    sendAlert = false;
                    break;
                }
                if (sendAlert) {
                    p.sendMessage(ChatColor.LIGHT_PURPLE + event.getPlayer().getName() + CC.D_PURPLE + " " + alert);
                }
            }
            return;
        });
        //Bukkit.broadcastMessage(String.valueOf(event.getPlayer().getName()) + " | " + alert);
    }

    @EventHandler
    public void onPlayerBan(final PlayerBanEvent event) {
        if (!this.plugin.isAntiCheatEnabled()) {
            event.setCancelled(true);
            return;
        }
        final Player player = event.getPlayer();
        if (player == null) {
            return;
        }
        this.plugin.getServer().broadcastMessage(ChatColor.STRIKETHROUGH + "--------------------------------------------------\n" + ChatColor.RED + "\u2718 " + ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.DARK_PURPLE + " was banned by " + ChatColor.LIGHT_PURPLE + "AntiGamingChair" + ChatColor.DARK_PURPLE + " for cheating.\n" + ChatColor.RED + ChatColor.STRIKETHROUGH + "--------------------------------------------------\n");
        this.plugin.getServer().getScheduler().runTask(this.plugin, () ->
                this.plugin.getServer().dispatchCommand(this.plugin.getServer().getConsoleSender(), "ban " + player.getName() + " Unfair Advantage -s"));
    }
}
