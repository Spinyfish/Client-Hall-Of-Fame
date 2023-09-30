package packet.impl.client.protection;

import lombok.Data;
import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;
import util.simpleobjects.TargetObject;

import java.util.List;

@Data
public final class lllIIllIlIIlIlllIllIlIIIIIlIlIIl implements ClientPacket {

    final List<TargetObject> lIlllIIllIIIlIIIIllIIIlIlI;
    final int lllIIllIlIlIlIlIlllIlIIIIIlIlIIl;
    final boolean llllIlllIlIIIIllIlIlIlIIIIlIlIIl;
    final boolean llllIlIlIlIIlllIlIIIIllIIIlIlIIl;
    final boolean lIIIllIIIlIlIIlllIlIlIlIIlllIlIl;
    final boolean lIIlllIlIlIlIIllIIIlIlIIIlllIlIl;

    @Override
    public void process(final IClientPacketHandler handler) {
        handler.handle(this);
    }
}
