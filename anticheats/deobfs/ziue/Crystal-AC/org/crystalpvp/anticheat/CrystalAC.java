package org.crystalpvp.anticheat;


import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.crystalpvp.anticheat.api.config.FileConfig;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.manager.PlayerDataManager;
import org.crystalpvp.anticheat.api.stats.bStats;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.api.util.DBUtil;
import org.crystalpvp.anticheat.command.*;
import org.crystalpvp.anticheat.data.version.ClientManager;
import org.crystalpvp.anticheat.database.mongo.MongoDB;
import org.crystalpvp.anticheat.database.mongo.MongoDownloader;
import org.crystalpvp.anticheat.database.mysql.MySQL;
import org.crystalpvp.anticheat.database.sqlite.SQLite;
import org.crystalpvp.anticheat.database.sqlite.SQLiteDownloader;
import org.crystalpvp.anticheat.gui.MainGUI;
import org.crystalpvp.anticheat.license.License;
import org.crystalpvp.anticheat.listener.BungeeListener;
import org.crystalpvp.anticheat.listener.ModListListener;
import org.crystalpvp.anticheat.listener.PlayerListener;
import org.crystalpvp.anticheat.packet.CustomMovementHandler;
import org.crystalpvp.anticheat.packet.CustomPacketHandler;
import org.crystalpvp.anticheat.runnables.InsertLogsTask;

import java.util.*;

import static pt.foxspigot.jar.FoxSpigot.INSTANCE;

@Setter
@Getter

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class CrystalAC extends JavaPlugin {

    public static String BUILD_NAME = "1.0.3";
    public static Integer BUILD_NUM = 10;
    @Getter
    private static CrystalAC instance;
    private static CrystalAC plugin;
    public ConsoleCommandSender consoleSender;
    @Getter
    MainGUI mainGUI;
    @Getter
    private String db;
    private MongoDB mongo;
    private MongoDatabase mongoDatabase;
    private FileConfig mainFileConfig;
    private FileConfig checkFileConfig;
    private PlayerDataManager playerDataManager;
    private ClientManager clientManager;
    private Set<UUID> receivingAlerts;
    private Set<UUID> receivingVerboseAlerts;
    private Set<String> disabledChecks;

    public static CrystalAC getPlugin() {
        return plugin;
    }

    @Override
    public void onEnable() {
        instance = this;

        createConfig();
        loadConfig();

        getServer().getConsoleSender().sendMessage(CC.DARK_GRAY +
                "\n  _____             __       _____  _____ \n" +
                " / ___/_____ _____ / /____ _/ / _ |/ ___/\n" +
                " /   / )__)  /(__)\\  )___/ )__)  )   /  \n" +
                "/ /__/ __/ // (_-</ __/ _ `/ / __ / /__ \n" +
                "\\___/_/  \\_, /___/\\__/\\_,_/_/_/ |_\\___/ \n" +
                "        /___/                           ");

        getServer().getConsoleSender().sendMessage("------------------");
        getServer().getConsoleSender().sendMessage("     CrystalAC    ");
        getServer().getConsoleSender().sendMessage("   By 4Remi#8652  ");
        getServer().getConsoleSender().sendMessage("------------------");

        if (!new License(getConfig().getString("License"), "http://www.mystraclient.store/verify.php", this).setConsoleLog(License.LogType.NORMAL).register())
            return;

        loadHandlers();
        loadManagers();
        loadListeners();
        loadDatabase();
        loadMetrics();
        loadModules();
        loadCommands();
        loadGUI();

        getServer().getConsoleSender().sendMessage("------------------");
        getServer().getConsoleSender().sendMessage("     CrystalAC    ");
        getServer().getConsoleSender().sendMessage("   By 4Remi#8652  ");
        getServer().getConsoleSender().sendMessage("------------------");
        getServer().getConsoleSender().sendMessage(CC.BLUE + "CrystalAC has been enabled ");
    }

    @Override
    public void onDisable() {
        getServer().getConsoleSender().sendMessage(CC.RED + "CrystalAC has been disabled.");
        getServer().getConsoleSender().sendMessage(CC.RED + "AntiCheat won't boot back up if this is a reload.");
    }

    private void createConfig() {
        this.mainFileConfig = new FileConfig(this, "config.yml");
        this.checkFileConfig = new FileConfig(this, "checks.yml");
    }

    private void loadConfig() {
        ConfigurationManager.getConfigOptions();
    }

    private void loadHandlers() {

        if (isClass("pt.foxspigot.jar.FoxSpigot")) {
            INSTANCE.addPacketHandler(new CustomPacketHandler(this));
            INSTANCE.addMovementHandler(new CustomMovementHandler(this));
            getServer().getConsoleSender().sendMessage(CC.GREEN + "FoxSpigot has been successfully loaded.");
        } else {

            getServer().getConsoleSender().sendMessage(CC.RED + CC.BOLD + "FoxSpigot has not been found.");
            this.getServer().getPluginManager().disablePlugin(this);
        }
    }

    private void loadManagers() {
        getServer().getConsoleSender().sendMessage(CC.GREEN + "Loading Managers...");
        CrystalAC.plugin = this;
        this.playerDataManager = new PlayerDataManager();
        this.clientManager = new ClientManager();
    }

    private void loadListeners() {
        getServer().getConsoleSender().sendMessage(CC.GREEN + "Loading Listeners...");

        this.getServer().getMessenger().registerIncomingPluginChannel(this, "BungeeCord", new BungeeListener(this));
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");

        this.getServer().getPluginManager().registerEvents(new PlayerListener(), this);
        this.getServer().getPluginManager().registerEvents(new ModListListener(), this);
    }

    private void loadDatabase() {

        db = DBUtil.getDBType();
        getServer().getConsoleSender().sendMessage(CC.GREEN + "Loading Database: " + db + "...");

        switch (db) {
            case "mongo": {
                getServer().getConsoleSender().sendMessage(CC.GREEN + "Downloading & Injecting MongoDB...");

                MongoDownloader.init();

                this.mongo = new MongoDB();
                break;
            }

            case "sqlite": {
                getServer().getConsoleSender().sendMessage(CC.GREEN + "Downloading & Injecting SQLite...");

                SQLiteDownloader.init();

                SQLite.init();
                break;
            }

            case "mysql": {
                MySQL.init();
                break;
            }

            case "none": {
                getServer().getConsoleSender().sendMessage(CC.RED + "No Database");
                break;
            }
        }


    }

    private void loadMetrics() {
        if (!ConfigurationManager.isMetricEnable()) {
            return;
        }

        getServer().getConsoleSender().sendMessage(CC.GREEN + "Loading Metrics...");
        bStats.enableMetrics();
    }

    public void loadModules() {
        getServer().getConsoleSender().sendMessage(CC.GREEN + "Loading Modules...");
        ConfigurationManager.getChecks();

        this.receivingAlerts = new HashSet<>();
        this.receivingVerboseAlerts = new HashSet<>();
        this.disabledChecks = new HashSet<>();

        this.getServer().getScheduler().runTaskTimer(this, new InsertLogsTask(), 20L * 60L * 5L, 20L * 60L * 5L);

        getServer().getConsoleSender().sendMessage(CC.GREEN + "Loaded " + ConfigurationManager.getTotalChecks() + " modules!");
    }

    public void loadCommands() {
        getServer().getConsoleSender().sendMessage(CC.GREEN + "Loading Commands...");

        List<String> banBroadcast;
        banBroadcast = new ArrayList<>();
        banBroadcast.add("crystalac");

        for (String value : banBroadcast) {
            this.getCommand(value).setExecutor(new ACCommand());
        }
        banBroadcast.clear();

        this.getCommand("alerts").setExecutor(new AlertsCommands());
        this.getCommand("bypass").setExecutor(new BypassCommand());
        this.getCommand("anticheat").setExecutor(new ACCommand());
        this.getCommand("acban").setExecutor(new ACBanCommand());
        this.getCommand("watch").setExecutor(new WatchCommand());
        this.getCommand("exempt").setExecutor(new ExemptCommand());
        //this.getCommand("banwave").setExecutor(new BanWaveCommand());

    }

    /*private void getPlugins() {
        if (this.getServer().getPluginManager().getPlugin("ViaVersion") != null) {
            CrystalAC.isUsingViaVersion = true;
            bulldog.isUsingProtocolSupport = false;
        } else if (this.getServer().getPluginManager().getPlugin("ProtocolSupport") != null) {
            CrystalAC.isUsingProtocolSupport = true;
            CrystalAC.isUsingViaVersion = false;
        }
        if (this.getServer().getPluginManager().getPlugin("ViaRewind") != null || this.getServer().getPluginManager().getPlugin("ViaBackwards") != null) {
            CrystalAC.isUsingViaRewind = true;
        }
    }*/


    private void loadGUI() {
        getServer().getConsoleSender().sendMessage(CC.GREEN + "Loading GUI...");
        this.mainGUI = new MainGUI(this);
    }

    public PlayerDataManager getPlayerDataManager() {
        return this.playerDataManager;
    }

    public ClientManager getClientManager() {
        return this.clientManager;

    }

    public void registerListener(Listener listener) {
        this.getServer().getPluginManager().registerEvents(listener, this);
    }

    public boolean isAntiCheatEnabled() {
        return MinecraftServer.getServer().tps1.getAverage() > 19.0 && MinecraftServer.az() + 100L > System.currentTimeMillis();
    }

    public boolean canAlert(Player player, boolean verbose) {
        if (!verbose) {
            return this.receivingAlerts.contains(player.getUniqueId());
        } else {
            return this.receivingVerboseAlerts.contains(player.getUniqueId());
        }
    }

    public void toggleAlerts(Player player, boolean verbose) {

        if (!verbose) {

            if (this.receivingAlerts.contains(player.getUniqueId())) {
                this.receivingAlerts.remove(player.getUniqueId());
                player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "You disabled normal alerts."));
            } else {
                this.receivingAlerts.add(player.getUniqueId());
                player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &a" + "You enabled normal alerts."));
                this.receivingVerboseAlerts.remove(player.getUniqueId());
            }

        } else {

            if (this.receivingVerboseAlerts.contains(player.getUniqueId())) {
                this.receivingVerboseAlerts.remove(player.getUniqueId());
                player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "You disabled verbose alerts."));
            } else {
                this.receivingVerboseAlerts.add(player.getUniqueId());

                this.receivingAlerts.remove(player.getUniqueId());
                player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &a" + "You enabled verbose alerts."));
            }


        }
    }

    public boolean isClass(String className) {
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}

