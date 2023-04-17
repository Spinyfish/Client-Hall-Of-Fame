package me.levansj01.verus.alert.manager;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.alert.Alert;
import me.levansj01.verus.api.API;
import me.levansj01.verus.api.wrapper.BanResult;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.manager.CheckManager;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.check.version.CheckVersion;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.manager.DataManager;
import me.levansj01.verus.discord.DiscordManager;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.messaging.MessagingHandler;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.config.VerusConfiguration;
import me.levansj01.verus.storage.database.Ban;
import me.levansj01.verus.storage.database.Log;
import me.levansj01.verus.util.hastebin.HasteBin;
import me.levansj01.verus.util.java.CachedSupplier;
import me.levansj01.verus.util.java.JavaV;
import me.levansj01.verus.util.java.StringUtil;
import me.levansj01.verus.util.java.WordUtils;
//import me.levansj01.verus.util.pastebin.AccountCredentials;
//import me.levansj01.verus.util.pastebin.Paste;
//import me.levansj01.verus.util.pastebin.PasteBin;
//import me.levansj01.verus.util.pastebin.PasteExpiration;
//import me.levansj01.verus.util.pastebin.PasteHighLight;
//import me.levansj01.verus.util.pastebin.PasteVisibility;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class AlertManager {
    public static AlertManager instance;
    public static final String PERMISSION_ALERTS = "verus.alerts";
    public static final String PERMISSION_ADMIN = "verus.admin";
    private static final String DEBUG;
    private final ExecutorService executorService = Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setPriority(3).setNameFormat("Verus Executor Thread").build());
    private static final ExecutorService hastebinService;

    public void run(Runnable runnable) {
        this.executorService.execute(runnable);
    }

    public void handleViolation(PlayerData playerData, Check check, Supplier<String> data, double vl, boolean disguise) {
        this.run(() -> this._handleViolation(playerData, check, CachedSupplier.of(data), vl, disguise));
    }

    private void _handleViolation(PlayerData playerData, Check check, Supplier<String> data, double vl, boolean disguise) {
        if (!playerData.isEnabled()) {
            return;
        }
        int currentViolation = (int) Math.floor(check.getViolations());
        check.setViolations(check.getViolations() + Math.min(5.0, vl));
        if (check.getViolations() > 0.0) {
            this.handleDebug(playerData, check, check.getViolations(), data);
        }
        if (currentViolation > 0) {
            if (currentViolation > check.getLastViolation()) {
                this.handleAlert(playerData, check, data, currentViolation);
                if (check.getViolations() >= (double)check.getMaxViolation()) {
                    this.handleBan(playerData, check, disguise);
                }
                if (disguise) {
                    Alert alert = new Alert(check, data, currentViolation);
                    playerData.getSpoofedAlerts().add(alert);
                    JavaV.trim(playerData.getSpoofedAlerts(), 5);
                    playerData.setCheckSpoofing(true);
                }
            }
            check.setLastViolation(currentViolation);
        }
    }

    public boolean handleAlert(PlayerData playerData, Check check, Supplier<String> data, int vl) {
        if (!CheckManager.getInstance().isEnabled(check)) {
            return false;
        }
        API api = API.getAPI();
        if (api != null && api.fireViolationEvent(playerData, check, vl)) {
            check.setViolations(check.getLastViolation());
            return false;
        }
        StorageEngine engine = StorageEngine.getInstance();
        VerusConfiguration config = engine.getVerusConfig();
        if (config.isBypassEnabled() && !config.isBypassAlerts() && playerData.getPlayer().hasPermission(config.getBypassPermission())) {
            return false;
        }
        if (engine.isConnected()) {
            engine.getDatabase().insertLog(Log.create(playerData, check, data, vl));
        }
        if (!check.getType().ignore()) {
            String playerName = playerData.getName();
            String message = this.getFormattedAlert(playerData, check, String.valueOf(vl));
            this.handleAlertBroadcast(check, message, playerName);
            if ((double)vl > (double)check.getMaxViolation() * 0.25 && vl % 3 == 0) {
                DiscordManager.getInstance().sendLog(playerData, message);
            }
        }
        return true;
    }

    public void handleAlertBroadcast(Check check, String message, String playerName) {
        Stream<Player> players = DataManager.getInstance().getPlayers().stream().filter(PlayerData::isAlerts).filter(data -> data.isFocused(check)).map(PlayerData::getPlayer);
        VerusConfiguration config = StorageEngine.getInstance().getVerusConfig();
        if (config.isAlertClick()) {
            BaseComponent[] components = TextComponent.fromLegacyText(message);
            HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                    TextComponent.fromLegacyText(ChatColor.translateAlternateColorCodes('&',
                            config.getAlertClickHover().replace("{player}", playerName))));
            ClickEvent clickEvent = new ClickEvent(ClickEvent.Action.RUN_COMMAND, config.getAlertClickCommand().replace("{player}", playerName));
            Arrays.stream(components).forEach(baseComponent -> {
                baseComponent.setHoverEvent(hoverEvent);
                baseComponent.setClickEvent(clickEvent);
            });
            players.forEach(player -> player.spigot().sendMessage(components));
        } else {
            players.forEach(player -> player.sendMessage(message));
        }
    }

    public void handleDebug(PlayerData playerData, Check check, double vl, Supplier<String> data) {
        DataManager.getInstance().getPlayers().stream().filter(PlayerData::isDebug)
                .filter(other -> other.getFocus() == null || other.getFocus().equals(check.getType())
                        && check.getSubType().equals(other.getFocusSubType())).map(PlayerData::getPlayer)
                .forEach(player -> player.sendMessage(String.format(DEBUG, playerData.getName(), check.getType().getName(),
                        check.getSubType() + (check.getCheckVersion() == CheckVersion.RELEASE ? ""
                                : (check.getCheckVersion() == CheckVersion.EXPERIMENTAL ? "^" : "*")), vl, data.get())));
    }

    public void handleBan(PlayerData playerData, Check check, boolean disguise) {
        VerusConfiguration config = StorageEngine.getInstance().getVerusConfig();
        if (!config.isAutoBan()) {
            return;
        }
        if (!CheckManager.getInstance().isAutoban(check)) {
            return;
        }
        if (!(check.getCheckVersion() != CheckVersion.RELEASE || playerData.isBanned() || playerData.isDebug() || config.isBypassEnabled() && playerData.getPlayer().hasPermission(config.getBypassPermission()))) {
            if (disguise) {
                playerData.setSpoofBan(true);
                playerData.setSpoofBanCheck(check);
            } else if (this.insertBan(playerData, check)) {
                playerData.setEnabled(false);
            }
        }
    }

    public boolean insertBan(PlayerData playerData, Check check) {
        StorageEngine engine = StorageEngine.getInstance();
        VerusConfiguration config = engine.getVerusConfig();
        String name = playerData.getName();
        String reason = WordUtils.capitalize(EnumMessage.CHEAT.get()) + "-";
        reason = config.isScrambleBanId() ? reason + StringUtil.generateScrambledId(4) : (check.getType() == CheckType.MANUAL || config.isRandomBanId() ? reason + StringUtil.generateRandomId() : reason + check.getType().getSuffix() + check.getSubType());
        String finalReason = reason;
        List<String> commands = config.getBanCommands().stream().map(command -> command.replace("%s", name).replace("{name}",
                name).replace("{player}", name).replace("{reason}", finalReason)).collect(Collectors.toCollection(LinkedList::new));
        playerData.setBanned(true);
        API api = API.getAPI();
        boolean announce = config.isBanAnnouncement();
        if (api != null) {
            BanResult result = api.fireBanEvent(playerData, check, announce, commands);
            if (!result.isBan()) {
                return false;
            }
            announce = result.isAnnounce();
        }
        if (announce) {
            String[] message = config.getBanMessage();
            for (String line : message) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', line.replace("%s", name)
                        .replace("{player}", name).replace("{reason}", reason)));
            }
        }
        if (config.isBungeeBans()) {
            MessagingHandler messagingHandler = MessagingHandler.getInstance();
            Player player = playerData.getPlayer();
            NMSManager.getInstance().postToMainThread(() -> commands.forEach(command -> messagingHandler.handleBan(player, command)));
        } else {
            ConsoleCommandSender console = Bukkit.getConsoleSender();
            NMSManager.getInstance().postToMainThread(() -> commands.forEach(command -> Bukkit.dispatchCommand(console, command)));
        }
        if (engine.isConnected()) {
            engine.getDatabase().insertBan(Ban.create(playerData, check));
        }
        DiscordManager.getInstance().sendBan(playerData, check);
        return true;
    }

    public void uploadLogs(CommandSender sender, UUID target, Iterable<Log> logs, boolean admin) {
        this.uploadLogs(sender, (admin ? "Admin " : "") + "Logs of (" + target + "):\n", logs, admin);
    }

    public void uploadLogs(CommandSender sender, String firstLine, Iterable<Log> logs, boolean admin) {
        StringBuilder line = new StringBuilder(firstLine);
        for (Log log : logs) {
            line.append(log.toString(admin));
        }
        String logUpload = line.toString();
        hastebinService.execute(() -> {
            for (String endpoint : HasteBin.ENDPOINTS) {
                sender.sendMessage(ChatColor.GRAY + "(Uploading logs to hastebin...)");
                try {
                    String log = HasteBin.paste(logUpload, endpoint);
                    sender.sendMessage(VerusPlugin.COLOR + (admin ? "Admin " : "") + "Logs URL: " + ChatColor.WHITE + log);
                    return;
                }
                catch (Throwable exception) {
                    if (exception instanceof IOException) continue;
                    exception.printStackTrace();
                }
            }
            sender.sendMessage(ChatColor.GRAY + "(Uploads failed, uploading to pastebin...)");
            /*try {
                String log = new PasteBin(new AccountCredentials("oeKULcnP-95AIFqgkDvkgqQU8AGt_0gI", "levansj05", "x4aL_zf#FZn3rT6"))
                        .createPaste(new Paste(firstLine, logUpload, PasteHighLight.TEXT, PasteExpiration.ONE_WEEK, PasteVisibility.UNLISTED));
                sender.sendMessage(VerusPlugin.COLOR + (admin ? "Admin " : "") + "Logs URL " + ChatColor.WHITE + log);
            }
            catch (Throwable throwable) {
                sender.sendMessage(ChatColor.RED + "Both uploads have failed");
            }*/
        });
    }

    private String getFormattedAlert(PlayerData playerData, Check check, String vl) {
        return ChatColor.translateAlternateColorCodes('&', StorageEngine.getInstance().getVerusConfig().getAlertMessage().replace("{name}", (VerusPlugin.COLOR + StorageEngine.getInstance().getVerusConfig().getAnticheatName())).replace("{player}", playerData.getName()).replace("{certainty}", StorageEngine.getInstance().getVerusConfig().getAlertCertainty()).replace("{cheat}", (check.getFriendlyName() + " " + check.getType().getSuffix() + check.getSubType() + (check.getCheckVersion() == CheckVersion.RELEASE ? "" : (check.getCheckVersion() == CheckVersion.EXPERIMENTAL ? "^" : "*")))).replace("{friendly}", check.getFriendlyName()).replace("{type}", check.getType().getName()).replace("{subType}", check.getSubType()).replace("{vl}", vl).replace("{maxvl}", String.valueOf((check.getMaxViolation() == Integer.MAX_VALUE ? 0 : check.getMaxViolation()))).replace("{ping}", String.valueOf(playerData.getTransactionPing())));
    }

    public static AlertManager getInstance() {
        return instance == null ? (instance = new AlertManager()) : instance;
    }

    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    static {
        DEBUG = VerusPlugin.COLOR + ChatColor.BOLD.toString() + "Verus-Debug " + ChatColor.DARK_GRAY
                + "> " + ChatColor.WHITE + "%s" + ChatColor.GRAY + " failed " + ChatColor.WHITE
                + "%s Type %s" + ChatColor.GRAY + " VL[" + VerusPlugin.COLOR + "%.1f." + ChatColor.GRAY + "] %s";
        hastebinService = Executors.newCachedThreadPool(new ThreadFactoryBuilder().setPriority(3).build());
    }
}