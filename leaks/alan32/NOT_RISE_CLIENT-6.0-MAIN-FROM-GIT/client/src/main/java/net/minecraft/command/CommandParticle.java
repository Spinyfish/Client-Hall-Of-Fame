package net.minecraft.command;

import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.Vec3;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.List;

public class CommandParticle extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "particle";
    }

    /**
     * Return the required permission level for this command.
     */
    public int getRequiredPermissionLevel() {
        return 2;
    }

    /**
     * Gets the usage string for the command.
     *
     * @param sender The {@link ICommandSender} who is requesting usage details.
     */
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.particle.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 8) {
            throw new WrongUsageException("commands.particle.usage");
        } else {
            boolean flag = false;
            EnumParticleTypes enumparticletypes = null;

            for (final EnumParticleTypes enumparticletypes1 : EnumParticleTypes.values()) {
                if (enumparticletypes1.hasArguments()) {
                    if (args[0].startsWith(enumparticletypes1.getParticleName())) {
                        flag = true;
                        enumparticletypes = enumparticletypes1;
                        break;
                    }
                } else if (args[0].equals(enumparticletypes1.getParticleName())) {
                    flag = true;
                    enumparticletypes = enumparticletypes1;
                    break;
                }
            }

            if (!flag) {
                throw new CommandException("commands.particle.notFound", args[0]);
            } else {
                final String s = args[0];
                final Vec3 vec3 = sender.getPositionVector();
                final double d6 = (float) parseDouble(vec3.xCoord, args[1], true);
                final double d0 = (float) parseDouble(vec3.yCoord, args[2], true);
                final double d1 = (float) parseDouble(vec3.zCoord, args[3], true);
                final double d2 = (float) parseDouble(args[4]);
                final double d3 = (float) parseDouble(args[5]);
                final double d4 = (float) parseDouble(args[6]);
                final double d5 = (float) parseDouble(args[7]);
                int i = 0;

                if (args.length > 8) {
                    i = parseInt(args[8], 0);
                }

                boolean flag1 = args.length > 9 && "force".equals(args[9]);

                final World world = sender.getEntityWorld();

                if (world instanceof WorldServer) {
                    final WorldServer worldserver = (WorldServer) world;
                    final int[] aint = new int[enumparticletypes.getArgumentCount()];

                    if (enumparticletypes.hasArguments()) {
                        final String[] astring = args[0].split("_", 3);

                        for (int j = 1; j < astring.length; ++j) {
                            try {
                                aint[j - 1] = Integer.parseInt(astring[j]);
                            } catch (final NumberFormatException var29) {
                                throw new CommandException("commands.particle.notFound", args[0]);
                            }
                        }
                    }

                    worldserver.spawnParticle(enumparticletypes, flag1, d6, d0, d1, i, d2, d3, d4, d5, aint);
                    notifyOperators(sender, this, "commands.particle.success", s, Integer.valueOf(Math.max(i, 1)));
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, EnumParticleTypes.getParticleNames()) : (args.length > 1 && args.length <= 4 ? func_175771_a(args, 1, pos) : (args.length == 10 ? getListOfStringsMatchingLastWord(args, "normal", "force") : null));
    }
}
