package packet;

import packet.handler.PacketHandler;

import java.io.Serializable;

public interface Packet<T extends PacketHandler> extends Serializable {
    void process(final T handler);
}
