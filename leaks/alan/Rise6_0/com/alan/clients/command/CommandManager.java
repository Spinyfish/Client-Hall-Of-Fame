package com.alan.clients.command;

import com.alan.clients.Client;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.input.ChatInputEvent;
import com.alan.clients.util.chat.ChatUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Patrick
 * @since 10/19/2021
 */
public final class CommandManager extends ArrayList<Command> {

    /**
     * Called on client start
     */
    public void init() {
//        final Reflections reflections = new Reflections("com.riseclient.rise.command.impl");
//
//        reflections.getSubTypesOf(Command.class).forEach(clazz -> {
//            try {
//                this.add(clazz.newInstance());
//            } catch (final Exception e) {
//                e.printStackTrace();
//            }
//        });

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
            ChatUtil.display("command.unknown");
        }

        event.setCancelled(true);
    };
}