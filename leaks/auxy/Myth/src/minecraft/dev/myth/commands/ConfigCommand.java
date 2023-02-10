/**
 * @project Myth
 * @author CodeMan
 * @at 07.08.22, 16:20
 */
package dev.myth.commands;

import dev.myth.api.command.Command;
import dev.myth.api.config.Config;
import dev.myth.api.logger.Logger;
import dev.myth.main.ClientMain;
import dev.myth.managers.ConfigManager;

@Command.Info(name = "config")
public class ConfigCommand extends Command {

    @Override
    public void run(String[] args) {
        ConfigManager configManager = ClientMain.INSTANCE.manager.getManager(ConfigManager.class);
        if(args.length < 1) {
            Logger.doLog(Logger.Type.ERROR, "Usage: config <load/save> <name> | config list | config visuals");
            return;
        }
        if(args[0].equalsIgnoreCase("list")) {
            Logger.doLog(Logger.Type.INFO, "Configs: ");
            StringBuilder sb = new StringBuilder();
            for(Config config : configManager.getConfigs().values()) {
                sb.append(config.getName()).append(", ");
            }
            Logger.doLog(Logger.Type.INFO, sb.toString());
            return;
        }
        if(args[0].equalsIgnoreCase("visuals")) {
            configManager.setLoadVisuals(!configManager.isLoadVisuals());
            Logger.doLog(Logger.Type.INFO, "Load visuals: " + configManager.isLoadVisuals());
            return;
        }
        if(args.length < 2) {
            Logger.doLog(Logger.Type.ERROR, "Usage: config <load/save> <name>");
            return;
        }

        Config config = configManager.getConfig(args[1]);

        switch (args[0].toLowerCase()) {
            case "load":
                if(config == null) {
                    Logger.doLog(Logger.Type.ERROR, "Config not found!");
                    return;
                }
                config.read();
                doLog(Logger.Type.INFO, "Loaded config " + config.getName());
                break;
            case "save":
                if(config == null) {
                    config = configManager.createConfig(args[1]);
                    doLog(Logger.Type.INFO, "Created config " + config.getName());
                    return;
                }
                config.write();
                doLog(Logger.Type.INFO, "Saved config " + config.getName());
                break;
            default:
                Logger.doLog(Logger.Type.ERROR, "Usage: config <load/save> <name>");
                break;
        }
    }
}
