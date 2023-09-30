package dev.africa.pandaware.impl.event.game;

import dev.africa.pandaware.api.event.Event;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class ServerJoinEvent extends Event {
    private String ip;
    private int port;
}
