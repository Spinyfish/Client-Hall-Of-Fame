/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;

public class CommandEntityData
extends CommandBase {
    @Override
    public String getCommandName() {
        return "entitydata";
    }

    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

    @Override
    public String getCommandUsage(ICommandSender sender) {
        return "commands.entitydata.usage";
    }

    @Override
    public void processCommand(ICommandSender sender, String[] args) throws CommandException {
        NBTTagCompound nbttagcompound2;
        if (args.length < 2) {
            throw new WrongUsageException("commands.entitydata.usage", new Object[0]);
        }
        Entity entity = CommandEntityData.func_175768_b(sender, args[0]);
        if (entity instanceof EntityPlayer) {
            throw new CommandException("commands.entitydata.noPlayers", entity.getDisplayName());
        }
        NBTTagCompound nbttagcompound = new NBTTagCompound();
        entity.writeToNBT(nbttagcompound);
        NBTTagCompound nbttagcompound1 = (NBTTagCompound)nbttagcompound.copy();
        try {
            nbttagcompound2 = JsonToNBT.getTagFromJson(CommandEntityData.getChatComponentFromNthArg(sender, args, 1).getUnformattedText());
        }
        catch (NBTException nbtexception) {
            throw new CommandException("commands.entitydata.tagError", nbtexception.getMessage());
        }
        nbttagcompound2.removeTag("UUIDMost");
        nbttagcompound2.removeTag("UUIDLeast");
        nbttagcompound.merge(nbttagcompound2);
        if (nbttagcompound.equals(nbttagcompound1)) {
            throw new CommandException("commands.entitydata.failed", nbttagcompound.toString());
        }
        entity.readFromNBT(nbttagcompound);
        CommandEntityData.notifyOperators(sender, (ICommand)this, "commands.entitydata.success", nbttagcompound.toString());
    }

    @Override
    public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }
}

