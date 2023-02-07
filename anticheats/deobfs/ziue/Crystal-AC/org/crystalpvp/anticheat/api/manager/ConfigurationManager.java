package org.crystalpvp.anticheat.api.manager;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.checks.CheckCategory;
import org.crystalpvp.anticheat.api.config.ConfigCursor;
import org.crystalpvp.anticheat.api.util.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

@Setter
public final class ConfigurationManager extends JavaPlugin {

    @Getter
    private static List<String> banBroadcast;
    @Getter
    private static String alerts;
    @Getter
    private static String alertsVerbose;
    @Getter
    private static String banCommand;
    @Getter
    private static PunishmentType punishmentType;
    @Getter
    private static String kickCommand;
    @Getter
    private static String configTheme;
    @Getter
    private static int totalChecks = 0;

    private File yml = new File(this.getDataFolder() + "/checks.yml");
    private FileConfiguration Config = YamlConfiguration.loadConfiguration(yml);

    private static int check = -1;

    @Getter public static List<String> ChecksList = new ArrayList<>();
    public static List<CheckCategory> ChecksCategory = new ArrayList<>();

    public static List<Boolean> Enabled = new ArrayList<>();
    public static List<Boolean> AutoBans = new ArrayList<>();
    public static List<Integer> BanVL = new ArrayList<>();



    public static void getChecks() {
        getCheck("AimAssistA", CheckCategory.COMBAT);
        getCheck("AimAssistB", CheckCategory.COMBAT);
        getCheck("AimAssistC", CheckCategory.COMBAT);
        getCheck("AimAssistD", CheckCategory.COMBAT);
        getCheck("AimAssistE", CheckCategory.COMBAT);
        getCheck("AimAssistF", CheckCategory.COMBAT);
        getCheck("AimAssistG", CheckCategory.COMBAT);
        getCheck("AimAssistH", CheckCategory.COMBAT);

        getCheck("AutoClickerA", CheckCategory.COMBAT);
        getCheck("AutoClickerB", CheckCategory.COMBAT);
        getCheck("AutoClickerC", CheckCategory.COMBAT);
        getCheck("AutoClickerD", CheckCategory.COMBAT);
        getCheck("AutoClickerE", CheckCategory.COMBAT);
        getCheck("AutoClickerF", CheckCategory.COMBAT);
        getCheck("AutoClickerG", CheckCategory.COMBAT);
        getCheck("AutoClickerH", CheckCategory.COMBAT);
        getCheck("AutoClickerI", CheckCategory.COMBAT);
        getCheck("AutoClickerJ", CheckCategory.COMBAT);
        getCheck("AutoClickerK", CheckCategory.COMBAT);
        getCheck("AutoClickerL", CheckCategory.COMBAT);
        getCheck("AutoClickerM", CheckCategory.COMBAT);
        getCheck("AutoClickerN", CheckCategory.COMBAT);
        getCheck("AutoClickerO", CheckCategory.COMBAT);

        getCheck("BadPacketsA", CheckCategory.OTHER);
        getCheck("BadPacketsB", CheckCategory.OTHER);
        getCheck("BadPacketsC", CheckCategory.OTHER);
        getCheck("BadPacketsD", CheckCategory.OTHER);
        getCheck("BadPacketsE", CheckCategory.OTHER);
        getCheck("BadPacketsF", CheckCategory.OTHER);
        getCheck("BadPacketsG", CheckCategory.OTHER);
        getCheck("BadPacketsH", CheckCategory.OTHER);
        getCheck("BadPacketsI", CheckCategory.OTHER);
        getCheck("BadPacketsJ", CheckCategory.OTHER);
        getCheck("BadPacketsK", CheckCategory.OTHER);

        getCheck("BlinkA", CheckCategory.MOVEMENT);

        getCheck("FastBowA", CheckCategory.COMBAT);

        getCheck("FastRefillA", CheckCategory.OTHER);

        getCheck("FlyA", CheckCategory.MOVEMENT);
        getCheck("FlyB", CheckCategory.MOVEMENT);
        getCheck("FlyC", CheckCategory.MOVEMENT);
        getCheck("FlyD", CheckCategory.MOVEMENT);
        getCheck("FlyE", CheckCategory.MOVEMENT);
        getCheck("FlyF", CheckCategory.MOVEMENT);
        getCheck("FlyG", CheckCategory.MOVEMENT);
        getCheck("FlyH", CheckCategory.MOVEMENT);
        getCheck("FlyI", CheckCategory.MOVEMENT);
        getCheck("FlyJ", CheckCategory.MOVEMENT);
        getCheck("FlyK", CheckCategory.MOVEMENT);

        getCheck("HitBoxA", CheckCategory.COMBAT);

        getCheck("InventoryA", CheckCategory.MOVEMENT);
        getCheck("InventoryB", CheckCategory.MOVEMENT);
        getCheck("InventoryC", CheckCategory.MOVEMENT);
        getCheck("InventoryD", CheckCategory.MOVEMENT);

        getCheck("JesusA", CheckCategory.MOVEMENT);
        getCheck("JesusB", CheckCategory.MOVEMENT);
        getCheck("JesusC", CheckCategory.MOVEMENT);
        getCheck("JesusD", CheckCategory.MOVEMENT);

        getCheck("KillAuraA", CheckCategory.COMBAT);
        getCheck("KillAuraB", CheckCategory.COMBAT);
        getCheck("KillAuraC", CheckCategory.COMBAT);
        getCheck("KillAuraD", CheckCategory.COMBAT);
        getCheck("KillAuraE", CheckCategory.COMBAT);
        getCheck("KillAuraF", CheckCategory.COMBAT);
        getCheck("KillAuraG", CheckCategory.COMBAT);
        getCheck("KillAuraH", CheckCategory.COMBAT);
        getCheck("KillAuraI", CheckCategory.COMBAT);
        getCheck("KillAuraJ", CheckCategory.COMBAT);
        getCheck("KillAuraK", CheckCategory.COMBAT);
        getCheck("KillAuraL", CheckCategory.COMBAT);
        getCheck("KillAuraM", CheckCategory.COMBAT);
        getCheck("KillAuraN", CheckCategory.COMBAT);
        getCheck("KillAuraO", CheckCategory.COMBAT);
        getCheck("KillAuraP", CheckCategory.COMBAT);
        getCheck("KillAuraQ", CheckCategory.COMBAT);
        getCheck("KillAuraR", CheckCategory.COMBAT);
        getCheck("KillAuraS", CheckCategory.COMBAT);

        getCheck("NoFallA", CheckCategory.MOVEMENT);
        getCheck("NoFallB", CheckCategory.MOVEMENT);
        getCheck("NoFallC", CheckCategory.MOVEMENT);
        getCheck("NoFallD", CheckCategory.MOVEMENT);

        getCheck("NoSlowDownA", CheckCategory.MOVEMENT);
        getCheck("NoSlowDownB", CheckCategory.MOVEMENT);

        getCheck("PhaseA", CheckCategory.MOVEMENT);
        getCheck("PhaseB", CheckCategory.MOVEMENT);

        getCheck("PingSpoofA", CheckCategory.OTHER);

        getCheck("RangeA", CheckCategory.COMBAT);
        getCheck("RangeB", CheckCategory.COMBAT);
        getCheck("RangeC", CheckCategory.COMBAT);
        getCheck("RangeD", CheckCategory.COMBAT);
        getCheck("RangeE", CheckCategory.COMBAT);
        getCheck("RangeF", CheckCategory.COMBAT);
        getCheck("RangeG", CheckCategory.COMBAT);

        getCheck("HeadRotationsA", CheckCategory.OTHER);

        getCheck("ScaffoldA", CheckCategory.OTHER);
        getCheck("ScaffoldB", CheckCategory.OTHER);
        getCheck("ScaffoldC", CheckCategory.OTHER);

        getCheck("StepA", CheckCategory.MOVEMENT);
        getCheck("StepB", CheckCategory.MOVEMENT);
        getCheck("StepC", CheckCategory.MOVEMENT);
        getCheck("StepD", CheckCategory.MOVEMENT);

        getCheck("TimerA", CheckCategory.OTHER);
        getCheck("TimerB", CheckCategory.OTHER);
        getCheck("TimerC", CheckCategory.OTHER);

        getCheck("VClipA", CheckCategory.MOVEMENT);
        getCheck("VClipB", CheckCategory.MOVEMENT);

        getCheck("VelocityA", CheckCategory.MOVEMENT);
        getCheck("VelocityB", CheckCategory.MOVEMENT);
        getCheck("VelocityC", CheckCategory.MOVEMENT);
        getCheck("VelocityD", CheckCategory.MOVEMENT);
        getCheck("VelocityE", CheckCategory.MOVEMENT);
        getCheck("VelocityF", CheckCategory.MOVEMENT);

        getCheck("WTapA", CheckCategory.MOVEMENT);
        getCheck("WTapB", CheckCategory.MOVEMENT);

        getCheck("SpeedA", CheckCategory.MOVEMENT);
        getCheck("SpeedB", CheckCategory.MOVEMENT);
        getCheck("SpeedC", CheckCategory.MOVEMENT);
        getCheck("SpeedD", CheckCategory.MOVEMENT);

    }

    public static void getCheck(String module, CheckCategory category) {
        ++totalChecks;
        ++check;
        ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getCheckFileConfig(), module);

        if (!cursor.exists("enabled")
                || !cursor.exists("autoBans.enabled")
                || !cursor.exists("autoBans.banVl")) {
            throw new RuntimeException("Missing configuration option for check " + module);
        }

        ChecksList.add(module);
        ChecksCategory.add(category);
        Enabled.add(cursor.getBoolean("enabled"));
        AutoBans.add(cursor.getBoolean("autoBans.enabled"));
        BanVL.add(cursor.getInt("autoBans.banVl"));
   }


    public static void toggleCheck(Player sender, String module) {
        ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getCheckFileConfig(), module);

        if (!cursor.exists("enabled") && cursor.exists("enabled")) {
            sender.sendMessage(CC.RED + "The check: " + module + " doesn't exist!");
            return;
        }

        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i).contains(module)) {
                if (Enabled.get(i) == true) {
                    cursor.set("enabled", false);
                    cursor.save();
                    Enabled.set(i, false);
                    sender.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &f" + "Check: &b" + module + " &cdisabled."));
                    return;
                } else if (Enabled.get(i) == false) {
                    cursor.set("enabled", true);
                    cursor.save();
                    Enabled.set(i, true);
                    sender.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &f" + "Check: &b" + module + " &aenabled."));
                }
            }
        }
    }


    public static void toggleAutoBan(Player sender, String module) {
        ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getCheckFileConfig(), module);

        if (!cursor.exists("autoBans.enabled") && cursor.exists("autoBans.enabled")) {
            sender.sendMessage(CC.RED + "The check: " + module + " doesn't exist!");
            return;
        }

        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i).contains(module)) {
                if (AutoBans.get(i) == true) {
                    cursor.set("autoBans.enabled", false);
                    cursor.save();
                    AutoBans.set(i, false);
                    sender.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &f" + "AutoBans: &b" + module + " &cdisabled."));
                    return;
                } else if (AutoBans.get(i) == false) {
                    cursor.set("autoBans.enabled", true);
                    cursor.save();
                    AutoBans.set(i, true);
                    sender.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &f" + "AutoBans: &b" + module + " &aenabled."));
                }
            }
        }
        //sender.sendMessage(CC.RED + "The check: " + module + " doesn't exist!");
    }


    public static void toggleAutoBanNoNotify(String module) {
        ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getCheckFileConfig(), module);

        if (!cursor.exists("autoBans.enabled")) {
            return;
        }

        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i).contains(module)) {
                if (AutoBans.get(i) == true) {
                    cursor.set("autoBans.enabled", false);
                    cursor.save();
                    AutoBans.set(i, false);
                    return;
                } else if (AutoBans.get(i) == false) {
                    cursor.set("autoBans.enabled", true);
                    cursor.save();
                    AutoBans.set(i, true);
                }
            }
        }
    }

    public static void setVl(Player sender, String module, Integer vl) {
        ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getCheckFileConfig(), module);

        if (!cursor.exists("autoBans.banVl")) {
            sender.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &f" + "Check: &b" + module + " &cdoesn't exist!"));
            return;
        }

        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i).contains(module)) {
                cursor.set("autoBans.banVl", vl);
                cursor.save();
                sender.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &f" + "BanVl: &b" + module + " &fset from &b" + BanVL.get(i) + " &fto &b" + vl));
                BanVL.set(i, vl);
            }
        }
    }


    public static void setVlNoNotify(String module, Integer vl) {
        ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getCheckFileConfig(), module);

        if (!cursor.exists("autoBans.banVl")) {
            return;
        }

        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i).contains(module)) {
                cursor.set("autoBans.banVl", vl);
                cursor.save();
                BanVL.set(i, vl);
            }
        }
    }

    public static void getVl(Player sender, String module) {

        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i).contains(module)) {
                sender.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &f" + "BanVL: &b" + module + "'s &fVL: &b" + BanVL.get(i)));
                return;
            }
        }
        sender.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &f" + "BanVL: &b" + module + " &fnot found."));
    }


    public static int alertVl(String module) {

        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i).contains(module)) {
                return BanVL.get(i);
            }
        }
        return 999999;
    }

    public static boolean isEnabled(String module) {
        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i) == module) {
                if (Enabled.get(i) == true) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static boolean isAutoBanning(String module) {
        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i) == module) {
                if (AutoBans.get(i) == true) {
                    return true;
                } else {
                    return false;
                }
            }
        }
        return false;
    }

    public static int banVL(String module) {
        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i) == module) {
                return BanVL.get(i);
            }
        }
        return 1000;
    }

    public static List<Integer> getChecks(String check) {

        List<Integer> checksInt = new ArrayList<>();

        for (int i = 0; i < ChecksList.size(); ++i) {
            if (ChecksList.get(i).contains(check)) {
                checksInt.add(i);
            }
        }
        return checksInt;
    }


    public static int getInfoInt(String module, String info) {
        ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getCheckFileConfig(), module);

        if (!cursor.exists(info)) {
            throw new RuntimeException("Missing configuration option");
        }

        return cursor.getInt(info);
    }



    public static String getInfoString(String module, String info) {
        ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getCheckFileConfig(), module);

        if (!cursor.exists(info)) {
            throw new RuntimeException("Missing configuration option");
        }

        return cursor.getString(info);
    }


    public static List<String> getInfoStringList(String module, String info) {
        ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getCheckFileConfig(), module);

        if (!cursor.exists(info)) {
            throw new RuntimeException("Missing configuration option");
        }

        return cursor.getStringList(info);
    }


    public static boolean getInfoBool(String module, String info) {
        ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getCheckFileConfig(), module);

        if (!cursor.exists(info)) {
            throw new RuntimeException("Missing configuration option");
        }


        return cursor.getBoolean(info);
    }



    public static void reload(Player player) {
        player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &f" + "Reloading.."));
        BanVL.clear();
        ChecksList.clear();
        Enabled.clear();
        AutoBans.clear();
        banBroadcast.clear();


        if (configTheme != CrystalAC.getInstance().getConfig().getString("theme") && !CrystalAC.getInstance().getConfig().getString("theme").equalsIgnoreCase("none")) {
            configTheme = CrystalAC.getInstance().getConfig().getString("theme");
            player.sendMessage(CC.translate("&8[&b&lCrystal&r&8] &7> &c" + "Theme can't be modified while the AC is running."));
        }

        alerts = CrystalAC.getInstance().getConfig().getString("flag-alerts");
        alertsVerbose = CrystalAC.getInstance().getConfig().getString("flag-alerts-verbose");
        banBroadcast = CrystalAC.getInstance().getConfig().getStringList("ban-broadcast");
        banCommand = CrystalAC.getInstance().getConfig().getString("ban-command");
        kickCommand = CrystalAC.getInstance().getConfig().getString("kick-command");
        punishmentType = PunishmentType.getPunishmenttype(CrystalAC.getInstance().getConfig().getString("punishment-type"));

        ConfigurationManager.getChecks();
        player.sendMessage(CC.GREEN + "CrystalAC Reloaded!");
    }


    public static void getConfigOptions() {
        configTheme = CrystalAC.getInstance().getConfig().getString("theme");

        alerts = "";
        banCommand = "";
        kickCommand = "";
        punishmentType = PunishmentType.NONE;


        alerts = CrystalAC.getInstance().getConfig().getString("flag-alerts");
        alertsVerbose = CrystalAC.getInstance().getConfig().getString("flag-alerts-verbose");
        banBroadcast = CrystalAC.getInstance().getConfig().getStringList("ban-broadcast");

        banCommand = CrystalAC.getInstance().getConfig().getString("ban-command");
        kickCommand = CrystalAC.getInstance().getConfig().getString("kick-command");
        punishmentType = PunishmentType.getPunishmenttype(CrystalAC.getInstance().getConfig().getString("punishment-type"));
    }



    public static boolean isMetricEnable() {
        return CrystalAC.getInstance().getConfig().getBoolean("metrics");
    }


    public static CheckCategory getCheckCategory(int i) {
        return ChecksCategory.get(i);
    }
    
}