package net.minecraft.command;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandToggleDownfall extends CommandBase {
   @Override
   public String getCommandName() {
      return "toggledownfall";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.downfall.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      this.toggleDownfall();
      notifyOperators(sender, this, "commands.downfall.success", new Object[0]);
   }

   protected void toggleDownfall() {
      WorldInfo worldinfo = MinecraftServer.getServer().worldServers[0].getWorldInfo();
      worldinfo.setRaining(!worldinfo.isRaining());
   }
}
