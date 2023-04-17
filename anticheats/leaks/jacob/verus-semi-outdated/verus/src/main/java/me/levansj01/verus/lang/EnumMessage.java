package me.levansj01.verus.lang;

import net.md_5.bungee.api.*;
import me.levansj01.verus.type.*;
import me.levansj01.verus.*;

public enum EnumMessage
{
    TOP_DESCRIPTION("TOP_DESCRIPTION", 32, "command.commands.top_description", "Gather players with the most violations"),
    PLAYER_NO_LOGS("PLAYER_NO_LOGS", 41, "command.logs.nologs", "This player has no logs."),
    CHEAT("CHEAT", 2, "cheat", "cheat"),
    LANG_COMMAND("LANG_COMMAND", 12, "command.commands.name.lang", "reloadlang"),
    TOGGLE_CHECK_COMMAND("TOGGLE_CHECK_COMMAND", 15, "command.commands.name.togglecheck", "togglecheck"),
    SET_VL_COMMAND("SET_VL_COMMAND", 14, "command.commands.name.setvl", "setvl"),
    INFO_DESCRIPTION("INFO_DESCRIPTION", 31, "command.commands.info_description", "View useful information about a player"),
    UPDATE_CONFIG_COMMAND("UPDATE_CONFIG_COMMAND", 16, "command.commands.name.updateconfig", "updateconfig"),
    ALERTS_COMMAND("ALERTS_COMMAND", 9, "command.commands.name.alerts", Loader.getAlertsCommand()),
    DISCORD_LOG("DISCORD_LOG", 43, "discord.log", "```scala\n{log}\n```\n```scala\nClient Version: {version}\nClient Data: {client}\nClient Brand: {brand}\n```"),
    LOG_FORMAT("LOG_FORMAT", 42, "log.format", "({date} | {timestamp}) {player} failed {checkType} Type {subType} VL: {vl} | Ping: {ping}ms Lag: {lag}ms"),
    LIST_CMDS_DESCRIPTION("LIST_CMDS_DESCRIPTION", 33, "command.commands.listcmds_description", "View per-check commands"),
    PING_SELF("PING_SELF", 37, "command.ping.self", VerusPlugin.COLOR + "Your ping is " + ChatColor.GRAY + "{ping}"),
    TOP_ARGUMENT("TOP_ARGUMENT", 23, "command.commands.arguments.top", "top"),
    INFO_ARGUMENT("INFO_ARGUMENT", 22, "command.commands.arguments.info", "info"),
    COMMAND_PLAYER_NOT_FOUND("COMMAND_PLAYER_NOT_FOUND", 5, "command.player_not_found", "Player not found"),
    MANUAL_BAN_COMMAND("MANUAL_BAN_COMMAND", 8, "command.commands.name.manualban", Loader.getManualbanCommand()),
    ALERTS_ENABLED_COMMAND("ALERTS_ENABLED_COMMAND", 39, "alerts.enabled", "You are now viewing alerts"),
    PLAYER("PLAYER", 0, "player", "player"),
    ALERTS_DISABLED_COMMAND("ALERTS_DISABLED_COMMAND", 40, "alerts.disabled", "You are no longer viewing alerts"),
    FOCUS_COMMAND("FOCUS_COMMAND", 13, "command.commands.name.vfocus", "vfocus"),
    COMMAND_PLAYER_NEVER_LOGGED_ON("COMMAND_PLAYER_NEVER_LOGGED_ON", 4, "command.player_never_logged_on", "This player has never logged onto the server"),
    REMOVE_CMD_DESCRIPTION("REMOVE_CMD_DESCRIPTION",35, "command.commands.removecmd_description", "Remove a per-check command"),
    VIEW_PLAYER_INFO("VIEW_PLAYER_INFO", 36, "command.top.view_player_info", "Click to view {player}'s info"),
    CHECK_LOGS_COMMAND("CHECK_LOGS_COMMAND", 11, "command.commands.name.checklogs", Loader.getChecklogsCommand()),
    CHECK_DESCRIPTION("CHECK_DESCRIPTION", 29, "command.commands.check_description", "Check a players' " + VerusPlugin.getNameFormatted() + " related bans"),
    CHECK_CMDS_COMMAND("CHECK_CMDS_COMMAND", 17, "command.commands.name.checkcmds", "checkcmds"),
    RESTART_ARGUMENT("RESTART_ARGUMENT", 19, "command.commands.arguments.restart", "restart"),
    GUI_ARGUMENT("GUI_ARGUMENT", 20, "command.commands.arguments.gui", "gui"),
    COMMAND_PERMISSION("COMMAND_PERMISSION", 3, "command.permission", ChatColor.RED + "You do not have permission to do this."),
    PING_COMMAND("PING_COMMAND", 10, "command.commands.name.ping", Loader.getPingCommand()),
    LOGS_COMMAND("LOGS_COMMAND", 6, "command.commands.name.logs", Loader.getLogsCommand()),
    REMOVE_CMD_ARGUMENT("REMOVE_CMD_ARGUMENT", 26, "command.commands.arguments.removecmd", "removecmd"),
    LIST_CMDS_ARGUMENT("LIST_CMDS_ARGUMENT", 24, "command.commands.arguments.listcmds", "listcmds"),
    CHECK_ARGUMENT("CHECK_ARGUMENT", 21, "command.commands.arguments.check", "check"),
    RECENT_LOGS_COMMAND("RECENT_LOGS_COMMAND", 7, "command.commands.name.recentlogs", Loader.getRecentlogsCommand()),
    ADD_CMD_ARGUMENT("ADD_CMD_ARGUMENT", 25, "command.commands.arguments.addcmd", "addcmd"),
    VIOLATIONS("VIOLATIONS", 1, "violations", "violations"),
    COMMANDS_DESCRIPTION("COMMANDS_DESCRIPTION", 30, "command.commands.commands_description", "View all " + VerusPlugin.getNameFormatted() + " related commands"),
    GUI_DESCRIPTION("GUI_DESCRIPTION", 28, "command.commands.gui_description", "View and control " + VerusPlugin.getNameFormatted()),
    COMMANDS_ARGUMENT("COMMANDS_ARGUMENT", 18, "command.commands.arguments.commands", "commands"),
    RESTART_DESCRIPTION("RESTART_DESCRIPTION", 27, "command.commands.restart_description", "Restart and automatically update"),
    PING_OTHER("PING_OTHER", 38, "command.ping.other", VerusPlugin.COLOR + "{player}'s ping is " + ChatColor.GRAY + "{ping}"),
    DISCORD_BAN("DISCORD_BAN", 44, "discord.ban", "```scala\nPlayer: {player}\nDetected By: {checkType} {subType}\nClient Version: {version}\nClient Data: {client}\nClient Brand: {brand}\n```"),
    ADD_CMD_DESCRIPTION("ADD_CMD_DESCRIPTION", 34, "command.commands.addcmd_description", "Add a per-check command");

    private String value;
    private final String location;
    private final String fallback;

    public String getFallback() {
        return this.fallback;
    }

    public String get() {
        if (this.value == null) {
            return this.fallback;
        }
        return this.value;
    }

    public void setValue(final String value) {
        this.value = value;
    }

    private EnumMessage(final String s, final int n, final String location, final String fallback) {
        this.location = location;
        this.fallback = fallback;
    }

    public String getLocation() {
        return this.location;
    }
}
