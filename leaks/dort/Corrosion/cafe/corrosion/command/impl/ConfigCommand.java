package cafe.corrosion.command.impl;

import cafe.corrosion.Corrosion;
import cafe.corrosion.attributes.CommandAttributes;
import cafe.corrosion.command.ICommand;
import cafe.corrosion.config.ConfigManager;
import cafe.corrosion.config.base.Config;
import cafe.corrosion.config.base.impl.DynamicConfig;
import cafe.corrosion.util.player.PlayerUtil;
import java.util.List;

@CommandAttributes(
    name = "config"
)
public class ConfigCommand implements ICommand {
    private static final String BASE_ERROR = "Try -config (load/save/web/list) [name]";

    public void handle(String[] args) {
        ConfigManager configManager = Corrosion.INSTANCE.getConfigManager();
        if (args.length != 2) {
            if (args.length == 1 && args[0].equalsIgnoreCase("list")) {
                List<String> configNames = configManager.getConfigNames();
                if (configNames.size() == 0) {
                    PlayerUtil.sendMessage("No configs were found!");
                } else {
                    String[] array = (String[])configNames.stream().map((configx) -> {
                        if (!configx.contains("-web")) {
                            return configx;
                        } else {
                            String replaced = configx.replace("-web", "");
                            return replaced + " (&cWeb&7)";
                        }
                    }).toArray((x$0) -> {
                        return new String[x$0];
                    });
                    PlayerUtil.sendMessage("Found the following configurations:");
                    PlayerUtil.sendMessage(array);
                }
            } else {
                PlayerUtil.sendMessage("Try -config (load/save/web/list) [name]");
            }
        } else {
            String action = args[0];
            String name = args[1];
            String var5 = action.toLowerCase();
            byte var6 = -1;
            switch(var5.hashCode()) {
            case 117588:
                if (var5.equals("web")) {
                    var6 = 2;
                }
                break;
            case 3327206:
                if (var5.equals("load")) {
                    var6 = 0;
                }
                break;
            case 3522941:
                if (var5.equals("save")) {
                    var6 = 1;
                }
            }

            Config config;
            switch(var6) {
            case 0:
                config = configManager.getConfig(name, false);
                if (config == null) {
                    PlayerUtil.sendMessage("Try -config (load/save/web/list) [name]");
                    return;
                }

                config.load(name);
                PlayerUtil.sendMessage("Successfully loaded config " + name + "!");
                break;
            case 1:
                configManager.saveConfig(name);
                PlayerUtil.sendMessage("Successfully saved config " + name + "!");
                break;
            case 2:
                config = configManager.getConfig(name, true);
                if (config == null) {
                    PlayerUtil.sendMessage("Searching for a web config named " + name + "!");
                    new DynamicConfig(name);
                    return;
                }

                config.load(name);
            }

        }
    }
}
