package me.uncodable.srt.impl.commands.client.impl;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.commands.client.api.Command;
import me.uncodable.srt.impl.commands.client.api.CommandInfo;
import me.uncodable.srt.impl.utils.ConfigUtils;

@CommandInfo(
   name = "Config",
   desc = "Allows you to perform configuration manipulation; allows you to load or save configurations.",
   usage = ".config save <config file> OR .config load <config file> [load binds]"
)
public class Configuration extends Command {
   @Override
   public void exec(String[] args) {
      switch(args.length) {
         case 3:
            String var3 = args[1].toLowerCase();
            switch(var3) {
               case "save":
                  if (!ConfigUtils.saveConfiguration(args[2])) {
                     Ries.INSTANCE.msg("Failed to save the current configuration.");
                     return;
                  }

                  Ries.INSTANCE.msg("Successfully saved the current configuration.");
                  return;
               case "load":
                  if (!ConfigUtils.loadConfiguration(args[2], false)) {
                     Ries.INSTANCE.msg("Failed to load the provided configuration.");
                     return;
                  }

                  Ries.INSTANCE.msg("Successfully loaded the provided configuration.");
                  return;
               default:
                  this.printUsage();
                  return;
            }
         case 4:
            boolean loadBinds = Boolean.parseBoolean(args[3]);
            if (args[1].equalsIgnoreCase("load")) {
               if (!ConfigUtils.loadConfiguration(args[2], loadBinds)) {
                  Ries.INSTANCE.msg("Failed to load the provided configuration.");
                  return;
               }

               Ries.INSTANCE.msg("Successfully loaded the provided configuration.");
               return;
            }

            this.printUsage();
            return;
         default:
            this.printUsage();
      }
   }
}
