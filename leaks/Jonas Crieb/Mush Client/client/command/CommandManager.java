package client.command;

import client.Client;
import client.event.Listener;
import client.event.annotations.EventLink;
import client.event.impl.input.ChatInputEvent;
import client.util.chat.ChatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public final class CommandManager extends ArrayList<Command> {

    public void init() {
        Client.INSTANCE.getEventBus().register(this);
    }

    public <T extends Command> T get(final String name) {
        // noinspection unchecked
        return (T) this.stream()
                .filter(command -> Arrays.stream(command.getExpressions())
                        .anyMatch(expression -> expression.equalsIgnoreCase(name))
                ).findAny().orElse(null);
    }

    public <T extends Command> T get(final Class<? extends Command> clazz) {
        // noinspection unchecked
        return (T) this.stream()
                .filter(command -> command.getClass() == clazz)
                .findAny().orElse(null);
    }

    @EventLink()
    public final Listener<ChatInputEvent> onChatInput = event -> {

        String message = event.getMessage();

        if (!message.startsWith(".")) return;

        message = message.substring(1);
        final String[] args = message.split(" ");

        final AtomicBoolean commandFound = new AtomicBoolean(false);

        try {
            this.stream()
                    .filter(command ->
                            Arrays.stream(command.getExpressions())
                                    .anyMatch(expression ->
                                            expression.equalsIgnoreCase(args[0])))
                    .forEach(command -> {
                        commandFound.set(true);
                        command.execute(args);
                    });
        } catch (final Exception ex) {
            ex.printStackTrace();
        }

        if (!commandFound.get()) {
            ChatUtil.display("Unknown command! Try .help if you're lost");
        }

        event.setCancelled(true);
    };
}
