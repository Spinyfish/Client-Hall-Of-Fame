package packet.impl.server.community;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;
import community.Message;
import util.type.EvictingList;

import java.util.ArrayList;
import java.util.EventListener;

@AllArgsConstructor@Data
public final class ServerCommunityPopulatePacket implements ServerPacket {

    private EvictingList<Message> messages;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }
}
