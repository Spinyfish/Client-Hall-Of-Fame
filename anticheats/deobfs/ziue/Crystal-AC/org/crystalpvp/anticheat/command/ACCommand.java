package org.crystalpvp.anticheat.command;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.manager.ConfigurationManager;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.api.util.CrystalUtil;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;

import net.minecraft.server.v1_8_R3.MinecraftServer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


import java.util.ArrayList;
import java.util.List;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class ACCommand implements CommandExecutor {

    private CrystalAC plugin;

    public ACCommand() {
        this.plugin = CrystalAC.getInstance();
    }
    public String firstColor;
    public String secondColor;
    private String command = ConfigurationManager.getConfigTheme().toLowerCase();


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;

            if (ConfigurationManager.getConfigTheme().equalsIgnoreCase("crystalac")) {
                firstColor = "&7";
                secondColor = "&3";
                command = "anticheat";
            } else {
                firstColor = "&b";
                secondColor = "&b";
                command = "anticheat";
            }

            if (args.length == 0) {
                if (sender.hasPermission("ac.command.help") || sender.hasPermission("crystalac.admin") )
                {
                    if (sender.hasPermission("ac.command.help") || sender.hasPermission("crystalac.admin") ) {
                        player.sendMessage(CC.translate("§8§l------»&r  " + secondColor + "&lCrystalAC" + "  §8§l«------"));
                        player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " togglecheck" + ChatColor.GRAY + "- Toggle any spesific check"));
                        player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " information" + ChatColor.GRAY + "- Show AC information"));
                        player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " profile" + ChatColor.GRAY + "- Show players profile's"));
                        player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " checks" + ChatColor.GRAY + "- Show all checks"));
                        player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " acban" + ChatColor.GRAY + "- Ban player as anticheat"));
                        player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " bypass" + ChatColor.GRAY + "- Bypass the anticheat"));
                        player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " alerts" + ChatColor.GRAY + "- Toggle alerts"));
                        player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " setVl" + ChatColor.GRAY + "- Set custom VL"));
                        player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " getVl" + ChatColor.GRAY + "- Get custom VL"));
                        //player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " reload"));
                        player.sendMessage(CC.translate(secondColor + "/" + "crystalac" + " gui" + ChatColor.GRAY + "- Open GUI"));
                        player.sendMessage(CC.translate("&8---------------------------"));
                    }
                } else {
                    player.sendMessage(CC.translate("&cYou don't have permission to use this command."));
                }
                return true;

            }

            else if (args[0].equalsIgnoreCase("gui")) {
                if (sender.hasPermission("ac.command.gui") || sender.hasPermission("crystalac.admin") ) {
                    player.openInventory(CrystalAC.getInstance().getMainGUI().getGui().getInventory());
                } else {
                    player.sendMessage(CC.translate("&cYou don't have permission to use this command."));
                }
                return true;
            }


            else if (args[0].equalsIgnoreCase("checks") ) {
                if (sender.hasPermission("ac.command.checks") ) {
                    player.sendMessage(CC.translate("§8§l§m----------------------------(&r &8&l(Checks) &8&l&m}-------------------------------="));
                    player.sendMessage(CC.translate("&bAimAssist &7(A-H)"));
                    player.sendMessage(CC.translate("&bAutoClicker &7(A-L)"));
                    player.sendMessage(CC.translate("&bBadPackets &7(A-L)"));
                    player.sendMessage(CC.translate("&bBlink &7(A)"));
                    player.sendMessage(CC.translate("&bFastBow &7(A)"));
                    player.sendMessage(CC.translate("&bFastBreak &7(A)"));
                    player.sendMessage(CC.translate("&bFastRefill &7(A)"));
                    player.sendMessage(CC.translate("&bFly &7(A-F)"));
                    player.sendMessage(CC.translate("&bInventory &7(A-D)"));
                    player.sendMessage(CC.translate("&bJesus &7(A-C)"));
                    player.sendMessage(CC.translate("&bKillAura &7(A-S)"));
                    player.sendMessage(CC.translate("&bNoFall &7(A-E)"));
                    player.sendMessage(CC.translate("&bNoSlowDown &7(A-B)"));
                    player.sendMessage(CC.translate("&bPhase &7(A-D)"));
                    player.sendMessage(CC.translate("&bPingSpoof &7(A)"));
                    player.sendMessage(CC.translate("&bRange &7(A-D)"));
                    player.sendMessage(CC.translate("&bScaffold &7(A-C)"));
                    player.sendMessage(CC.translate("&bSpeed &7(A-D)"));
                    player.sendMessage(CC.translate("&bStep &7(A-C)"));
                    player.sendMessage(CC.translate("&bTimer &7(A-C)"));
                    player.sendMessage(CC.translate("&bVClip &7(A-B)"));
                    player.sendMessage(CC.translate("&bVelocity &7(A-E)"));
                    player.sendMessage(CC.translate("&bWTap &7(A-B)"));
                    player.sendMessage(CC.translate("&7Total checks: &b" + ConfigurationManager.getTotalChecks()));
                    player.sendMessage(CC.translate("§8§l§m--------------------------------------------------------------------"));
                } else {
                    player.sendMessage(CC.translate("&cYou don't have permission to use this command."));
                }
                return true;

            } else if (args[0].equalsIgnoreCase("profile") || args[0].equalsIgnoreCase("prof")) {
                if (sender.hasPermission("ac.command.profile") || sender.hasPermission( "crystalac.admin") ) {

                    Player target = Bukkit.getPlayer(args[1]);

                    if(target == null) {
                        player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Player is not online."));
                    }

                    if (args.length == 1) {
                        player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /" + command + " profile " + "<player>"));
                        return true;
                    } else {


                        player.sendMessage(CC.translate("§8§l------»&r  " + secondColor + "&lCrystalAC" + "  §8§l«------"));
                        player.sendMessage(CC.translate(" "));
                        player.sendMessage(CC.translate("&bInformation for " + target.getDisplayName() + ":"));
                        player.sendMessage(CC.translate(" "));

                        if (target != null) {
                            final PlayerData playerData = CrystalAC.getInstance().getPlayerDataManager().getPlayerData(target);

                            /*
                                Time Util
                             */

                            String time;
                            if (TimesUtil.differenceTimeSecond(playerData.getJoinTime(), System.currentTimeMillis()) > 60) {
                                if (TimesUtil.differenceTimeMinutes(playerData.getJoinTime(), System.currentTimeMillis()) > 60) {
                                    time = String.format("%s hour(s)", TimesUtil.differenceTimeHour(playerData.getJoinTime(), System.currentTimeMillis()));
                                } else {
                                    time = String.format("%s minute(s)", TimesUtil.differenceTimeMinutes(playerData.getJoinTime(), System.currentTimeMillis()));
                                }
                            } else {
                                time = String.format("%s second(s)", TimesUtil.differenceTimeSecond(playerData.getJoinTime(), System.currentTimeMillis()));
                            }

                            if (playerData.getClient() != null) {
                                player.sendMessage(CC.translate("&b" + target.getDisplayName() + "&7 is on &b" + playerData.getClient().getName()));
                            } else {
                                player.sendMessage(CC.translate("&b" + target.getDisplayName() + "&7 is on &b" + "Unknown Version"));
                            }

                            int playerping = ((CraftPlayer) target).getHandle().ping;

                            player.sendMessage(CC.translate("&7Total Violations: &7(&b" + playerData.getTotalVl() + "&7)"));
                            player.sendMessage(CC.translate("&7Play time: &b" + time));
                            if(target.isOp()) {
                                player.sendMessage(CC.translate("&7Admin: &b" + "Yes"));
                            } else {
                                player.sendMessage(CC.translate("&7Admin: &b" + "No"));
                            }
                            player.sendMessage(CC.translate("&7Ping: &b" + playerping));
                            player.sendMessage(CC.translate("&8---------------------------"));

                        } else {
                            player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "That player is not online."));
                        }
                        return true;
                        }
                    } else {
                        player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "You don't have permission to use this command."));
                        return true;
                }
            }

            else if (args[0].equalsIgnoreCase("version") && args.length == 1 || args[0].equalsIgnoreCase("ver") && args.length == 1 ) {
                if (sender.hasPermission("ac.command.version") || sender.hasPermission("crystalac.admin") ) {
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> " + secondColor + "/" + command + " version" + firstColor + " <player>"));

                } else {
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "You don't have permission to use this command."));
                }
                return true;
            }



            else if (args[0].equalsIgnoreCase("alerts")) {
                if (sender.hasPermission("ac.command.alerts") ) {
                    if (args.length == 1) {
                        player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /" + command + " alerts" + "&b <normal/verbose>"));
                    } else if (args.length >= 2) {
                        if (args[1].equalsIgnoreCase("normal") || (args[1].equalsIgnoreCase("n"))) {
                            CrystalAC.getInstance().toggleAlerts(player, false);
                        } else if (args[1].equalsIgnoreCase("verbose") || (args[1].equalsIgnoreCase("v"))) {
                            CrystalAC.getInstance().toggleAlerts(player, true);
                        } else {
                            player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /" + command + " alerts" + "&b <normal/verbose>"));
                        }
                    }
                } else {
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "You don't have permission to use this command."));
                }
                return true;

            } else if (args[0].equalsIgnoreCase("information") || args[0].equalsIgnoreCase("info") || args[0].equalsIgnoreCase("information")) {
                if (sender.hasPermission("ac.command.info") ) {
                    final long usedMemory = (Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 2L / 1048576L;
                    final long allocatedMemory = Runtime.getRuntime().totalMemory() / 1048576L;
                    player.sendMessage(CC.translate("§8§l------»&r  " + secondColor + "&lCrystalAC" + "  §8§l«------"));
                    sender.sendMessage(CC.translate(secondColor + "Developer &7» " + ChatColor.GRAY + "4Remi"));
                    sender.sendMessage(CC.translate(secondColor + "Version &7» " + ChatColor.GRAY + CrystalAC.BUILD_NAME + " - BUILD: " + CrystalAC.BUILD_NUM));
                    sender.sendMessage(CC.translate(secondColor + "Server Version &7» " + ChatColor.GRAY + Bukkit.getVersion()));
                    sender.sendMessage(CC.translate(secondColor + "Bukkit Version &7» " + ChatColor.GRAY + Bukkit.getBukkitVersion()));
                    String nmsPackage = "";
                    //sender.sendMessage(CC.translate(secondColor + "Java info &7» " + ChatColor.GRAY + System.getProperty("java.version") + "; " + System.getProperty("java.vm.vendor") + "; " + System.getProperty("java.vm.name")));
                    sender.sendMessage(CC.translate(secondColor + "Load &7» " + ChatColor.GRAY + usedMemory + "/" + allocatedMemory + " MB"));
                    sender.sendMessage(CC.translate(secondColor + "Last Tick Time &7» " + ChatColor.GRAY + (System.currentTimeMillis() - MinecraftServer.az()) + "ms"));
                    List<String> plugNames = new ArrayList<>();
                    for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
                        plugNames.add(plugin.getName());
                    }
                    sender.sendMessage(CC.translate(secondColor + "All Plugins loaded " + "(" + secondColor + Bukkit.getPluginManager().getPlugins().length + "): " + ChatColor.GRAY + String.join(", ", plugNames)));
                    player.sendMessage(CC.translate("&8---------------------------"));
                } else {
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "You don't have permission to use this command."));
                }
                return true;

            } else if (args[0].equalsIgnoreCase("setVl")) {
                if (sender.hasPermission("ac.command.setvl") ) {
                    if (args.length == 1) {
                        player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /" + command + " setVl" + "&b <check> <int>"));
                    } else if (args.length == 2) {
                        player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /" + command + " setVl <check>" + "&b <int>"));
                    } else {
                        ConfigurationManager.setVl(player, args[1], Integer.parseInt(args[2]));
                    }
                } else {
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "You don't have permission to use this command."));
                }
                return true;

            } else if (args[0].equalsIgnoreCase("toggleCheck") || args[0].equalsIgnoreCase("toggleChecks")) {
                if (sender.hasPermission(command + "ac.command.togglecheck")) {
                    if (args.length == 1) {
                        player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /" + command + " togglecheck" + "&b <check>"));
                    } else {
                        ConfigurationManager.toggleCheck(player, args[1]);
                    }
                    return true;
                } else {
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "You don't have permission to use this command."));
                    return true;
                }
            } else if (args[0].equalsIgnoreCase("getVl")) {
                if (sender.hasPermission("ac.command.getvl") ) {
                    if (args.length == 1) {
                        player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Usage: /" + command + " getVl" + "&b <check>"));
                    } else if (args.length == 2) {
                        ConfigurationManager.getVl(player, args[1]);
                    }
                } else {
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "You don't have permission to use this command."));
                }
                return true;

            } else if (args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("crystalac.dev")) {
                    ConfigurationManager.reload(player);
                    return true;
                } else {
                    player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "You don't have permission to use this command."));
                    return true;
                }

            }


        } else {
            sender.sendMessage(CC.translate("&cYou cannot use CrystalAC via console, in-game only."));
            return true;
        }
        return true;
    }
}
