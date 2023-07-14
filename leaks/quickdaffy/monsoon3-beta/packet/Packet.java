/*
 * Decompiled with CFR 0.152.
 */
package packet;

import java.io.Serializable;
import packet.handler.PacketHandler;

public interface Packet<T extends PacketHandler>
extends Serializable {
    public void process(T var1);
}

