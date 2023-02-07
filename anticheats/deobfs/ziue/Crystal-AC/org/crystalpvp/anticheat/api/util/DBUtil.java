package org.crystalpvp.anticheat.api.util;

import org.crystalpvp.anticheat.CrystalAC;

import java.io.Closeable;

public class DBUtil {

    public static void close(Closeable... closeables) {
        try {
            for (Closeable closeable : closeables) if (closeable != null) closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void close(AutoCloseable... closeables) {
        try {
            for (AutoCloseable closeable : closeables) if (closeable != null) closeable.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getDBType() {
        return CrystalAC.getInstance().getConfig().getString("database").toLowerCase();
    }
}
