/*
 * Decompiled with CFR 0.152.
 */
package packet.impl.server.community;

import community.Message;
import packet.handler.impl.IServerPacketHandler;
import packet.type.ServerPacket;
import util.type.EvictingList;

public final class ServerCommunityPopulatePacket
implements ServerPacket {
    private EvictingList<Message> messages;

    @Override
    public void process(IServerPacketHandler handler) {
        handler.handle(this);
    }

    public ServerCommunityPopulatePacket(EvictingList<Message> messages) {
        this.messages = messages;
    }

    public EvictingList<Message> getMessages() {
        return this.messages;
    }

    public void setMessages(EvictingList<Message> messages) {
        this.messages = messages;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof ServerCommunityPopulatePacket)) {
            return false;
        }
        ServerCommunityPopulatePacket other = (ServerCommunityPopulatePacket)o;
        EvictingList<Message> this$messages = this.getMessages();
        EvictingList<Message> other$messages = other.getMessages();
        return !(this$messages == null ? other$messages != null : !((Object)this$messages).equals(other$messages));
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        EvictingList<Message> $messages = this.getMessages();
        result = result * 59 + ($messages == null ? 43 : ((Object)$messages).hashCode());
        return result;
    }

    public String toString() {
        return "ServerCommunityPopulatePacket(messages=" + this.getMessages() + ")";
    }
}

