package club.mineman.antigamingchair.commands;

import club.mineman.antigamingchair.AntiGamingChair;
import club.mineman.antigamingchair.commands.subcommands.*;
import club.mineman.antigamingchair.util.StringUtil;
import club.mineman.antigamingchair.util.CC;
import java.util.HashMap;
import java.util.Map;

import com.google.common.collect.Maps;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class AGCCommand extends Command {

   private final Map<String, SubCommand> subCommandMap = Maps.newHashMap();
   private final AntiGamingChair plugin;

   public AGCCommand(AntiGamingChair plugin) {
      super("agc");
      this.plugin = plugin;
      this.setUsage(CC.RED + "Usage: /agc <subcommand> [player]");
      this.setPermissionMessage(StringUtil.NO_PERMISSION);
      this.subCommandMap.put("alerts", new AlertsCommand(plugin));
      this.subCommandMap.put("watch", new WatchCommand(plugin));
      this.subCommandMap.put("info", new InfoCommand(plugin));
      this.subCommandMap.put("rangevl", new RangeVlCommand(plugin));
      this.subCommandMap.put("toggle", new ToggleCommand());
      this.subCommandMap.put("filter", new FilterCommand(plugin));
   }

   public boolean execute(CommandSender sender, String alias, String[] args) {
      if(!sender.hasPermission("anticheat.agc")) {
         return true;
      } else if(!(sender instanceof Player)) {
         sender.sendMessage(StringUtil.PLAYER_ONLY);
         return true;
      } else {
         Player player = (Player)sender;
         String subCommandString = args.length < 1?"help":args[0].toLowerCase();
         SubCommand subCommand = this.subCommandMap.get(subCommandString);
         if(subCommand == null) {
            player.sendMessage(CC.RED + this.getUsage());
            return true;
         } else {
            Player target = args.length > 1?this.plugin.getServer().getPlayer(args[1]):null;
            subCommand.execute(player, target, args);
            return true;
         }
      }
   }
}
