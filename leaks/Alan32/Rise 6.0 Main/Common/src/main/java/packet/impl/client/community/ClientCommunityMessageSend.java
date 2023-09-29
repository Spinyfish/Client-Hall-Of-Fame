package packet.impl.client.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

@Data@AllArgsConstructor
public final class ClientCommunityMessageSend implements ClientPacket {
    public String message;

    @Override
    public void process(final IClientPacketHandler handler) {
        handler.handle(this);
    }
}
