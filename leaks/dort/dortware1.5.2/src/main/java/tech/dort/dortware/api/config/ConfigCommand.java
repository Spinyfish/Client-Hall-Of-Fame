package tech.dort.dortware.api.config;

import tech.dort.dortware.Client;
import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.utils.player.ChatUtil;

import static tech.dort.dortware.impl.utils.player.ChatUtil.displayChatMessage;


public class ConfigCommand extends Command {

    public ConfigCommand() {
        super(new CommandData("config"));
    }

    @Override
    public void run(String command, String... args) {
        try {
            switch (args[1].toLowerCase()) {
                case "load": {
                    try {
                        Config config = Client.INSTANCE.getConfigManager().find(args[2].toLowerCase());
                        config.load(Client.INSTANCE.getFileManager());
                        displayChatMessage("Loaded config " + args[2].toLowerCase() + ".");
                    } catch (Exception exc) {
                        exc.printStackTrace();
                        displayChatMessage("That config doesn't exist!");
                    }
                    break;
                }

                case "save": {
                    if (!Client.INSTANCE.getConfigManager().saveIfAdded(args[2].toLowerCase())) {
                        Config nigger = new Config(args[2].toLowerCase());
                        nigger.save();
                        Client.INSTANCE.getConfigManager().add(nigger);
                    }
                    displayChatMessage("Saved config " + args[2].toLowerCase() + ".");
                    break;
                }

                case "cloud": {
                    displayChatMessage("SoonTM.");
                    break;
                }

                case "reload": {
                    Client.INSTANCE.getConfigManager().onCreated();
                    displayChatMessage("Reloaded.");
                    break;
                }

                case "list": {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Configs: \n");
                    int i = 0;
                    for (Config config : Client.INSTANCE.getConfigManager().getObjects()) {
                        i++;
                        stringBuilder.append("Config ").append(i).append(": ").append(config.getName());
                        stringBuilder.append("\n");
                    }
                    ChatUtil.displayChatMessage(stringBuilder.toString());
                    break;
                }

                default: {
                    showHelp();
                    break;
                }
            }
        } catch (Exception exc) {
            showHelp();
        }
    }

    private void showHelp() {
        String prefix = ".";
        displayChatMessage("Invalid syntax! Showing help:");
        displayChatMessage(prefix + "config load (config) - Loads a config locally.");
        displayChatMessage(prefix + "config save (config) - Saves a config locally.");
        displayChatMessage(prefix + "config reload - Reloads the config list.");
        displayChatMessage(prefix + "config list - Shows all of your saved configs.");
    }
}
