package xyz.unnamed.anticheat.model.packet.impl.v_1_8_8.outbound;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityTeleport;
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
@PacketWrapper(nmsClass = PacketPlayOutEntityTeleport.class)
public class PacketOutEntityTeleport implements UPacket {
    private int entityId;
    private double x;
    private double y;
    private double z;
    private byte yaw;
    private byte pitch;
    private boolean onGround;

    @Override
    public void handle(PlayerData playerData) {
        // TODO: packet in handler
    }

    @Override
    public void read(UWrappedPacketDataSerializer packetDataSerializer) {
        this.entityId = packetDataSerializer.readVarInt();
        this.x = packetDataSerializer.readDouble();
        this.y = packetDataSerializer.readDouble();
        this.z = packetDataSerializer.readDouble();
        this.yaw = packetDataSerializer.readByte();
        this.pitch = packetDataSerializer.readByte();
        this.onGround = packetDataSerializer.readBoolean();
    }
}