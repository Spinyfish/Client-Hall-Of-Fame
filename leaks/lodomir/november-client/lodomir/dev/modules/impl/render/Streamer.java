/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.event.impl.EventGetPackets;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.util.ChatComponentText;

public class Streamer
extends Module {
    private String name = "You";

    public Streamer() {
        super("Streamer", 0, Category.RENDER);
    }

    @Subscribe
    public void onGetPackets(EventGetPackets event) {
        if (event.packets instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat)event.packets;
            if (packet.getChatComponent().getUnformattedText().replaceAll("", "").contains(mc.getSession().getUsername())) {
                packet.chatComponent = new ChatComponentText(packet.getChatComponent().getFormattedText().replaceAll("", "").replaceAll(mc.getSession().getUsername(), this.name));
            }
        } else if (event.packets instanceof S3CPacketUpdateScore) {
            S3CPacketUpdateScore packet2 = (S3CPacketUpdateScore)event.packets;
            if (packet2.getObjectiveName().replaceAll("", "").contains(mc.getSession().getUsername())) {
                packet2.setObjective(packet2.getObjectiveName().replaceAll("", "").replaceAll(mc.getSession().getUsername(), this.name));
            }
            if (packet2.getPlayerName().replaceAll("", "").contains(mc.getSession().getUsername())) {
                packet2.setName(packet2.getPlayerName().replaceAll("", "").replaceAll(mc.getSession().getUsername(), this.name));
            }
        }
        super.onGetPackets(event);
    }
}

