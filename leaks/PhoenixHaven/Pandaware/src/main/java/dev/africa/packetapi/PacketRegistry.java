package dev.africa.packetapi;

import dev.africa.packetapi.impl.ActionPacket;
import dev.africa.packetapi.impl.AuthenticationPacket;
import dev.africa.packetapi.impl.IRCPacket;
import dev.africa.packetapi.impl.PingPacket;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@Getter
@RequiredArgsConstructor
public enum PacketRegistry {
    ACTION("ActionPacket", ActionPacket.class),
    AUTHENTICATION("AuthenticationPacket", AuthenticationPacket.class),
    IRC("IRCPacket", IRCPacket.class),
    PING("PingPacket", PingPacket.class);

    public static PacketRegistry getByName(String name) {
        return Arrays.stream(PacketRegistry.values())
                .filter(packetRegistry -> packetRegistry.getPacketName().equals(name))
                .findFirst()
                .orElse(null);
    }

    private final String packetName;
    private final Class<? extends Packet> clazz;
}
