package xyz.unnamed.anticheat.model.packet.impl.v_1_8_8.outbound;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutTransaction;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.model.packet.UPacket;
import xyz.unnamed.anticheat.model.packet.impl.PacketWrapper;
import xyz.unnamed.anticheat.model.packet.nms.UWrappedPacketDataSerializer;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@Getter
@PacketWrapper(nmsClass = PacketPlayOutTransaction.class)
public class PacketOutTransaction implements UPacket {
    private short id;

    @Override
    public void handle(PlayerData playerData) {

    }

    @Override
    public void read(UWrappedPacketDataSerializer packetDataSerializer) {
        packetDataSerializer.readByte();
        this.id = packetDataSerializer.readShort();
        packetDataSerializer.readBoolean();
    }
}
