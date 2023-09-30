/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command.server;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.network.play.server.S05PacketSpawnPosition;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

public class CommandSetDefaultSpawnpoint
extends CommandBase {
    @Override
    public String getCommandName() {
        return "setworldspawn";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.setworldspawn.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        BlockPos blockpos;
        if (args.length == 0) {
            blockpos = CommandSetDefaultSpawnpoint.getCommandSenderAsPlayer(sender).getPosition();
        } else {
            if (args.length != 3 || sender.getEntityWorld() == null) {
                throw new WrongUsageException("commands.setworldspawn.usage", new Object[0]);
            }
            blockpos = CommandSetDefaultSpawnpoint.parseBlockPos(sender, args, 0, true);
        }
        sender.getEntityWorld().setSpawnPoint(blockpos);
        MinecraftServer.getServer().getConfigurationManager().sendPacketToAllPlayers(new S05PacketSpawnPosition(blockpos));
        CommandSetDefaultSpawnpoint.notifyOperators(sender, (ICommand)this, "commands.setworldspawn.success", blockpos.getX(), blockpos.getY(), blockpos.getZ());
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length > 0 && args.length <= 3 ? CommandSetDefaultSpawnpoint.func_175771_a(args, 0, pos) : null;
    }
}

