/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.client.community;

import packet.handler.impl.IClientPacketHandler;
import packet.type.ClientPacket;

public final class ClientCommunityMessageSend
implements ClientPacket {
    public String message;

    @Override
    public void process(IClientPacketHandler handler) {
        handler.handle(this);
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ClientCommunityMessageSend)) {
            return false;
        }
        ClientCommunityMessageSend other = (ClientCommunityMessageSend)o;
        String this$message = this.getMessage();
        String other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        String $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "ClientCommunityMessageSend(message=" + this.getMessage() + ")";
    }

    public ClientCommunityMessageSend(String message) {
        this.message = message;
    }
}

