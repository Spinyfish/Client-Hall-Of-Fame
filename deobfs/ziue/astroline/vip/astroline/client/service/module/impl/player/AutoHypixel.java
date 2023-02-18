/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.S45PacketTitle
 *  vip.astroline.client.service.event.impl.packet.EventReceivePacket
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 */
package vip.astroline.client.service.module.impl.player;

import net.minecraft.network.play.server.S45PacketTitle;
import vip.astroline.client.service.event.impl.packet.EventReceivePacket;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;

public class AutoHypixel
extends Module {
    public static int wins = 0;

    public AutoHypixel() {
        super("AutoHypixel", Category.Player, 0, false);
    }

    @EventTarget
    public void onPacket(EventReceivePacket event) {
        if (!(event.getPacket() instanceof S45PacketTitle)) return;
        S45PacketTitle packet = (S45PacketTitle)event.getPacket();
        if (packet.getMessage() == null) return;
        String message = packet.getMessage().getFormattedText().toLowerCase();
        if (message.contains("you died") || message.contains("game over")) {
            AutoHypixel.mc.thePlayer.sendChatMessage("/play solo_insane");
        } else {
            if (!message.contains("you win")) {
                if (!message.contains("victory")) return;
            }
            AutoHypixel.mc.thePlayer.sendChatMessage("/play solo_insane");
            ++wins;
        }
    }

    public void onEnable() {
        super.onEnable();
    }

    public void onDisable() {
        super.onDisable();
    }
}
