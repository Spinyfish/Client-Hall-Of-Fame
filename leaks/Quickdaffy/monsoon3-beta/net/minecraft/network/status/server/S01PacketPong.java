/*
 * Decompiled with CFR 0.152.
 */
package net.minecraft.network.status.server;

import java.io.IOException;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.status.INetHandlerStatusClient;

public class S01PacketPong
implements Packet<INetHandlerStatusClient> {
    private long clientTime;

    public S01PacketPong() {
    }

    public S01PacketPong(long time) {
        this.clientTime = time;
    }

    @Override
    public void readPacketData(PacketBuffer buf) throws IOException {
        this.clientTime = buf.readLong();
    }

    @Override
    public void writePacketData(PacketBuffer buf) throws IOException {
        buf.writeLong(this.clientTime);
    }

    @Override
    public void processPacket(INetHandlerStatusClient handler) {
        handler.handlePong(this);
    }
}

