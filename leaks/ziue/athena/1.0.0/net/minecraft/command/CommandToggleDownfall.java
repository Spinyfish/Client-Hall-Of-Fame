package net.minecraft.command;

import net.minecraft.server.*;
import net.minecraft.world.storage.*;

public class CommandToggleDownfall extends CommandBase
{
    @Override
    public String getCommandName() {
        return "toggledownfall";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.downfall.usage";
    }
    
    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        this.toggleDownfall();
        CommandBase.notifyOperators(sender, this, "commands.downfall.success", new Object[0]);
    }
    
    protected void toggleDownfall() {
        final WorldInfo worldinfo = MinecraftServer.getServer().worldServers[0].getWorldInfo();
        worldinfo.setRaining(!worldinfo.isRaining());
    }
}
