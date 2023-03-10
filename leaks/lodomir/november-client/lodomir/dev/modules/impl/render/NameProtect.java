/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.eventbus.Subscribe
 */
package lodomir.dev.modules.impl.render;

import com.google.common.eventbus.Subscribe;
import lodomir.dev.November;
import lodomir.dev.event.impl.network.EventGetPacket;
import lodomir.dev.modules.Category;
import lodomir.dev.modules.Module;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.network.play.server.S3CPacketUpdateScore;
import net.minecraft.util.ChatComponentText;

public class NameProtect
extends Module {
    private String name;

    public NameProtect() {
        super("NameProtect", 0, Category.RENDER);
        this.name = November.INSTANCE.name;
    }

    @Override
    @Subscribe
    public void onGetPacket(EventGetPacket event) {
        if (event.packet instanceof S02PacketChat) {
            S02PacketChat packet = (S02PacketChat)event.packet;
            if (packet.getChatComponent().getUnformattedText().replaceAll("", "").contains(mc.getSession().getUsername())) {
                packet.chatComponent = new ChatComponentText(packet.getChatComponent().getFormattedText().replaceAll("", "").replaceAll(mc.getSession().getUsername(), this.name));
            }
        } else if (event.packet instanceof S3CPacketUpdateScore) {
            S3CPacketUpdateScore packet2 = (S3CPacketUpdateScore)event.packet;
            if (packet2.getObjectiveName().replaceAll("", "").contains(mc.getSession().getUsername())) {
                packet2.setObjective(packet2.getObjectiveName().replaceAll("", "").replaceAll(mc.getSession().getUsername(), this.name));
            }
            if (packet2.getPlayerName().replaceAll("", "").contains(mc.getSession().getUsername())) {
                packet2.setName(packet2.getPlayerName().replaceAll("", "").replaceAll(mc.getSession().getUsername(), this.name));
            }
        }
        super.onGetPacket(event);
    }
}

