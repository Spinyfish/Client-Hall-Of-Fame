package xyz.unnamed.anticheat.model.packet.nms.impl;

import io.netty.buffer.ByteBuf;
import lombok.SneakyThrows;
import net.minecraft.server.v1_8_R3.Packet;
import net.minecraft.server.v1_8_R3.PacketDataSerializer;
import xyz.unnamed.anticheat.model.packet.nms.UWrappedPacketDataSerializer;

/**
 * Copyright (c) 2022 - Tranquil, LLC.
 *
 * @author incognito@tranquil.cc
 * @date 4/8/2023
 */

public class WrappedPacketDataSerializer_v1_8_R3 implements UWrappedPacketDataSerializer {
    private PacketDataSerializer packetDataSerializer = null;

    @Override
    public void setBuffer(ByteBuf buf) {
        this.packetDataSerializer = new PacketDataSerializer(buf);
    }

    @Override
    @SneakyThrows
    public void serializePacket(Object packet) {
        Packet<?> nmsPacket = (Packet<?>) packet;
        nmsPacket.b(this.packetDataSerializer);
    }

    @Override
    public short readShort() {
        return this.packetDataSerializer.readShort();
    }

    @Override
    public boolean readBoolean() {
        return this.packetDataSerializer.readBoolean();
    }

    @Override
    public byte readByte() {
        return this.packetDataSerializer.readByte();
    }

    @Override
    public short readUnsignedByte() {
        return this.packetDataSerializer.readUnsignedByte();
    }

    @Override
    public float readFloat() {
        return this.packetDataSerializer.readFloat();
    }

    @Override
    public double readDouble() {
        return this.packetDataSerializer.readDouble();
    }


    @Override
    public String readString(int maxLength) {
        return this.packetDataSerializer.c(maxLength);
    }

    @Override
    public byte[] readByteArray() {
        return this.packetDataSerializer.a();
    }

    @Override
    public <T extends Enum<T>> T readEnum(Class<T> oclass) {
        return this.packetDataSerializer.a(oclass);
    }

    @Override
    public int readVarInt() {
        return this.packetDataSerializer.e();
    }
}
