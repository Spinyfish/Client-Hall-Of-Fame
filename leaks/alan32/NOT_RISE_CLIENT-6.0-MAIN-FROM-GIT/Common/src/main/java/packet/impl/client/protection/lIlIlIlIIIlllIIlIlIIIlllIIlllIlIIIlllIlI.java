package packet.impl.client.protection;

import lombok.AllArgsConstructor;
import lombok.Data;
import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

@Data@AllArgsConstructor
public final class lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI implements ClientPacket {
    private String lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI;
    private int lIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI;

    @Override
    public void process(final IClientPacketHandler handler) {
        handler.handle(this);
    }
}
