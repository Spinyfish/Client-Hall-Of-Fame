/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.server.protection;

import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;

public final class lIllIlIlIlIIIlllIIIlllIIIIlllI
implements ServerPacket {
    private String lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI;
    private int lIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }

    public String getLIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI() {
        return this.lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI;
    }

    public int getLIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI() {
        return this.lIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI;
    }

    public void setLIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI(String lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI) {
        this.lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI = lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI;
    }

    public void setLIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI(int lIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI) {
        this.lIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI = lIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof lIllIlIlIlIIIlllIIIlllIIIIlllI)) {
            return false;
        }
        lIllIlIlIlIIIlllIIIlllIIIIlllI other = (lIllIlIlIlIIIlllIIIlllIIIIlllI)o;
        if (this.getLIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI() != other.getLIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI()) {
            return false;
        }
        String this$lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI = this.getLIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI();
        String other$lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI = other.getLIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI();
        return !(this$lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI == null ? other$lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI != null : !this$lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI.equals(other$lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getLIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI();
        String $lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI = this.getLIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI();
        result = result * 59 + ($lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI == null ? 43 : $lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI.hashCode());
        return result;
    }

    public String toString() {
        return "lIllIlIlIlIIIlllIIIlllIIIIlllI(lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI=" + this.getLIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI() + ", lIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI=" + this.getLIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI() + ")";
    }

    public lIllIlIlIlIIIlllIIIlllIIIIlllI(String lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI, int lIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI) {
        this.lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI = lIlIlIIIlllIlIIIlllIlIlIIIllIlIIIlllIllI;
        this.lIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI = lIlIlIIIlllIllIlIIIlllIIIIlIlIIIlllIlllI;
    }
}

