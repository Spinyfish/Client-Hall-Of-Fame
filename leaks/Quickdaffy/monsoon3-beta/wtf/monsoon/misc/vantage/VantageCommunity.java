/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.misc.vantage;

import community.Message;
import io.github.nevalackin.homoBus.Listener;
import io.github.nevalackin.homoBus.annotations.EventLink;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import packet.impl.client.community.ClientCommunityMessageSend;
import packet.impl.server.community.ServerCommunityMessageSend;
import wtf.monsoon.Wrapper;
import wtf.monsoon.impl.event.EventBackendPacket;
import wtf.monsoon.impl.event.EventChatMessageSent;

public class VantageCommunity {
    @EventLink
    public final Listener<EventChatMessageSent> eventChatMessageSentListener = event -> {
        String message = event.getContent();
        if (message.startsWith("-")) {
            this.sendIRCMessage(message.substring(1));
            event.setCancelled(true);
        }
    };
    @EventLink
    private final Listener<EventBackendPacket> eventListener = e -> {
        if (e.getPacket() instanceof ServerCommunityMessageSend) {
            ServerCommunityMessageSend packet = (ServerCommunityMessageSend)e.getPacket();
            Message message = packet.getMessage();
            String chatPrefix = (Object)((Object)EnumChatFormatting.GOLD) + "[IRC] " + (Object)((Object)EnumChatFormatting.AQUA) + message.username + (Object)((Object)EnumChatFormatting.DARK_GRAY) + " >> " + (Object)((Object)EnumChatFormatting.WHITE);
            Wrapper.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(chatPrefix + message.message));
        }
    };

    public void sendIRCMessage(String message) {
        ClientCommunityMessageSend packet = new ClientCommunityMessageSend(message);
        Wrapper.getMonsoon().getNetworkManager().getCommunication().write(packet);
    }
}

