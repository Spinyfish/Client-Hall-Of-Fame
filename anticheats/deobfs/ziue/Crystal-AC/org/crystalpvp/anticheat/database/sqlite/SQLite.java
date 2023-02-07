package org.crystalpvp.anticheat.database.sqlite;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.config.ConfigCursor;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.database.utils.Query;
import lombok.Getter;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;

public class SQLite {

    @Getter public static String table = "";

    public static Connection conn;


    public static void init() {
        try {
            Class.forName("org.sqlite.JDBC");


            ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getMainFileConfig(), "sqlite");

            if (!cursor.exists("authentication.table")) {
                throw new RuntimeException("Missing configuration option");
            }

            table = cursor.getString("authentication.table");

            String url = "jdbc:sqlite:" + CrystalAC.getInstance().getDataFolder().getAbsolutePath() + File.separator + "database.sqlite";
            conn = DriverManager.getConnection(url);
            Query.use(conn);

            try {
                Query.prepare("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
                        "`UUID` TEXT NOT NULL," +
                        "`FLAG` TEXT NOT NULL," +
                        "`CLIENT` TEXT NOT NULL," +
                        "`PING` BIGINT NOT NULL," +
                        "`TPS` DOUBLE NOT NULL," +
                        "`TIMESTAMP` BIGINT NOT NULL)").execute();
            } catch (Exception e) {
                CrystalAC.getInstance().getServer().getConsoleSender().sendMessage(CC.RED + "Failed to create SQLite table: " + e.getMessage());
                e.printStackTrace();
            }


            CrystalAC.getInstance().getServer().getConsoleSender().sendMessage(CC.GREEN + "Connection to MySQL has been established.");
        } catch (Exception e) {
            CrystalAC.getInstance().getServer().getConsoleSender().sendMessage(CC.RED + "Failed to load SQLite: " + e.getMessage());
            e.printStackTrace();
        }
    }



    public static void use() {
        try {
            if (conn.isClosed()) {
                String url = "jdbc:sqlite:" + CrystalAC.getInstance().getDataFolder().getAbsolutePath() + File.separator + "database.sqlite";
                conn = DriverManager.getConnection(url);
                Query.use(conn);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
