package de.verschwiegener.atero.command.commands;

import de.verschwiegener.atero.Management;
import de.verschwiegener.atero.command.Command;
import de.verschwiegener.atero.ui.ingame.ConfigUI;
import de.verschwiegener.atero.util.chat.ChatUtil;
import de.verschwiegener.atero.util.files.config.Config;
import de.verschwiegener.atero.util.files.config.ConfigType;
import net.minecraft.client.Minecraft;

public class ConfigCommand extends Command {

    public ConfigCommand() {
        super("Config", "Loads a config", new String[]{"locale / online", "load / list / save(1,locale)", "<Name>(2,save) / <config>(2,load)", "<Description>(2,save)", "<IP>(2,save)"});
    }

    @Override
    public void onCommand(final String[] args) {
        System.err.println("args[1]: " + args[1]);
        switch (args[1]) {
            case "locale":
                switch (args[2]) {

                    case "list":
                        ChatUtil.sendMessage("§6------§fConfigs §6------");
                        for (final Config config : Management.instance.configmgr.configs) {
                            if (config.getType() == ConfigType.locale) {
                                ChatUtil.addConfigMessage(config);
                            }
                        }
                        break;

                    case "load":
                        try {
                            Management.instance.configmgr.getConfigByName(args[3], ConfigType.locale).loadConfig();
                            ChatUtil.sendMessageWithPrefix("Config loaded");
                        } catch (final Exception e) {
                            ChatUtil.sendMessageWithPrefix("Config not found: " + args[3]);
                        }
                        break;

                    case "save":
                        System.out.println("Save");
                        Minecraft.getMinecraft().displayGuiScreen(new ConfigUI());
                        System.out.println("Args: " + args.length);
                        if (args.length == 6) {
                            try {
                                Management.instance.configmgr.configs.add(new Config(args[3], args[4], args[5], ConfigType.locale));
                            } catch (final Exception e) {
                                Management.instance.configmgr.configs.add(new Config(args[3], ConfigType.locale));
                            }
                        } else {
                            Management.instance.configmgr.configs.add(new Config(args[3], ConfigType.locale));
                        }
                        break;
                }
                break;

            case "online":
                switch (args[2]) {
                    case "list":
                        ChatUtil.sendMessage("§6------§fConfigs §6------");
                        for (final Config config : Management.instance.configmgr.configs) {
                            if (config.getType() == ConfigType.online) {
                                ChatUtil.addConfigMessage(config);
                            }
                        }
                        break;
                    case "load":
                        try {
                            Management.instance.configmgr.getConfigByName(args[3], ConfigType.online).loadConfig();
                        } catch (final NullPointerException e) {
                            ChatUtil.sendMessageWithPrefix("Config not found: " + args[3]);
                        }
                        break;
                }
                break;
        }
    }

}
