package byron.Mono.command;

import byron.Mono.Mono;
import byron.Mono.command.impl.Bind;
import byron.Mono.command.impl.Plugins;
import byron.Mono.event.impl.EventChat;

import java.util.ArrayList;

public class CommandManager {

    ArrayList<Command> commands = new ArrayList<>();


    public CommandManager() {
        init();
    }

    private final void init()
    {
        commands.add(new Bind());
    }

    public void handleChat(EventChat e) {
        for(Command command : commands) {
            if(e.getMessage().startsWith(command.getPrefix())) {
                e.setCancelled(true);

                if(e.getMessage().startsWith(command.getPrefix() + (command.getName()))|| e.getMessage().startsWith(command.getPrefix() + command.getAlias()))
                {
                    String message = e.getMessage().replace(command.getPrefix() + ((e.getMessage().startsWith(command.getPrefix() + (command.getName()))) ? command.getName() : command.getAlias()) + " ", "");
                    command.onCommand(message.split(" "));
                }
                else
                {
                    Mono.INSTANCE.sendAlert("Could not find command.");
                }

            }
        }
    }

}
