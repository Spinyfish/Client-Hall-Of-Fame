/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.client.login;

import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

public final class lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl
implements ClientPacket {
    private final String email;
    private final String password;
    private final String hostName;
    private final String systemName;
    private final String osName;
    private final String hardwareID;
    private final String clientID;

    @Override
    public void process(IClientPacketHandler handler) {
        handler.handle(this);
    }

    public lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl(String email, String password, String hostName, String systemName, String osName, String hardwareID, String clientID) {
        this.email = email;
        this.password = password;
        this.hostName = hostName;
        this.systemName = systemName;
        this.osName = osName;
        this.hardwareID = hardwareID;
        this.clientID = clientID;
    }

    public String getEmail() {
        return this.email;
    }

    public String getPassword() {
        return this.password;
    }

    public String getHostName() {
        return this.hostName;
    }

    public String getSystemName() {
        return this.systemName;
    }

    public String getOsName() {
        return this.osName;
    }

    public String getHardwareID() {
        return this.hardwareID;
    }

    public String getClientID() {
        return this.clientID;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl)) {
            return false;
        }
        lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl other = (lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl)o;
        String this$email = this.getEmail();
        String other$email = other.getEmail();
        if (this$email == null ? other$email != null : !this$email.equals(other$email)) {
            return false;
        }
        String this$password = this.getPassword();
        String other$password = other.getPassword();
        if (this$password == null ? other$password != null : !this$password.equals(other$password)) {
            return false;
        }
        String this$hostName = this.getHostName();
        String other$hostName = other.getHostName();
        if (this$hostName == null ? other$hostName != null : !this$hostName.equals(other$hostName)) {
            return false;
        }
        String this$systemName = this.getSystemName();
        String other$systemName = other.getSystemName();
        if (this$systemName == null ? other$systemName != null : !this$systemName.equals(other$systemName)) {
            return false;
        }
        String this$osName = this.getOsName();
        String other$osName = other.getOsName();
        if (this$osName == null ? other$osName != null : !this$osName.equals(other$osName)) {
            return false;
        }
        String this$hardwareID = this.getHardwareID();
        String other$hardwareID = other.getHardwareID();
        if (this$hardwareID == null ? other$hardwareID != null : !this$hardwareID.equals(other$hardwareID)) {
            return false;
        }
        String this$clientID = this.getClientID();
        String other$clientID = other.getClientID();
        return !(this$clientID == null ? other$clientID != null : !this$clientID.equals(other$clientID));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $email = this.getEmail();
        result = result * 59 + ($email == null ? 43 : $email.hashCode());
        String $password = this.getPassword();
        result = result * 59 + ($password == null ? 43 : $password.hashCode());
        String $hostName = this.getHostName();
        result = result * 59 + ($hostName == null ? 43 : $hostName.hashCode());
        String $systemName = this.getSystemName();
        result = result * 59 + ($systemName == null ? 43 : $systemName.hashCode());
        String $osName = this.getOsName();
        result = result * 59 + ($osName == null ? 43 : $osName.hashCode());
        String $hardwareID = this.getHardwareID();
        result = result * 59 + ($hardwareID == null ? 43 : $hardwareID.hashCode());
        String $clientID = this.getClientID();
        result = result * 59 + ($clientID == null ? 43 : $clientID.hashCode());
        return result;
    }

    public String toString() {
        return "lIIIIIllIIIIlIIIlIIllIlIIlIlIIIl(email=" + this.getEmail() + ", password=" + this.getPassword() + ", hostName=" + this.getHostName() + ", systemName=" + this.getSystemName() + ", osName=" + this.getOsName() + ", hardwareID=" + this.getHardwareID() + ", clientID=" + this.getClientID() + ")";
    }
}

