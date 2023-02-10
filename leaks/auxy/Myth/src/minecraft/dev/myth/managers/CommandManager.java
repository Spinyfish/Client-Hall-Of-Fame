/**
 * @project Myth
 * @author CodeMan
 * @at 05.08.22, 16:01
 */
package dev.myth.managers;

import dev.myth.api.command.Command;
import dev.myth.api.logger.Logger;
import dev.myth.api.manager.Manager;
import dev.myth.commands.*;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class CommandManager implements Manager {

    @Getter
    private final Map<Class<? extends Command>, Command> commands = new HashMap<>();

    @Override
    public void run() {
        addCommand(BindCommand.class);
        addCommand(ToggleCommand.class);
        addCommand(SettingCommand.class);
        addCommand(HelpCommand.class);
        addCommand(ConfigCommand.class);
        addCommand(FriendCommand.class);
    }

    @Override
    public void shutdown() {

    }

    private void addCommand(final Class<? extends Command> clazz) {
        try {
            final Command cmd = clazz.newInstance();
            this.commands.put(clazz, cmd);
        } catch (InstantiationException | IllegalAccessException ignored) {}
    }
    public <T extends Command> T getCommand(final Class<T> clazz) {
        return (T) this.commands.get(clazz);
    }
    public <T extends Command> T getCommand(final String name) {
        return (T) this.commands.values().stream().filter(command -> command.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public void call(final String msg) {
        String cmd = msg.split(" ")[0];
        Command command = getCommand(cmd);
        if(command == null) {
            Logger.doLog(Logger.Type.ERROR, "Command not found!");
            return;
        }
        if(msg.length() > cmd.length() + 1) command.run(msg.substring(cmd.length() + 1).split(" "));
        else command.run(new String[0]);
    }
}
