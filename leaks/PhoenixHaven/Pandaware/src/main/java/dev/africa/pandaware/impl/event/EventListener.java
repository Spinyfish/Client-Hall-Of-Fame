package dev.africa.pandaware.impl.event;

import dev.africa.pandaware.Client;
import dev.africa.pandaware.api.event.interfaces.EventCallback;
import dev.africa.pandaware.api.event.interfaces.EventHandler;
import dev.africa.pandaware.api.event.interfaces.EventListenable;
import dev.africa.pandaware.impl.event.player.ChatEvent;

public class EventListener implements EventListenable {

    @EventHandler
    EventCallback<ChatEvent> onChat = event ->
            Client.getInstance().getCommandManager().handleCommands(event);
}
