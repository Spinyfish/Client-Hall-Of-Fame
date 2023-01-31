package net.optifine.player;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import net.minecraft.src.Config;
import net.optifine.http.IFileDownloadListener;

public class PlayerConfigurationReceiver implements IFileDownloadListener {
   private String player = null;

   public PlayerConfigurationReceiver(String player) {
      this.player = player;
   }

   @Override
   public void fileDownloadFinished(String url, byte[] bytes, Throwable exception) {
      if (bytes != null) {
         try {
            String s = new String(bytes, "ASCII");
            JsonParser jsonparser = new JsonParser();
            JsonElement jsonelement = jsonparser.parse(s);
            PlayerConfigurationParser playerconfigurationparser = new PlayerConfigurationParser(this.player);
            PlayerConfiguration playerconfiguration = playerconfigurationparser.parsePlayerConfiguration(jsonelement);
            if (playerconfiguration != null) {
               playerconfiguration.setInitialized(true);
               PlayerConfigurations.setPlayerConfiguration(this.player, playerconfiguration);
            }
         } catch (Exception var9) {
            Config.dbg("Error parsing configuration: " + url + ", " + var9.getClass().getName() + ": " + var9.getMessage());
         }
      }
   }
}
