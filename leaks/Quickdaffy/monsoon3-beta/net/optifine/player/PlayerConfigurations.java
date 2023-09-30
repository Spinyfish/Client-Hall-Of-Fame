/*
 * Decompiled with CFR 0.152.
 */
package net.optifine.player;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.model.ModelBiped;
import net.optifine.http.FileDownloadThread;
import net.optifine.http.HttpUtils;
import net.optifine.player.PlayerConfiguration;
import net.optifine.player.PlayerConfigurationReceiver;

public class PlayerConfigurations {
    private static Map mapConfigurations = null;
    private static boolean reloadPlayerItems = Boolean.getBoolean("player.models.reload");
    private static long timeReloadPlayerItemsMs = System.currentTimeMillis();

    public static void renderPlayerItems(ModelBiped modelBiped, AbstractClientPlayer player, float scale, float partialTicks) {
        PlayerConfiguration playerconfiguration = PlayerConfigurations.getPlayerConfiguration(player);
        if (playerconfiguration != null) {
            playerconfiguration.renderPlayerItems(modelBiped, player, scale, partialTicks);
        }
    }

    public static synchronized PlayerConfiguration getPlayerConfiguration(AbstractClientPlayer player) {
        String s1;
        EntityPlayerSP abstractclientplayer;
        if (reloadPlayerItems && System.currentTimeMillis() > timeReloadPlayerItemsMs + 5000L && (abstractclientplayer = Minecraft.getMinecraft().thePlayer) != null) {
            PlayerConfigurations.setPlayerConfiguration(abstractclientplayer.getNameClear(), null);
            timeReloadPlayerItemsMs = System.currentTimeMillis();
        }
        if ((s1 = player.getNameClear()) == null) {
            return null;
        }
        PlayerConfiguration playerconfiguration = (PlayerConfiguration)PlayerConfigurations.getMapConfigurations().get(s1);
        if (playerconfiguration == null) {
            playerconfiguration = new PlayerConfiguration();
            PlayerConfigurations.getMapConfigurations().put(s1, playerconfiguration);
            PlayerConfigurationReceiver playerconfigurationreceiver = new PlayerConfigurationReceiver(s1);
            String s = HttpUtils.getPlayerItemsUrl() + "/users/" + s1 + ".cfg";
            FileDownloadThread filedownloadthread = new FileDownloadThread(s, playerconfigurationreceiver);
            filedownloadthread.start();
        }
        return playerconfiguration;
    }

    public static synchronized void setPlayerConfiguration(String player, PlayerConfiguration pc) {
        PlayerConfigurations.getMapConfigurations().put(player, pc);
    }

    private static Map getMapConfigurations() {
        if (mapConfigurations == null) {
            mapConfigurations = new HashMap();
        }
        return mapConfigurations;
    }
}

