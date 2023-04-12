package xyz.unnamed.anticheat.model.packet.impl.v_1_8_8.inbound;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import org.bukkit.Bukkit;
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
@PacketWrapper(nmsClass = PacketPlayInFlying.class)
public class PacketInFlying implements UPacket {
    protected double x, y, z;
    protected float yaw, pitch;
    protected boolean ground, rot, pos;

    @Override
    public void handle(PlayerData playerData) {
        playerData.getPositionProcessor().update(this);
        playerData.getCollisionProcessor().update(this);

    }

    @Override
    public void read(UWrappedPacketDataSerializer packetDataSerializer) {
        this.ground = packetDataSerializer.readUnsignedByte() != 0;
    }
}
