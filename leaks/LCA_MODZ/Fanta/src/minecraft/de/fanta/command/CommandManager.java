package de.fanta.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.fanta.command.impl.Bind;
import de.fanta.command.impl.Config;
import de.fanta.command.impl.FriendCommand;
import de.fanta.command.impl.Global;
import de.fanta.command.impl.Help;
import de.fanta.command.impl.Reload;
import de.fanta.command.impl.Report;
import de.fanta.command.impl.SkidIrcCommand;
import de.fanta.command.impl.Spec;
import de.fanta.command.impl.Toggle;
import de.fanta.command.impl.UserName;
import de.fanta.command.impl.setSkin;

public class CommandManager {

    public static List<Command> commands = new ArrayList();

    public final String Chat_Prefix = ".";

    public CommandManager(){
    addCommand(new Bind());
    addCommand(new Toggle());
    addCommand(new Reload());
    addCommand(new FriendCommand());
    addCommand(new SkidIrcCommand());
    addCommand(new Global());
    addCommand(new Config());
    addCommand(new Spec());
    addCommand(new Report());
    addCommand(new UserName());
    addCommand(new setSkin());
    addCommand(new Help());
    
    }

    public void addCommand(Command cmd){
        this.commands.add(cmd);
    }

    public boolean execute(String text){
        if(!text.startsWith(Chat_Prefix)){
            return false;
        }
        text = text.substring(1);

        final String[] arguments = text.split(" ");
        for(Command cmd : this.commands){
            if(cmd.getName().equalsIgnoreCase(arguments[0])){
                String[] args = (String[]) Arrays.copyOfRange(arguments, 1, arguments.length);
                cmd.execute(args);
                return true;
            }
        }
        return false;
    }
}
