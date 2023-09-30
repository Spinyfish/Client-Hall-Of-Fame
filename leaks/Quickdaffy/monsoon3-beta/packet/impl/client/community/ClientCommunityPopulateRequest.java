/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.client.community;

import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

public final class ClientCommunityPopulateRequest
implements ClientPacket {
    @Override
    public void process(IClientPacketHandler handler) {
        handler.handle(this);
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        return o instanceof ClientCommunityPopulateRequest;
    }

    public int hashCode() {
        boolean result = true;
        return 1;
    }

    public String toString() {
        return "ClientCommunityPopulateRequest()";
    }
}

