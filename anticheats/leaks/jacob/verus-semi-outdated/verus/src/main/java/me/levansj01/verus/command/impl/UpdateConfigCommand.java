package me.levansj01.verus.command.impl;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import me.levansj01.verus.VerusPlugin;
import me.levansj01.verus.command.BaseCommand;
import me.levansj01.verus.lang.EnumMessage;
import me.levansj01.verus.storage.StorageEngine;
import me.levansj01.verus.storage.config.VerusConfiguration;
import me.levansj01.verus.util.BukkitUtil;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.StringUtil;

public class UpdateConfigCommand extends BaseCommand {
    private final VerusConfiguration verusConfig;
    private final YamlConfiguration configuration;
    private final Set<String> keys;

    public void execute(CommandSender var1, String[] var2) {
        if (var2.length >= 1 && var2.length <= 2) {
            if (BukkitUtil.hasPermission(var1, this.getPermission()) || BukkitUtil.hasPermission(var1, "verus.admin")) {
                String var3 = var2[0];
                if (!this.keys.contains(var3)) {
                    var1.sendMessage(ChatColor.RED + "Invalid config option!");
                    return;
                }

                Object var4 = this.configuration.get(var3);
                if (var2.length == 1) {
                    if (this.instanceOf(var4)) {
                        var1.sendMessage(String.format(VerusPlugin.COLOR + "Config option for %s: %s",
                                ChatColor.WHITE + var3 + VerusPlugin.COLOR, ChatColor.WHITE + String.valueOf(var4)));
                    } else {
                        var1.sendMessage(ChatColor.RED + "Cannot view this config option!");
                    }

                    return;
                }

                String var5 = var2[1];
                boolean var6;
                if (var4 instanceof String) {
                    if (!(var6 = Objects.equals(var4, var5))) {
                        this.configuration.set(var3, var5);
                        this.verusConfig.saveConfig();
                    }
                } else if (var4 instanceof Boolean) {
                    boolean var7 = Boolean.parseBoolean(var5);
                    if (!(var6 = Objects.equals(var4, var7))) {
                        this.configuration.set(var3, var7);
                        this.verusConfig.saveConfig();
                    }
                } else if (var4 instanceof Double) {
                    double var9 = Double.parseDouble(var5);
                    if (!(var6 = Objects.equals(var4, var9))) {
                        this.configuration.set(var3, var9);
                        this.verusConfig.saveConfig();
                    }
                } else {
                    if (!(var4 instanceof Integer)) {
                        var1.sendMessage(ChatColor.RED + "Cannot update this config option!");
                        return;
                    }

                    int var10 = Integer.parseInt(var5);
                    if (!(var6 = Objects.equals(var4, var10))) {
                        this.configuration.set(var3, var10);
                        this.verusConfig.saveConfig();
                    }
                }

                if (var6) {
                    var1.sendMessage(ChatColor.RED + "Option is already set to that value!");
                    return;
                }

                var1.sendMessage(String.format(VerusPlugin.COLOR + "Updated Config option for %s from %s to %s", ChatColor.WHITE + var3 + VerusPlugin.COLOR, ChatColor.WHITE + String.valueOf(var4) + VerusPlugin.COLOR, ChatColor.WHITE + var5 + VerusPlugin.COLOR));
                var1.sendMessage(ChatColor.RED + ChatColor.ITALIC.toString() + "Some options will require a restart to take affect!");
            }

        } else {
            var1.sendMessage(this.getUsageMessage());
        }
    }

    public List<String> tabComplete(CommandSender var1, String var2, String[] var3) throws IllegalArgumentException {
        if (var3.length < 1 || !BukkitUtil.hasPermission(var1, this.getPermission()) && !BukkitUtil.hasPermission(var1, "verus.admin")) {
            return super.tabComplete(var1, var2, var3);
        } else {
            ArrayList var4 = Lists.newArrayList();
            StringUtil.copyPartialMatches(var3[0], (Iterable)this.keys.stream().filter((var1x) -> {
                return this.instanceOf(this.configuration.get(var1x));
            }).collect(Collectors.toList()), var4);
            Collections.sort(var4);
            return var4;
        }
    }

    private boolean instanceOf(Object var1) {
        boolean var10000;
        if (!(var1 instanceof String) && !(var1 instanceof Double) && !(var1 instanceof Integer) && !(var1 instanceof Boolean)) {
            var10000 = false;
        } else {
            var10000 = true;
        }

        return var10000;
    }

    public UpdateConfigCommand() {
        super(EnumMessage.UPDATE_CONFIG_COMMAND.get());
        this.setPermission("verus.staff.updateconfig");
        this.setUsageMessage(ChatColor.RED + "Usage: /updateconfig <location> <value>");
        this.verusConfig = StorageEngine.getInstance().getVerusConfig();
        this.configuration = this.verusConfig.getConfiguration();
        this.keys = this.configuration.getKeys(true);
    }
}