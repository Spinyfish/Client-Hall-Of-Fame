package packet.impl.server.protection;

import lombok.AllArgsConstructor;
import lombok.Data;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;

@Data@AllArgsConstructor
public final class lIllIlIlIlIIIlllIIIlllIIIIlllI implements ServerPacket {
    private String lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI;
    private int lIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }
}
