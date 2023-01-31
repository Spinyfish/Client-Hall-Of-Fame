package intent.AquaDev.aqua.command;

import intent.AquaDev.aqua.command.impl.commands.FriendCommand;
import intent.AquaDev.aqua.command.impl.commands.Global;
import intent.AquaDev.aqua.command.impl.commands.Ign;
import intent.AquaDev.aqua.command.impl.commands.SkidIrcCommand;
import intent.AquaDev.aqua.command.impl.commands.bind;
import intent.AquaDev.aqua.command.impl.commands.config;
import intent.AquaDev.aqua.command.impl.commands.toggle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandSystem {
   public static List<Command> commands = new ArrayList<>();
   public final String Chat_Prefix = ".";

   public CommandSystem() {
      this.addCommand(new bind());
      this.addCommand(new toggle());
      this.addCommand(new config());
      this.addCommand(new SkidIrcCommand());
      this.addCommand(new Ign());
      this.addCommand(new Global());
      this.addCommand(new FriendCommand());
   }

   public void addCommand(Command cmd) {
      commands.add(cmd);
   }

   public boolean execute(String text) {
      if (!text.startsWith(".")) {
         return false;
      } else {
         text = text.substring(1);
         String[] arguments = text.split(" ");

         for(Command cmd : commands) {
            if (cmd.getName().equalsIgnoreCase(arguments[0])) {
               String[] args = Arrays.copyOfRange(arguments, 1, arguments.length);
               cmd.execute(args);
               return true;
            }
         }

         return false;
      }
   }
}
