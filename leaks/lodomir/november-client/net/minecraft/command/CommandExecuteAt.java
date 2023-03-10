/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.IChatComponent;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;

public class CommandExecuteAt
extends CommandBase {
    @Override
    public String getCommandName() {
        return "execute";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.execute.usage";
    }

    @Override
    public void processCommand(final ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 5) {
            throw new WrongUsageException("commands.execute.usage", new Object[0]);
        }
        final Entity entity = CommandExecuteAt.getEntity(sender, args[0], Entity.class);
        final double d0 = CommandExecuteAt.parseDouble(entity.posX, args[1], false);
        final double d1 = CommandExecuteAt.parseDouble(entity.posY, args[2], false);
        final double d2 = CommandExecuteAt.parseDouble(entity.posZ, args[3], false);
        final BlockPos blockpos = new BlockPos(d0, d1, d2);
        int i = 4;
        if ("detect".equals(args[4]) && args.length > 10) {
            World world = entity.getEntityWorld();
            double d3 = CommandExecuteAt.parseDouble(d0, args[5], false);
            double d4 = CommandExecuteAt.parseDouble(d1, args[6], false);
            double d5 = CommandExecuteAt.parseDouble(d2, args[7], false);
            Block block = CommandExecuteAt.getBlockByText(sender, args[8]);
            int k = CommandExecuteAt.parseInt(args[9], -1, 15);
            BlockPos blockpos1 = new BlockPos(d3, d4, d5);
            IBlockState iblockstate = world.getBlockState(blockpos1);
            if (iblockstate.getBlock() != block || k >= 0 && iblockstate.getBlock().getMetaFromState(iblockstate) != k) {
                throw new CommandException("commands.execute.failed", "detect", entity.getName());
            }
            i = 10;
        }
        String s = CommandExecuteAt.buildString(args, i);
        ICommandSender icommandsender = new ICommandSender(){

            @Override
            public String getName() {
                return entity.getName();
            }

            @Override
            public IChatComponent getDisplayName() {
                return entity.getDisplayName();
            }

            @Override
            public void addChatMessage(IChatComponent component) {
                sender.addChatMessage(component);
            }

            @Override
            public boolean canCommandSenderUseCommand(int permLevel, String commandName) {
                return sender.canCommandSenderUseCommand(permLevel, commandName);
            }

            @Override
            public BlockPos getPosition() {
                return blockpos;
            }

            @Override
            public Vec3 getPositionVector() {
                return new Vec3(d0, d1, d2);
            }

            @Override
            public World getEntityWorld() {
                return entity.worldObj;
            }

            @Override
            public Entity getCommandSenderEntity() {
                return entity;
            }

            @Override
            public boolean sendCommandFeedback() {
                MinecraftServer minecraftserver = MinecraftServer.getServer();
                return minecraftserver == null || minecraftserver.worldServers[0].getGameRules().getBoolean("commandBlockOutput");
            }

            @Override
            public void setCommandStat(CommandResultStats.Type type, int amount) {
                entity.setCommandStat(type, amount);
            }
        };
        ICommandManager icommandmanager = MinecraftServer.getServer().getCommandManager();
        try {
            int j = icommandmanager.executeCommand(icommandsender, s);
            if (j < 1) {
                throw new CommandException("commands.execute.allInvocationsFailed", s);
            }
        }
        catch (Throwable var23) {
            throw new CommandException("commands.execute.failed", s, entity.getName());
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandExecuteAt.getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : (args.length > 1 && args.length <= 4 ? CommandExecuteAt.func_175771_a(args, 1, pos) : (args.length > 5 && args.length <= 8 && "detect".equals(args[4]) ? CommandExecuteAt.func_175771_a(args, 5, pos) : (args.length == 9 && "detect".equals(args[4]) ? CommandExecuteAt.getListOfStringsMatchingLastWord(args, Block.blockRegistry.getKeys()) : null)));
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}

