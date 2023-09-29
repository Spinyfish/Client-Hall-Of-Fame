package net.minecraft.command;

import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

import java.util.List;

public class CommandBlockData extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "blockdata";
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
        return "commands.blockdata.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 4) {
            throw new WrongUsageException("commands.blockdata.usage");
        } else {
            sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 0);
            final BlockPos blockpos = parseBlockPos(sender, args, 0, false);
            final World world = sender.getEntityWorld();

            if (!world.isBlockLoaded(blockpos)) {
                throw new CommandException("commands.blockdata.outOfWorld");
            } else {
                final TileEntity tileentity = world.getTileEntity(blockpos);

                if (tileentity == null) {
                    throw new CommandException("commands.blockdata.notValid");
                } else {
                    final NBTTagCompound nbttagcompound = new NBTTagCompound();
                    tileentity.writeToNBT(nbttagcompound);
                    final NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttagcompound.copy();
                    final NBTTagCompound nbttagcompound2;

                    try {
                        nbttagcompound2 = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(sender, args, 3).getUnformattedText());
                    } catch (final NBTException nbtexception) {
                        throw new CommandException("commands.blockdata.tagError", nbtexception.getMessage());
                    }

                    nbttagcompound.merge(nbttagcompound2);
                    nbttagcompound.setInteger("x", blockpos.getX());
                    nbttagcompound.setInteger("y", blockpos.getY());
                    nbttagcompound.setInteger("z", blockpos.getZ());

                    if (nbttagcompound.equals(nbttagcompound1)) {
                        throw new CommandException("commands.blockdata.failed", nbttagcompound.toString());
                    } else {
                        tileentity.readFromNBT(nbttagcompound);
                        tileentity.markDirty();
                        world.markBlockForUpdate(blockpos);
                        sender.setCommandStat(CommandResultStats.Type.AFFECTED_BLOCKS, 1);
                        notifyOperators(sender, this, "commands.blockdata.success", nbttagcompound.toString());
                    }
                }
            }
        }
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length > 0 && args.length <= 3 ? func_175771_a(args, 0, pos) : null;
    }
}
