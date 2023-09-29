package net.minecraft.command;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class CommandEntityData extends CommandBase {
    /**
     * Gets the name of the command
     */
    public String getCommandName() {
        return "entitydata";
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
        return "commands.entitydata.usage";
    }

    /**
     * Callback when the command is invoked
     *
     * @param sender The {@link ICommandSender sender} who executed the command
     * @param args   The arguments that were passed with the command
     */
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length < 2) {
            throw new WrongUsageException("commands.entitydata.usage");
        } else {
            final Entity entity = func_175768_b(sender, args[0]);

            if (entity instanceof EntityPlayer) {
                throw new CommandException("commands.entitydata.noPlayers", entity.getDisplayName());
            } else {
                final NBTTagCompound nbttagcompound = new NBTTagCompound();
                entity.writeToNBT(nbttagcompound);
                final NBTTagCompound nbttagcompound1 = (NBTTagCompound) nbttagcompound.copy();
                final NBTTagCompound nbttagcompound2;

                try {
                    nbttagcompound2 = JsonToNBT.getTagFromJson(getChatComponentFromNthArg(sender, args, 1).getUnformattedText());
                } catch (final NBTException nbtexception) {
                    throw new CommandException("commands.entitydata.tagError", nbtexception.getMessage());
                }

                nbttagcompound2.removeTag("UUIDMost");
                nbttagcompound2.removeTag("UUIDLeast");
                nbttagcompound.merge(nbttagcompound2);

                if (nbttagcompound.equals(nbttagcompound1)) {
                    throw new CommandException("commands.entitydata.failed", nbttagcompound.toString());
                } else {
                    entity.readFromNBT(nbttagcompound);
                    notifyOperators(sender, this, "commands.entitydata.success", nbttagcompound.toString());
                }
            }
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
}
