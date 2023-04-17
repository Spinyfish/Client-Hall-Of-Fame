package club.mineman.antigamingchair;

import club.mineman.antigamingchair.commands.AGCCommand;
import club.mineman.antigamingchair.listener.BanWaveListener;
import club.mineman.antigamingchair.listener.PlayerListener;
import club.mineman.antigamingchair.manager.AlertsManager;
import club.mineman.antigamingchair.manager.BanWaveManager;
import club.mineman.antigamingchair.manager.LogManager;
import club.mineman.antigamingchair.manager.PlayerDataManager;
import club.mineman.antigamingchair.packet.CustomMoveHandler;
import club.mineman.antigamingchair.packet.CustomPacketHandler;
import gg.ragemc.spigot.RageSpigot;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.command.Command;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;

public class AntiGamingChair extends JavaPlugin {
    private static AntiGamingChair instance;
    private PlayerDataManager playerDataManager;
    private BanWaveManager banWaveManager;
    private AlertsManager alertsManager;
    private LogManager logManager;
    private double rangeVl;
    private double rangeVlLow;

    public AntiGamingChair() {
        this.rangeVl = 25.0;
        this.rangeVlLow = 25.0;
    }

    public static AntiGamingChair getInstance() {
        return AntiGamingChair.instance;
    }

    public void onEnable() {
        (AntiGamingChair.instance = this).registerPacketHandler();
        this.registerManagers();
        this.registerListeners();
        this.registerCommands();
        this.registerRunnables();
    }

    public void onDisable() {
    }

    public boolean isAntiCheatEnabled() {
        return MinecraftServer.getServer().tps1.getAverage() > 19.0 && MinecraftServer.LAST_TICK_TIME + 100L > System.currentTimeMillis();
    }

    private void registerPacketHandler() {
        RageSpigot.INSTANCE.addMovementHandler(new CustomMoveHandler(this));
        RageSpigot.INSTANCE.addPacketHandler(new CustomPacketHandler(this));
    }

    private void registerManagers() {
        this.alertsManager = new AlertsManager(this);
        this.banWaveManager = new BanWaveManager(this);
        this.playerDataManager = new PlayerDataManager(this);
        this.logManager = new LogManager();
    }

    private void registerListeners() {
        this.getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
        this.getServer().getPluginManager().registerEvents(new BanWaveListener(this), this);
    }

    private void registerCommands() {
        Arrays.asList(new AGCCommand(this)).forEach(this::registerCommand);
    }

    private void registerRunnables() {
    }

    private void registerCommand(final Command cmd) {
        this.registerCommand(cmd, this.getName());
    }

    public void registerCommand(final Command cmd, final String fallbackPrefix) {
        MinecraftServer.getServer().server.getCommandMap().register(cmd.getName(), fallbackPrefix, cmd);
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public BanWaveManager getBanWaveManager() {
        return this.banWaveManager;
    }

    public AlertsManager getAlertsManager() {
        return this.alertsManager;
    }

    public LogManager getLogManager() {
        return this.logManager;
    }

    public double getRangeVl() {
        return this.rangeVl;
    }

    public void setRangeVl(final double rangeVl) {
        this.rangeVl = rangeVl;
    }

    public double getRangeVlLow() {
        return this.rangeVlLow;
    }

    public void setRangeVlLow(final double rangeVlLow) {
        this.rangeVlLow = rangeVlLow;
    }
}
