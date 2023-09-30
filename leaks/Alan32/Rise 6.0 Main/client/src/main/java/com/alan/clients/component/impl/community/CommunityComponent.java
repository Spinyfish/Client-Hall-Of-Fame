package com.alan.clients.component.impl.community;


import com.alan.clients.component.Component;
import com.alan.clients.newevent.Listener;
import com.alan.clients.newevent.annotations.EventLink;
import com.alan.clients.newevent.impl.other.BackendPacketEvent;
import community.Message;
import packet.Packet;
import packet.impl.server.community.ServerCommunityMessageSend;
import packet.impl.server.community.ServerCommunityPopulatePacket;
import util.type.EvictingList;

public class CommunityComponent extends Component {
    public static EvictingList<Message> messages = new EvictingList<>(200);

    @EventLink()
    public final Listener<BackendPacketEvent> onBackend = event -> {
        Packet packet = event.getPacket();

        if (packet instanceof ServerCommunityPopulatePacket) {
            ServerCommunityPopulatePacket serverCommunityPopulatePacket = ((ServerCommunityPopulatePacket) packet);

            messages.clear();
            messages.addAll(serverCommunityPopulatePacket.getMessages());
        } else if (packet instanceof ServerCommunityMessageSend) {
            ServerCommunityMessageSend messageSend = ((ServerCommunityMessageSend) packet);

            messages.add(messageSend.getMessage());
        }
    };
}
