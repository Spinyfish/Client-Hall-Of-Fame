package cafe.corrosion.command.manager;

import cafe.corrosion.attributes.CommandAttributes;
import cafe.corrosion.command.ICommand;
import cafe.corrosion.command.impl.ConfigCommand;
import cafe.corrosion.command.impl.FriendCommand;
import cafe.corrosion.command.impl.ToggleCommand;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class CommandManager {
    private final Map<CommandAttributes, ICommand> commandAttributes = new HashMap();

    public void init() {
        Arrays.asList(new ToggleCommand(), new ConfigCommand(), new FriendCommand()).forEach((command) -> {
            Class<?> clazz = command.getClass();
            if (!clazz.isAnnotationPresent(CommandAttributes.class)) {
                throw new RuntimeException("No CommandAttributes found for command " + clazz.getSimpleName());
            } else {
                CommandAttributes attributes = (CommandAttributes)clazz.getDeclaredAnnotation(CommandAttributes.class);
                this.commandAttributes.put(attributes, command);
            }
        });
    }

    public void processCommand(String commandName, String[] commandContext) {
        this.commandAttributes.entrySet().stream().filter((entry) -> {
            CommandAttributes attributes = (CommandAttributes)entry.getKey();
            return attributes.name().equalsIgnoreCase(commandName);
        }).map(Entry::getValue).forEach((command) -> {
            command.handle(commandContext);
        });
    }
}
