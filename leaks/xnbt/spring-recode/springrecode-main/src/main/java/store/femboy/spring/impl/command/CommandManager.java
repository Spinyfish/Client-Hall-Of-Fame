package store.femboy.spring.impl.command;

import store.femboy.spring.impl.command.impl.Bind;
import store.femboy.spring.impl.command.impl.Help;
import store.femboy.spring.impl.event.impl.EventChat;
import store.femboy.spring.impl.util.AddChatMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandManager {

    public CommandManager() {
        setup();
    }

    public List<Command> commands = new ArrayList<Command>();
    public String prefix = ".";

    public void setup() {
        commands.add(new Help());
        commands.add(new Bind());
    }

    public void handleChat(EventChat e) {
        String message = e.getMessage();

        if (!message.startsWith(prefix)) {
            return;
        }

        message = message.substring(prefix.length());

        boolean foundCommand = false;

        if (message.split(" ").length > 0) {
            String commandName = message.split(" ")[0];

            for (Command c : commands) {
                if (c.aliases.contains(commandName) || c.name.equalsIgnoreCase(commandName)) {
                    c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
                    e.setCancelled(true);
                    foundCommand = true;
                    break;
                }
            }
        }

        if (!foundCommand) {
            AddChatMessage.print("Couldn't find this command. Please type .help to find out what the commands are.");
            e.setCancelled(true);
        }
    }
}
