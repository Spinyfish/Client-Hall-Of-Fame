package net.minecraft.command;

import java.util.List;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.world.EnumDifficulty;

public class CommandDifficulty extends CommandBase {
   @Override
   public String getCommandName() {
      return "difficulty";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public String getCommandUsage(ICommandSender sender) {
      return "commands.difficulty.usage";
   }

   @Override
   public void processCommand(ICommandSender sender, String[] args) throws CommandException {
      if (args.length <= 0) {
         throw new WrongUsageException("commands.difficulty.usage");
      } else {
         EnumDifficulty enumdifficulty = this.getDifficultyFromCommand(args[0]);
         MinecraftServer.getServer().setDifficultyForAllWorlds(enumdifficulty);
         notifyOperators(sender, this, "commands.difficulty.success", new Object[]{new ChatComponentTranslation(enumdifficulty.getDifficultyResourceKey())});
      }
   }

   protected EnumDifficulty getDifficultyFromCommand(String p_180531_1_) throws CommandException, NumberInvalidException {
      return p_180531_1_.equalsIgnoreCase("peaceful") || p_180531_1_.equalsIgnoreCase("p")
         ? EnumDifficulty.PEACEFUL
         : (
            p_180531_1_.equalsIgnoreCase("easy") || p_180531_1_.equalsIgnoreCase("e")
               ? EnumDifficulty.EASY
               : (
                  !p_180531_1_.equalsIgnoreCase("normal") && !p_180531_1_.equalsIgnoreCase("n")
                     ? (
                        !p_180531_1_.equalsIgnoreCase("hard") && !p_180531_1_.equalsIgnoreCase("h")
                           ? EnumDifficulty.getDifficultyEnum(parseInt(p_180531_1_, 0, 3))
                           : EnumDifficulty.HARD
                     )
                     : EnumDifficulty.NORMAL
               )
         );
   }

   @Override
   public List<String> addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
      return args.length == 1 ? getListOfStringsMatchingLastWord(args, new String[]{"peaceful", "easy", "normal", "hard"}) : null;
   }
}
