package axolotl.cheats.commands;

import axolotl.Axolotl;
import axolotl.cheats.settings.CommandSettings;

import java.util.LinkedList;

public class CommandManager {

    public CommandSettings settings;

    public String var1 = "HF";

    public LinkedList<Command> commands = new LinkedList<Command>();

    public CommandManager(CommandSettings settings) {
        this.settings = settings;
    }

    public void loadCommand(Command command) {
        commands.add(command);
    }

    public void unloadCommand(Command command) {
        if(commands.contains(command)) {
            commands.remove(command);
        }
    }

    public boolean onChat(String message) {
        if(message.startsWith(settings.prefix)) {
            for(Command command : commands) {
                if (message.startsWith(settings.prefix + command.name)) {

                    String[] args = message.replace(settings.prefix + command.name + " ", "").split(" ");
                    String response = command.onCommand(args, message);

                    Axolotl.INSTANCE.sendMessage(response);

                    return true;
                }
            }

            Axolotl.INSTANCE.sendMessage("That is not a valid command! (say " + settings.prefix + "help for help) " + message);
            return true;
        }
        return false;
    }

}
