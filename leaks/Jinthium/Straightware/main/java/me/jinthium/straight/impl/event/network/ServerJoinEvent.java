package me.jinthium.straight.impl.event.network;

import me.jinthium.straight.api.event.Event;

public final class ServerJoinEvent extends Event {
    public String ip;
    public int port;

    public ServerJoinEvent(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}