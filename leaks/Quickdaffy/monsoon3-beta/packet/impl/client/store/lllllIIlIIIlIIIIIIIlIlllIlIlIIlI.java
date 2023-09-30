/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.client.store;

import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

public final class lllllIIlIIIlIIIIIIIlIlllIlIlIIlI
implements ClientPacket {
    @Override
    public void process(IClientPacketHandler handler) {
        handler.handle(this);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return o instanceof lllllIIlIIIlIIIIIIIlIlllIlIlIIlI;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }

    public String toString() {
        return "lllllIIlIIIlIIIIIIIlIlllIlIlIIlI()";
    }
}

