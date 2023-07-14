/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.server.store;

import java.util.ArrayList;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;
import store.File;

public final class IllIIIllllIlIlIIIllIlIllllIIllll
implements ServerPacket {
    private final ArrayList<File> files;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }

    public IllIIIllllIlIlIIIllIlIllllIIllll(ArrayList<File> files) {
        this.files = files;
    }

    public ArrayList<File> getFiles() {
        return this.files;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof IllIIIllllIlIlIIIllIlIllllIIllll)) {
            return false;
        }
        IllIIIllllIlIlIIIllIlIllllIIllll other = (IllIIIllllIlIlIIIllIlIllllIIllll)o;
        ArrayList<File> this$files = this.getFiles();
        ArrayList<File> other$files = other.getFiles();
        return !(this$files == null ? other$files != null : !((Object)this$files).equals(other$files));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        ArrayList<File> $files = this.getFiles();
        result = result * 59 + ($files == null ? 43 : ((Object)$files).hashCode());
        return result;
    }

    public String toString() {
        return "IllIIIllllIlIlIIIllIlIllllIIllll(files=" + this.getFiles() + ")";
    }
}

