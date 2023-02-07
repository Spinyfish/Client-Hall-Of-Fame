package org.crystalpvp.anticheat.api;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.util.TimesUtil;
import org.crystalpvp.anticheat.data.player.PlayerData;
import org.crystalpvp.anticheat.database.mysql.MySQL;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class CrystalAPI {


    public static String getPlayTime(Player player) {
        PlayerData playerData = CrystalAC.getPlugin().getPlayerDataManager().getPlayerData(player);

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

        return time;
    }

    public static int getTotalFlags(Player player) {
        PlayerData playerData = CrystalAC.getPlugin().getPlayerDataManager().getPlayerData(player);

        return playerData.getTotalVl();
    }

    public static boolean isBanned(Player player) {
        PlayerData playerData = CrystalAC.getPlugin().getPlayerDataManager().getPlayerData(player);

        return playerData.isBanned();
    }

    public static String getClient(Player player) {
        PlayerData playerData = CrystalAC.getPlugin().getPlayerDataManager().getPlayerData(player);

        return String.format("%s", playerData.getClient());
    }


    public static int getAlertFlags(Player player, String check) {
        int flags = 0;

        try {
            MySQL.use();


            PreparedStatement statement = MySQL.getConn().prepareStatement("SELECT * FROM ALERTS WHERE UUID=? AND FLAG=?");
            statement.setString(1, player.getUniqueId().toString());
            statement.setString(2, check);

            ResultSet result = statement.executeQuery();

            while (result.next()) {
                flags++;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }


        return flags;
    }
}
