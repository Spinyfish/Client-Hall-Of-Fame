/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.utils.world;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;

public final class ServerUtils {
    private static final Map<String, Long> serverIpPingCache;
    public static long timeJoined;
    public static String serverIp;
    private static final String hypixel = "hypixel.net";
    private static final Minecraft mc;

    private ServerUtils() {
    }

    public static void update(String ip, long ping) {
        serverIpPingCache.put(ip, ping);
    }

    public static long getPingToServer(String ip) {
        return serverIpPingCache.get(ip);
    }

    public static boolean isOnServer(String ip) {
        return !mc.isSingleplayer() && ServerUtils.getCurrentServerIP().endsWith(ip);
    }

    public static String getCurrentServerIP() {
        return mc.isSingleplayer() ? "Singleplayer" : ServerUtils.mc.getCurrentServerData().serverIP;
    }

    public static boolean isOnHypixel() {
        return ServerUtils.isOnServer(hypixel);
    }

    public static long getPingToCurrentServer() {
        return mc.isSingleplayer() ? 0L : ServerUtils.getPingToServer(ServerUtils.getCurrentServerIP());
    }

    public static String getSessionLengthString() {
        serverIp = mc.getCurrentServerData() != null ? ServerUtils.mc.getCurrentServerData().serverIP : "Singleplayer";
        long totalSeconds = (System.currentTimeMillis() - timeJoined) / 1000L;
        long hours = totalSeconds / 3600L;
        long minutes = totalSeconds % 3600L / 60L;
        long seconds = totalSeconds % 60L;
        return (hours > 0L ? hours + "h " : "") + minutes + "m " + seconds + "s";
    }

    static {
        timeJoined = System.currentTimeMillis();
        mc = Minecraft.getMinecraft();
        serverIpPingCache = new HashMap<String, Long>();
    }
}

