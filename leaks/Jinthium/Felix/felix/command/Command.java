package felix.command;

import felix.util.other.Logger;

public abstract class Command {

    private String commandName;
    
    private String[] commandArguments;

    public Command(String commandName) {
        this.commandName = commandName;
    }

    public abstract String usage();

    public void printUsage() {
        Logger.print(this.usage());
    }

    public abstract void executeCommand(String[] commandArguments);

    public String getCommandName() {
        return commandName;
    }

    public void setCommandName(String commandName) {
        this.commandName = commandName;
    }

    public String[] getCommandArguments() {
        return commandArguments;
    }

    public void setCommandArguments(String[] commandArguments) {
        this.commandArguments = commandArguments;
    }
}
