package packet.impl.client.store;

import lombok.AllArgsConstructor;
import lombok.Getter;
import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;
import store.File;

@AllArgsConstructor@Getter
public final class lIIlIIIIIllIlllllllIIlllIlIllIlI implements ClientPacket {

    public File file;

    @Override
    public void process(final IClientPacketHandler handler) {
        handler.handle(this);
    }
}
