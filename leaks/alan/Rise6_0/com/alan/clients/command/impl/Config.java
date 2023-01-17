package com.alan.clients.command.impl;

import com.alan.clients.command.Command;
import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.file.config.ConfigFile;
import com.alan.clients.util.file.config.ConfigManager;
import com.alan.clients.util.localization.Localization;
import net.minecraft.event.ClickEvent;
import net.minecraft.event.HoverEvent;
import net.minecraft.util.ChatComponentText;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public final class Config extends Command {

    public Config() {
        super("command.config.description", "config", "cfg", "settings");
    }

    @Override
    public void execute(final String[] args) {
        final ConfigManager configManager = instance.getConfigManager();
        final String command = args[1].toLowerCase();

        switch (args.length) {
            case 3:
                final String name = args[2];

                switch (command) {
                    case "load":
                        configManager.update();

                        final ConfigFile config = configManager.get(name, false);

                        if (config != null) {
                            CompletableFuture.runAsync(() -> {
                                if (config.read()) {
                                    ChatUtil.display("command.config.loaded", name);
                                } else {
                                    ChatUtil.display("command.config.notfound");
                                }
                            });
                        } else {
                            ChatUtil.display("command.config.notfound");
                        }
                        break;

                    case "save":
                    case "create":
                        if (name.equalsIgnoreCase("latest")) {
                            ChatUtil.display("command.config.reserved");
                            return;
                        }

                        CompletableFuture.runAsync(() -> {
                            configManager.set(name);

                            ChatUtil.display("command.config.saved");
                        });
                        break;

                    default:
                        ChatUtil.display("command.config.usage");
                        break;
                }
                break;

            case 2:
                switch (command) {
                    case "list":
                        ChatUtil.display("command.config.selectload");

                        configManager.update();

                        configManager.forEach(configFile -> {
                            final String configName = configFile.getFile().getName().replace(".json", "");
                            final String configCommand = ".config load " + configName;
                            final String color = getTheme().getChatAccentColor().toString();

                            final ChatComponentText chatText = new ChatComponentText(color + "> " + configName);
                            final ChatComponentText hoverText = new ChatComponentText(String.format(Localization.get("command.config.loadhover"), configName));

                            chatText.getChatStyle().setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, configCommand))
                                    .setChatHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, hoverText));

                            mc.thePlayer.addChatMessage(chatText);
                        });
                        break;

                    case "folder":
                        try {
                            Desktop desktop = Desktop.getDesktop();
                            File dirToOpen = new File(String.valueOf(ConfigManager.CONFIG_DIRECTORY));
                            desktop.open(dirToOpen);
                            ChatUtil.display("command.config.folder");
                        } catch (IllegalArgumentException | IOException iae) {
                            ChatUtil.display("command.config.notfound");
                        }
                        break;

                    default:
                        ChatUtil.display("command.config.actions");
                        break;
                }
                break;

            default:
                ChatUtil.display("command.config.actions");
                break;
        }
    }
}
