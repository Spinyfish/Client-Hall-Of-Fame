package niggerlib.mc.protocol.packet.ingame.client;

import niggerlib.mc.protocol.packet.MinecraftPacket;
import niggerlib.packetlib.io.NetInput;
import niggerlib.packetlib.io.NetOutput;

import java.io.IOException;

public class ClientKeepAlivePacket extends MinecraftPacket {
    private long id;

    @SuppressWarnings("unused")
    private ClientKeepAlivePacket() {
    }

    public ClientKeepAlivePacket(long id) {
        this.id = id;
    }

    public long getPingId() {
        return this.id;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.id = in.readLong();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeLong(this.id);
    }
}
