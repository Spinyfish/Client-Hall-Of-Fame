package crispy.features.commands.impl;

import crispy.Crispy;
import crispy.features.commands.Command;
import crispy.features.commands.CommandInfo;

@CommandInfo(name = "Help", alias = "help", description = "Brings up the help menu", syntax = ".help")
public class HelpCommand extends Command {
    @Override
    public void onCommand(String arg, String[] args) throws Exception {
        message("All available commands:", true);
        for(Command command : Crispy.INSTANCE.getCommandManager().getCommands()) {
            if (!command.equals(this)) {
                message(command.getName() + ": " + command.getDescription(), true);
            }
        }
    }
}
