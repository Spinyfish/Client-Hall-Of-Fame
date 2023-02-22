package packet.impl.client.general;

import lombok.Data;
import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

@Data
public final class ClientKeepAlive implements ClientPacket {

    @Override
    public void process(final IClientPacketHandler handler) {
        handler.handle(this);
    }
}
