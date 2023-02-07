package org.crystalpvp.anticheat.database.mysql;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.config.ConfigCursor;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.database.utils.Query;
import lombok.Getter;

import java.sql.Connection;
import java.sql.DriverManager;

public class MySQL {

    public static String host = "";
    public static String port = "";
    public static String ssl = "";
    public static String database = "";
    public static String username = "";
    public static String password = "";
    @Getter public static String table = "";

    @Getter private static Connection conn;

    public static void init() {
        try {
            if (conn == null || conn.isClosed()) {
                Class.forName("com.mysql.jdbc.Driver");

                ConfigCursor cursor = new ConfigCursor(CrystalAC.getInstance().getMainFileConfig(), "mysql");

                if (!cursor.exists("host")
                        || !cursor.exists("port")
                        || !cursor.exists("database")
                        || !cursor.exists("ssl")
                        || !cursor.exists("authentication.username")
                        || !cursor.exists("authentication.password")
                        || !cursor.exists("authentication.table")) {
                    throw new RuntimeException("Missing configuration option");
                }

                host = cursor.getString("host");
                port = cursor.getString("port");
                ssl = cursor.getString("ssl");
                database = cursor.getString("database");

                username = cursor.getString("authentication.username");
                password = cursor.getString("authentication.password");
                table = cursor.getString("authentication.table");

                conn = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/?useSSL=" + ssl, username, password);
                Query.use(conn);
                Query.prepare("CREATE DATABASE IF NOT EXISTS `" + database + "`").execute();
                Query.prepare("USE `" + database + "`").execute();

                try {
                    Query.prepare("CREATE TABLE IF NOT EXISTS `" + table + "` (" +
                            "`UUID` TEXT NOT NULL," +
                            "`FLAG` TEXT NOT NULL," +
                            "`CLIENT` TEXT NOT NULL," +
                            "`PING` BIGINT NOT NULL," +
                            "`TPS` DOUBLE NOT NULL," +
                            "`TIMESTAMP` BIGINT NOT NULL)").execute();
                } catch (Exception e) {
                    CrystalAC.getInstance().getServer().getConsoleSender().sendMessage(CC.RED + "Failed to create MySQL table: " + e.getMessage());
                    e.printStackTrace();
                }

                CrystalAC.getInstance().getServer().getConsoleSender().sendMessage(CC.GREEN + "Connection to MySQL has been established.");
            }
        } catch (Exception e) {
            CrystalAC.getInstance().getServer().getConsoleSender().sendMessage(CC.RED + "Failed to load MySQL: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void use() {
        try {
            init();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
