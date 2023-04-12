package xyz.unnamed.anticheat.model.packet.impl.v_1_8_8.outbound;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;
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
@PacketWrapper(nmsClass = PacketPlayOutPosition.class)
public class PacketOutPosition implements UPacket {
    private double x;
    private double y;
    private double z;
    private float yaw;
    private float pitch;

    @Override
    public void handle(PlayerData playerData) {
        // TODO: Add implementation for handling this packet
    }

    @Override
    public void read(UWrappedPacketDataSerializer packetDataSerializer) {
        this.x = packetDataSerializer.readDouble();
        this.y = packetDataSerializer.readDouble();
        this.z = packetDataSerializer.readDouble();
        this.yaw = packetDataSerializer.readFloat();
        this.pitch = packetDataSerializer.readFloat();
    }
}
