package org.crystalpvp.anticheat.listener;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.event.player.AlertType;
import org.crystalpvp.anticheat.api.event.player.PlayerAlertEvent;
import org.crystalpvp.anticheat.api.event.player.PlayerBanEvent;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.api.util.TaskUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.crystalpvp.anticheat.database.utils.Logs;
import io.netty.buffer.Unpooled;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;


import java.text.MessageFormat;
import java.util.List;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class PlayerListener implements Listener {


	@EventHandler
	public void onWindowClick(InventoryClickEvent event) {

		Player player = ((Player) event.getWhoClicked());
		final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player);

		playerData.setWindowClick(System.currentTimeMillis());
		playerData.setLastSlotClicked(event.getSlot());


		try {
			playerData.setLastItemSlotClicked(String.format("%s", event.getCurrentItem().getData()));
		} catch (Exception ignored) {

		}


		try {
			playerData.setLastSlotType(String.format("%s", event.getSlotType()));
		} catch (Exception ignored) {

		}


		playerData.setShiftClicking(event.isShiftClick());
	}


	@EventHandler
	public void onShoot(EntityShootBowEvent event) {
		if (event.getEntity() instanceof Player) {

			Player player = ((Player) event.getEntity()).getPlayer();
			final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player);


			playerData.setLastShoot(System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		CrystalAC.getInstance().getPlayerDataManager().addPlayerData(event.getPlayer());

		final Player player = event.getPlayer();
		final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player);



		playerData.setJoinTime(System.currentTimeMillis());
		playerData.setRespawnTime(System.currentTimeMillis());
		playerData.setLastTeleport(System.currentTimeMillis());
		playerData.setSpawnPoint(player.getLocation());

		CrystalAC.getInstance().getServer().getScheduler().runTaskLaterAsynchronously(CrystalAC.getInstance(), () -> {
			final PlayerConnection playerConnection = ((CraftPlayer) event.getPlayer()).getHandle().playerConnection;
			final PacketPlayOutCustomPayload packetPlayOutCustomPayload = new PacketPlayOutCustomPayload("REGISTER",
					new PacketDataSerializer(Unpooled.wrappedBuffer("CB-Client".getBytes()))
			);
			final PacketPlayOutCustomPayload packetPlayOutCustomPayload2 = new PacketPlayOutCustomPayload("REGISTER",
					new PacketDataSerializer(Unpooled.wrappedBuffer("CC".getBytes()))
			);
			final PacketPlayOutCustomPayload packetPlayOutCustomPayload3 = new PacketPlayOutCustomPayload("REGISTER",
					new PacketDataSerializer(Unpooled.wrappedBuffer("Lunar-Client".getBytes()))
			);

			playerConnection.sendPacket(packetPlayOutCustomPayload);
			playerConnection.sendPacket(packetPlayOutCustomPayload2);
			playerConnection.sendPacket(packetPlayOutCustomPayload3);
		}, 10L);
	}

	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		final Player player = event.getPlayer();
		final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player);

		if (playerData != null) {
			playerData.setSendingVape(true);
			playerData.setLastTeleport(System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
		final Player player = event.getPlayer();
		final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player);

		if (playerData != null) {
			playerData.setInventoryOpen(false);
			playerData.setSpawnPoint(player.getLocation());
			playerData.setJoinTime(System.currentTimeMillis());
		}
	}

	@EventHandler
	public void onPlayerAlert(PlayerAlertEvent event) {
		if (!CrystalAC.getInstance().isAntiCheatEnabled()) {
			event.setCancelled(true);
			return;
		}

		final Player player = event.getPlayer();

		if (player == null) {
			return;
		}


		final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player);

		if (playerData == null) {
			return;
		}


		playerData.setTotalVl(playerData.getTotalVl() + 1);
		final String extra = event.getExtra() == null ? "" : event.getExtra();

		if (event.getAlertType() == AlertType.RELEASE) {
			for (Player onlinePlayer : CrystalAC.getInstance().getServer().getOnlinePlayers()) {
				if (CrystalAC.getInstance().canAlert(onlinePlayer, false)) {

					final String alerts = ConfigurationManager.getAlerts();

                    final String message = CC.translate (new MessageFormat(alerts.replaceAll("%player%", "{0}").replaceAll("%check%", "{1}").replaceAll("%ping%", "{2}").replaceAll("%vl%", "{3}"))
                            .format(new Object[]{
									player.getName(), event.getCheckName(), ((CraftPlayer) player).getHandle().ping, (playerData.getTotalVl() + 1)
                            }));

					onlinePlayer.sendMessage(message);



				} else if (CrystalAC.getInstance().canAlert(onlinePlayer, true)) {

					final String alerts = ConfigurationManager.getAlertsVerbose();

                    final String message = CC.translate (new MessageFormat(alerts.replaceAll("%player%", "{0}").replaceAll("%check%", "{1}").replaceAll("%ping%", "{2}/Tps: {3}").replaceAll("%extra%", "{4}"))
                            .format(new Object[]{
									player.getDisplayName(), event.getCheckName(), ((CraftPlayer) player).getHandle().ping, MinecraftServer.getServer().tps1.getAverage(), extra
                            }));

                    onlinePlayer.sendMessage(message);


                }
			}
		}


		else if (event.getAlertType() == AlertType.EXPERIMENTAL) {
			for (Player onlinePlayer : CrystalAC.getInstance().getServer().getOnlinePlayers()) {
				if (CrystalAC.getInstance().canAlert(onlinePlayer, false)) {

					final String alerts = ConfigurationManager.getAlerts();

					final String message = CC.translate (new MessageFormat(alerts.replaceAll("%player%", "{0}").replaceAll("%check%", "{1}").replaceAll("%ping%", "{2}").replaceAll("%vl%", "{3}"))
							.format(new Object[]{
									player.getDisplayName(), event.getCheckName(), ((CraftPlayer) player).getHandle().ping, (playerData.getTotalVl() + 1)
							}));

					onlinePlayer.sendMessage(CC.BLUE + CC.BOLD + "(DEV) " + message);



				} else if (CrystalAC.getInstance().canAlert(onlinePlayer, true)) {

					final String alerts = ConfigurationManager.getAlertsVerbose();

					final String message = CC.translate (new MessageFormat(alerts.replaceAll("%player%", "{0}").replaceAll("%check%", "{1}").replaceAll("%ping%", "{2}/Tps: {3}").replaceAll("%extra%", "{4}"))
							.format(new Object[]{
									player.getDisplayName(), event.getCheckName(), ((CraftPlayer) player).getHandle().ping, MinecraftServer.getServer().tps1.getAverage(), extra
							}));

					onlinePlayer.sendMessage(CC.BLUE + CC.BOLD + "(DEV) " + message);


				}
			}
		}

		final boolean add = Logs.getQueue().add(new Logs(
				player.getUniqueId(),
				event.getCheckName(),
				playerData.getClient().getName(),
				playerData.getPing(),
				MinecraftServer.getServer().tps1.getAverage(),
				System.currentTimeMillis()
		));
	}

	public int getPing ( final Player player){
		final int ping = ((CraftPlayer) player).getHandle().ping;
		if (ping >= 100) {
			return ping - 20;
		}
		if (ping >= 50) {
			return ping - 9;
		}
		if (ping >= 20) {
			return ping - 4;
		}
		return ping;
	}

	@EventHandler
	public void onPlayerBan(PlayerBanEvent event) {
		if (!CrystalAC.getInstance().isAntiCheatEnabled()) {
			event.setCancelled(true);
			return;
		}


		final Player player = event.getPlayer();

		if (CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player).isExempted()) {
			for (Player onlinePlayer : CrystalAC.getInstance().getServer().getOnlinePlayers()) {
				if (CrystalAC.getInstance().canAlert(onlinePlayer, false)) {
					onlinePlayer.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &b" + player.getDisplayName() + "&fis exempted by the anticheat so is therefor not banned."));
				}
				if (CrystalAC.getInstance().canAlert(onlinePlayer, true)) {
					onlinePlayer.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &b" + player.getDisplayName() + "&fis exempted by the anticheat so is therefor not banned."));
				}
			}
			return;
		}



		final List<String> messages = ConfigurationManager.getBanBroadcast();

		for (String s : messages) {
			Bukkit.getServer().broadcastMessage(ChatColor.translateAlternateColorCodes('&', s).replaceAll("%player%", player.getDisplayName()));
		}

		TaskUtil.run(new Runnable() {
			@Override
			public void run() {
				CrystalAC.getInstance().getServer().dispatchCommand(
						CrystalAC.getInstance().getServer().getConsoleSender(),
						ConfigurationManager.getBanCommand().replaceAll("%player%", player.getDisplayName())
				);
			}
		});
	}



	@EventHandler
	public void onPlayerKick(PlayerKickEvent event) {

		final Player player = event.getPlayer();

		if (player == null) {
			return;
		}

		if (CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player).isExempted()) {
			for (Player onlinePlayer : CrystalAC.getInstance().getServer().getOnlinePlayers()) {
				if (CrystalAC.getInstance().canAlert(onlinePlayer, false)) {
					onlinePlayer.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &b" + player.getDisplayName() + " &fis exempted by the anticheat so is therefor not kicked."));
				}
				if (CrystalAC.getInstance().canAlert(onlinePlayer, true)) {
					onlinePlayer.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &b" + player.getDisplayName() + " &fis exempted by the anticheat so is therefor not kicked."));
				}
			}
			return;
		}


		TaskUtil.run(new Runnable() {
			@Override
			public void run() {
				CrystalAC.getInstance().getServer().dispatchCommand(
						CrystalAC.getInstance().getServer().getConsoleSender(),
						ConfigurationManager.getKickCommand().replaceAll("%player%", player.getDisplayName())
				);
			}
		});
	}

	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		final Player player = event.getPlayer();

		if (player == null) {
			return;
		}


		final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(player);

		if (playerData == null) {
			return;
		}

		playerData.setSpawnPoint(player.getLocation());
	}
}
