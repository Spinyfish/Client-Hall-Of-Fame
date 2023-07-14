/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.manager;

import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.command.Command;
import wtf.monsoon.api.util.entity.PlayerUtil;
import wtf.monsoon.impl.event.EventChatMessageSent;

public class CommandManager {
    private final List<Command> commands = new ArrayList<Command>();
    private final String prefix = ".";
    @EventLink
    public final Listener<EventChatMessageSent> eventChatMessageSentListener = event -> {
        String message = event.getContent();
        if (message.startsWith(".")) {
            event.cancel();
            String[] args = message.split(" ");
            String commandName = args[0].substring(".".length());
            String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);
            Command executedCommand = this.commands.stream().filter(command -> command.getName().equalsIgnoreCase(commandName)).findFirst().orElse(null);
            if (executedCommand != null) {
                executedCommand.execute(commandArgs);
            } else {
                PlayerUtil.sendClientMessage("Command does not exist!");
            }
        }
    };

    public CommandManager() {
        Wrapper.getEventBus().subscribe(this);
    }

    public List<Command> getCommands() {
        return this.commands;
    }
}

