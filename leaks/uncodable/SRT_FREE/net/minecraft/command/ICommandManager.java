package net.minecraft.command;

import java.util.List;
import java.util.Map;
import net.minecraft.util.BlockPos;

public interface ICommandManager {
   int executeCommand(ICommandSender var1, String var2);

   List<String> getTabCompletionOptions(ICommandSender var1, String var2, BlockPos var3);

   List<ICommand> getPossibleCommands(ICommandSender var1);

   Map<String, ICommand> getCommands();
}
