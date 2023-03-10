/*
 * Decompiled with CFR 0.152.
 */
package lodomir.dev.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lodomir.dev.November;
import lodomir.dev.commands.Command;
import lodomir.dev.commands.impl.Bind;
import lodomir.dev.commands.impl.ClientName;
import lodomir.dev.commands.impl.Cloud;
import lodomir.dev.commands.impl.Config;
import lodomir.dev.commands.impl.Friend;
import lodomir.dev.commands.impl.HClip;
import lodomir.dev.commands.impl.Help;
import lodomir.dev.commands.impl.Name;
import lodomir.dev.commands.impl.Panic;
import lodomir.dev.commands.impl.Spammer;
import lodomir.dev.commands.impl.Teleport;
import lodomir.dev.commands.impl.Toggle;
import lodomir.dev.commands.impl.VClip;
import lodomir.dev.commands.impl.Visible;
import lodomir.dev.event.impl.game.EventChat;

public class CommandManager {
    public static Command[] command;
    public List<Command> commands = new ArrayList<Command>();
    public String prefix = ".";

    public CommandManager() {
        this.setup();
    }

    public void setup() {
        this.commands.add(new Toggle());
        this.commands.add(new ClientName());
        this.commands.add(new Config());
        this.commands.add(new VClip());
        this.commands.add(new Spammer());
        this.commands.add(new HClip());
        this.commands.add(new Bind());
        this.commands.add(new Visible());
        this.commands.add(new Cloud());
        this.commands.add(new Panic());
        this.commands.add(new Name());
        this.commands.add(new Teleport());
        this.commands.add(new Help());
        this.commands.add(new Friend());
    }

    public void handleChat(EventChat event) {
        String message = event.getMessage();
        if (!message.startsWith(this.prefix)) {
            return;
        }
        event.setCancelled(true);
        message = message.substring(this.prefix.length());
        boolean foundCommand = false;
        if (message.split(" ").length > 0) {
            String commandName = message.split(" ")[0];
            for (Command c : this.commands) {
                if (!c.aliases.contains(commandName) && !c.name.equalsIgnoreCase(commandName)) continue;
                c.onCommand(Arrays.copyOfRange(message.split(" "), 1, message.split(" ").length), message);
                foundCommand = true;
                break;
            }
        }
        if (!foundCommand) {
            November.Log("Command does not exist.");
        }
    }
}

