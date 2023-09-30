package dev.tenacity.intent.cloud.data;

import lombok.Getter;

public class CloudConfig extends CloudData {

    @Getter
    private final String server;

    public CloudConfig(String name, String description, String shareCode, String author, String version, String lastUpdated, String server, boolean ownership) {
        super(name, description, shareCode, author, version, lastUpdated, ownership);
        this.server = server;
    }

}
