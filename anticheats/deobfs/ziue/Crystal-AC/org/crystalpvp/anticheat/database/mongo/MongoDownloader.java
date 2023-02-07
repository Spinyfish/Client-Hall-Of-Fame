package org.crystalpvp.anticheat.database.mongo;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.util.CC;
import org.crystalpvp.anticheat.api.util.MongoDBUtil;

import java.io.File;

public class MongoDownloader {
    public static void init() {
        try {
            File mongo_lib = new File(CrystalAC.getInstance().getDataFolder(), "mongo.jar");
            if (!mongo_lib.exists()) {
                MongoDBUtil.download(mongo_lib, "http://central.maven.org/maven2/org/mongodb/mongo-java-driver/3.5.0/mongo-java-driver-3.5.0.jar");
            }
            MongoDBUtil.injectURL(mongo_lib.toURI().toURL());
        } catch (Exception e) {
            CrystalAC.getInstance().getServer().getConsoleSender().sendMessage(CC.RED + "Failed to load MongoDB:" + e.getMessage());
        }
        CrystalAC.getInstance().getServer().getConsoleSender().sendMessage(CC.GREEN + "MongoDB's injection success!");
    }
}

