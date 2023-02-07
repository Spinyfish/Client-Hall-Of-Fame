package org.crystalpvp.anticheat.database.sqlite;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.api.util.MongoDBUtil;

import java.io.File;

public class SQLiteDownloader {

    public static void init() {
        try {
            File sqlite_lib = new File(CrystalAC.getInstance().getDataFolder(), "mongo.jar");
            if (!sqlite_lib.exists()) {
                MongoDBUtil.download(sqlite_lib, "https://bitbucket.org/xerial/sqlite-jdbc/downloads/sqlite-jdbc-3.19.3.jar");
            }
            MongoDBUtil.injectURL(sqlite_lib.toURI().toURL());
        } catch (Exception e) {
            CrystalAC.getInstance().getServer().getConsoleSender().sendMessage(CC.RED + "Failed to load SQLite:" + e.getMessage());
        }
        CrystalAC.getInstance().getServer().getConsoleSender().sendMessage(CC.GREEN + "SQLite's injection success!");
    }

}
