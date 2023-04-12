package xyz.unnamed.anticheat.model.packet.impl.v_1_8_8.outbound;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityVelocity;
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
@PacketWrapper(nmsClass = PacketPlayOutEntityVelocity.class)
public class PacketOutEntityVelocity implements UPacket {
    private int entityId;
    private double velocityX;
    private double velocityY;
    private double velocityZ;

    @Override
    public void handle(PlayerData playerData) {
        // TODO: Add implementation for handling this packet
    }

    @Override
    public void read(UWrappedPacketDataSerializer packetDataSerializer) {
        this.entityId = packetDataSerializer.readVarInt();
        this.velocityX = Math.abs(packetDataSerializer.readShort() / 8000.0);
        this.velocityY = packetDataSerializer.readShort() / 8000.0;
        this.velocityZ = Math.abs(packetDataSerializer.readShort() / 8000.0);
    }
}