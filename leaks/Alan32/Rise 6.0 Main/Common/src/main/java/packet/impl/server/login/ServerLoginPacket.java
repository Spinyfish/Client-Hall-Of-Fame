package packet.impl.server.login;

import lombok.Data;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;

@Data
public final class ServerLoginPacket implements ServerPacket {

    private final boolean success;
    private final String information;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }
}
