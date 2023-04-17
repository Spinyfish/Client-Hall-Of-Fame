package club.mineman.antigamingchair.manager;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.data.PlayerData;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerDataManager {
    private final Map<UUID, PlayerData> playerDataMap;
    private final AntiGamingChair plugin;

    public PlayerDataManager(final AntiGamingChair plugin) {
        this.playerDataMap = new HashMap<>();
        this.plugin = plugin;
    }

    public void addPlayerData(final Player player) {
        this.playerDataMap.put(player.getUniqueId(), new PlayerData(this.plugin));
    }

    public void removePlayerData(final Player player) {
        this.playerDataMap.remove(player.getUniqueId());
    }

    public PlayerData getPlayerData(final Player player) {
        if(this.playerDataMap.get(player.getUniqueId()) == null){
            PlayerData playerData = new PlayerData(this.plugin);
            this.playerDataMap.put(player.getUniqueId(), playerData);
        }
        return this.playerDataMap.get(player.getUniqueId());
    }
}
