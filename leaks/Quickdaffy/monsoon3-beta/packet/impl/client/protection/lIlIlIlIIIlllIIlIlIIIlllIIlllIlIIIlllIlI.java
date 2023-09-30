/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.client.protection;

import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

public final class lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI
implements ClientPacket {
    private String lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI;
    private int lIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI;

    @Override
    public void process(IClientPacketHandler handler) {
        handler.handle(this);
    }

    public String getLIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI() {
        return this.lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI;
    }

    public int getLIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI() {
        return this.lIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI;
    }

    public void setLIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI(String lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI) {
        this.lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI = lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI;
    }

    public void setLIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI(int lIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI) {
        this.lIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI = lIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI)) {
            return false;
        }
        lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI other = (lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI)o;
        if (this.getLIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI() != other.getLIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI()) {
            return false;
        }
        String this$lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI = this.getLIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI();
        String other$lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI = other.getLIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI();
        return !(this$lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI == null ? other$lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI != null : !this$lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI.equals(other$lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getLIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI();
        String $lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI = this.getLIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI();
        result = result * 59 + ($lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI == null ? 43 : $lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI.hashCode());
        return result;
    }

    public String toString() {
        return "lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI(lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI=" + this.getLIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI() + ", lIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI=" + this.getLIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI() + ")";
    }

    public lIlIlIlIIIlllIIlIlIIIlllIIlllIlIIIlllIlI(String lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI, int lIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI) {
        this.lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI = lIlIllIlIIIlllIIlIIIlllIIIlllIlIIIlllIlI;
        this.lIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI = lIllIlIlIlIIIlllIIIlllIIIIllIlIIIlllIllI;
    }
}

