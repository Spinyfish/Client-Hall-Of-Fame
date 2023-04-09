package niggerlib.mc.protocol.packet.ingame.client.world;

import niggerlib.mc.protocol.packet.MinecraftPacket;
import niggerlib.packetlib.io.NetInput;
import niggerlib.packetlib.io.NetOutput;

import java.io.IOException;

public class ClientTeleportConfirmPacket extends MinecraftPacket {
    private int id;

    @SuppressWarnings("unused")
    private ClientTeleportConfirmPacket() {
    }

    public ClientTeleportConfirmPacket(int id) {
        this.id = id;
    }

    public int getTeleportId() {
        return this.id;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.id = in.readVarInt();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.id);
    }
}
