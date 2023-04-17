package me.levansj01.verus.api;

import java.util.List;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.api.wrapper.BanResult;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.data.PlayerData;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.material.MaterialData;
import org.bukkit.plugin.Plugin;

public abstract class API {

    private String version = "Unknown";
    private static Boolean enabled = null;
    private static API api = null;

    @Deprecated
    protected boolean fireBanEvent(PlayerData var1, Check var2) {
        return false;
    }

    public static Plugin fetchPlugin() {
        return Bukkit.getPluginManager().getPlugin("VerusAPI");
    }

    protected static boolean isEnabled() {
        if (enabled == null) {
            enabled = Bukkit.getPluginManager().isPluginEnabled("VerusAPI");
        }

        return enabled;
    }

    public BanResult fireBanEvent(PlayerData playerData, Check check, boolean isAnnounce, List<?> list) {
        if (this.fireBanEvent(playerData, check))
            return BanResult.CANCEL;
        if (isAnnounce)
            return BanResult.ANNOUNCE;
        return BanResult.SILENT;
    }

    public static API getAPI() {
        if (!isEnabled()) {
            return null;
        } else {
            if (api == null) {
                Plugin plugin = fetchPlugin();
                String version = plugin.getDescription().getVersion();

                try {
                    api = Class.forName("me.levansj01.verus.api.impl.API" + version.replace(".", "_")).asSubclass(API.class).newInstance();
                    api.setVersion(version);
                } catch (Exception exception) {
                    exception.printStackTrace();
                    enabled = false;
                    return api;
                }
            }
            return api;
        }
    }

    public MaterialData getFakeBlock(Player player, World world, int i, int i2, int i3) {
        return null;
    }

    public boolean fireInitEvent(PlayerData playerData) {
        return false;
    }

    public void disable() {
    }

    public abstract boolean fireViolationEvent(PlayerData playerData, Check check, int i);

    public static void check() {
        enabled = null;
    }

    public void enable(VerusPlugin plugin) {
    }

    public void setVersion(String var1) {
        this.version = var1;
    }

    public String getVersion() {
        return this.version;
    }
}
