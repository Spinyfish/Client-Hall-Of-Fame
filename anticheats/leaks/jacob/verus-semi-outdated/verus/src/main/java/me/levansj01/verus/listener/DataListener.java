package me.levansj01.verus.listener;

import me.levansj01.verus.data.manager.*;
import org.bukkit.entity.*;
import me.levansj01.verus.util.*;
import org.bukkit.event.*;
import me.levansj01.verus.data.client.*;
import me.levansj01.verus.data.*;
import me.levansj01.verus.storage.*;
import org.bukkit.event.block.*;
import me.levansj01.verus.compat.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;
import java.util.function.*;
import me.levansj01.verus.verus2.data.player.*;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.*;
import org.bukkit.event.inventory.*;
import me.levansj01.verus.gui.*;
import org.bukkit.inventory.*;
import me.levansj01.verus.util.item.*;
import org.bukkit.*;
import me.levansj01.verus.util.java.*;
import java.util.stream.*;
import me.levansj01.verus.*;
import me.levansj01.verus.type.*;
import java.util.*;

public class DataListener implements Listener
{
    private static final String DEVELOPER_MESSAGE;
    private static final DataManager MANAGER;
    private static final List FORGE_CHANNELS;
    
    public void sendDeveloperMessage(final Player player) {
        player.sendMessage(DataListener.DEVELOPER_MESSAGE);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(final PlayerJoinEvent playerJoinEvent) {
        final Player player = playerJoinEvent.getPlayer();
        NMSManager.getInstance().setAsyncPotionEffects(player);
        DataListener.MANAGER.getExecutorService().submit(DataListener::lambda.onJoin.0);
        if (BukkitUtil.isDev(player)) {
            this.sendDeveloperMessage(player);
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onRegister(final PlayerRegisterChannelEvent playerRegisterChannelEvent) {
        final Player player = playerRegisterChannelEvent.getPlayer();
        final String channel = playerRegisterChannelEvent.getChannel();
        if (channel != null && DataListener.FORGE_CHANNELS.contains(channel)) {
            final PlayerData player2 = DataManager.getInstance().getPlayer(player.getPlayer());
            if (player2 != null) {
                player2.getClientData().setType(ClientType.FORGE);
            }
        }
    }
    
    @EventHandler
    public void onExtend(final BlockPistonExtendEvent blockPistonExtendEvent) {
        if (StorageEngine.getInstance().getVerusConfig().isSlimePushFix()) {
            this.slimePush(blockPistonExtendEvent.getBlock(), blockPistonExtendEvent.getBlocks());
        }
    }
    
    @EventHandler
    public void onRetract(final BlockPistonRetractEvent blockPistonRetractEvent) {
        if (StorageEngine.getInstance().getVerusConfig().isSlimePushFix() && NMSManager.getInstance().getServerVersion().after(ServerVersion.v1_7_R4)) {
            this.slimePush(blockPistonRetractEvent.getBlock(), blockPistonRetractEvent.getBlocks());
        }
    }
    
    private void slimePush(final Block block, @NotNull final List list) {
        if (list.isEmpty()) {
            return;
        }
        block.getWorld().getPlayers().forEach(DataListener::lambda.slimePush.1);
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onQuit(final PlayerQuitEvent playerQuitEvent) {
        DataListener.MANAGER.getExecutorService().submit(DataListener::lambda.onQuit.2);
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onDamage(final EntityDamageEvent entityDamageEvent) {
        if (entityDamageEvent.getEntity() instanceof Player) {
            final Player player = (Player)entityDamageEvent.getEntity();
            switch (DataListener.DataListener.1..SwitchMap.org.bukkit.event.entity.EntityDamageEvent.DamageCause[entityDamageEvent.getCause().ordinal()]) {
                case 1: {
                    final PlayerData player2 = DataListener.MANAGER.getPlayer(player);
                    if (player2 != null) {
                        player2.reset(TickerType.SUFFOCATING);
                        break;
                    }
                    break;
                }
                case 2: {
                    final PlayerData player3 = DataListener.MANAGER.getPlayer(player);
                    if (player3 != null) {
                        player3.setFallDamage(NMSManager.getInstance().getCurrentTick());
                        break;
                    }
                    break;
                }
            }
        }
    }
    
    @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
    public void onVelocityEvent(final PlayerVelocityEvent playerVelocityEvent) {
        final PlayerData player = DataListener.MANAGER.getPlayer(playerVelocityEvent.getPlayer());
        if (player != null && player.getFallDamage() + 1 == NMSManager.getInstance().getCurrentTick()) {
            player.setFallDamage(0);
            if (playerVelocityEvent.getVelocity().getY() <= 0.08 && !StorageEngine.getInstance().getVerusConfig().isFixSlimeBlocks()) {
                playerVelocityEvent.setCancelled(true);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onDeath(final PlayerDeathEvent playerDeathEvent) {
        final PlayerData player = DataListener.MANAGER.getPlayer(playerDeathEvent.getEntity());
        if (player != null) {
            player.reset();
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onTeleport(final PlayerTeleportEvent playerTeleportEvent) {
        if (VerusTypeLoader.isDev()) {
            final Location to = playerTeleportEvent.getTo();
            if (to.getPitch() == 0.0f) {
                to.setPitch((float)(Math.random() - 0.5) * 0.01f);
            }
            if (to.getYaw() == 0.0f) {
                to.setYaw((float)(Math.random() - 0.5) * 0.01f);
            }
        }
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onFish(final PlayerFishEvent playerFishEvent) {
        if (playerFishEvent.getCaught() instanceof Player) {
            final PlayerData player = DataListener.MANAGER.getPlayer((Player)playerFishEvent.getCaught());
            if (player != null) {
                player.reset(TickerType.HOOKED);
            }
        }
    }
    
    @EventHandler
    public void onClick(final InventoryClickEvent inventoryClickEvent) {
        final Inventory inventory = (inventoryClickEvent.getClickedInventory() != null) ? inventoryClickEvent.getClickedInventory() : inventoryClickEvent.getInventory();
        if (inventory != null && inventory.getHolder() instanceof GUI) {
            ((GUI)inventory.getHolder()).onClick(inventoryClickEvent);
            inventoryClickEvent.setCancelled(true);
        }
        else {
            final InventoryView view = inventoryClickEvent.getView();
            if (view != null) {
                final Inventory topInventory = view.getTopInventory();
                if (topInventory != null && topInventory.getHolder() instanceof GUI) {
                    inventoryClickEvent.setCancelled(true);
                }
            }
        }
    }
    
    private static void lambda.onQuit.2(final Player player) {
        DataListener.MANAGER.uninject(player);
    }
    
    private static void lambda.slimePush.1(final World world, final Location location, final List list, final Player player) {
        final Location location2 = player.getLocation();
        if (location2.getWorld().equals(world) && location2.distanceSquared(location) < 100.0) {
            final Set sticky = MaterialList.STICKY;
            for (final Block block : list) {
                if (sticky.contains(block.getType()) && location2.distanceSquared(block.getLocation()) < 6.25) {
                    final PlayerData player2 = DataManager.getInstance().getPlayer(player);
                    if (player2 == null) {
                        break;
                    }
                    player2.reset(TickerType.SLIME_PUSH);
                }
            }
        }
    }
    
    private static void lambda.onJoin.0(final Player player) {
        DataListener.MANAGER.inject(player);
    }
    
    static {
        DEVELOPER_MESSAGE = String.format(JavaV.stream(ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------", ChatColor.WHITE + "This server is running %s (%s)", ChatColor.GRAY + ChatColor.STRIKETHROUGH.toString() + "------------------------------------------").collect(Collectors.joining("\n")), VerusPlugin.COLOR + VerusPlugin.getNameFormatted() + " Anticheat" + ChatColor.GRAY, Loader.getUsername());
        MANAGER = DataManager.getInstance();
        FORGE_CHANNELS = Arrays.asList("FML|HS", "FML", "FML|MP", "FORGE", "legacy:fml", "legacy:fml|hs", "legacy:forge");
    }
}
