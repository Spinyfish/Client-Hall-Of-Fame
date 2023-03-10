/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.command;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityCommandBlock;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandStats
extends CommandBase {
    @Override
    public String getCommandName() {
        return "stats";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.stats.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        CommandResultStats commandresultstats;
        CommandResultStats.Type commandresultstats$type;
        int i;
        boolean flag;
        if (args.length < 1) {
            throw new WrongUsageException("commands.stats.usage", new Object[0]);
        }
        if (args[0].equals("entity")) {
            flag = false;
        } else {
            if (!args[0].equals("block")) {
                throw new WrongUsageException("commands.stats.usage", new Object[0]);
            }
            flag = true;
        }
        if (flag) {
            if (args.length < 5) {
                throw new WrongUsageException("commands.stats.block.usage", new Object[0]);
            }
            i = 4;
        } else {
            if (args.length < 3) {
                throw new WrongUsageException("commands.stats.entity.usage", new Object[0]);
            }
            i = 2;
        }
        String s = args[i++];
        if ("set".equals(s)) {
            if (args.length < i + 3) {
                if (i == 5) {
                    throw new WrongUsageException("commands.stats.block.set.usage", new Object[0]);
                }
                throw new WrongUsageException("commands.stats.entity.set.usage", new Object[0]);
            }
        } else {
            if (!"clear".equals(s)) {
                throw new WrongUsageException("commands.stats.usage", new Object[0]);
            }
            if (args.length < i + 1) {
                if (i == 5) {
                    throw new WrongUsageException("commands.stats.block.clear.usage", new Object[0]);
                }
                throw new WrongUsageException("commands.stats.entity.clear.usage", new Object[0]);
            }
        }
        if ((commandresultstats$type = CommandResultStats.Type.getTypeByName(args[i++])) == null) {
            throw new CommandException("commands.stats.failed", new Object[0]);
        }
        World world = sender.getEntityWorld();
        if (flag) {
            BlockPos blockpos = CommandStats.parseBlockPos(sender, args, 1, false);
            TileEntity tileentity = world.getTileEntity(blockpos);
            if (tileentity == null) {
                throw new CommandException("commands.stats.noCompatibleBlock", blockpos.getX(), blockpos.getY(), blockpos.getZ());
            }
            if (tileentity instanceof TileEntityCommandBlock) {
                commandresultstats = ((TileEntityCommandBlock)tileentity).getCommandResultStats();
            } else {
                if (!(tileentity instanceof TileEntitySign)) {
                    throw new CommandException("commands.stats.noCompatibleBlock", blockpos.getX(), blockpos.getY(), blockpos.getZ());
                }
                commandresultstats = ((TileEntitySign)tileentity).getStats();
            }
        } else {
            Entity entity = CommandStats.func_175768_b(sender, args[1]);
            commandresultstats = entity.getCommandStats();
        }
        if ("set".equals(s)) {
            String s1 = args[i++];
            String s2 = args[i];
            if (s1.length() == 0 || s2.length() == 0) {
                throw new CommandException("commands.stats.failed", new Object[0]);
            }
            CommandResultStats.func_179667_a(commandresultstats, commandresultstats$type, s1, s2);
            CommandStats.notifyOperators(sender, (ICommand)this, "commands.stats.success", commandresultstats$type.getTypeName(), s2, s1);
        } else if ("clear".equals(s)) {
            CommandResultStats.func_179667_a(commandresultstats, commandresultstats$type, null, null);
            CommandStats.notifyOperators(sender, (ICommand)this, "commands.stats.cleared", commandresultstats$type.getTypeName());
        }
        if (flag) {
            BlockPos blockpos1 = CommandStats.parseBlockPos(sender, args, 1, false);
            TileEntity tileentity1 = world.getTileEntity(blockpos1);
            tileentity1.markDirty();
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandStats.getListOfStringsMatchingLastWord(args, "entity", "block") : (args.length == 2 && args[0].equals("entity") ? CommandStats.getListOfStringsMatchingLastWord(args, this.func_175776_d()) : (args.length >= 2 && args.length <= 4 && args[0].equals("block") ? CommandStats.func_175771_a(args, 1, pos) : (!(args.length == 3 && args[0].equals("entity") || args.length == 5 && args[0].equals("block")) ? (!(args.length == 4 && args[0].equals("entity") || args.length == 6 && args[0].equals("block")) ? (!(args.length == 6 && args[0].equals("entity") || args.length == 8 && args[0].equals("block")) ? null : CommandStats.getListOfStringsMatchingLastWord(args, this.func_175777_e())) : CommandStats.getListOfStringsMatchingLastWord(args, CommandResultStats.Type.getTypeNames())) : CommandStats.getListOfStringsMatchingLastWord(args, "set", "clear"))));
    }

    protected String[] func_175776_d() {
        return MinecraftServer.getServer().getAllUsernames();
    }

    protected List<String> func_175777_e() {
        Collection<ScoreObjective> collection = MinecraftServer.getServer().worldServerForDimension(0).getScoreboard().getScoreObjectives();
        ArrayList list = Lists.newArrayList();
        for (ScoreObjective scoreobjective : collection) {
            if (scoreobjective.getCriteria().isReadOnly()) continue;
            list.add(scoreobjective.getName());
        }
        return list;
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return args.length > 0 && args[0].equals("entity") && index == 1;
    }
}

