/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.List;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CommandParticle
extends CommandBase {
    @Override
    public String getCommandName() {
        return "particle";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.particle.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        World world;
        if (args.length < 8) {
            throw new WrongUsageException("commands.particle.usage", new Object[0]);
        }
        boolean flag = false;
        EnumParticleTypes enumparticletypes = null;
        for (EnumParticleTypes enumparticletypes1 : EnumParticleTypes.values()) {
            if (enumparticletypes1.hasArguments()) {
                if (!args[0].startsWith(enumparticletypes1.getParticleName())) continue;
                flag = true;
                enumparticletypes = enumparticletypes1;
                break;
            }
            if (!args[0].equals(enumparticletypes1.getParticleName())) continue;
            flag = true;
            enumparticletypes = enumparticletypes1;
            break;
        }
        if (!flag) {
            throw new CommandException("commands.particle.notFound", args[0]);
        }
        String s = args[0];
        Vec3 vec3 = sender.getPositionVector();
        double d6 = (float)CommandParticle.parseDouble(vec3.xCoord, args[1], true);
        double d0 = (float)CommandParticle.parseDouble(vec3.yCoord, args[2], true);
        double d1 = (float)CommandParticle.parseDouble(vec3.zCoord, args[3], true);
        double d2 = (float)CommandParticle.parseDouble(args[4]);
        double d3 = (float)CommandParticle.parseDouble(args[5]);
        double d4 = (float)CommandParticle.parseDouble(args[6]);
        double d5 = (float)CommandParticle.parseDouble(args[7]);
        int i = 0;
        if (args.length > 8) {
            i = CommandParticle.parseInt(args[8], 0);
        }
        boolean flag1 = false;
        if (args.length > 9 && "force".equals(args[9])) {
            flag1 = true;
        }
        if ((world = sender.getEntityWorld()) instanceof WorldServer) {
            WorldServer worldserver = (WorldServer)world;
            int[] aint = new int[enumparticletypes.getArgumentCount()];
            if (enumparticletypes.hasArguments()) {
                String[] astring = args[0].split("_", 3);
                for (int j = 1; j < astring.length; ++j) {
                    try {
                        aint[j - 1] = Integer.parseInt(astring[j]);
                        continue;
                    }
                    catch (NumberFormatException var29) {
                        throw new CommandException("commands.particle.notFound", args[0]);
                    }
                }
            }
            worldserver.spawnParticle(enumparticletypes, flag1, d6, d0, d1, i, d2, d3, d4, d5, aint);
            CommandParticle.notifyOperators(sender, (ICommand)this, "commands.particle.success", s, Math.max(i, 1));
        }
    }

    @Override
    public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
        return args.length == 1 ? CommandParticle.getListOfStringsMatchingLastWord(args, EnumParticleTypes.getParticleNames()) : (args.length > 1 && args.length <= 4 ? CommandParticle.func_175771_a(args, 1, pos) : (args.length == 10 ? CommandParticle.getListOfStringsMatchingLastWord(args, "normal", "force") : null));
    }
}

