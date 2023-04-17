package me.levansj01.verus.storage.config;

import java.io.*;

import org.bukkit.configuration.file.*;
import me.levansj01.verus.storage.database.pool.*;
import me.levansj01.verus.util.mongodb.*;
import me.levansj01.launcher.*;

import java.util.function.*;
import java.util.stream.*;

import me.levansj01.verus.type.*;
import me.levansj01.verus.compat.*;

import java.util.logging.*;
import java.util.concurrent.*;
import java.util.*;

import org.bukkit.*;

public class VerusConfiguration {
    private String alertClickCommand;
    private String sqlPassword;
    private boolean butterflyBans;
    private int sqlPool;
    private String discordLogsUrl;
    private boolean announcement;
    private boolean bypassEnabled;
    private boolean checkPersistence;
    private int persistenceSeconds;
    private int sqlPort;
    private int mongoPort;
    private boolean discordLogs;
    private String discordBansUrl;
    private boolean databaseCheckPersistence;
    private boolean fixSlimeBlocks;
    private File file;
    private int logsCleanupDays;
    private YamlConfiguration checkConfiguration;
    private boolean compression;
    private YamlConfiguration configuration;
    private boolean slimePushFix;
    private String sqlUsername;
    private String geyserPrefix;
    private int sqlPushSeconds;
    private boolean schemBans;
    private boolean vanishPing;
    private List<String> banCommands;
    private String anticheatName;
    private boolean pingCommand;
    private boolean discordBans;
    private boolean ignoreLag;
    private int maxClicksPerSecond;
    private boolean hardDisable;
    private String recentlogsMessage;
    private String mongoHost;
    private boolean schem;
    private boolean scrambleBanId;
    private String sqlDatabase;
    private String sqlHost;
    private String sqlDatabaseType;
    private String messagingChannel;
    private ConnectionType sqlConnectionType;
    private boolean unloadedChunkFix;
    private String bypassPermission;
    private boolean heavyChecks;
    private String secretKeyPath;
    private String announcementMessage;
    private boolean moreTransactions;
    private boolean sendStats;
    private boolean butterflyAlerts;
    private int pingTimeout;
    private boolean pingKickCheck;
    private boolean blockLog;
    private boolean geyser;
    private String mongoDatabase;
    private boolean mongoAuthEnabled;
    private boolean sqlEnabled;
    private boolean bypassAlerts;
    private boolean cleanup;
    private File checkFile;
    private MongoCredential mongoCredential;
    private String checkDatabaseName;
    private String alertMessage;
    private String[] banMessage;
    private boolean banAnnouncement;
    private boolean alertClick;
    private boolean directMemory;
    private int heavyTicks;
    private int bansCleanupDays;
    private boolean mongoEnabled;
    private boolean heavyPullback;
    private String alertCertainty;
    private boolean randomBanId;
    private int persistenceMins;
    private boolean pingKick;
    private boolean bungeeBans;
    private String checkCommandsDatabaseName;
    private boolean persistence;
    private String alertClickHover;
    private YamlConfiguration banConfiguration;
    private boolean autoBan;

    public void setRecentlogsMessage(final String recentlogsMessage) {
        this.recentlogsMessage = recentlogsMessage;
    }

    public void setVanishPing(final boolean vanishPing) {
        this.vanishPing = vanishPing;
    }

    public boolean isDiscordLogs() {
        return this.discordLogs;
    }

    public void readConfig() {
        final Logger logger = VerusLauncher.getPlugin().getLogger();
        this.anticheatName = this.configuration.getString("verus.name", "&9&lVerus");
        this.alertCertainty = this.configuration.getString("verus.alerts.certainty", "&7failed");
        this.alertMessage = this.configuration.getString("verus.alerts.message", "{name} &8> &f{player} &7{certainty} &f{cheat} &7VL[&9{vl}&7]");
        this.alertClick = this.configuration.getBoolean("verus.alerts.click.enabled", this.alertClick);
        this.alertClickCommand = this.configuration.getString("verus.alerts.click.command", "/tp {player}");
        this.alertClickHover = this.configuration.getString("verus.alerts.click.hover", "Click to teleport to {player}.");
        final YamlConfiguration configuration = this.configuration;
        final String s = "verus.bans.commands";
        final String[] array = new String[1];
        array[0] = "ban {player} &9&lVerus &8> &fCheating";
        this.banCommands = this.readList(configuration.get(s, (Object) array));
        this.banCommands = (List) this.banCommands.stream().map(VerusConfiguration::lambda.readConfig .0).
        collect(Collectors.toList());
        final Object value = this.configuration.get("verus.bans.message", (Object) this.banMessage);
        if (value instanceof String) {
            final String[] banMessage = new String[1];
            banMessage[0] = (String) value;
            this.banMessage = banMessage;

        } else if (value instanceof List) {
            this.banMessage = (String[]) ((List) value).toArray(new String[0]);

        } else {
            this.banMessage = (String[]) value;
        }
        this.autoBan = this.configuration.getBoolean("verus.bans.enabled", this.autoBan);
        this.banAnnouncement = this.configuration.getBoolean("verus.bans.announce", this.banAnnouncement);
        this.randomBanId = this.configuration.getBoolean("verus.bans.randomize", this.randomBanId);
        this.scrambleBanId = this.configuration.getBoolean("verus.bans.scramble", this.scrambleBanId);
        this.recentlogsMessage = this.configuration.getString("verus.recentlogs.message", "{time} {name} &7Failed &f{type} Type {subType} &7VL: &f{vl} &7P: &f{ping}");
        this.geyser = this.configuration.getBoolean("verus.geyser.enabled", this.geyser);
        this.geyserPrefix = this.configuration.getString("verus.geyser.prefix", this.geyserPrefix);
        this.bungeeBans = this.configuration.getBoolean("verus.bungee.bans", this.bungeeBans);
        this.secretKeyPath = this.configuration.getString("verus.bungee.secretKeyPath", this.secretKeyPath);
        this.messagingChannel = this.configuration.getString("verus.bungee.messagingChannel", this.messagingChannel);
        this.bypassEnabled = this.configuration.getBoolean("verus.permissions.bypass.enabled", this.bypassEnabled);
        this.bypassPermission = this.configuration.getString("verus.permissions.bypass.permission", this.bypassPermission);
        this.bypassAlerts = this.configuration.getBoolean("verus.permissions.bypass.alerts", this.bypassAlerts);
        this.persistence = this.configuration.getBoolean("verus.checks.persistence.enabled", this.persistence);
        this.persistenceMins = this.configuration.getInt("verus.checks.persistence.mins", this.persistenceMins);
        this.persistenceSeconds = this.configuration.getInt("verus.checks.persistence.seconds", this.persistenceSeconds);
        this.schemBans = this.configuration.getBoolean("verus.checks.schemprinter.bans", this.schemBans);
        this.schem = this.configuration.getBoolean("verus.checks.schemprinter.enabled", this.schem);
        this.butterflyBans = this.configuration.getBoolean("verus.checks.butterflyclicking.bans", this.butterflyBans);
        this.butterflyAlerts = this.configuration.getBoolean("verus.checks.butterflyclicking.enabled", this.butterflyAlerts);
        this.heavyChecks = this.configuration.getBoolean("verus.checks.heavy.enabled", this.heavyChecks);
        this.heavyTicks = this.configuration.getInt("verus.checks.heavy.ticks", this.heavyTicks);
        this.heavyPullback = this.configuration.getBoolean("verus.checks.heavy.pullback", this.heavyPullback);
        this.hardDisable = this.configuration.getBoolean("verus.checks.hardDisable.enabled");
        this.pingKick = this.configuration.getBoolean("verus.checks.pingKick.enabled", this.pingKick);
        this.pingKickCheck = this.configuration.getBoolean("verus.checks.pingKick.combat", this.pingKickCheck);
        this.pingTimeout = this.configuration.getInt("verus.checks.pingKick.timeout", this.pingTimeout);
        this.pingTimeout = Math.max(15, Math.min(45, this.pingTimeout));
        this.slimePushFix = this.configuration.getBoolean("verus.checks.speed.slimePushFix", this.slimePushFix);
        int moreTransactions;
        if (VerusTypeLoader.isEnterprise() || this.configuration.getBoolean("verus.checks.reach.moreTransactions", this.moreTransactions)) {
            moreTransactions = (1);

        } else {
            moreTransactions = (0);
        }
        this.moreTransactions = (moreTransactions != 0);
        this.ignoreLag = this.configuration.getBoolean("verus.checks.reach.ignoreLag", this.ignoreLag);
        this.unloadedChunkFix = this.configuration.getBoolean("verus.checks.fly.unloadedChunkFix", this.unloadedChunkFix);
        this.fixSlimeBlocks = this.configuration.getBoolean("verus.checks.fly.fixSlimeBlocks", this.fixSlimeBlocks);
        this.maxClicksPerSecond = this.configuration.getInt("verus.checks.autoclicker.maxcps", this.maxClicksPerSecond);
        if (!this.schemBans || !this.schem) {
            logger.warning("Disabling Schematica Printer checks may also allow scaffold and other cheats to bypass");
        }
        this.pingCommand = this.configuration.getBoolean("verus.commands.ping.enabled", this.pingCommand);
        this.vanishPing = this.configuration.getBoolean("verus.commands.ping.vanish", this.vanishPing);
        this.cleanup = this.configuration.getBoolean("verus.database.cleanup.enabled", this.cleanup);
        this.logsCleanupDays = this.configuration.getInt("verus.database.cleanup.logs.days", this.logsCleanupDays);
        this.bansCleanupDays = this.configuration.getInt("verus.database.cleanup.bans.days", this.bansCleanupDays);
        this.mongoEnabled = this.configuration.getBoolean("verus.mongo.enabled", this.mongoEnabled);
        this.mongoHost = this.configuration.getString("verus.mongo.host", this.mongoHost);
        this.mongoPort = this.configuration.getInt("verus.mongo.port", this.mongoPort);
        this.mongoDatabase = this.configuration.getString("verus.mongo.database", this.mongoDatabase);
        this.mongoAuthEnabled = this.configuration.getBoolean("verus.mongo.auth.enabled", this.mongoAuthEnabled);
        if (this.mongoAuthEnabled) {
            this.mongoCredential = MongoCredential.createCredential(this.configuration.getString("verus.mongo.auth.username", "verus"), this.configuration.getString("verus.mongo.auth.database", this.mongoDatabase), this.configuration.getString("verus.mongo.auth.password", "").toCharArray());
        }
        this.sqlEnabled = this.configuration.getBoolean("verus.sql.enabled", this.sqlEnabled);
        this.sqlHost = this.configuration.getString("verus.sql.host", this.sqlHost);
        this.sqlPushSeconds = this.configuration.getInt("verus.sql.push.seconds", this.sqlPushSeconds);
        this.sqlPort = this.configuration.getInt("verus.sql.port", this.sqlPort);
        this.sqlPool = this.configuration.getInt("verus.sql.pool", this.sqlPool);
        this.sqlDatabase = this.configuration.getString("verus.sql.database", this.sqlDatabase);
        this.sqlDatabaseType = this.configuration.getString("verus.sql.type", this.sqlDatabaseType);
        this.sqlUsername = this.configuration.getString("verus.sql.auth.username", this.sqlUsername);
        this.sqlPassword = this.configuration.getString("verus.sql.auth.password", this.sqlPassword);
        this.discordLogs = this.configuration.getBoolean("verus.discord.logs.enabled", this.discordLogs);
        this.discordLogsUrl = this.configuration.getString("verus.discord.logs.url", this.discordLogsUrl);
        this.discordBans = this.configuration.getBoolean("verus.discord.bans.enabled", this.discordBans);
        this.discordBansUrl = this.configuration.getString("verus.discord.bans.url", this.discordBansUrl);
        if (this.heavyPullback && NMSManager.getInstance().getServerVersion() != ServerVersion.v1_8_R3) {
            this.heavyPullback = ((0) != 0);
            logger.warning("Heavy pullback is only available on 1.8.8 server version");
        }
        this.sqlConnectionType = ConnectionType.valueOf(this.sqlDatabaseType.toUpperCase());

        if (!this.moreTransactions) {
            if (this.ignoreLag) {
                logger.severe("ignoreLag is enabled but moreTransactions is disabled, this may cause reach false positives.");

            } else {
                logger.info("moreTransactions is disabled, sending more transactions may improve the accuracy of reach checks");
            }
        }
        if (!this.heavyChecks) {
            logger.warning("Heavy checks are not enabled, it is recommended that you enable this if you wish to block spoof-ground bypasses");
        }
        if (!this.pingKick && !this.pingKickCheck) {
            logger.warning("Ping kick/combat is not enabled, it is recommended that you enable it to avoid ping spoof bypasses");
        }
        if (this.mongoEnabled || this.sqlEnabled) {
            if (!this.persistence) {
                logger.warning("Check persistence is not enabled, it is recommended that you enable it to avoid relog bypasses");

            }
        } else if (this.persistence) {
            logger.info("You must be connected to a database in order to use check data persistence");
            this.persistence = ((0) != 0);
        }
    }

    public void setIgnoreLag(final boolean ignoreLag) {
        this.ignoreLag = ignoreLag;
    }

    public void setSqlDatabaseType(final String sqlDatabaseType) {
        this.sqlDatabaseType = sqlDatabaseType;
    }

    public void setFixSlimeBlocks(final boolean fixSlimeBlocks) {
        this.fixSlimeBlocks = fixSlimeBlocks;
    }

    public List<String> getBanCommands() {
        return this.banCommands;
    }

    public void setSqlConnectionType(final ConnectionType sqlConnectionType) {
        this.sqlConnectionType = sqlConnectionType;
    }

    public void setBypassPermission(final String bypassPermission) {
        this.bypassPermission = bypassPermission;
    }

    public boolean isBungeeBans() {
        return this.bungeeBans;
    }

    public void saveConfig(final YamlConfiguration yamlConfiguration, final File file) {
        yamlConfiguration.save(file);

    }

    public void setUnloadedChunkFix(final boolean unloadedChunkFix) {
        this.unloadedChunkFix = unloadedChunkFix;
    }

    public String getMongoDatabase() {
        return this.mongoDatabase;
    }

    public int getSqlPool() {
        return this.sqlPool;
    }

    public void setSqlPassword(final String sqlPassword) {
        this.sqlPassword = sqlPassword;
    }

    public MongoCredential getMongoCredential() {
        return this.mongoCredential;
    }

    public String getAnnouncementMessage() {
        return this.announcementMessage;
    }

    public int getMaxClicksPerSecond() {
        return this.maxClicksPerSecond;
    }

    public long getPersistenceMillis() {
        return TimeUnit.MINUTES.toMillis(this.persistenceMins) + TimeUnit.SECONDS.toMillis(this.persistenceSeconds);
    }

    public String[] getBanMessage() {
        return this.banMessage;
    }

    public void setMongoHost(final String mongoHost) {
        this.mongoHost = mongoHost;
    }

    public void setMongoCredential(final MongoCredential mongoCredential) {
        this.mongoCredential = mongoCredential;
    }

    public void setButterflyBans(final boolean butterflyBans) {
        this.butterflyBans = butterflyBans;
    }

    public void setAnnouncementMessage(final String announcementMessage) {
        this.announcementMessage = announcementMessage;
    }

    public int getMongoPort() {
        return this.mongoPort;
    }

    public String getSqlUsername() {
        return this.sqlUsername;
    }

    public void setMongoEnabled(final boolean mongoEnabled) {
        this.mongoEnabled = mongoEnabled;
    }

    public void setFile(final File file) {
        this.file = file;
    }

    public void setSecretKeyPath(final String secretKeyPath) {
        this.secretKeyPath = secretKeyPath;
    }

    public boolean isHeavyChecks() {
        return this.heavyChecks;
    }

    public void setHeavyPullback(final boolean heavyPullback) {
        this.heavyPullback = heavyPullback;
    }

    public int getSqlPort() {
        return this.sqlPort;
    }

    public boolean isRandomBanId() {
        return this.randomBanId;
    }

    public void setConfiguration(final YamlConfiguration configuration) {
        this.configuration = configuration;
    }

    public void loadConfig(final YamlConfiguration yamlConfiguration, final File file) {
        yamlConfiguration.load(file);

    }

    public int getHeavyTicks() {
        return this.heavyTicks;
    }

    public String getAlertClickHover() {
        return this.alertClickHover;
    }

    public void setDiscordBans(final boolean discordBans) {
        this.discordBans = discordBans;
    }

    public boolean isDiscordBans() {
        return this.discordBans;
    }

    public void setMoreTransactions(final boolean moreTransactions) {
        this.moreTransactions = moreTransactions;
    }

    public boolean isBypassEnabled() {
        return this.bypassEnabled;
    }

    public void setCheckFile(final File checkFile) {
        this.checkFile = checkFile;
    }

    public void setBypassAlerts(final boolean bypassAlerts) {
        this.bypassAlerts = bypassAlerts;
    }

    public String getSqlPassword() {
        return this.sqlPassword;
    }

    public void setHeavyTicks(final int heavyTicks) {
        this.heavyTicks = heavyTicks;
    }

    public boolean isMongoEnabled() {
        return this.mongoEnabled;
    }

    public boolean isGeyser() {
        return this.geyser;
    }

    public void setCheckDatabaseName(final String checkDatabaseName) {
        this.checkDatabaseName = checkDatabaseName;
    }

    public void setDatabaseCheckPersistence(final boolean databaseCheckPersistence) {
        this.databaseCheckPersistence = databaseCheckPersistence;
    }

    public void setSchem(final boolean schem) {
        this.schem = schem;
    }

    public boolean isPingKickCheck() {
        return this.pingKickCheck;
    }

    public boolean isUnloadedChunkFix() {
        return this.unloadedChunkFix;
    }

    public void setBlockLog(final boolean blockLog) {
        this.blockLog = blockLog;
    }

    public String getSqlDatabaseType() {
        return this.sqlDatabaseType;
    }

    public boolean isAnnouncement() {
        return this.announcement;
    }

    public String getAlertMessage() {
        return this.alertMessage;
    }

    public void setCheckPersistence(final boolean checkPersistence) {
        this.checkPersistence = checkPersistence;
    }

    public boolean isCleanup() {
        return this.cleanup;
    }

    public void setMongoDatabase(final String mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public void setScrambleBanId(final boolean scrambleBanId) {
        this.scrambleBanId = scrambleBanId;
    }

    public boolean isPingCommand() {
        return this.pingCommand;
    }

    public String getSqlDatabase() {
        return this.sqlDatabase;
    }

    public YamlConfiguration getCheckConfiguration() {
        return this.checkConfiguration;
    }

    public int getBansCleanupDays() {
        return this.bansCleanupDays;
    }

    public boolean isHeavyPullback() {
        return this.heavyPullback;
    }

    public void setCheckConfiguration(final YamlConfiguration checkConfiguration) {
        this.checkConfiguration = checkConfiguration;
    }

    public void setGeyserPrefix(final String geyserPrefix) {
        this.geyserPrefix = geyserPrefix;
    }

    public boolean isButterflyBans() {
        return this.butterflyBans;
    }

    public boolean isBanAnnouncement() {
        return this.banAnnouncement;
    }

    public int getPingTimeout() {
        return this.pingTimeout;
    }

    public void setPersistence(final boolean persistence) {
        this.persistence = persistence;
    }

    public void setDiscordBansUrl(final String discordBansUrl) {
        this.discordBansUrl = discordBansUrl;
    }

    public void setAlertClickCommand(final String alertClickCommand) {
        this.alertClickCommand = alertClickCommand;
    }

    public boolean isPingKick() {
        return this.pingKick;
    }

    public void setPingKickCheck(final boolean pingKickCheck) {
        this.pingKickCheck = pingKickCheck;
    }

    public boolean isBlockLog() {
        return this.blockLog;
    }

    public void setAlertClick(final boolean alertClick) {
        this.alertClick = alertClick;
    }

    public void setPingTimeout(final int pingTimeout) {
        this.pingTimeout = pingTimeout;
    }

    public File getFile() {
        return this.file;
    }

    public boolean isMongoAuthEnabled() {
        return this.mongoAuthEnabled;
    }

    public YamlConfiguration getConfiguration() {
        return this.configuration;
    }

    public void setCompression(final boolean compression) {
        this.compression = compression;
    }

    public void setBypassEnabled(final boolean bypassEnabled) {
        this.bypassEnabled = bypassEnabled;
    }

    public boolean isVanishPing() {
        return this.vanishPing;
    }

    public boolean isSqlEnabled() {
        return this.sqlEnabled;
    }

    public void setSlimePushFix(final boolean slimePushFix) {
        this.slimePushFix = slimePushFix;
    }

    public void enable() {
        this.setupConfig();
        this.readConfig();
    }

    public String getAlertClickCommand() {
        return this.alertClickCommand;
    }

    public String getCheckDatabaseName() {
        return this.checkDatabaseName;
    }

    public ConnectionType getSqlConnectionType() {
        return this.sqlConnectionType;
    }

    public String getSqlHost() {
        return this.sqlHost;
    }

    public void setAlertCertainty(final String alertCertainty) {
        this.alertCertainty = alertCertainty;
    }

    public boolean isAutoBan() {
        return this.autoBan;
    }

    public void setAnnouncement(final boolean announcement) {
        this.announcement = announcement;
    }

    public boolean isAlertClick() {
        return this.alertClick;
    }

    public boolean isCheckPersistence() {
        return this.checkPersistence;
    }

    public void setPingKick(final boolean pingKick) {
        this.pingKick = pingKick;
    }

    public void setMongoPort(final int mongoPort) {
        this.mongoPort = mongoPort;
    }

    public void setHeavyChecks(final boolean heavyChecks) {
        this.heavyChecks = heavyChecks;
    }

    public String getDiscordBansUrl() {
        return this.discordBansUrl;
    }

    public String getGeyserPrefix() {
        return this.geyserPrefix;
    }

    public void setSqlPort(final int sqlPort) {
        this.sqlPort = sqlPort;
    }

    public boolean isScrambleBanId() {
        return this.scrambleBanId;
    }

    public String getAlertCertainty() {
        return this.alertCertainty;
    }

    public boolean isCompression() {
        return this.compression;
    }

    public String getRecentlogsMessage() {
        return this.recentlogsMessage;
    }

    public boolean isSchem() {
        return this.schem;
    }

    public String getAnticheatName() {
        return this.anticheatName;
    }

    public boolean isDirectMemory() {
        return this.directMemory;
    }

    public boolean isSendStats() {
        return this.sendStats;
    }

    public void setBanAnnouncement(final boolean banAnnouncement) {
        this.banAnnouncement = banAnnouncement;
    }

    public int getPersistenceMins() {
        return this.persistenceMins;
    }

    public void setSqlPushSeconds(final int sqlPushSeconds) {
        this.sqlPushSeconds = sqlPushSeconds;
    }

    public VerusConfiguration() {
        this.configuration = new YamlConfiguration();
        this.banConfiguration = new YamlConfiguration();
        this.checkConfiguration = new YamlConfiguration();
        final String[] banMessage = new String[1];
        banMessage[0] = "&9&lVerus &8> &f{player} &7has been removed from the network";
        this.banMessage = banMessage;
        this.bypassPermission = "verus.bypass";
        this.checkDatabaseName = "checks";
        this.checkCommandsDatabaseName = "checkCommands";
        this.geyserPrefix = "*";
        this.alertClick = true;
        this.sendStats = true;
        this.autoBan = true;
        this.schemBans = true;
        this.schem = true;
        this.butterflyAlerts = true;
        this.heavyChecks = true;
        this.hardDisable = true;
        this.bypassAlerts = true;
        this.persistence = true;
        this.cleanup = true;
        this.pingKick = true;
        this.pingKickCheck = true;
        this.moreTransactions = VerusTypeLoader.isEnterprise();
        this.ignoreLag = true;
        this.unloadedChunkFix = true;
        int randomBanId;
        if (!VerusTypeLoader.isEnterprise()) {
            randomBanId = (1);

        } else {
            randomBanId = (0);
        }
        this.randomBanId = (randomBanId = true);
        this.secretKeyPath = "plugins" + File.separator + "Verus" + File.separator + "secretKey";
        String messagingChannel;
        if (NMSManager.getInstance().getServerVersion().afterEq(ServerVersion.v1_13_R2)) {
            messagingChannel = "bungeecord:main";

        } else {
            messagingChannel = "BungeeCord";
        }
        this.messagingChannel = messagingChannel;
        this.persistenceMins = (60);
        this.logsCleanupDays = (90);
        this.bansCleanupDays = (730);
        this.pingTimeout = (20);
        this.heavyTicks = (5);
        this.maxClicksPerSecond = (20);
        this.mongoHost = "localhost";
        this.mongoDatabase = "Verus";
        this.mongoPort = (27017);
        this.sqlUsername = "verus";
        this.sqlPassword = "";
        this.sqlHost = "localhost";
        this.sqlDatabase = "Verus";
        this.sqlDatabaseType = ConnectionType.MYSQL.name();
        this.sqlPushSeconds = (10);
        this.sqlPort = (3306);
        this.sqlPool = (4);
        this.discordLogsUrl = "";
        this.discordBansUrl = "";
    }

    public void setAlertMessage(final String alertMessage) {
        this.alertMessage = alertMessage;
    }

    public void setPersistenceSeconds(final int persistenceSeconds) {
        this.persistenceSeconds = persistenceSeconds;
    }

    public int getSqlPushSeconds() {
        return this.sqlPushSeconds;
    }

    public boolean isPersistence() {
        return this.persistence;
    }

    public void setCleanup(final boolean cleanup) {
        this.cleanup = cleanup;
    }

    public boolean isFixSlimeBlocks() {
        return this.fixSlimeBlocks;
    }

    public void setBanConfiguration(final YamlConfiguration banConfiguration) {
        this.banConfiguration = banConfiguration;
    }

    public void setSqlEnabled(final boolean sqlEnabled) {
        this.sqlEnabled = sqlEnabled;
    }

    public void setMaxClicksPerSecond(final int maxClicksPerSecond) {
        this.maxClicksPerSecond = maxClicksPerSecond;
    }

    public void setDiscordLogs(final boolean discordLogs) {
        this.discordLogs = discordLogs;
    }

    public String getBypassPermission() {
        return this.bypassPermission;
    }

    public boolean isDatabaseCheckPersistence() {
        return this.databaseCheckPersistence;
    }

    public void setRandomBanId(final boolean randomBanId) {
        this.randomBanId = randomBanId;
    }

    public List readList(final Object o) {
        if (o instanceof List) {
            return (List) o;
        }
        return Arrays.asList((Object[]) o);
    }

    public void setSqlHost(final String sqlHost) {
        this.sqlHost = sqlHost;
    }

    private void setupConfig() {
        final VerusLauncher plugin = VerusLauncher.getPlugin();
        this.file = new File(plugin.getDataFolder(), "verus.yml");
        this.checkFile = new File(plugin.getDataFolder(), "checks.yml");
        this.loadConfig(this.configuration, this.file);
        this.loadConfig(this.checkConfiguration, this.checkFile);
        this.checkConfiguration.options().copyHeader((boolean) ((1) = true)).header("Please do not edit this configuration, please use /verus gui to configure checks");
        this.configuration.options().copyDefaults((boolean) ((1) = true)).copyHeader((boolean) ((1) = true)).header("Verus - Unauthorized distribution of this program could lead to legal prosecution\n\n-------------------------------------------------------------------------------------\nname - Display name of anti-cheat in alert messages\nsendStats - Controls whether stats are sent to our session server\n\nalerts certainty - Placeholder in alert message\nalerts message - Alert message\nalerts click enabled - Whether alerts should be clickable\nalerts click command - Command executed when alert is clicked\nalerts click hover - Message shown when alerts are hovered over\n\nbans commands - Commands executed when a player is banned\nbans message - Ban announcement message which is broadcast to the server\nbans enabled - Whether bans are enabled\nbans announce - Whether bans should be announced and broadcast to the server\nbans randomize - Generate a random Cheat-ID e.g Cheat-5A\nbans scramble -         Generate a scrambled Cheat-ID e.g Cheat-J7Dq\n\nrecentlogs message - Message format of /recentlogs command\n\ngeyser enable - Whether Verus should ignore players with the geyser prefix\ngeyser prefix - The player name prefix which Verus should ignore players with\n\nbungee bans - If you have VerusLink installed on BungeeCord, you may enable this so that the ban commands are executed on BungeeCord instead of the server\nbungee secretKeyPath - Path of secret key used in VerusLink\nbungee messagingChannel - Plugin messaging channel used by VerusLink\n\npermissions bypass enabled - Whether the bypass permission (which stops a player being banned) should be enabled\npermissions bypass permission - Permission node of bypass permission\npermissions bypass alerts - Whether bypassing players should set off alerts\n\ncommands ping enabled - Whether the /ping command should be enabled and registered\ncommands ping vanish - If enabled, players will not be able to check ping for vanished players\n\nchecks persiste        nce enabled - Whether check persistence should be enabled, this means that VLs are saved so when a player relogs or switches servers their VLs are preserved\nchecks persistence mins - How many minutes should violation data be saved for \nchecks persistence seconds - How many seconds should violation data be saved for \n\nchecks schemprinter bans - Whether checks which are flagged by schematica printer should ban\nchecks schemprinter alerts - Whether checks which are flagged by schematica printer should alert\n\nchecks butterflyclicking bans - Whether checks which can be flagged by butterfly clicking should ban\nchecks butterflyclicking alerts - Whether checks which can be flagged by butterfly clicking should alert\n\nchecks heavy enabled - Whether checks which can be more performance heavy should be enabled\nchecks heavy ticks - How often in client ticks (50ms) should the server check the player for ground spoof in Fly 4A\n\nchecks hardDisable enabled - Whether checks which have both alerts and bans d        isabled should not be loaded in when a player joins\n\nchecks pingKick enabled - Whether players with incredibly high ping should be kicked from the server, this prevents ping spoofers from disabling parts of the anticheat\nchecks pingKick combat - Whether BadPackets Type G7 should be enabled, so when a player attacks with impossibly high ping they are flagged\nchecks pingKick timeout - The time in seconds (1s = 1000ms) after which a player should be kicked\n\nchecks speed modifier item - Whether speed checks should check the players current item for NMS modifiers which affect player speed\nchecks speed modifier armor - Whether speed checks should check the players armor for NMS modifiers\nchecks speed slimePushFix - Whether players that have been pushed by a slime should be ignored by Speed A, if disabled falses could be caused\n\nchecks reach moreTransactions - Whether more transaction packets should be sent to the client to improve reach accuracy\nchecks reach ignoreLag - Whether players which are         lagging should be ignored, allows for the disabling of some reach checks\n\nchecks fly unloadedChunkFix - Whether players which are moving as if they are in an unloaded chunk should be ignored by Fly Type I, allows for a glide bypass.\nchecks fly fixSlimeBlocks - Whether slime block velocity should be same as default, can create self damage bypasses.\n\nchecks autoclicker maxcps - The amount of cps before AutoClicker X will flag\n\ndatabase cleanup enabled - Whether the logs and bans should expire after a certain amount of time so that the database does not become too large\ndatabase cleanup logs days - After how many days should logs expire\ndatabase cleanup bans days - After how many days should bans expire\n\ndiscord logs enabled - Display logs via discord (May get limited due to the amount of logs)\ndiscord logs url - Webhook URL\ndiscord bans enabled - Display bans via discord\ndiscord bans url - Webhook URL\n-------------------------------------------------------------------------------------\n");
        this.configuration.addDefault("verus.name", (Object) "&9&lVerus");
        this.configuration.addDefault("verus.sendstats", (Object) this.sendStats);
        this.configuration.addDefault("verus.alerts.certainty", (Object) "&7failed");
        this.configuration.addDefault("verus.alerts.message", (Object) "{name} &8> &f{player} &7{certainty} &f{cheat} &7VL[&9{vl}&7]");
        this.configuration.addDefault("verus.alerts.click.enabled", (Object) this.alertClick);
        this.configuration.addDefault("verus.alerts.click.command", (Object) "/tp {player}");
        this.configuration.addDefault("verus.alerts.click.hover", (Object) "&9Click to teleport to {player}.");
        final YamlConfiguration configuration = this.configuration;
        final String s = "verus.bans.commands";
        final String[] array = new String[1];
        array[0] = "ban {player} &9&lVerus &8> &fCheating";
        configuration.addDefault(s, (Object) array);
        this.configuration.addDefault("verus.bans.message", (Object) this.banMessage);
        this.configuration.addDefault("verus.bans.enabled", (Object) (boolean) ((1) = true));
        this.configuration.addDefault("verus.bans.announce", (Object) (boolean) ((0) = true));
        this.configuration.addDefault("verus.bans.randomize", (Object) this.randomBanId);
        this.configuration.addDefault("verus.bans.scramble", (Object) this.scrambleBanId);
        this.configuration.addDefault("verus.recentlogs.message", (Object) "{time} {name} &7Failed &f{type} Type {subType} &7VL: &f{vl} &7P: &f{ping}");
        this.configuration.addDefault("verus.geyser.enabled", (Object) this.geyser);
        this.configuration.addDefault("verus.geyser.prefix", (Object) this.geyserPrefix);
        if (this.configuration.contains("verus.badlionapi")) {
            this.configuration.set("verus.badlionapi", (Object) null);
        }
        this.configuration.addDefault("verus.bungee.bans", (Object) this.bungeeBans);
        this.configuration.addDefault("verus.bungee.secretKeyPath", (Object) this.secretKeyPath);
        this.configuration.addDefault("verus.bungee.messagingChannel", (Object) this.messagingChannel);
        this.configuration.addDefault("verus.permissions.bypass.enabled", (Object) this.bypassEnabled);
        this.configuration.addDefault("verus.permissions.bypass.permission", (Object) this.bypassPermission);
        this.configuration.addDefault("verus.permissions.bypass.alerts", (Object) this.bypassAlerts);
        this.configuration.addDefault("verus.commands.ping.enabled", (Object) this.pingCommand);
        this.configuration.addDefault("verus.commands.ping.vanish", (Object) this.vanishPing);
        this.configuration.addDefault("verus.checks.persistence.enabled", (Object) this.persistence);
        this.configuration.addDefault("verus.checks.persistence.mins", (Object) this.persistenceMins);
        this.configuration.addDefault("verus.checks.persistence.seconds", (Object) this.persistenceSeconds);
        this.configuration.addDefault("verus.checks.schemprinter.bans", (Object) this.schemBans);
        this.configuration.addDefault("verus.checks.schemprinter.enabled", (Object) this.schem);
        this.configuration.addDefault("verus.checks.butterflyclicking.bans", (Object) this.butterflyBans);
        this.configuration.addDefault("verus.checks.butterflyclicking.enabled", (Object) this.butterflyAlerts);
        this.configuration.addDefault("verus.checks.heavy.enabled", (Object) this.heavyChecks);
        this.configuration.addDefault("verus.checks.heavy.ticks", (Object) this.heavyTicks);
        this.configuration.addDefault("verus.checks.heavy.pullback", (Object) this.heavyPullback);
        this.configuration.addDefault("verus.checks.hardDisable.enabled", (Object) this.hardDisable);
        this.configuration.addDefault("verus.checks.pingKick.enabled", (Object) this.pingKick);
        this.configuration.addDefault("verus.checks.pingKick.combat", (Object) this.pingKickCheck);
        this.configuration.addDefault("verus.checks.pingKick.timeout", (Object) this.pingTimeout);
        if (this.configuration.contains("verus.checks.speed.itemModifier")) {
            this.configuration.set("verus.checks.speed.itemModifier", (Object) null);
        }
        if (this.configuration.contains("verus.checks.speed.modifier")) {
            this.configuration.set("verus.checks.speed.modifier", (Object) null);
        }
        this.configuration.addDefault("verus.checks.speed.slimePushFix", (Object) this.slimePushFix);
        this.configuration.addDefault("verus.checks.reach.moreTransactions", (Object) this.moreTransactions);
        this.configuration.addDefault("verus.checks.reach.ignoreLag", (Object) this.ignoreLag);
        this.configuration.addDefault("verus.checks.fly.unloadedChunkFix", (Object) this.unloadedChunkFix);
        this.configuration.addDefault("verus.checks.fly.fixSlimeBlocks", (Object) this.fixSlimeBlocks);
        this.configuration.addDefault("verus.checks.autoclicker.maxcps", (Object) this.maxClicksPerSecond);
        this.configuration.addDefault("verus.database.cleanup.enabled", (Object) this.cleanup);
        this.configuration.addDefault("verus.database.cleanup.logs.days", (Object) this.logsCleanupDays);
        this.configuration.addDefault("verus.database.cleanup.bans.days", (Object) this.bansCleanupDays);
        this.configuration.addDefault("verus.mongo.enabled", (Object) this.mongoEnabled);
        this.configuration.addDefault("verus.mongo.host", (Object) this.mongoHost);
        this.configuration.addDefault("verus.mongo.port", (Object) this.mongoPort);
        this.configuration.addDefault("verus.mongo.database", (Object) this.mongoDatabase);
        this.configuration.addDefault("verus.mongo.auth.enabled", (Object) this.mongoAuthEnabled);
        this.configuration.addDefault("verus.mongo.auth.username", (Object) "verus");
        this.configuration.addDefault("verus.mongo.auth.password", (Object) "");
        this.configuration.addDefault("verus.mongo.auth.database", (Object) this.configuration.getString("verus.mongo.database", this.mongoDatabase));
        this.configuration.addDefault("verus.sql.enabled", (Object) this.sqlEnabled);
        this.configuration.addDefault("verus.sql.host", (Object) this.sqlHost);
        this.configuration.addDefault("verus.sql.push.seconds", (Object) this.sqlPushSeconds);
        this.configuration.addDefault("verus.sql.port", (Object) this.sqlPort);
        this.configuration.addDefault("verus.sql.pool", (Object) this.sqlPool);
        this.configuration.addDefault("verus.sql.database", (Object) this.sqlDatabase);
        this.configuration.addDefault("verus.sql.type", (Object) this.sqlDatabaseType);
        this.configuration.addDefault("verus.sql.auth.username", (Object) this.sqlUsername);
        this.configuration.addDefault("verus.sql.auth.password", (Object) this.sqlPassword);
        this.configuration.addDefault("verus.discord.logs.enabled", (Object) this.discordLogs);
        this.configuration.addDefault("verus.discord.logs.url", (Object) this.discordLogsUrl);
        this.configuration.addDefault("verus.discord.bans.enabled", (Object) this.discordBans);
        this.configuration.addDefault("verus.discord.bans.url", (Object) this.discordBansUrl);
        this.saveConfig(this.configuration, this.file);
        this.saveConfig(this.checkConfiguration, this.checkFile);
    }

    public int getLogsCleanupDays() {
        return this.logsCleanupDays;
    }

    public void setDiscordLogsUrl(final String discordLogsUrl) {
        this.discordLogsUrl = discordLogsUrl;
    }

    private static String lambda.readConfig.0(
    final String s)

    {
        return ChatColor.translateAlternateColorCodes((char) (38), s);
    }

    public boolean isBypassAlerts() {
        return this.bypassAlerts;
    }

    public boolean isMoreTransactions() {
        return this.moreTransactions;
    }

    public String getDiscordLogsUrl() {
        return this.discordLogsUrl;
    }

    public void setAnticheatName(final String anticheatName) {
        this.anticheatName = anticheatName;
    }

    public int getPersistenceSeconds() {
        return this.persistenceSeconds;
    }

    public boolean isHardDisable() {
        return this.hardDisable;
    }

    public String getSecretKeyPath() {
        return this.secretKeyPath;
    }

    public void setButterflyAlerts(final boolean butterflyAlerts) {
        this.butterflyAlerts = butterflyAlerts;
    }

    public void setAutoBan(final boolean autoBan) {
        this.autoBan = autoBan;
    }

    public boolean isSlimePushFix() {
        return this.slimePushFix;
    }

    public void setDirectMemory(final boolean directMemory) {
        this.directMemory = directMemory;
    }

    public void setSqlUsername(final String sqlUsername) {
        this.sqlUsername = sqlUsername;
    }

    public void saveConfig() {
        this.saveConfig(this.configuration, this.file);
    }

    public void setGeyser(final boolean geyser) {
        this.geyser = geyser;
    }

    public void setBanCommands(final List banCommands) {
        this.banCommands = banCommands;
    }

    public boolean isButterflyAlerts() {
        return this.butterflyAlerts;
    }

    public boolean isIgnoreLag() {
        return this.ignoreLag;
    }

    public String getCheckCommandsDatabaseName() {
        return this.checkCommandsDatabaseName;
    }

    public void setSchemBans(final boolean schemBans) {
        this.schemBans = schemBans;
    }

    public void setMessagingChannel(final String messagingChannel) {
        this.messagingChannel = messagingChannel;
    }

    public void setCheckCommandsDatabaseName(final String checkCommandsDatabaseName) {
        this.checkCommandsDatabaseName = checkCommandsDatabaseName;
    }

    public void setHardDisable(final boolean hardDisable) {
        this.hardDisable = hardDisable;
    }

    public String getMessagingChannel() {
        return this.messagingChannel;
    }

    public void disable() {
    }

    public void setLogsCleanupDays(final int logsCleanupDays) {
        this.logsCleanupDays = logsCleanupDays;
    }

    public void setSqlPool(final int sqlPool) {
        this.sqlPool = sqlPool;
    }

    public void setSendStats(final boolean sendStats) {
        this.sendStats = sendStats;
    }

    public void setBansCleanupDays(final int bansCleanupDays) {
        this.bansCleanupDays = bansCleanupDays;
    }

    public boolean isSchemBans() {
        return this.schemBans;
    }

    public void setBanMessage(final String[] banMessage) {
        this.banMessage = banMessage;
    }

    public void setPingCommand(final boolean pingCommand) {
        this.pingCommand = pingCommand;
    }

    public void setPersistenceMins(final int persistenceMins) {
        this.persistenceMins = persistenceMins;
    }

    public YamlConfiguration getBanConfiguration() {
        return this.banConfiguration;
    }

    public void setBungeeBans(final boolean bungeeBans) {
        this.bungeeBans = bungeeBans;
    }

    public File getCheckFile() {
        return this.checkFile;
    }

    public void setSqlDatabase(final String sqlDatabase) {
        this.sqlDatabase = sqlDatabase;
    }

    public void setAlertClickHover(final String alertClickHover) {
        this.alertClickHover = alertClickHover;
    }

    public void setMongoAuthEnabled(final boolean mongoAuthEnabled) {
        this.mongoAuthEnabled = mongoAuthEnabled;
    }

    public String getMongoHost() {
        return this.mongoHost;
    }
}
