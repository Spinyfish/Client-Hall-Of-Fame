/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.command.Command
 *  vip.astroline.client.service.command.impl.BindCommand
 */
package vip.astroline.client.service.command;

import java.util.ArrayList;
import java.util.Iterator;
import vip.astroline.client.Astroline;
import vip.astroline.client.service.command.Command;
import vip.astroline.client.service.command.impl.BindCommand;

public class CommandManager {
    private static final ArrayList<Command> commands = new ArrayList();

    public CommandManager() {
        CommandManager.addCommand((Command)new BindCommand(new String[]{"bind"}, "Bind modules"));
    }

    public static ArrayList<Command> getCommands() {
        return commands;
    }

    public static void addCommand(Command command) {
        commands.add(command);
    }

    public void callCommand(String input) {
        String[] split = input.split(" ");
        String command = split[0];
        String args = input.substring(command.length()).trim();
        Iterator<Command> iterator = CommandManager.getCommands().iterator();
        while (iterator.hasNext()) {
            Command c = iterator.next();
            if (!c.getName().equals(command)) continue;
            try {
                c.executeCommand(args, args.split(" "));
            }
            catch (Exception e) {
                Astroline.INSTANCE.tellPlayer(c.getSyntax());
            }
        }
    }
}
