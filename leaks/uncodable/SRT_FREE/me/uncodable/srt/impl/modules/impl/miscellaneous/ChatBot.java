package me.uncodable.srt.impl.modules.impl.miscellaneous;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.packet.EventPacket;
import me.uncodable.srt.impl.events.events.packet.EventSendChatMessage;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import me.uncodable.srt.impl.utils.AIUtils;
import me.uncodable.srt.impl.utils.EntityUtils;
import me.uncodable.srt.impl.utils.PacketUtils;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.network.play.server.S02PacketChat;

@ModuleInfo(
   internalName = "ChatBot",
   name = "Chat Bot",
   desc = "Allows you to host a chat bot AI with commands.",
   category = Module.Category.MISCELLANEOUS,
   exp = true
)
public class ChatBot extends Module {
   private static String LAST_CLIENT_MESSAGE;
   private static boolean MESSAGE_HAS_PLAYER_NAME;
   public static final me.uncodable.srt.impl.utils.Timer TIMER = new me.uncodable.srt.impl.utils.Timer();

   public ChatBot(int key, boolean enabled) {
      super(key, enabled);
   }

   @Override
   public void onEnable() {
      Ries.INSTANCE.msg("Enabling Chat Bot AI.");
   }

   @Override
   public void onDisable() {
      Ries.INSTANCE.msg("Disabling Chat Bot AI.");
   }

   @EventTarget(
      target = EventSendChatMessage.class
   )
   public void onChat(EventSendChatMessage e) {
      LAST_CLIENT_MESSAGE = e.getMessage();
   }

   @EventTarget(
      target = EventPacket.class
   )
   public void onPacket(EventPacket e) {
      if (e.getPacket() instanceof S02PacketChat) {
         S02PacketChat packet = PacketUtils.getPacket(e.getPacket());
         String message = packet.getChatComponent().getUnformattedText();
         AIUtils.logChat(message);
         if (!TIMER.elapsed(300L) || message.contains(LAST_CLIENT_MESSAGE)) {
            return;
         }

         for(NetworkPlayerInfo networkplayerinfo : EntityUtils.getPlayerList()) {
            if (message.toLowerCase().contains(EntityUtils.getPlayerName(networkplayerinfo).toLowerCase())) {
               MESSAGE_HAS_PLAYER_NAME = true;
            }
         }

         if (MESSAGE_HAS_PLAYER_NAME) {
            AIUtils.respond(message);
            MESSAGE_HAS_PLAYER_NAME = false;
         }

         TIMER.reset();
      }
   }
}
