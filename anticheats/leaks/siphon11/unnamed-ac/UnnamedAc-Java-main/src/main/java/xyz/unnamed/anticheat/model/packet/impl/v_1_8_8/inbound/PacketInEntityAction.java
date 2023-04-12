package xyz.unnamed.anticheat.model.packet.impl.v_1_8_8.inbound;

import lombok.Getter;
import net.minecraft.server.v1_8_R3.PacketPlayInEntityAction;
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
@PacketWrapper(nmsClass = PacketPlayInEntityAction.class)
public class PacketInEntityAction implements UPacket {
    private int entityId;
    private PacketPlayInEntityAction.EnumPlayerAction action;
    private int jumpBoost;

    @Override
    public void handle(PlayerData playerData) {
        // TODO: Add implementation for handling this packet
    }

    @Override
    public void read(UWrappedPacketDataSerializer packetDataSerializer) {
        this.entityId = packetDataSerializer.readVarInt();
        this.action = packetDataSerializer.readEnum(PacketPlayInEntityAction.EnumPlayerAction.class);
        this.jumpBoost = packetDataSerializer.readVarInt();
    }
}