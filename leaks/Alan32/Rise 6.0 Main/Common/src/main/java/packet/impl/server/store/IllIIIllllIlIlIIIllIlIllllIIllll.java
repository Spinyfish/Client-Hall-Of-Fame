package packet.impl.server.store;

import lombok.Data;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;
import store.File;

import java.util.ArrayList;

@Data
public final class IllIIIllllIlIlIIIllIlIllllIIllll implements ServerPacket {

    private final ArrayList<File> files;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }
}
