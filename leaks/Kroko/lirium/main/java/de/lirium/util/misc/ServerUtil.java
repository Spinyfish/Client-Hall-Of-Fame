package de.lirium.util.misc;

import de.lirium.Client;
import god.buddy.aot.BCompiler;
import me.errordev.http.resolver.ResolvedIP;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;

public class ServerUtil {

    public static String lastIP;
    public static int lastPort;
    public static ServerData lastData;
    public static long joinedTime;
    private static final Minecraft mc = Minecraft.getMinecraft();
    public static ResolvedIP lastResolvedIP;

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static boolean isCubeCraft() {
        if (!(Client.INSTANCE.getProfileManager().get() != null && Client.INSTANCE.getProfileManager().get().getName().equalsIgnoreCase("Cubecraft")))
            return false;
        return isOnServer("cubecraft.net");
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static boolean isHypixel() {
        //if (!(Client.INSTANCE.getProfileManager().get() != null && Client.INSTANCE.getProfileManager().get().getName().equalsIgnoreCase("Hypixel")))
        //    return false;
        return isOnServer("hypixel.net");
    }

    @BCompiler(aot = BCompiler.AOT.AGGRESSIVE)
    public static boolean isOnServer(String server) {
        return mc.getCurrentServerData() != null
                && (lastIP.endsWith("." + server)
                || lastIP.endsWith("." + server + "."))
                && !mc.isSingleplayer() && (System.currentTimeMillis() - joinedTime <= 20000 || lastResolvedIP != null)
                && (lastResolvedIP == null || !lastResolvedIP.getResolvedCanonHost().contains("127.0.0.1") && !lastResolvedIP.getResolvedIP().contains("127.0.0.1"));
    }

}