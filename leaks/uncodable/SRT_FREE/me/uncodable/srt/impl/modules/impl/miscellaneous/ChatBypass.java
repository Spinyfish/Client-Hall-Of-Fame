package me.uncodable.srt.impl.modules.impl.miscellaneous;

import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.packet.EventSendChatMessage;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;
import org.apache.commons.lang3.RandomUtils;

@ModuleInfo(
   internalName = "ChatBypass",
   name = "Chat Bypass",
   desc = "Allows you to swear out of your ass without any server interference.\nFree speech is a requirement, like hating the transgenders.",
   category = Module.Category.MISCELLANEOUS,
   legit = true
)
public class ChatBypass extends Module {
   private static final char[] chars = new char[]{'⛟', '⛠', '⛡', '⛢', '⛣', '⛤', '❓'};

   public ChatBypass(int key, boolean enabled) {
      super(key, enabled);
   }

   @EventTarget(
      target = EventSendChatMessage.class
   )
   public void onChat(EventSendChatMessage e) {
      if (!e.getMessage().startsWith("/")) {
         StringBuilder builder = new StringBuilder();
         int count = 0;

         for(char character : e.getMessage().toCharArray()) {
            builder.append(character);
            if (count % 2 == 0) {
               builder.append(chars[RandomUtils.nextInt(0, chars.length)]);
            }

            ++count;
         }

         e.setMessage(builder.toString());
      }
   }
}
