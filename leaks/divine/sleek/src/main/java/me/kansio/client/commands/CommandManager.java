package me.kansio.client.commands;

import com.google.common.eventbus.Subscribe;
import lombok.Getter;
import me.kansio.client.Client;
import me.kansio.client.commands.impl.*;
import me.kansio.client.event.impl.ChatEvent;
import me.kansio.client.utils.chat.ChatUtil;

import java.util.ArrayList;

public class CommandManager {

    @Getter private ArrayList<Command> commands = new ArrayList<>();

    public CommandManager() {
        registerCommands();
        Client.getInstance().getEventBus().register(this);
    }

    @Subscribe
    public void callCommand(ChatEvent event) {
        if (!event.getMessage().startsWith(".")) return;
        event.setCancelled(true);
        String cmd = event.getMessage();
        String[] split = cmd.split(" ");
        String command = split[0];
        String args = cmd.substring(command.length()).trim();
        for (Command command1 : commands) {
            String cmdName = "." + command1.getClass().getAnnotation(CommandData.class).name();
            if (cmdName.equalsIgnoreCase(command)) {
                try {
                    command1.run(args.split(" "));
                } catch (Exception e) {
                    ChatUtil.log("Invalid command usage.");
                }
                break;
            } else {
                for (String alias : command1.getClass().getAnnotation(CommandData.class).aliases()) {
                    String aliasName = "." + alias;
                    if (aliasName.equalsIgnoreCase(command)) {
                        try {
                            command1.run(args.split(" "));
                        } catch (Exception e) {
                            ChatUtil.log("Invalid command usage.");
                        }
                        break;
                    }
                }
            }
        }

    }

    public void clearCommands() {
        commands.clear();
    }

    public void registerCommands() {
        commands.add(new ToggleCommand());
        commands.add(new ReloadCommand());
        commands.add(new BindCommand());
        commands.add(new UsersCommand());
        commands.add(new BindsCommand());
        commands.add(new ConfigCommand());
        commands.add(new VClipCommand());
        commands.add(new FriendCommand());
        commands.add(new FocusCommand());
        commands.add(new NameCommand());
        commands.add(new HelpCommand());
        commands.add(new HClipCommand());
        commands.add(new PanicCommand());
    }
}
