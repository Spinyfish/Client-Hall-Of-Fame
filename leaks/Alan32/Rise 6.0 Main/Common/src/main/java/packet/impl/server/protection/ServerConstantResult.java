package packet.impl.server.protection;

import lombok.AllArgsConstructor;
import lombok.Data;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;

@Data@AllArgsConstructor
public final class ServerConstantResult implements ServerPacket {
    public double l = 0; // PI
    public double I = 0; // TAU
    public float J = 0; // Max Pitch
    public double O = 0; // 180

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }
}
