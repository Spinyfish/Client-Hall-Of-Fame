/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.List;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public interface ICommand
extends Comparable<ICommand> {
    public String getCommandName();

    public String getCommandUsage(ICommandSender var1);

    public List<String> getCommandAliases();

    public void processCommand(ICommandSender var1, String[] var2) throws CommandException;

    public boolean canCommandSenderUseCommand(ICommandSender var1);

    public List<String> addTabCompletionOptions(ICommandSender var1, String[] var2, BlockPos var3);

    public boolean isUsernameIndex(String[] var1, int var2);
}

