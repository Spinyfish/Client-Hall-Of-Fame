package org.crystalpvp.anticheat.runnables;

import org.crystalpvp.anticheat.CrystalAC;
import org.crystalpvp.anticheat.api.util.MathUtil;
import org.crystalpvp.anticheat.database.mongo.MongoDB;
import org.crystalpvp.anticheat.database.mysql.MySQL;
import org.crystalpvp.anticheat.database.sqlite.SQLite;
import org.crystalpvp.anticheat.database.utils.Logs;
import org.crystalpvp.anticheat.database.utils.Query;
import org.bson.Document;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *  Coded by 4Remi#8652
 *   DO NOT REMOVE
 */

public class InsertLogsTask implements Runnable {

    @Override
    public void run() {
        final List<Document> documents = new ArrayList<>();
        final Iterator<Logs> iterator = Logs.getQueue().iterator();
        final String db = CrystalAC.getInstance().getDb();

        switch (db) {
            case "mongo":


                while (iterator.hasNext()) {
                    final Logs log = iterator.next();
                    final Document document = new Document();

                    document.put("uuid", log.getUuid().toString());
                    document.put("flag", log.getFlag());
                    document.put("client", log.getClient());
                    document.put("ping", log.getPing());
                    document.put("tps", log.getTps());
                    document.put("timestamp", log.getTimestamp());

                    documents.add(document);

                    iterator.remove();
                }

                if (!documents.isEmpty()) {

                    try {
                        MongoDB.getInstance().getLogs().insertMany(documents);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
                break;


            case "sqlite":


                while (iterator.hasNext()) {
                    final Logs log = iterator.next();

                    try {
                        SQLite.use();
                        Query.prepare("INSERT INTO `" + SQLite.getTable() + "` (`UUID`,`FLAG`,`CLIENT`,`PING`,`TPS`,`TIMESTAMP`) VALUES (?,?,?,?,?,?)")
                                .append(log.getUuid().toString())
                                .append(log.getFlag())
                                .append(log.getClient())
                                .append(log.getPing())
                                .append(MathUtil.doubleDecimal(log.getTps(), 3))
                                .append(log.getTimestamp()).execute();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    iterator.remove();
                }
                break;


            case "mysql":


                while (iterator.hasNext()) {
                    final Logs log = iterator.next();

                    try {
                        MySQL.use();
                        Query.prepare("INSERT INTO `" + MySQL.getTable() + "` (`UUID`,`FLAG`,`CLIENT`,`PING`,`TPS`,`TIMESTAMP`) VALUES (?,?,?,?,?,?)")
                                .append(log.getUuid().toString())
                                .append(log.getFlag())
                                .append(log.getClient())
                                .append(log.getPing())
                                .append(MathUtil.doubleDecimal(log.getTps(), 3))
                                .append(log.getTimestamp()).execute();


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    iterator.remove();
                }
                break;


            case "none":

                while (iterator.hasNext()) {
                    iterator.next();

                    iterator.remove();
                }


                break;

        }
    }
}
