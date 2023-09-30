/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.server.general;

import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;

public final class llIllllIIIlIIlIIllIlIIIllIIIIIIl
implements ServerPacket {
    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return o instanceof llIllllIIIlIIlIIllIlIIIllIIIIIIl;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }

    public String toString() {
        return "llIllllIIIlIIlIIllIlIIIllIIIIIIl()";
    }
}

