package me.levansj01.verus.command.impl;

import com.google.common.collect.Lists;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.manager.CheckManager;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.command.BaseArgumentCommand;
import me.levansj01.verus.compat.NMSManager;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.manager.DataManager;
import me.levansj01.verus.gui.manager.GUIManager;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.database.Ban;
import me.levansj01.verus.storage.database.Database;
import me.levansj01.verus.storage.database.check.CheckValues;
import me.levansj01.verus.type.VerusTypeLoader;
import me.levansj01.verus.util.BukkitUtil;
import me.levansj01.verus.util.java.WordUtils;
import me.levansj01.verus.verus2.data.player.TickerType;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

public class VerusCommand extends BaseArgumentCommand {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM HH:mm z");
    private static final String CMD_FORMAT;
    private final String FORMAT;

    private void sendCommands(CommandSender sender) {
        sender.sendMessage(VerusPlugin.COLOR + VerusPlugin.getNameFormatted() + " Commands");
        sender.sendMessage(String.format(CMD_FORMAT, EnumMessage.ALERTS_COMMAND.get(), "Enable/Disable alerts"));
        sender.sendMessage(String.format(CMD_FORMAT, EnumMessage.LOGS_COMMAND.get(), "Obtain player logs"));
        sender.sendMessage(String.format(CMD_FORMAT, EnumMessage.RECENT_LOGS_COMMAND.get(), "Obtain a players' last 20 logs"));
        sender.sendMessage(String.format(CMD_FORMAT, EnumMessage.TOGGLE_CHECK_COMMAND.get(), "Toggle check alerts/bans"));
        if (BukkitUtil.hasPermission(sender, "verus.admin")) {
            sender.sendMessage(String.format(CMD_FORMAT, EnumMessage.MANUAL_BAN_COMMAND.get(), "Manually ban a player using " + VerusPlugin.getNameFormatted()));
        }

        if (StorageEngine.getInstance().getVerusConfig().isPingCommand()) {
            sender.sendMessage(String.format(CMD_FORMAT, EnumMessage.ALERTS_COMMAND.get(), "Displays a players' ping"));
        }

        if (VerusTypeLoader.isCustom()) {
            sender.sendMessage(String.format(CMD_FORMAT, EnumMessage.SET_VL_COMMAND.get(), "Set check ban vl"));
        }

    }

    static {
        CMD_FORMAT = ChatColor.GRAY + "- " + VerusPlugin.COLOR + "/%s: " + ChatColor.WHITE + "%s";
    }

    public VerusCommand() {
        super(VerusPlugin.getName());
        this.FORMAT = ChatColor.GRAY + "- " + VerusPlugin.COLOR + "%s's " + WordUtils.capitalize(EnumMessage.VIOLATIONS.get()) + ": " + ChatColor.WHITE + "%s";
        this.setPermission("verus.staff");
        this.addArgument(new BaseArgumentCommand.CommandArgument( EnumMessage.COMMANDS_ARGUMENT.get(), EnumMessage.COMMANDS_DESCRIPTION.get(), (sender, s) -> {
            this.sendCommands(sender);
        }));
        this.addArgument(new BaseArgumentCommand.CommandArgument( EnumMessage.RESTART_ARGUMENT.get(), EnumMessage.RESTART_DESCRIPTION.get(), (sender, s) -> {
            if (!BukkitUtil.hasPermission(sender, "verus.restart")) {
                sender.sendMessage(this.getPermissionMessage());
            } else {
                VerusPlugin.restart();
            }
        }));
        this.addArgument(new BaseArgumentCommand.CommandArgument(EnumMessage.GUI_ARGUMENT.get(), EnumMessage.GUI_DESCRIPTION.get(), (sender, s) -> {
            if (sender instanceof ConsoleCommandSender) {
                sender.sendMessage(ChatColor.RED + "This command can only be ran by players.");
            } else if (!BukkitUtil.hasPermission(sender, "verus.staff.gui")) {
                sender.sendMessage(this.getPermissionMessage());
            } else {
                GUIManager.getInstance().getMainGui().openGui((Player)sender);
            }
        }));
        this.addArgument(new BaseArgumentCommand.CommandArgument(EnumMessage.CHECK_ARGUMENT.get(), EnumMessage.CHECK_DESCRIPTION.get(), "(player)", (sender, args) -> {
            if (args.length != 2) {
                this.sendHelp(sender);
            } else {
                StorageEngine storageEngine = StorageEngine.getInstance();
                if (!storageEngine.isConnected()) {
                    sender.sendMessage(ChatColor.RED + "Please connect to a database to use this command.");
                } else {
                    Database database = storageEngine.getDatabase();
                    database.getUUID(args[1], (uuid) -> {
                        if (uuid == null) {
                            sender.sendMessage(ChatColor.RED + EnumMessage.COMMAND_PLAYER_NEVER_LOGGED_ON.get());
                        } else {
                            database.getBans(uuid, bans -> {
                                if (!bans.iterator().hasNext()) {
                                    sender.sendMessage(ChatColor.RED + "This player hasn't been banned.");
                                } else {
                                    sender.sendMessage(VerusPlugin.COLOR + args[1] + "'s ban(s):");

                                    for (Ban ban : bans) {
                                        sender.sendMessage(ChatColor.GRAY + "- " + VerusPlugin.COLOR + "Date: " + ChatColor.WHITE
                                                + DATE_FORMAT.format(new Date(ban.getTimestamp())) + VerusPlugin.COLOR + " Type: "
                                                + ChatColor.WHITE + ban.getType() + " " + ban.getSubType());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        }));
        this.addArgument(new BaseArgumentCommand.CommandArgument(EnumMessage.INFO_ARGUMENT.get(), EnumMessage.INFO_DESCRIPTION.get(), "(player)", (sender, args) -> {
            if (args.length != 2) {
                this.sendHelp(sender);
            } else {
                Player target = Bukkit.getPlayer(args[1]);
                if (target == null) {
                    sender.sendMessage(ChatColor.RED + EnumMessage.COMMAND_PLAYER_NOT_FOUND.get());
                } else {
                    PlayerData playerData = DataManager.getInstance().getPlayer(target);
                    if (playerData == null) {
                        sender.sendMessage(ChatColor.RED + EnumMessage.COMMAND_PLAYER_NOT_FOUND.get());
                    } else {
                        BaseComponent[] baseComponents = null;
                        int totalViolations = (int)Math.floor(playerData.getTotalViolations());
                        if (totalViolations > 0) {
                            baseComponents = TextComponent.fromLegacyText(ChatColor.GRAY + ChatColor.ITALIC.toString() + "Hover for violations...");
                            String capitalize = WordUtils.capitalize(EnumMessage.VIOLATIONS.get());
                            StringBuilder builder = new StringBuilder();

                            for (Check check : playerData.getCheckData().getChecks()) {
                                if (CheckManager.getInstance().isEnabled(check)) {
                                    int violations = (int) Math.floor(check.getViolations());
                                    if (violations > 0) {
                                        builder.append(VerusPlugin.COLOR).append(check.getType().getName()).append(" ")
                                                .append(check.getSubType()).append(" ").append(capitalize).append(": ")
                                                .append(ChatColor.WHITE).append(violations).append(" \n");
                                    }
                                }
                            }

                            builder.append("\n").append(VerusPlugin.COLOR).append(capitalize).append(": ").append(ChatColor.WHITE).append(totalViolations);
                            HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(builder.toString()));
                            Arrays.stream(baseComponents).forEach(component -> {
                                component.setHoverEvent(hoverEvent);
                            });
                        }

                        sender.sendMessage(playerData.getInfo());
                        if (sender instanceof Player) {
                            Player player = (Player)sender;
                            if (baseComponents != null) {
                                player.spigot().sendMessage(baseComponents);
                            }

                            if (BukkitUtil.isDev(player)) {
                                sender.sendMessage(ChatColor.GRAY + "Stats: " + ChatColor.WHITE +
                                        String.format("(current=%s) (lag=%s / fast=%s) (teleporting=%s / v2=%s) (keepalive=%s %sms / transaction=%s %sms) " +
                                                "(ping=%s / maxping=%s) (spawned=%s / ground=%s / vehicle=%s / fly=%s|%s / survival=%s) (gl=%s|%s)",
                                                playerData.getTotalTicks(), playerData.hasLag(), playerData.hasFast(), playerData.isTeleporting(),
                                                playerData.isTeleportingV2(), playerData.getLastKeepAlive(), playerData.getPing(),
                                                playerData.getTickerMap().get(TickerType.LAST_SENT_TRANSACTION), playerData.getTransactionPing(),
                                                playerData.getPingTicks(), playerData.getMaxPingTicks(), playerData.isSpawned(), playerData.getLocation().getGround(),
                                                playerData.isVehicle(), playerData.canFly(), playerData.isFlying(), playerData.isSurvival(),
                                                NMSManager.getInstance().isGliding(player), playerData.isGliding()));
                            }
                        }
                    }
                }
            }
        }));
        this.addArgument(new BaseArgumentCommand.CommandArgument(EnumMessage.TOP_ARGUMENT.get(), EnumMessage.TOP_DESCRIPTION.get(), (sender, args) -> {
            List<PlayerData> mostViolations = DataManager.getInstance().getMostViolations();
            boolean b = false;
            int i = 0;

            do {
                if (i >= Math.min(mostViolations.size(), 10)) {
                    if (!b) {
                        sender.sendMessage(ChatColor.RED + "No players with " + EnumMessage.VIOLATIONS.get() + " were found.");
                    }

                    return;
                }

                PlayerData playerData = mostViolations.get(i);
                if (playerData != null && playerData.getTotalViolations() > 1.0) {
                    if (i == 0) {
                        sender.sendMessage(VerusPlugin.COLOR + "Top Cheaters (" + WordUtils.capitalize(EnumMessage.VIOLATIONS.get()) + "): ");
                    }

                    String name = playerData.getName();
                    BaseComponent[] components = TextComponent.fromLegacyText(String.format(this.FORMAT, playerData.getName(), (int)Math.floor(playerData.getTotalViolations())));
                    HoverEvent hoverEvent = new HoverEvent(Action.SHOW_TEXT, TextComponent.fromLegacyText(EnumMessage.VIEW_PLAYER_INFO.get().replace("{player}", name)));
                    ClickEvent clickEvent = new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/" + VerusPlugin.getName() + " info " + name);
                    Arrays.stream(components).forEach(component -> {
                        component.setHoverEvent(hoverEvent);
                        component.setClickEvent(clickEvent);
                    });
                    if (sender instanceof Player) {
                        ((Player)sender).spigot().sendMessage(components);
                    } else {
                        sender.sendMessage(TextComponent.toLegacyText(components));
                    }

                    b = true;
                }

                ++i;
            } while(true);
        }));
        if (VerusTypeLoader.isDev()) {
            this.addArgument(new BaseArgumentCommand.CommandArgument(EnumMessage.LIST_CMDS_ARGUMENT.get(), EnumMessage.LIST_CMDS_DESCRIPTION.get(),
                    "(checkType) (subType)", (sender, args) -> {
                if (args.length != 3) {
                    this.sendHelp(sender);
                } else {
                    String checkTypeS = args[1];

                    CheckType checkType;
                    try {
                        checkType = CheckType.valueOf(checkTypeS);
                    } catch (Exception exception) {
                        sender.sendMessage(ToggleCheckCommand.validTypesMessage);
                        return;
                    }

                    String checkSort = checkType.ordinal() + "" + args[2];
                    CheckValues checkValues = CheckManager.getInstance().getValues(checkSort);
                    if (checkValues == null) {
                        sender.sendMessage(ChatColor.RED + "A check with that ID does not exist!");
                    } else if (!checkValues.hasCommands()) {
                        sender.sendMessage(ChatColor.RED + "This check does not contain any per-check commands!");
                    } else {
                        int i = 0;
                        StringBuilder builder = new StringBuilder(VerusPlugin.COLOR + "Commands for " + ChatColor.WHITE + checkTypeS + " " + args[2] + "\n");

                        for (String cmd : checkValues.getCommands()) {
                            builder.append(String.format(ChatColor.GRAY + "- " + VerusPlugin.COLOR + "'%s' %s\n", ChatColor.WHITE
                                    + cmd, ChatColor.GRAY + ChatColor.ITALIC.toString() + "(Index: " + i++ + ")"));
                        }
                        sender.sendMessage(builder.toString());
                    }
                }
            }));
            this.addArgument(new BaseArgumentCommand.CommandArgument(EnumMessage.ADD_CMD_ARGUMENT.get(), EnumMessage.ADD_CMD_DESCRIPTION.get(),
                    "(checkType) (subType) (command)", (sender, args) -> {
                if (args.length < 4) {
                    this.sendHelp(sender);
                } else {
                    String checkTypeS = args[1];

                    CheckType checkType;
                    try {
                        checkType = CheckType.valueOf(checkTypeS);
                    } catch (Exception exception) {
                        sender.sendMessage(ToggleCheckCommand.validTypesMessage);
                        return;
                    }
                    String checkOrdinal = checkType.ordinal() + "" + args[2];
                    CheckValues checkValues = CheckManager.getInstance().getValues(checkOrdinal);
                    if (checkValues == null) {
                        sender.sendMessage(ChatColor.RED + "A check with that ID does not exist!");
                    } else {
                        String[] shit = new String[args.length - 3];
                        System.arraycopy(args, 3, shit, 0, shit.length);
                        String joinStr = String.join(" ", shit);
                        if (joinStr.contains(",")) {
                            sender.sendMessage(ChatColor.RED + "You cannot include ',' in a per-check command!");
                        } else if (checkValues.hasCommand(joinStr)) {
                            sender.sendMessage(ChatColor.RED + "This per-check command already exists!");
                        } else {
                            CheckManager.getInstance().addCommand(checkValues, joinStr);
                            sender.sendMessage(String.format(VerusPlugin.COLOR + "Added '%s' to %s %s.",
                                    ChatColor.WHITE + joinStr + VerusPlugin.COLOR, ChatColor.WHITE + checkTypeS, args[2] + VerusPlugin.COLOR));
                        }
                    }
                }
            }));
            this.addArgument(new BaseArgumentCommand.CommandArgument(EnumMessage.REMOVE_CMD_ARGUMENT.get(),
                    EnumMessage.REMOVE_CMD_DESCRIPTION.get(), "(checkType) (subType) (index)", (sender, args) -> {
                if (args.length != 4) {
                    this.sendHelp(sender);
                } else {
                    String checkTypeS = args[1];

                    CheckType checkType;
                    try {
                        checkType = CheckType.valueOf(checkTypeS);
                    } catch (Exception exception) {
                        sender.sendMessage(ToggleCheckCommand.validTypesMessage);
                        return;
                    }

                    String checkOrdinal = checkType.ordinal() + "" + args[2];
                    CheckValues checkValues = CheckManager.getInstance().getValues(checkOrdinal);
                    if (checkValues == null) {
                        sender.sendMessage(ChatColor.RED + "A check with that ID does not exist!");
                    } else {
                        int cmdId;
                        try {
                            cmdId = Integer.parseInt(args[3]);
                        } catch (Exception exception) {
                            sender.sendMessage(ChatColor.RED + "Please enter a valid value!");
                            return;
                        }

                        if (cmdId + 1 > checkValues.getCommandsSize()) {
                            sender.sendMessage(ChatColor.RED + "That per-check command does not exist!");
                        } else {
                            String cmd = checkValues.getCommand(cmdId);
                            CheckManager.getInstance().removeCommand(checkValues, cmdId);
                            sender.sendMessage(String.format(VerusPlugin.COLOR + "Removed '%s' from %s %s.",
                                    ChatColor.WHITE + cmd + VerusPlugin.COLOR, ChatColor.WHITE + checkTypeS, args[2] + VerusPlugin.COLOR));
                        }
                    }
                }
            }));
        }

    }

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {
        if (args.length == 1 && BukkitUtil.hasPermission(sender, "verus.staff")) {
            ArrayList<String> list = Lists.newArrayList();
            StringUtil.copyPartialMatches(args[0], this.arguments.keySet(), list);
            Collections.sort(list);
            return list;
        } else {
            if (args.length == 2) {
                String subCommand = args[0];
                if (subCommand.equalsIgnoreCase(EnumMessage.LIST_CMDS_ARGUMENT.get())
                        || subCommand.equalsIgnoreCase(EnumMessage.ADD_CMD_ARGUMENT.get())
                        || subCommand.equalsIgnoreCase(EnumMessage.REMOVE_CMD_ARGUMENT.get())) {
                    ArrayList<String> completed = Lists.newArrayList();
                    StringUtil.copyPartialMatches(args[1], ToggleCheckCommand.validCheckTypes, completed);
                    Collections.sort(completed);
                    return completed;
                }

                if (args[0].equalsIgnoreCase(EnumMessage.INFO_ARGUMENT.get()) || args[0].equalsIgnoreCase(EnumMessage.CHECK_ARGUMENT.get())) {
                    Player player;
                    ArrayList<String> completed = new ArrayList<>();
                    if (sender instanceof Player) {
                        player = (Player)sender;
                    } else {
                        player = null;
                    }
                    for (Player online : sender.getServer().getOnlinePlayers()) {
                        String name = online.getName();
                        if (player != null && !player.canSee(online)) {
                            if (StringUtil.startsWithIgnoreCase(name, args[args.length - 1])) {
                                completed.add(name);
                            }
                        }
                    }

                    completed.sort(String.CASE_INSENSITIVE_ORDER);
                    return completed;
                }
            }

            return super.tabComplete(sender, label, args);
        }
    }
}