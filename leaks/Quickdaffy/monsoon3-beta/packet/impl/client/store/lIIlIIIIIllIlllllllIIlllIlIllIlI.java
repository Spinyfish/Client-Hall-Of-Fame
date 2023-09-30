/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.client.store;

import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;
import store.File;

public final class lIIlIIIIIllIlllllllIIlllIlIllIlI
implements ClientPacket {
    public File file;

    @Override
    public void process(IClientPacketHandler handler) {
        handler.handle(this);
    }

    public lIIlIIIIIllIlllllllIIlllIlIllIlI(File file) {
        this.file = file;
    }

    public File getFile() {
        return this.file;
    }
}

