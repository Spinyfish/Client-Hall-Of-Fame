package me.levansj01.verus.command.impl;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.check.manager.CheckManager;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.command.BaseCommand;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.storage.database.check.CheckValues;
import me.levansj01.verus.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class ToggleCheckCommand extends BaseCommand {
    public static final String validTypesMessage;
    private static final String FORMAT;
    public static final List<String> validCheckTypes = Arrays.stream(CheckType.values()).filter(CheckType::valid).map(Enum::name).collect(Collectors.toList());

    @Override
    public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {
        if (args.length == 1 && BukkitUtil.hasPermission(sender, "verus.staff.vl")) {
            List<String> list = Lists.newArrayList();
            StringUtil.copyPartialMatches(args[0], validCheckTypes, list);
            Collections.sort(list);
            return list;
        } else {
            return super.tabComplete(sender, label, args);
        }
    }

    public ToggleCheckCommand() {
        super(EnumMessage.TOGGLE_CHECK_COMMAND.get());
        this.setPermission("verus.staff.toggle");
        this.setUsageMessage(ChatColor.RED + "Usage: /togglecheck <checkType> <subType> <alerts>");
    }

    static {
        validTypesMessage = String.format(ChatColor.RED + "Valid types: %s", String.join(", ", validCheckTypes));
        FORMAT = VerusPlugin.COLOR + "Updated %s state for %s from %s to %s";
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(this.getUsageMessage());
        } else {
            if (BukkitUtil.hasPermission(sender, this.getPermission()) || BukkitUtil.hasPermission(sender, "verus.admin")) {
                String checkTypeS = args[0];

                CheckType checkType;
                try {
                    checkType = CheckType.valueOf(checkTypeS);
                } catch (Exception exception) {
                    sender.sendMessage(validTypesMessage);
                    return;
                }

                String checkOrdinal = checkType.ordinal() + "" + args[1];
                CheckValues checkValues = CheckManager.getInstance().getValues(checkOrdinal);
                if (checkValues == null) {
                    sender.sendMessage(ChatColor.RED + "A check with that ID does not exist!");
                    return;
                }

                CheckManager checkManager = CheckManager.getInstance();
                if (Boolean.parseBoolean(args[2])) {
                    boolean isAlert = checkValues.isAlert();

                    checkManager.setEnabled(checkOrdinal, !isAlert);
                    StringBuilder builder = (new StringBuilder()).append(ChatColor.WHITE);

                    sender.sendMessage(String.format(FORMAT, "Alert", ChatColor.WHITE + checkTypeS
                            + " " + args[1] + VerusPlugin.COLOR, ChatColor.WHITE + String.valueOf(isAlert)
                            + VerusPlugin.COLOR, builder.append(!isAlert).append(VerusPlugin.COLOR).toString()));
                } else {
                    boolean isPunish = checkValues.isPunish();

                    checkManager.setAutoban(checkOrdinal, !isPunish);
                    StringBuilder builder = (new StringBuilder()).append(ChatColor.WHITE);

                    sender.sendMessage(String.format(FORMAT, "Bannable", ChatColor.WHITE + checkTypeS + " " + args[1] + VerusPlugin.COLOR,
                            ChatColor.WHITE + String.valueOf(isPunish) + VerusPlugin.COLOR,
                            builder.append(!checkValues.isPunish()).append(VerusPlugin.COLOR)));
                }
            }

        }
    }
}
