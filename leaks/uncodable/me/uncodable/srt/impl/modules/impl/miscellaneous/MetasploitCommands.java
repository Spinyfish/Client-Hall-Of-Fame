package me.uncodable.srt.impl.modules.impl.miscellaneous;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.commands.metasploit.api.MetasploitCommand;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.packet.EventSendChatMessage;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "MetasploitCommands",
   name = "Metasploit Commands",
   desc = "Allows you to use Metasploit modules in SRT.\nExperience the true hacker-man experience.",
   category = Module.Category.MISCELLANEOUS,
   legit = true
)
public class MetasploitCommands extends Module {
   public MetasploitCommands(int key, boolean enabled) {
      super(key, enabled);
   }

   @EventTarget(
      target = EventSendChatMessage.class
   )
   public void onChat(EventSendChatMessage e) {
      if (e.getMessage().startsWith("->")) {
         boolean hit = false;
         e.setCancelled(true);

         for(MetasploitCommand command : Ries.INSTANCE.getMetasploitCommandManager().getMetasploitCommands()) {
            if (e.getMessage().toLowerCase().split("->")[1].startsWith(command.getInfo().name().toLowerCase()) && !hit) {
               Ries.INSTANCE.msg(String.format("Executing the Metasploit Framework command \"%s...\"", command.getInfo().name()));
               command.exec(e.getMessage().split(" "));
               hit = true;
            }
         }

         if (!hit) {
            Ries.INSTANCE.msg("Unknown Metasploit Framework command.");
         }
      }
   }
}
