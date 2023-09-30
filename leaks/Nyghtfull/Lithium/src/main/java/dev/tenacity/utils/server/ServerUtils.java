package dev.tenacity.utils.server;

import dev.tenacity.utils.Utils;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class ServerUtils implements Utils {

    public static ServerData lastServer;
    private static boolean redirecting;

    public static boolean serverCheck(String ip) {
        if (mc.getCurrentServerData() == null)
            return false;

        ip = ip.toLowerCase();
        String server = mc.isSingleplayer() ? "" : mc.getCurrentServerData().serverIP.toLowerCase();
        return server.endsWith("." + ip) || server.equals(ip);
    }

    public static boolean isGeniuneHypixel() {
        return isOnHypixel() && !redirecting;
    }

    public static boolean isOnHypixel() {
        if (mc.isSingleplayer() || mc.getCurrentServerData() == null || mc.getCurrentServerData().serverIP == null)
            return false;
        String ip = mc.getCurrentServerData().serverIP.toLowerCase();
        if (ip.contains("hypixel")) {
            if (mc.thePlayer == null) return true;
            String brand = mc.thePlayer.getClientBrand();
            return brand != null && brand.startsWith("Hypixel BungeeCord");
        }
        return false;
    }

    @SuppressWarnings("ForLoopReplaceableByForEach")
    public static boolean isInLobby() {
        if (mc.theWorld == null) return true;
        List<Entity> entities = mc.theWorld.getLoadedEntityList();
        for (int i = 0; i < entities.size(); i++) {
            Entity entity = entities.get(i);
            if (entity != null && entity.getName().equals("§e§lCLICK TO PLAY")) {
                return true;
            }
        }
        return false;
    }

    @SuppressWarnings("resource")
    public static boolean isHostsRedirectingHypixel() throws IOException {
        Path value = Paths.get(System.getenv("SystemDrive") + "\\Windows\\System32\\drivers\\etc\\hosts");
        return !Files.notExists(value) && Files.lines(value).anyMatch(s -> s.toLowerCase().contains("hypixel"));
    }

    public static void updateRedirecting() throws IOException {
        redirecting = isHostsRedirectingHypixel();
    }

    public static boolean isOnSameTeam(EntityLivingBase ent) {
        if (mc.thePlayer != null) {
            String displayName = mc.thePlayer.getDisplayName().getUnformattedText();
            if (displayName.contains("§")) {
                int start = displayName.indexOf("§");
                String substring = displayName.substring(start, start + 2);
                return ent != null && ent.getDisplayName().getFormattedText().contains(substring);
            }
        }
        return false;
    }

}
