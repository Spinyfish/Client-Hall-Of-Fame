package net.minecraft.network.play.client;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;

import java.io.IOException;

public class C20CustomPacket implements Packet {

    private int id;

    public C20CustomPacket(int id) {
        this.id = id;
    }

    public void readPacketData(PacketBuffer data) throws IOException { }

    @Override
    public void writePacketData(PacketBuffer var1) throws IOException {
        var1.writeByte(this.id);
    }

    @Override
    public void processPacket(INetHandler var1) {

    }
}
