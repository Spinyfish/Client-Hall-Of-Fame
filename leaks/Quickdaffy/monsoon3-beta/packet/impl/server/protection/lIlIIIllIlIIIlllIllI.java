/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.server.protection;

import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;

public final class lIlIIIllIlIIIlllIllI
implements ServerPacket {
    public double l = 0.0;
    public double I = 0.0;
    public float J = 0.0f;
    public double O = 0.0;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }

    public double getL() {
        return this.l;
    }

    public double getI() {
        return this.I;
    }

    public float getJ() {
        return this.J;
    }

    public double getO() {
        return this.O;
    }

    public void setL(double l) {
        this.l = l;
    }

    public void setI(double I) {
        this.I = I;
    }

    public void setJ(float J) {
        this.J = J;
    }

    public void setO(double O) {
        this.O = O;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof lIlIIIllIlIIIlllIllI)) {
            return false;
        }
        lIlIIIllIlIIIlllIllI other = (lIlIIIllIlIIIlllIllI)o;
        if (Double.compare(this.getL(), other.getL()) != 0) {
            return false;
        }
        if (Double.compare(this.getI(), other.getI()) != 0) {
            return false;
        }
        if (Float.compare(this.getJ(), other.getJ()) != 0) {
            return false;
        }
        return Double.compare(this.getO(), other.getO()) == 0;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        long $l = Double.doubleToLongBits(this.getL());
        result = result * 59 + (int)($l >>> 32 ^ $l);
        long $I = Double.doubleToLongBits(this.getI());
        result = result * 59 + (int)($I >>> 32 ^ $I);
        result = result * 59 + Float.floatToIntBits(this.getJ());
        long $O = Double.doubleToLongBits(this.getO());
        result = result * 59 + (int)($O >>> 32 ^ $O);
        return result;
    }

    public String toString() {
        return "lIlIIIllIlIIIlllIllI(l=" + this.getL() + ", I=" + this.getI() + ", J=" + this.getJ() + ", O=" + this.getO() + ")";
    }

    public lIlIIIllIlIIIlllIllI(double l, double I, float J, double O) {
        this.l = l;
        this.I = I;
        this.J = J;
        this.O = O;
    }
}

