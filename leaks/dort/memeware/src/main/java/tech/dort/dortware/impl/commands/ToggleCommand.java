package tech.dort.dortware.impl.commands;

import tech.dort.dortware.Client;
import tech.dort.dortware.api.command.Command;
import tech.dort.dortware.api.command.CommandData;
import tech.dort.dortware.impl.utils.player.ChatUtil;

import java.util.NoSuchElementException;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super(new CommandData("toggle", "t"));
    }

    @Override
    public void run(String command, String... args) {
        try {
            switch (command.toLowerCase()) {
                case "t":
                case "toggle":
                    Client.INSTANCE.getModuleManager().getByNameIgnoreSpaceCaseInsensitive(args[1]).toggle();
                    ChatUtil.displayChatMessage("Toggled.");
                    break;
            }
        } catch (ArrayIndexOutOfBoundsException exception) {
            ChatUtil.displayChatMessage("Not enough arguments\nUsage:\n[toggle/t] [module]");
        } catch (NoSuchElementException exception) {
            ChatUtil.displayChatMessage("Invalid module.");
        } catch (Exception exception) {
            ChatUtil.displayChatMessage("The fuck did you do? LOL");
        }
    }
}
