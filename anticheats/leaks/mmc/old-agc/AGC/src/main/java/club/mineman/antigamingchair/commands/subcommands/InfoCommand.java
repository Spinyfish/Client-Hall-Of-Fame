package club.mineman.antigamingchair.commands.subcommands;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.commands.subcommands.SubCommand;
import club.mineman.antigamingchair.data.PlayerData;
import java.beans.ConstructorProperties;

import club.mineman.antigamingchair.util.CC;
import club.mineman.antigamingchair.util.StringUtil;
import org.bukkit.entity.Player;

public class InfoCommand implements SubCommand {
   private final AntiGamingChair plugin;

   public void execute(Player player, Player target, String[] args) {
      if(target == null) {
         player.sendMessage(String.format(StringUtil.PLAYER_NOT_FOUND, new Object[]{args[1]}));
      } else {
         PlayerData targetData = this.plugin.getPlayerDataManager().getPlayerData(target);
         StringBuilder builder = new StringBuilder("\u00a78 \u00a78 \u00a71 \u00a73 \u00a73 \u00a77 \u00a78 \u00a7r\n");
         builder.append(CC.L_PURPLE).append(target.getName()).append("\'s AGC info:\n");
         builder.append(CC.L_PURPLE).append("Client: ").append(CC.D_PURPLE).append(targetData.getClient().getName()).append("\n");
         builder.append(CC.L_PURPLE).append("Last CPS: ").append(CC.D_PURPLE).append(targetData.getCps()).append("\n");
         builder.append(CC.L_PURPLE).append("Ping: ").append(CC.D_PURPLE).append(targetData.getPing()).append("\n");
         builder.append("\u00a78 \u00a78 \u00a71 \u00a73 \u00a73 \u00a77 \u00a78 \u00a7r\n");
         player.sendMessage(builder.toString());
      }

   }

   @ConstructorProperties({"plugin"})
   public InfoCommand(AntiGamingChair plugin) {
      this.plugin = plugin;
   }
}
