package me.levansj01.verus.command.impl;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.check.Check;
import me.levansj01.verus.check.manager.CheckManager;
import me.levansj01.verus.check.type.CheckType;
import me.levansj01.verus.command.BaseCommand;
import me.levansj01.verus.data.CheckData;
import me.levansj01.verus.data.PlayerData;
import me.levansj01.verus.data.manager.DataManager;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.storage.database.check.CheckValues;
import me.levansj01.verus.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.util.StringUtil;

public class SetVLCommand extends BaseCommand {

    public SetVLCommand() {
        super(EnumMessage.SET_VL_COMMAND.get());
        this.setPermission("verus.staff.vl");
        this.setUsageMessage(ChatColor.RED + "Usage: /setvl <checkType> <subType> <violations>");
    }

    public List<String> tabComplete(CommandSender sender, String label, String[] args) throws IllegalArgumentException {
        if (args.length == 1 && BukkitUtil.hasPermission(sender, "verus.staff.vl")) {
            List<String> list = Lists.newArrayList();
            StringUtil.copyPartialMatches(args[0], ToggleCheckCommand.validCheckTypes, list);
            Collections.sort(list);
            return list;
        } else {
            return super.tabComplete(sender, label, args);
        }
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (args.length >= 2 && args.length <= 3) {
            if (BukkitUtil.hasPermission(sender, this.getPermission()) || BukkitUtil.hasPermission(sender, "verus.admin")) {
                String checkTypeS = args[0];

                CheckType checkType;
                try {
                    checkType = CheckType.valueOf(checkTypeS);
                } catch (Exception exception) {
                    sender.sendMessage(ToggleCheckCommand.validTypesMessage);
                    return;
                }

                String checkOrdinal = checkType.ordinal() + "" + args[1];
                CheckValues checkValue = CheckManager.getInstance().getValues(checkOrdinal);
                if (checkValue == null) {
                    sender.sendMessage(ChatColor.RED + "A check with that ID does not exist!");
                    return;
                }

                int maxViolation = checkValue.getMaxViolation();
                if (args.length == 2) {
                    sender.sendMessage(String.format(VerusPlugin.COLOR + "Max violations for %s: %s", ChatColor.WHITE + checkTypeS
                            + " " + args[1] + VerusPlugin.COLOR, ChatColor.WHITE + String.valueOf(maxViolation)));
                    return;
                }

                int vl;
                try {
                    vl = Integer.parseInt(args[2]);
                } catch (Exception exception) {
                    sender.sendMessage(ChatColor.RED + "Please enter a valid violation value.");
                    return;
                }

                if (vl < 5) {
                    sender.sendMessage(ChatColor.RED + "Max violation must be greater than 4.");
                    return;
                }

                for (PlayerData playerData : DataManager.getInstance().getPlayers()) {
                    CheckData checkData = playerData.getCheckData();
                    Check check = checkData.getByIdentifier().get(checkOrdinal);
                    if (check != null) {
                        check.setMaxViolation(vl);
                    }
                }

                CheckManager.getInstance().setMaxViolation(checkOrdinal, vl);
                sender.sendMessage(String.format(VerusPlugin.COLOR + "Updated violations for %s from %s to %s",
                        ChatColor.WHITE + checkTypeS + " " + args[1] + VerusPlugin.COLOR, ChatColor.WHITE
                                + String.valueOf(maxViolation) + VerusPlugin.COLOR,
                        ChatColor.WHITE + String.valueOf(vl) + VerusPlugin.COLOR));
            }

        } else {
            sender.sendMessage(this.getUsageMessage());
        }
    }
}