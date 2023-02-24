package me.kansio.client.event.impl;

import lombok.Getter;
import me.kansio.client.event.Event;

public class ServerJoinEvent extends Event {

    @Getter private String serverIP;
    @Getter private String ign;

    public ServerJoinEvent(String serverIP, String ign) {
        this.serverIP = serverIP;
        this.ign = ign;
    }
}
