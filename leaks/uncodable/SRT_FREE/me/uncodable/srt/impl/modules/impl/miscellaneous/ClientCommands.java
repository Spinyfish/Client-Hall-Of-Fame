package me.uncodable.srt.impl.modules.impl.miscellaneous;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.commands.client.api.Command;
import me.uncodable.srt.impl.events.api.EventTarget;
import me.uncodable.srt.impl.events.events.packet.EventSendChatMessage;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.ModuleInfo;

@ModuleInfo(
   internalName = "ClientCommands",
   name = "Client Commands",
   desc = "Allows you to command the client to do what you want to do.\nYou're now a wife beater telling your wife to make a sandwich!",
   category = Module.Category.MISCELLANEOUS,
   legit = true
)
public class ClientCommands extends Module {
   public ClientCommands(int key, boolean enabled) {
      super(key, enabled);
   }

   @EventTarget(
      target = EventSendChatMessage.class
   )
   public void onChat(EventSendChatMessage e) {
      if (e.getMessage().startsWith(".")) {
         e.setCancelled(true);
         if (e.getMessage().equals(".")) {
            Ries.INSTANCE
               .msg(
                  "Failed to execute your command. Did you mean to send a period to global chat? To send periods to the global chat, disable the §bclient §bcommands module in the miscellaneous category."
               );
            return;
         }

         String[] args = e.getMessage().replace(".", "").split(" ");
         boolean pass = false;

         for(Command command : Ries.INSTANCE.getCommandManager().getCommands()) {
            if (command.getInfo().name().equalsIgnoreCase(args[0])) {
               if (Ries.INSTANCE.getModuleManager().getModuleByName("Safeguard").isEnabled() && !command.getInfo().legit()) {
                  Ries.INSTANCE
                     .msg(
                        String.format(
                           "Command \"%s\" was not invoked to provide a fair advantage. You cannot execute illegitimate commands while §bsafeguard§7 is enabled.",
                           command.getInfo().name()
                        )
                     );
                  return;
               }

               command.exec(args);
               pass = true;
               break;
            }
         }

         if (!pass) {
            Ries.INSTANCE.msg("Failed to execute your command. The command does not appear to exist. Execute \".help\" for a list of valid commands.");
         }
      }
   }
}
