package net.minecraft.command;

import net.minecraft.server.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import java.util.*;

public class CommandDifficulty extends CommandBase
{
    @Override
    public String getCommandName() {
        return "difficulty";
    }
    
    @Override
    public int getRequiredPermissionLevel() {
        return 2;
    }
    
    @Override
    public String getCommandUsage(final ICommandSender sender) {
        return "commands.difficulty.usage";
    }
    
    @Override
    public void processCommand(final ICommandSender sender, final String[] args) throws CommandException {
        if (args.length <= 0) {
            throw new WrongUsageException("commands.difficulty.usage", new Object[0]);
        }
        final EnumDifficulty enumdifficulty = this.getDifficultyFromCommand(args[0]);
        MinecraftServer.getServer().setDifficultyForAllWorlds(enumdifficulty);
        CommandBase.notifyOperators(sender, this, "commands.difficulty.success", new ChatComponentTranslation(enumdifficulty.getDifficultyResourceKey(), new Object[0]));
    }
    
    protected EnumDifficulty getDifficultyFromCommand(final String p_180531_1_) throws CommandException, NumberInvalidException {
        return (!p_180531_1_.equalsIgnoreCase("peaceful") && !p_180531_1_.equalsIgnoreCase("p")) ? ((!p_180531_1_.equalsIgnoreCase("easy") && !p_180531_1_.equalsIgnoreCase("e")) ? ((!p_180531_1_.equalsIgnoreCase("normal") && !p_180531_1_.equalsIgnoreCase("n")) ? ((!p_180531_1_.equalsIgnoreCase("hard") && !p_180531_1_.equalsIgnoreCase("h")) ? EnumDifficulty.getDifficultyEnum(CommandBase.parseInt(p_180531_1_, 0, 3)) : EnumDifficulty.HARD) : EnumDifficulty.NORMAL) : EnumDifficulty.EASY) : EnumDifficulty.PEACEFUL;
    }
    
    @Override
    public List<String> addTabCompletionOptions(final ICommandSender sender, final String[] args, final BlockPos pos) {
        return (args.length == 1) ? CommandBase.getListOfStringsMatchingLastWord(args, "peaceful", "easy", "normal", "hard") : null;
    }
}
