/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.server.community;

import community.Message;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;

public final class ServerCommunityMessageSend
implements ServerPacket {
    public Message message;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }

    public Message getMessage() {
        return this.message;
    }

    public void setMessage(Message message) {
        this.message = message;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServerCommunityMessageSend)) {
            return false;
        }
        ServerCommunityMessageSend other = (ServerCommunityMessageSend)o;
        Message this$message = this.getMessage();
        Message other$message = other.getMessage();
        return !(this$message == null ? other$message != null : !this$message.equals(other$message));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        Message $message = this.getMessage();
        result = result * 59 + ($message == null ? 43 : $message.hashCode());
        return result;
    }

    public String toString() {
        return "ServerCommunityMessageSend(message=" + this.getMessage() + ")";
    }

    public ServerCommunityMessageSend(Message message) {
        this.message = message;
    }
}

