package intent.AquaDev.aqua.command.impl.commands;

import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.command.Command;

public class SkidIrcCommand extends Command {
   public SkidIrcCommand() {
      super("irc");
   }

   @Override
   public void execute(String[] args) {
      try {
         Aqua.INSTANCE.ircClient.executeCommand(String.join(" ", args));
      } catch (Exception var3) {
      }
   }
}
