package net.minecraft.command.server;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;

import java.util.List;

public class CommandTestFor extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "testfor";
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
        return "commands.testfor.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 1) {
            throw new WrongUsageException("commands.testfor.usage");
        } else {
            final Entity entity = func_175768_b(sender, args[0]);
            NBTTagCompound nbttagcompound = null;

            if (args.length >= 2) {
                try {
                    nbttagcompound = JsonToNBT.getTagFromJson(buildString(args, 1));
                } catch (final NBTException nbtexception) {
                    throw new CommandException("commands.testfor.tagError", nbtexception.getMessage());
                }
            }

            if (nbttagcompound != null) {
                final NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                entity.writeToNBT(nbttagcompound1);

                if (!NBTUtil.func_181123_a(nbttagcompound, nbttagcompound1, true)) {
                    throw new CommandException("commands.testfor.failure", entity.getCommandSenderName());
                }
            }

            notifyOperators(sender, this, "commands.testfor.success", entity.getCommandSenderName());
        }
    }

    /**
     * Return whether the specified command parameter index is a username parameter.
     *
     * @param args  The arguments that were given
     * @param index The argument index that we are checking
     */
    public boolean isUsernameIndex(final String[] args, final int index) {
        return index == 0;
    }

    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return args.length == 1 ? getListOfStringsMatchingLastWord(args, MinecraftServer.getServer().getAllUsernames()) : null;
    }
}
