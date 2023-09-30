/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.server.protection;

import java.util.List;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;
import util.simpleobjects.lllIIllIlIIlIlllIllIlIIIIIlIlIlI;

public final class lIllIIlllIIIIlIllIIIIllIlllllIll
implements ServerPacket {
    public List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> targets;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }

    public List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> getTargets() {
        return this.targets;
    }

    public void setTargets(List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> targets) {
        this.targets = targets;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof lIllIIlllIIIIlIllIIIIllIlllllIll)) {
            return false;
        }
        lIllIIlllIIIIlIllIIIIllIlllllIll other = (lIllIIlllIIIIlIllIIIIllIlllllIll)o;
        List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> this$targets = this.getTargets();
        List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> other$targets = other.getTargets();
        return !(this$targets == null ? other$targets != null : !((Object)this$targets).equals(other$targets));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> $targets = this.getTargets();
        result = result * 59 + ($targets == null ? 43 : ((Object)$targets).hashCode());
        return result;
    }

    public String toString() {
        return "lIllIIlllIIIIlIllIIIIllIlllllIll(targets=" + this.getTargets() + ")";
    }

    public lIllIIlllIIIIlIllIIIIllIlllllIll(List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> targets) {
        this.targets = targets;
    }
}

