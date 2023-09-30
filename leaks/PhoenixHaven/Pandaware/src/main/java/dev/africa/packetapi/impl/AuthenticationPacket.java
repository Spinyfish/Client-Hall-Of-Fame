package dev.africa.packetapi.impl;

import com.google.gson.annotations.SerializedName;
import dev.africa.packetapi.Packet;
import dev.africa.packetapi.PacketRegistry;
import lombok.Getter;

@Getter
public class AuthenticationPacket extends Packet {
    @SerializedName("hwid")
    private final String hwid;

    @SerializedName("user")
    private final String user;

    public AuthenticationPacket(String hwid, String user) {
        super(PacketRegistry.AUTHENTICATION);

        this.hwid = hwid;
        this.user = user;
    }
}
