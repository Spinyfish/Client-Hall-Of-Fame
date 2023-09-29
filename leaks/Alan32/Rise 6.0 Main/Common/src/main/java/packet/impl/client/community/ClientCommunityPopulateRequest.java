package packet.impl.client.community;

import lombok.Data;
import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

@Data
public final class ClientCommunityPopulateRequest implements ClientPacket {

    @Override
    public void process(final IClientPacketHandler handler) {
        handler.handle(this);
    }
}
