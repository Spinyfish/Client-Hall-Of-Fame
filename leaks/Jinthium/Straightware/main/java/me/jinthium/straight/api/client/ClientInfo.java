package me.jinthium.straight.api.client;

public class ClientInfo {
    private final String name, version;

    public ClientInfo(String name, String version) {
        this.name = name;
        this.version = version;
    }

    public final String getClientTitle(){
        return name + " " + version;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }
}
