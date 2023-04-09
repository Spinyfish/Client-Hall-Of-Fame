package niggerlib.mc.protocol.packet.ingame.server;

import niggerlib.mc.protocol.packet.MinecraftPacket;
import niggerlib.packetlib.io.NetInput;
import niggerlib.packetlib.io.NetOutput;

import java.io.IOException;

public class ServerSwitchCameraPacket extends MinecraftPacket {
    private int cameraEntityId;

    @SuppressWarnings("unused")
    private ServerSwitchCameraPacket() {
    }

    public ServerSwitchCameraPacket(int cameraEntityId) {
        this.cameraEntityId = cameraEntityId;
    }

    public int getCameraEntityId() {
        return this.cameraEntityId;
    }

    @Override
    public void read(NetInput in) throws IOException {
        this.cameraEntityId = in.readVarInt();
    }

    @Override
    public void write(NetOutput out) throws IOException {
        out.writeVarInt(this.cameraEntityId);
    }
}
