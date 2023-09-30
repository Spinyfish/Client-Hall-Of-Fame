/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.server.login;

import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;

public final class lIIlllIIIllIIIIIllIIIllllIllIllI
implements ServerPacket {
    private final boolean success;
    private final String information;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }

    public lIIlllIIIllIIIIIllIIIllllIllIllI(boolean success, String information) {
        this.success = success;
        this.information = information;
    }

    public boolean isSuccess() {
        return this.success;
    }

    public String getInformation() {
        return this.information;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof lIIlllIIIllIIIIIllIIIllllIllIllI)) {
            return false;
        }
        lIIlllIIIllIIIIIllIIIllllIllIllI other = (lIIlllIIIllIIIIIllIIIllllIllIllI)o;
        if (this.isSuccess() != other.isSuccess()) {
            return false;
        }
        String this$information = this.getInformation();
        String other$information = other.getInformation();
        return !(this$information == null ? other$information != null : !this$information.equals(other$information));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isSuccess() ? 79 : 97);
        String $information = this.getInformation();
        result = result * 59 + ($information == null ? 43 : $information.hashCode());
        return result;
    }

    public String toString() {
        return "lIIlllIIIllIIIIIllIIIllllIllIllI(success=" + this.isSuccess() + ", information=" + this.getInformation() + ")";
    }
}

