package packet.type;

import packet.Packet;
import packet.handler.impl.IServerPacketHandler;

public interface ServerPacket extends Packet<IServerPacketHandler> {
    @Override
    void process(final IServerPacketHandler handler);
}
