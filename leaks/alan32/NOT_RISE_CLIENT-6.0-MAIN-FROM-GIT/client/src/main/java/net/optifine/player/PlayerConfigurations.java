package net.optifine.player;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.model.ModelBiped;
import net.optifine.http.FileDownloadThread;
import net.optifine.http.HttpUtils;

import java.util.HashMap;
import java.util.Map;

public class PlayerConfigurations {
    private static Map mapConfigurations = null;
    private static final boolean reloadPlayerItems = Boolean.getBoolean("player.models.reload");
    private static long timeReloadPlayerItemsMs = System.currentTimeMillis();

    public static void renderPlayerItems(final ModelBiped modelBiped, final AbstractClientPlayer player, final float scale, final float partialTicks) {
        final PlayerConfiguration playerconfiguration = getPlayerConfiguration(player);

        if (playerconfiguration != null) {
            playerconfiguration.renderPlayerItems(modelBiped, player, scale, partialTicks);
        }
    }

    public static synchronized PlayerConfiguration getPlayerConfiguration(final AbstractClientPlayer player) {
        if (reloadPlayerItems && System.currentTimeMillis() > timeReloadPlayerItemsMs + 5000L) {
            final AbstractClientPlayer abstractclientplayer = Minecraft.getMinecraft().thePlayer;

            if (abstractclientplayer != null) {
                setPlayerConfiguration(abstractclientplayer.getNameClear(), null);
                timeReloadPlayerItemsMs = System.currentTimeMillis();
            }
        }

        final String s1 = player.getNameClear();

        if (s1 == null) {
            return null;
        } else {
            PlayerConfiguration playerconfiguration = (PlayerConfiguration) getMapConfigurations().get(s1);

            if (playerconfiguration == null) {
                playerconfiguration = new PlayerConfiguration();
                getMapConfigurations().put(s1, playerconfiguration);
                final PlayerConfigurationReceiver playerconfigurationreceiver = new PlayerConfigurationReceiver(s1);
                final String s = HttpUtils.getPlayerItemsUrl() + "/users/" + s1 + ".cfg";
                final FileDownloadThread filedownloadthread = new FileDownloadThread(s, playerconfigurationreceiver);
                filedownloadthread.start();
            }

            return playerconfiguration;
        }
    }

    public static synchronized void setPlayerConfiguration(final String player, final PlayerConfiguration pc) {
        getMapConfigurations().put(player, pc);
    }

    private static Map getMapConfigurations() {
        if (mapConfigurations == null) {
            mapConfigurations = new HashMap();
        }

        return mapConfigurations;
    }
}
