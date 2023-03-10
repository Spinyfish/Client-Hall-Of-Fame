/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.command;

import java.util.List;
import java.util.Map;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.util.BlockPos;

public interface ICommandManager {
    public int executeCommand(ICommandSender var1, String var2);

    public List<String> getTabCompletionOptions(ICommandSender var1, String var2, BlockPos var3);

    public List<ICommand> getPossibleCommands(ICommandSender var1);

    public Map<String, ICommand> getCommands();
}

