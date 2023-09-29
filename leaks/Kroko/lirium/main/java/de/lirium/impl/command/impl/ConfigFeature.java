package de.lirium.impl.command.impl;

import de.lirium.base.config.ConfigManager;
import de.lirium.impl.command.CommandFeature;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@CommandFeature.Info(name = "config", alias = "cfg")
public class ConfigFeature extends CommandFeature {

    @Override
    public String[] getArguments() {
        return new String[]{
                "folder",
                "list [type]",
                "load <name>",
                "online <name>",
                "save <name>"
        };
    }

    @Override
    public boolean execute(String[] args) {
        if (args.length >= 1) {
            switch (args[0].toLowerCase()) {
                case "list":
                    final HashMap<String, List<String>> configs = new HashMap<>();
                    configs.put("online", ConfigManager.getOnline());
                    configs.put("local", ConfigManager.getConfigs());

                    if (args.length >= 2 && !configs.containsKey(args[1].toLowerCase())) {
                        sendMessage("§cInvalid type: §e" + args[1]);
                        sendMessage("§eAvailable types: §b" + configs.keySet());
                        return true;
                    }

                    sendMessage("§7---- §c§lConfigs§7 ----");
                    for (String key : configs.keySet()) {
                        if (args.length >= 2 && !args[1].equalsIgnoreCase(key)) continue;
                        if (!configs.get(key).isEmpty()) {
                            sendMessage("§7>> §b§l" + key + " §7<<");

                            for (String config : configs.get(key))
                                sendMessage("§7- §e" + config);
                        }
                    }
                    return true;
            }
        }
        switch (args.length) {
            case 1:
                switch (args[0].toLowerCase()) {
                    case "folder":
                        ConfigManager.openFolder();
                        break;
                    default:
                        return false;
                }
            case 2:
                switch (args[0].toLowerCase()) {
                    case "load":
                        try {
                            final ConfigManager.ConfigData cfgData = ConfigManager.load(args[1]);
                            if (cfgData.isLoaded()) {
                                sendMessage("§aSuccessfully loaded config §e" + args[1]);
                                sendMessage("§eAuthor: §b" + cfgData.getAuthor() + (cfgData.getModifiedBy() != null ? " §7(§eModified by: §b" + cfgData.getModifiedBy() + "§7)" : ""));
                                sendMessage("§eCreated at: §b" + new SimpleDateFormat("MM/dd/yyyy").format(new Date(cfgData.getCreationDate())));
                            } else
                                sendMessage("§cConfig §e" + args[1] + "§c not found!");
                        } catch (Exception e) {
                            sendMessage("§cError while loading config: " + e.getMessage());
                        }
                        break;
                    case "online":
                        try {
                            String configName = null;
                            for (String name : ConfigManager.getOnline()) {
                                if (name.equalsIgnoreCase(args[1])) {
                                    configName = name;
                                    break;
                                }
                            }
                            if (configName != null) {
                                final ConfigManager.ConfigData cfgData = ConfigManager.loadOnline(configName, false);
                                if (cfgData.isLoaded()) {
                                    sendMessage("§aSuccessfully loaded §conline §aconfig §e" + configName);
                                    sendMessage("§eAuthor: §b" + cfgData.getAuthor() + (cfgData.getModifiedBy() != null ? " §7(§eModified by: §b" + cfgData.getModifiedBy() + "§7)" : ""));
                                    sendMessage("§eCreated at: §b" + new SimpleDateFormat("MM/dd/yyyy").format(new Date(cfgData.getCreationDate())));
                                } else
                                    sendMessage("§cConfig §e" + args[1] + "§c not found!");
                            } else
                                sendMessage("§cConfig §e" + args[1] + "§c not found!");
                        } catch (Exception e) {
                            sendMessage("§cError while loading config: " + e.getMessage());
                        }
                        break;
                    case "save":
                    case "create":
                        try {
                            ConfigManager.save(args[1]);
                            sendMessage("§aSuccessfully saved config §e" + args[1]);
                        } catch (Exception e) {
                            sendMessage("§cError while saving config: " + e.getMessage());
                        }
                        break;
                }
                break;
            default:
                return false;
        }
        return true;
    }
}