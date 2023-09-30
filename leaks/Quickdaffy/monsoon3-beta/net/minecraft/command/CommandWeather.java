/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.List;
import java.util.Random;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.WorldInfo;

public class CommandWeather
extends CommandBase {
    @Override
    public String getCommandName() {
        return "weather";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.weather.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        if (args.length >= 1 && args.length <= 2) {
            int i = (300 + new Random().nextInt(600)) * 20;
            if (args.length >= 2) {
                i = CommandWeather.parseInt(args[1], 1, 1000000) * 20;
            }
            WorldServer world = MinecraftServer.getServer().worldServers[0];
            WorldInfo worldinfo = world.getWorldInfo();
            if ("clear".equalsIgnoreCase(args[0])) {
                worldinfo.setCleanWeatherTime(i);
                worldinfo.setRainTime(0);
                worldinfo.setThunderTime(0);
                worldinfo.setRaining(false);
                worldinfo.setThundering(false);
                CommandWeather.notifyOperators(sender, (ICommand)this, "commands.weather.clear", new Object[0]);
            } else if ("rain".equalsIgnoreCase(args[0])) {
                worldinfo.setCleanWeatherTime(0);
                worldinfo.setRainTime(i);
                worldinfo.setThunderTime(i);
                worldinfo.setRaining(true);
                worldinfo.setThundering(false);
                CommandWeather.notifyOperators(sender, (ICommand)this, "commands.weather.rain", new Object[0]);
            } else {
                if (!"thunder".equalsIgnoreCase(args[0])) {
                    throw new WrongUsageException("commands.weather.usage", new Object[0]);
                }
                worldinfo.setCleanWeatherTime(0);
                worldinfo.setRainTime(i);
                worldinfo.setThunderTime(i);
                worldinfo.setRaining(true);
                worldinfo.setThundering(true);
                CommandWeather.notifyOperators(sender, (ICommand)this, "commands.weather.thunder", new Object[0]);
            }
        } else {
            throw new WrongUsageException("commands.weather.usage", new Object[0]);
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandWeather.getListOfStringsMatchingLastWord(args, "clear", "rain", "thunder") : null;
    }
}

