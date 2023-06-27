package net.minecraft.command.server;

import net.minecraft.server.*;
import net.minecraft.network.play.server.*;
import net.minecraft.network.*;
import net.minecraft.util.*;
import net.minecraft.command.*;
import java.util.*;

public class CommandSetDefaultSpawnpoint extends CommandBase
{
    @Override
    public String getCommandName() {
        return "setworldspawn";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.setworldspawn.usage";
    }
    
    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        BlockPos blockpos;
        if (args.length == 0) {
            blockpos = CommandBase.getCommandSenderAsPlayer(sender).getPosition();
        }
        else {
            if (args.length != 3 || sender.getEntityWorld() == null) {
                throw new WrongUsageException("commands.setworldspawn.usage", new Object[0]);
            }
            blockpos = CommandBase.parseBlockPos(sender, args, 0, true);
        }
        sender.getEntityWorld().setSpawnPoint(blockpos);
        MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new S05PacketSpawnPosition(blockpos));
        CommandBase.notifyOperators(sender, this, "commands.setworldspawn.success", blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }
    
    @Override
    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return (args.length > 0 && args.length <= 3) ? CommandBase.func_175771_a(args, 0, pos) : null;
    }
}
