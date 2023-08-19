package cc.swift.commands;

import cc.swift.Swift;
import cc.swift.commands.impl.*;
import cc.swift.events.ChatEvent;
import dev.codeman.eventbus.Handler;
import dev.codeman.eventbus.Listener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public final class CommandManager {

    private final HashMap<String, Command> commands = new HashMap<>();

    private final String prefix = ".";

    public void addCommand(Command command) {
        commands.put(command.name.toLowerCase(), command);
        for (String alias : command.aliases) {
            commands.put(alias.toLowerCase(), command);
        }
    }

    public void setupCommands() {
        addCommand(new SayCommand());
        addCommand(new BindCommand());
        addCommand(new HelpCommand());
        addCommand(new LoginCommand());
        addCommand(new ConfigCommand());
    }

    public ArrayList<Command> getCommands() {
        return new ArrayList<>(commands.values());
    }

    @Handler
    private final Listener<ChatEvent> chatEventListener = event -> {
        if (event.getText().startsWith(prefix)) {
            event.setCancelled(true);
            String[] split = event.getText().split(" ");
            String commandName = split[0].replace(prefix, "");
            if (commands.containsKey(commandName.toLowerCase())) {
                commands.get(commandName).onCommand(Arrays.copyOfRange(split, prefix.length(), split.length));
            }
        }
    };

}
