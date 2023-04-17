package club.mineman.antigamingchair.commands.subcommands;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.util.CC;
import java.beans.ConstructorProperties;
import org.bukkit.entity.Player;

public class RangeVlCommand implements SubCommand {
   private final AntiGamingChair plugin;

   public void execute(Player player, Player target, String[] args) {
      if(args.length == 1) {
         player.sendMessage(CC.RED + "The current range VL is " + this.plugin.getRangeVl());
      } else {
         this.plugin.setRangeVl(Double.parseDouble(args[1]));
         player.sendMessage(CC.GREEN + "Successfully set range VL");
      }
   }

   @ConstructorProperties({"plugin"})
   public RangeVlCommand(AntiGamingChair plugin) {
      this.plugin = plugin;
   }
}
