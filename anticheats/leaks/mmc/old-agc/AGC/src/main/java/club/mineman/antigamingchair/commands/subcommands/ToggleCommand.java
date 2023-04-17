package club.mineman.antigamingchair.commands.subcommands;

import club.mineman.antigamingchair.util.CC;
import java.util.HashSet;
import java.util.Set;
import org.bukkit.entity.Player;

public class ToggleCommand implements SubCommand {
   public static final Set DISABLED_CHECKS = new HashSet();

   public void execute(Player player, Player target, String[] args) {
      if(player.hasPermission("anticheat.check.toggle")) {
         String check = args[1].toUpperCase();
         if(!DISABLED_CHECKS.remove(check)) {
            DISABLED_CHECKS.add(check);
            player.sendMessage(CC.L_PURPLE + "Enabled check " + CC.D_PURPLE + check);
         } else {
            player.sendMessage(CC.L_PURPLE + "Disabled check " + CC.D_PURPLE + check);
         }

      }
   }
}
