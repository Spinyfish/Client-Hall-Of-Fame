/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.client.protection;

import java.util.List;
import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;
import util.simpleobjects.lllIIllIlIIlIlllIllIlIIIIIlIlIlI;

public final class lllIIllIlIIlIlllIllIlIIIIIlIlIIl
implements ClientPacket {
    final List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> lIlllIIllIIIlIIIIllIIIlIlI;
    final int lllIIllIlIlIlIlIlllIlIIIIIlIlIIl;
    final boolean llllIlllIlIIIIllIlIlIlIIIIlIlIIl;
    final boolean llllIlIlIlIIlllIlIIIIllIIIlIlIIl;
    final boolean lIIIllIIIlIlIIlllIlIlIlIIlllIlIl;
    final boolean lIIlllIlIlIlIIllIIIlIlIIIlllIlIl;

    @Override
    public void process(IClientPacketHandler handler) {
        handler.handle(this);
    }

    public lllIIllIlIIlIlllIllIlIIIIIlIlIIl(List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> lIlllIIllIIIlIIIIllIIIlIlI, int lllIIllIlIlIlIlIlllIlIIIIIlIlIIl, boolean llllIlllIlIIIIllIlIlIlIIIIlIlIIl, boolean llllIlIlIlIIlllIlIIIIllIIIlIlIIl, boolean lIIIllIIIlIlIIlllIlIlIlIIlllIlIl, boolean lIIlllIlIlIlIIllIIIlIlIIIlllIlIl) {
        this.lIlllIIllIIIlIIIIllIIIlIlI = lIlllIIllIIIlIIIIllIIIlIlI;
        this.lllIIllIlIlIlIlIlllIlIIIIIlIlIIl = lllIIllIlIlIlIlIlllIlIIIIIlIlIIl;
        this.llllIlllIlIIIIllIlIlIlIIIIlIlIIl = llllIlllIlIIIIllIlIlIlIIIIlIlIIl;
        this.llllIlIlIlIIlllIlIIIIllIIIlIlIIl = llllIlIlIlIIlllIlIIIIllIIIlIlIIl;
        this.lIIIllIIIlIlIIlllIlIlIlIIlllIlIl = lIIIllIIIlIlIIlllIlIlIlIIlllIlIl;
        this.lIIlllIlIlIlIIllIIIlIlIIIlllIlIl = lIIlllIlIlIlIIllIIIlIlIIIlllIlIl;
    }

    public List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> getLIlllIIllIIIlIIIIllIIIlIlI() {
        return this.lIlllIIllIIIlIIIIllIIIlIlI;
    }

    public int getLllIIllIlIlIlIlIlllIlIIIIIlIlIIl() {
        return this.lllIIllIlIlIlIlIlllIlIIIIIlIlIIl;
    }

    public boolean isLlllIlllIlIIIIllIlIlIlIIIIlIlIIl() {
        return this.llllIlllIlIIIIllIlIlIlIIIIlIlIIl;
    }

    public boolean isLlllIlIlIlIIlllIlIIIIllIIIlIlIIl() {
        return this.llllIlIlIlIIlllIlIIIIllIIIlIlIIl;
    }

    public boolean isLIIIllIIIlIlIIlllIlIlIlIIlllIlIl() {
        return this.lIIIllIIIlIlIIlllIlIlIlIIlllIlIl;
    }

    public boolean isLIIlllIlIlIlIIllIIIlIlIIIlllIlIl() {
        return this.lIIlllIlIlIlIIllIIIlIlIIIlllIlIl;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof lllIIllIlIIlIlllIllIlIIIIIlIlIIl)) {
            return false;
        }
        lllIIllIlIIlIlllIllIlIIIIIlIlIIl other = (lllIIllIlIIlIlllIllIlIIIIIlIlIIl)o;
        if (this.getLllIIllIlIlIlIlIlllIlIIIIIlIlIIl() != other.getLllIIllIlIlIlIlIlllIlIIIIIlIlIIl()) {
            return false;
        }
        if (this.isLlllIlllIlIIIIllIlIlIlIIIIlIlIIl() != other.isLlllIlllIlIIIIllIlIlIlIIIIlIlIIl()) {
            return false;
        }
        if (this.isLlllIlIlIlIIlllIlIIIIllIIIlIlIIl() != other.isLlllIlIlIlIIlllIlIIIIllIIIlIlIIl()) {
            return false;
        }
        if (this.isLIIIllIIIlIlIIlllIlIlIlIIlllIlIl() != other.isLIIIllIIIlIlIIlllIlIlIlIIlllIlIl()) {
            return false;
        }
        if (this.isLIIlllIlIlIlIIllIIIlIlIIIlllIlIl() != other.isLIIlllIlIlIlIIllIIIlIlIIIlllIlIl()) {
            return false;
        }
        List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> this$lIlllIIllIIIlIIIIllIIIlIlI = this.getLIlllIIllIIIlIIIIllIIIlIlI();
        List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> other$lIlllIIllIIIlIIIIllIIIlIlI = other.getLIlllIIllIIIlIIIIllIIIlIlI();
        return !(this$lIlllIIllIIIlIIIIllIIIlIlI == null ? other$lIlllIIllIIIlIIIIllIIIlIlI != null : !((Object)this$lIlllIIllIIIlIIIIllIIIlIlI).equals(other$lIlllIIllIIIlIIIIllIIIlIlI));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getLllIIllIlIlIlIlIlllIlIIIIIlIlIIl();
        result = result * 59 + (this.isLlllIlllIlIIIIllIlIlIlIIIIlIlIIl() ? 79 : 97);
        result = result * 59 + (this.isLlllIlIlIlIIlllIlIIIIllIIIlIlIIl() ? 79 : 97);
        result = result * 59 + (this.isLIIIllIIIlIlIIlllIlIlIlIIlllIlIl() ? 79 : 97);
        result = result * 59 + (this.isLIIlllIlIlIlIIllIIIlIlIIIlllIlIl() ? 79 : 97);
        List<lllIIllIlIIlIlllIllIlIIIIIlIlIlI> $lIlllIIllIIIlIIIIllIIIlIlI = this.getLIlllIIllIIIlIIIIllIIIlIlI();
        result = result * 59 + ($lIlllIIllIIIlIIIIllIIIlIlI == null ? 43 : ((Object)$lIlllIIllIIIlIIIIllIIIlIlI).hashCode());
        return result;
    }

    public String toString() {
        return "lllIIllIlIIlIlllIllIlIIIIIlIlIIl(lIlllIIllIIIlIIIIllIIIlIlI=" + this.getLIlllIIllIIIlIIIIllIIIlIlI() + ", lllIIllIlIlIlIlIlllIlIIIIIlIlIIl=" + this.getLllIIllIlIlIlIlIlllIlIIIIIlIlIIl() + ", llllIlllIlIIIIllIlIlIlIIIIlIlIIl=" + this.isLlllIlllIlIIIIllIlIlIlIIIIlIlIIl() + ", llllIlIlIlIIlllIlIIIIllIIIlIlIIl=" + this.isLlllIlIlIlIIlllIlIIIIllIIIlIlIIl() + ", lIIIllIIIlIlIIlllIlIlIlIIlllIlIl=" + this.isLIIIllIIIlIlIIlllIlIlIlIIlllIlIl() + ", lIIlllIlIlIlIIllIIIlIlIIIlllIlIl=" + this.isLIIlllIlIlIlIIllIIIlIlIIIlllIlIl() + ")";
    }
}

