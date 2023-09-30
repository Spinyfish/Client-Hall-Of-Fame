package packet.impl.server.general;

import lombok.Data;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;

@Data
public final class ServerKeepAlive implements ServerPacket {
    
    @Override
    public void process(final IServerPacketHandler handler) {
        handler.handle(this);
    }
}
