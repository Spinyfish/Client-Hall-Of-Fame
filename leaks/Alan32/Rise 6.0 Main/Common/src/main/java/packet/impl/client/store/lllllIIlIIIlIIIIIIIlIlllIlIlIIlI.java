package packet.impl.client.store;

import lombok.Data;
import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

@Data
public final class lllllIIlIIIlIIIIIIIlIlllIlIlIIlI implements ClientPacket {

    @Override
    public void process(final IClientPacketHandler handler) {
        handler.handle(this);
    }
}
