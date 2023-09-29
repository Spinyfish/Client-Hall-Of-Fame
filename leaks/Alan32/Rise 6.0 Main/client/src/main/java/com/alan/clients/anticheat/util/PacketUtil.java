package com.alan.clients.anticheat.util;

import lombok.experimental.UtilityClass;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S14PacketEntity;

@UtilityClass
public class PacketUtil {

    public boolean isRelMove(final Packet<?> p) {
        return p instanceof S14PacketEntity;
    }

    public boolean isLook(final Packet<?> p) {
        return p instanceof S14PacketEntity.S17PacketEntityLookMove
                || p instanceof S14PacketEntity.S16PacketEntityLook;
    }
}
