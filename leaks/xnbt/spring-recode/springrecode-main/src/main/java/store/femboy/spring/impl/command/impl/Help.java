package store.femboy.spring.impl.command.impl;

import store.femboy.spring.Spring;
import store.femboy.spring.impl.command.Command;
import store.femboy.spring.impl.util.AddChatMessage;

public class Help extends Command {

    public Help() {
        super("Help", "Shows this message", "help", "h");
    }

    @Override
    public void onCommand(String[] args, String command) {
        AddChatMessage.print("List of commands:");
        for (Command c : Spring.INSTANCE.commandManager.commands) {
            AddChatMessage.print(String.format("- %s", c.name + " - " + c.description));
        }
    }
}
