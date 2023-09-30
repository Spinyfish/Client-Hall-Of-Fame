/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.misc;

import wtf.monsoon.api.util.Util;

public final class ServerUtil
extends Util {
    public static boolean isOnServer(String ip) {
        if (mc.isSingleplayer()) {
            return false;
        }
        return ServerUtil.getCurrentServerIP().endsWith(ip);
    }

    public static String getCurrentServerIP() {
        if (mc.isSingleplayer()) {
            return "Singleplayer";
        }
        return ServerUtil.mc.getCurrentServerData().serverIP;
    }

    public static boolean isHypixel() {
        return ServerUtil.isOnServer("hypixel.net") || ServerUtil.isOnServer("ilovecatgirls.xyz");
    }
}

