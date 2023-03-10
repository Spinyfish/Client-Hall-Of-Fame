/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.MathHelper;
import net.minecraft.world.border.WorldBorder;

public class CommandWorldBorder
extends CommandBase {
    @Override
    public String getCommandName() {
        return "worldborder";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.worldborder.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException("commands.worldborder.usage", new Object[0]);
        }
        WorldBorder worldborder = this.getWorldBorder();
        if (args[0].equals("set")) {
            long i;
            if (args.length != 2 && args.length != 3) {
                throw new WrongUsageException("commands.worldborder.set.usage", new Object[0]);
            }
            double d0 = worldborder.getTargetSize();
            double d2 = CommandWorldBorder.parseDouble(args[1], 1.0, 6.0E7);
            long l = i = args.length > 2 ? CommandWorldBorder.parseLong(args[2], 0L, 9223372036854775L) * 1000L : 0L;
            if (i > 0L) {
                worldborder.setTransition(d0, d2, i);
                if (d0 > d2) {
                    CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.setSlowly.shrink.success", String.format("%.1f", d2), String.format("%.1f", d0), Long.toString(i / 1000L));
                } else {
                    CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.setSlowly.grow.success", String.format("%.1f", d2), String.format("%.1f", d0), Long.toString(i / 1000L));
                }
            } else {
                worldborder.setTransition(d2);
                CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.set.success", String.format("%.1f", d2), String.format("%.1f", d0));
            }
        } else if (args[0].equals("add")) {
            if (args.length != 2 && args.length != 3) {
                throw new WrongUsageException("commands.worldborder.add.usage", new Object[0]);
            }
            double d4 = worldborder.getDiameter();
            double d8 = d4 + CommandWorldBorder.parseDouble(args[1], -d4, 6.0E7 - d4);
            long i1 = worldborder.getTimeUntilTarget() + (args.length > 2 ? CommandWorldBorder.parseLong(args[2], 0L, 9223372036854775L) * 1000L : 0L);
            if (i1 > 0L) {
                worldborder.setTransition(d4, d8, i1);
                if (d4 > d8) {
                    CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.setSlowly.shrink.success", String.format("%.1f", d8), String.format("%.1f", d4), Long.toString(i1 / 1000L));
                } else {
                    CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.setSlowly.grow.success", String.format("%.1f", d8), String.format("%.1f", d4), Long.toString(i1 / 1000L));
                }
            } else {
                worldborder.setTransition(d8);
                CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.set.success", String.format("%.1f", d8), String.format("%.1f", d4));
            }
        } else if (args[0].equals("center")) {
            if (args.length != 3) {
                throw new WrongUsageException("commands.worldborder.center.usage", new Object[0]);
            }
            BlockPos blockpos = sender.getPosition();
            double d1 = CommandWorldBorder.parseDouble((double)blockpos.getX() + 0.5, args[1], true);
            double d3 = CommandWorldBorder.parseDouble((double)blockpos.getZ() + 0.5, args[2], true);
            worldborder.setCenter(d1, d3);
            CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.center.success", d1, d3);
        } else if (args[0].equals("damage")) {
            if (args.length < 2) {
                throw new WrongUsageException("commands.worldborder.damage.usage", new Object[0]);
            }
            if (args[1].equals("buffer")) {
                if (args.length != 3) {
                    throw new WrongUsageException("commands.worldborder.damage.buffer.usage", new Object[0]);
                }
                double d5 = CommandWorldBorder.parseDouble(args[2], 0.0);
                double d9 = worldborder.getDamageBuffer();
                worldborder.setDamageBuffer(d5);
                CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.damage.buffer.success", String.format("%.1f", d5), String.format("%.1f", d9));
            } else if (args[1].equals("amount")) {
                if (args.length != 3) {
                    throw new WrongUsageException("commands.worldborder.damage.amount.usage", new Object[0]);
                }
                double d6 = CommandWorldBorder.parseDouble(args[2], 0.0);
                double d10 = worldborder.getDamageAmount();
                worldborder.setDamageAmount(d6);
                CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.damage.amount.success", String.format("%.2f", d6), String.format("%.2f", d10));
            }
        } else if (args[0].equals("warning")) {
            if (args.length < 2) {
                throw new WrongUsageException("commands.worldborder.warning.usage", new Object[0]);
            }
            int j = CommandWorldBorder.parseInt(args[2], 0);
            if (args[1].equals("time")) {
                if (args.length != 3) {
                    throw new WrongUsageException("commands.worldborder.warning.time.usage", new Object[0]);
                }
                int k = worldborder.getWarningTime();
                worldborder.setWarningTime(j);
                CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.warning.time.success", j, k);
            } else if (args[1].equals("distance")) {
                if (args.length != 3) {
                    throw new WrongUsageException("commands.worldborder.warning.distance.usage", new Object[0]);
                }
                int l = worldborder.getWarningDistance();
                worldborder.setWarningDistance(j);
                CommandWorldBorder.notifyOperators(sender, (ICommand)this, "commands.worldborder.warning.distance.success", j, l);
            }
        } else {
            if (!args[0].equals("get")) {
                throw new WrongUsageException("commands.worldborder.usage", new Object[0]);
            }
            double d7 = worldborder.getDiameter();
            sender.setCommandStat(CommandResultStats.Type.QUERY_RESULT, MathHelper.floor_double(d7 + 0.5));
            sender.addChatMessage(new ChatComponentTranslation("commands.worldborder.get.success", String.format("%.0f", d7)));
        }
    }

    protected WorldBorder getWorldBorder() {
        return MinecraftServer.getServer().worldServers[0].getWorldBorder();
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandWorldBorder.getListOfStringsMatchingLastWord(args, "set", "center", "damage", "warning", "add", "get") : (args.length == 2 && args[0].equals("damage") ? CommandWorldBorder.getListOfStringsMatchingLastWord(args, "buffer", "amount") : (args.length >= 2 && args.length <= 3 && args[0].equals("center") ? CommandWorldBorder.func_181043_b(args, 1, pos) : (args.length == 2 && args[0].equals("warning") ? CommandWorldBorder.getListOfStringsMatchingLastWord(args, "time", "distance") : null)));
    }
}

