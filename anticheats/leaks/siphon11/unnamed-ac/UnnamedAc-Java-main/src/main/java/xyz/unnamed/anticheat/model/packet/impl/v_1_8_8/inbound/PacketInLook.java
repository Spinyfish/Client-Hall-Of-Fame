package xyz.unnamed.anticheat.model.packet.impl.v_1_8_8.inbound;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInFlying;
import xyz.unnamed.anticheat.model.PlayerData;
import xyz.unnamed.anticheat.model.packet.impl.PacketWrapper;
import xyz.unnamed.anticheat.model.packet.nms.UWrappedPacketDataSerializer;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

@Getter
@PacketWrapper(nmsClass = PacketPlayInFlying.PacketPlayInLook.class)
public class PacketInLook extends PacketInFlying {
    @Override
    public void handle(PlayerData playerData) {
        super.handle(playerData);
    }

    @Override
    public void read(UWrappedPacketDataSerializer packetDataSerializer) {
        this.yaw = packetDataSerializer.readFloat();
        this.pitch = packetDataSerializer.readFloat();
        this.rot = true;
        super.read(packetDataSerializer);
    }
}
