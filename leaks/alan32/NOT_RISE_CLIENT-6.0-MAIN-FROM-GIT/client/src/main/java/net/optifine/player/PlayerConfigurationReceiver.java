package net.optifine.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.src.Config;
import net.optifine.http.IFileDownloadListener;

import java.nio.charset.StandardCharsets;

public class PlayerConfigurationReceiver implements IFileDownloadListener {
    private String player = null;

    public PlayerConfigurationReceiver(final String player) {
        this.player = player;
    }

    public void fileDownloadFinished(final String url, final byte[] bytes, final Throwable exception) {
        if (bytes != null) {
            try {
                final String s = new String(bytes, StandardCharsets.US_ASCII);
                final JsonParser jsonparser = new JsonParser();
                final JsonElement jsonelement = jsonparser.parse(s);
                final PlayerConfigurationParser playerconfigurationparser = new PlayerConfigurationParser(this.player);
                final PlayerConfiguration playerconfiguration = playerconfigurationparser.parsePlayerConfiguration(jsonelement);

                if (playerconfiguration != null) {
                    playerconfiguration.setInitialized(true);
                    PlayerConfigurations.setPlayerConfiguration(this.player, playerconfiguration);
                }
            } catch (final Exception exception1) {
                Config.dbg("Error parsing configuration: " + url + ", " + exception1.getClass().getName() + ": " + exception1.getMessage());
            }
        }
    }
}
