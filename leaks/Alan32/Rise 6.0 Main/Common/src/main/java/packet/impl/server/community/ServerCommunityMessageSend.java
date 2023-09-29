package packet.impl.server.community;

import community.Message;
import lombok.AllArgsConstructor;
import lombok.Data;
import packet.handler.impl.IClientPacketHandler;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ClientPacket;
import packet.type.ServerPacket;

@Data@AllArgsConstructor
public final class ServerCommunityMessageSend implements ServerPacket {
    public Message message;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }
}
