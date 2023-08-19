package cc.swift.commands.impl;

import cc.swift.Swift;
import cc.swift.commands.Command;
import cc.swift.config.Config;
import cc.swift.util.ChatUtil;
import tv.twitch.chat.Chat;

import java.util.Map;
import java.util.Objects;

public class ConfigCommand extends Command {
    public ConfigCommand() {
        super("Config", "Manages configs", new String[]{"con"});
    }

    @Override
    public void onCommand(String[] args) {
        switch (args.length) {
            default: {
                ChatUtil.printChatMessage(".config save <name>");
                ChatUtil.printChatMessage(".config load <name>");
                ChatUtil.printChatMessage(".config delete <name>");
                break;
            }
            case 1:
                if (!Objects.equals(args[0], "list")) {
                    ChatUtil.printChatMessage(".config save <name>");
                    ChatUtil.printChatMessage(".config load <name>");
                    ChatUtil.printChatMessage(".config delete <name>");
                    break;
                }
                ChatUtil.printChatMessage(Swift.INSTANCE.getConfigManager().getConfigs().size() + " configs: ");
                for (Map.Entry<String, Config> config : Swift.INSTANCE.getConfigManager().getConfigs().entrySet()) {
                    ChatUtil.printChatMessage("- " + config.getKey());
                }
            case 2:
                switch (args[0]) {
                    case "save": {
                        Swift.INSTANCE.getConfigManager().saveConfig(args[1]);
                        break;
                    }
                    case "load": {
                        Config config = Swift.INSTANCE.getConfigManager().getConfig(args[1]);
                        if (config != null) {
                            config.loadConfig();
                            ChatUtil.printChatMessage("loaded config");
                        } else {
                            ChatUtil.printChatMessage("config dont exist");
                        }
                        break;
                    }
                    case "delete":
                    case "remove":
                    case "del": {
                        Config config = Swift.INSTANCE.getConfigManager().getConfig(args[1]);
                        if (config != null) {
                            if (Swift.INSTANCE.getConfigManager().deleteConfig(config)) {
                                ChatUtil.printChatMessage("deleted config");
                            } else {
                                ChatUtil.printChatMessage("failed to delete config");
                            }
                        } else {
                            ChatUtil.printChatMessage("failed to delete config");
                        }
                        break;
                    }
                }
                break;
        }
    }
}
