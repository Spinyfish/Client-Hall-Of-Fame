package dev.africa.packetapi.impl;

import com.google.gson.annotations.SerializedName;
import dev.africa.packetapi.Packet;
import dev.africa.packetapi.PacketRegistry;
import lombok.Getter;

@Getter
public class ActionPacket extends Packet {
    @SerializedName("action")
    private final String action;

    @SerializedName("data")
    private final String data;

    public ActionPacket(String action, String data) {
        super(PacketRegistry.ACTION);

        this.action = action;
        this.data = data;
    }
}
