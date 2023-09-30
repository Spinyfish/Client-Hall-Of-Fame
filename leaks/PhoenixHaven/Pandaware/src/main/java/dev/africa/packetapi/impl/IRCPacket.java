package dev.africa.packetapi.impl;

import com.google.gson.annotations.SerializedName;
import dev.africa.packetapi.Packet;
import dev.africa.packetapi.PacketRegistry;
import lombok.Getter;

@Getter
public class IRCPacket extends Packet {
    @SerializedName("user")
    private final String user;

    @SerializedName("message")
    private final String message;

    public IRCPacket(String user, String message) {
        super(PacketRegistry.IRC);

        this.user = user;
        this.message = message;
    }
}
