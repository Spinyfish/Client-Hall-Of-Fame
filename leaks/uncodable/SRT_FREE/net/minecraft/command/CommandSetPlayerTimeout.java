package net.minecraft.command;

import net.minecraft.server.MinecraftServer;

public class CommandSetPlayerTimeout extends CommandBase {
   @Override
   public String getCommandName() {
      return "setidletimeout";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 3;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.setidletimeout.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length != 1) {
         throw new WrongUsageException("commands.setidletimeout.usage");
      } else {
         int i = parseInt(args[0], 0);
         MinecraftServer.getServer().setPlayerIdleTimeout(i);
         notifyOperators(sender, this, "commands.setidletimeout.success", new Object[]{i});
      }
   }
}
