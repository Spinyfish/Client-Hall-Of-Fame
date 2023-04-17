package club.mineman.antigamingchair.commands.subcommands;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.data.PlayerData;
import java.beans.ConstructorProperties;

import club.mineman.antigamingchair.util.CC;
import club.mineman.antigamingchair.util.StringUtil;
import org.bukkit.entity.Player;

public class FilterCommand implements SubCommand {
   private final AntiGamingChair plugin;

   public void execute(Player player, Player target, String[] args) {
      PlayerData playerData = this.plugin.getPlayerDataManager().getPlayerData(player);
      if(args.length < 2) {
         StringBuilder phrase1 = new StringBuilder("\u00a78 \u00a78 \u00a71 \u00a73 \u00a73 \u00a77 \u00a78 \u00a7r\n");
         phrase1.append(CC.L_PURPLE).append("Filtered phrases:\n");
         phrase1.append(playerData.getFilteredPhrases().toString().replace("[", CC.D_PURPLE).replace("]", "").replaceAll(", ", "\n"));
         phrase1.append("\n\u00a78 \u00a78 \u00a71 \u00a73 \u00a73 \u00a77 \u00a78 \u00a7r\n");
         player.sendMessage(phrase1.toString());
      } else {
         String phrase = StringUtil.buildMessage(args, 1);
         playerData.toggleKeywordFilter(phrase.toLowerCase());
         player.sendMessage(CC.L_PURPLE + "The phrase " + CC.D_PURPLE + phrase + CC.L_PURPLE + " is " + (playerData.isKeywordFiltered(phrase.toLowerCase())?"now filtered":"no longer filtered") + " in your alerts!");
      }
   }

   @ConstructorProperties({"plugin"})
   public FilterCommand(AntiGamingChair plugin) {
      this.plugin = plugin;
   }
}
