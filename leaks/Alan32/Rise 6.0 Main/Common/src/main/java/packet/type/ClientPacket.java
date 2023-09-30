package packet.type;

import packet.Packet;
import packet.handler.impl.IClientPacketHandler;

public interface ClientPacket extends Packet<IClientPacketHandler> {
    @Override
    void process(final IClientPacketHandler handler);
}
