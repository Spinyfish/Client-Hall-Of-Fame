package dev.africa.packetapi.impl;

import com.google.gson.annotations.SerializedName;
import dev.africa.packetapi.Packet;
import dev.africa.packetapi.PacketRegistry;
import lombok.Getter;

@Getter
public class PingPacket extends Packet {
    @SerializedName("currentTime")
    private final long currentTime;

    public PingPacket(long currentTime) {
        super(PacketRegistry.PING);

        this.currentTime = currentTime;
    }
}
