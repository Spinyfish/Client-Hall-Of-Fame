package net.minecraft.command;

import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.WorldSettings;

public class CommandDefaultGameMode extends CommandGameMode
{
    public String getCommandName()
    {
        return "defaultgamemode";
    }

    public String getCommandUsage(ICommandSender sender)
    {
        return "commands.defaultgamemode.usage";
    }

    public void processCommand(ICommandSender sender, String[] args) throws CommandException
    {
        if (args.length <= 0)
        {
            throw new WrongUsageException("commands.defaultgamemode.usage", new Object[0]);
        }
        else
        {
            WorldSettings.GameType worldsettings$gametype = this.getGameModeFromCommand(sender, args[0]);
            this.setGameType(worldsettings$gametype);
            notifyOperators(sender, this, "commands.defaultgamemode.success", new Object[] {new ChatComponentTranslation("gameMode." + worldsettings$gametype.getName(), new Object[0])});
        }
    }

    protected void setGameType(WorldSettings.GameType gameMode)
    {
        MinecraftServer minecraftserver = MinecraftServer.getServer();
        minecraftserver.setGameType(gameMode);

        if (minecraftserver.getForceGamemode())
        {
            for (EntityPlayerMP entityplayermp : MinecraftServer.getServer().getConfigurationManager().getPlayerList())
            {
                entityplayermp.setGameType(gameMode);
                entityplayermp.fallDistance = 0.0F;
            }
        }
    }
}
