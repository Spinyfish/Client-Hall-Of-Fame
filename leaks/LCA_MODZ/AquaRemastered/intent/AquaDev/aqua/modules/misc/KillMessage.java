package intent.AquaDev.aqua.modules.misc;

import events.Event;
import events.listeners.EventPacket;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import java.util.Random;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S02PacketChat;

public class KillMessage extends Module {
   private final String[] messages = new String[]{
      "Get Good Get Aqua, Tenacity = Shit?", "ac is trash", "ezs", "Hacking is Fun", "Aqua Best", "Killed BY Aqua, https://discord.gg/qnzcdrCx7Q"
   };

   public KillMessage() {
      super("KillMessage", Module.Type.Misc, "KillMessage", 0, Category.Misc);
   }

   @Override
   public void onEnable() {
      super.onEnable();
   }

   @Override
   public void onDisable() {
      super.onDisable();
   }

   @Override
   public void onEvent(Event event) {
      if (event instanceof EventPacket) {
         Packet e = EventPacket.getPacket();
         Random rnd = new Random();
         if (e instanceof S02PacketChat) {
            S02PacketChat s02PacketChat = (S02PacketChat)e;
            String cp21 = s02PacketChat.getChatComponent().getUnformattedText();
            if (cp21.contains("was killed by " + mc.session.getUsername())) {
               mc.thePlayer.sendChatMessage(this.messages[rnd.nextInt(this.messages.length)]);
            }

            if (cp21.contains("was slain by " + mc.session.getUsername())) {
               mc.thePlayer.sendChatMessage(this.messages[rnd.nextInt(this.messages.length)]);
            }
         }
      }
   }
}
