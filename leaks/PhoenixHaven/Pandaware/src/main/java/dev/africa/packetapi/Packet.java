package dev.africa.packetapi;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public class Packet {
    @SerializedName("assemblyName")
    private final String assemblyName;

    @SerializedName("packetName")
    private final String packetName;

    public Packet(PacketRegistry packetRegistry) {
        this.assemblyName = "Zinc.Application.Packet.Impl." + packetRegistry.getPacketName();
        this.packetName = packetRegistry.getPacketName();
    }
}
