package me.levansj01.verus.data.manager;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.api.API;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.client.ClientType;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.config.VerusConfiguration;
import me.levansj01.verus.util.java.JavaV;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.geysermc.floodgate.api.FloodgateApi;

public class DataManager {
    private final Map<UUID, PlayerData> players = new ConcurrentHashMap();
    private final Map<Integer, WeakReference<PlayerData>> playersById = new ConcurrentHashMap();
    private final ExecutorService executorService = NMSManager.getInstance().getDataExecutorService("Data Manager Thread %s");
    private static DataManager instance;
    public final VerusPlugin plugin;

    public void uninject(Player player) {
        PlayerData playerData = (PlayerData) this.players.remove((Object)player.getUniqueId());
        WeakReference weakReference = this.playersById.remove((Object)player.getEntityId());
        if (weakReference != null) {
            weakReference.clear();
        }
        if (playerData != null) {
            playerData.saveData();
            playerData.setEnabled(false);
            NMSManager.getInstance().uninject(playerData);
        }
        if (playerData != null) {
            playerData.release();
        }
    }

    public PlayerData getPlayer(UUID uUID) {
        return this.players.get(uUID);
    }

    public Map<Integer, WeakReference<PlayerData>> getPlayersById() {
        return this.playersById;
    }

    public List<PlayerData> getMostViolations() {
        return (List)this.players.values().stream().sorted(Collections.reverseOrder((Comparator)Comparator.comparingDouble(PlayerData::getTotalViolations))).collect(Collectors.toList());
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public DataManager(VerusPlugin verusPlugin) {
        this.plugin = verusPlugin;
    }

    public static void enable(VerusPlugin verusPlugin) {
        instance = new DataManager(verusPlugin);
        NMSManager.getInstance().postToMainThread(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                NMSManager.getInstance().setAsyncPotionEffects(player);
            }
        });
        instance.getExecutorService().execute(() -> {
            for (Player player : Bukkit.getOnlinePlayers()) {
                instance.inject(player);
            }
        });
    }

    public PlayerData getPlayer(int n) {
        WeakReference weakReference = this.playersById.get(n);
        if (weakReference == null) {
            return null;
        }
        return (PlayerData)weakReference.get();
    }

    public VerusPlugin getPlugin() {
        return this.plugin;
    }

    public Collection<PlayerData> getPlayers() {
        return this.players.values();
    }

    public PlayerData getPlayer(Player player) {
        return this.getPlayer(player.getUniqueId());
    }

    public static void disable() throws InterruptedException {
        if (instance != null) {
            JavaV.shutdownAndAwaitTermination(DataManager.instance.executorService, 5L, TimeUnit.SECONDS);
            Thread thread = new Thread(() -> Bukkit.getOnlinePlayers().forEach(instance::uninject));
            thread.start();
            thread.join(TimeUnit.SECONDS.toMillis(5L));
            for (PlayerData playerData : instance.getPlayers()) {
                playerData.release();
            }
        }
        instance = null;
    }

    public boolean isPlayer(int n) {
        return this.playersById.containsKey(n);
    }

    public static DataManager getInstance() {
        return instance;
    }

    public void inject(Player player) {
        if (player != null && !player.hasMetadata("fake")) {
            PlayerData playerData = new PlayerData(player, this.plugin.getTypeLoader().loadChecks());
            StorageEngine storageEngine = StorageEngine.getInstance();
            VerusConfiguration verusConfiguration = storageEngine.getVerusConfig();
            NMSManager nMSManager = NMSManager.getInstance();
            API aPI = API.getAPI();
            if (aPI != null && aPI.fireInitEvent(playerData) || verusConfiguration.isGeyser() && player.getName().startsWith(verusConfiguration.getGeyserPrefix()) || nMSManager.isFloodGateAPI() && FloodgateApi.getInstance().isFloodgatePlayer(player.getUniqueId())) {
                playerData.setEnabled(false);
                playerData.getClientData().setType(ClientType.BEDROCK);
            }
            this.players.put(player.getUniqueId(), playerData);
            this.playersById.put(player.getEntityId(), new WeakReference(playerData));
            if (playerData.isEnabled()) {
                nMSManager.inject(playerData);
            }
            if (storageEngine.isConnected()) {
                playerData.loadData();
            }
        }
    }
}
