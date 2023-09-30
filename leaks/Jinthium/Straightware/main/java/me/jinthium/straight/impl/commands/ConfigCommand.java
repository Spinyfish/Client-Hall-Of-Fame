package me.jinthium.straight.impl.commands;

import me.jinthium.straight.api.command.Command;
import me.jinthium.straight.api.config.LocalConfig;
import me.jinthium.straight.impl.Client;
import me.jinthium.straight.impl.utils.file.FileUtils;

public class ConfigCommand extends Command {


    public ConfigCommand() {
        super("config", "save/load/delete a config", ".config list or .config <save/load/delete> <config name>", "cfg");
    }

    @Override
    public void execute(String[] args) {
        if(args.length == 1 && args[0].equalsIgnoreCase("list")){
            Client.INSTANCE.getConfigManager().localConfigs.forEach(config -> sendChatWithPrefix(config.getName()));
            return;
        }

        if(args.length != 2){
            usage();
        }else{
            String choice = args[0];
            String configName = args[1];

            switch (choice) {
                case "save" -> {
                    Client.INSTANCE.getConfigManager().saveConfig(configName);
                }
                case "load" -> {
                    LocalConfig localConfig = Client.INSTANCE.getConfigManager().getConfig(configName);
                    if(configName != null)
                        Client.INSTANCE.getConfigManager().loadConfig(FileUtils.readFile(localConfig.getFile()));
                    else
                        sendChatWithPrefix("Config doesnt exist!");
                }
                case "delete" -> {
                    LocalConfig localConfig = Client.INSTANCE.getConfigManager().getConfig(configName);
                    if(configName != null)
                        Client.INSTANCE.getConfigManager().delete(localConfig.getName());
                    else
                        sendChatWithPrefix("Config doesnt exist");
                }
            }
        }
    }
}
