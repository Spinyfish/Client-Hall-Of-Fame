/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.network.play.server.S02PacketChat
 *  vip.astroline.client.service.event.impl.move.EventPreUpdate
 *  vip.astroline.client.service.event.impl.packet.EventReceivePacket
 *  vip.astroline.client.service.event.types.EventTarget
 *  vip.astroline.client.service.module.Category
 *  vip.astroline.client.service.module.Module
 *  vip.astroline.client.service.module.impl.player.CheckThread
 *  vip.astroline.client.service.module.value.BooleanValue
 *  vip.astroline.client.service.module.value.FloatValue
 *  vip.astroline.client.service.module.value.ModeValue
 *  vip.astroline.client.storage.utils.other.TimeHelper
 */
package vip.astroline.client.service.module.impl.player;

import net.minecraft.network.play.server.S02PacketChat;
import vip.astroline.client.service.event.impl.move.EventPreUpdate;
import vip.astroline.client.service.event.impl.packet.EventReceivePacket;
import vip.astroline.client.service.event.types.EventTarget;
import vip.astroline.client.service.module.Category;
import vip.astroline.client.service.module.Module;
import vip.astroline.client.service.module.impl.player.CheckThread;
import vip.astroline.client.service.module.value.BooleanValue;
import vip.astroline.client.service.module.value.FloatValue;
import vip.astroline.client.service.module.value.ModeValue;
import vip.astroline.client.storage.utils.other.TimeHelper;

public class StaffAnalyser
extends Module {
    public static ModeValue notificationType = new ModeValue("StaffAnalyser", "Notification Type", "Notification", new String[]{"Chat Message"});
    public static BooleanValue hideNoBan = new BooleanValue("StaffAnalyser", "Hide 0 Ban", Boolean.valueOf(false));
    public static FloatValue delay = new FloatValue("StaffAnalyser", "Check Delay", 60.0f, 10.0f, 300.0f, 1.0f);
    public static String key = null;
    TimeHelper sendNewApiTimer = new TimeHelper();
    CheckThread thread = new CheckThread();
    public static int lastBanned = 0;

    public StaffAnalyser() {
        super("StaffAnalyser", Category.Player, 0, false);
        this.thread.start();
    }

    @EventTarget
    public void onPre(EventPreUpdate e) {
        if (!this.sendNewApiTimer.isDelayComplete(3000.0)) return;
        if (key != null) return;
        StaffAnalyser.mc.thePlayer.sendChatMessage("/api new");
        this.sendNewApiTimer.reset();
    }

    @EventTarget
    public void onPacket(EventReceivePacket e) {
        if (!(e.getPacket() instanceof S02PacketChat)) return;
        S02PacketChat chatPacket = (S02PacketChat)e.getPacket();
        String chatMessage = chatPacket.getChatComponent().getUnformattedText();
        if (!chatMessage.matches("Your new API key is ........-....-....-....-............")) return;
        e.setCancelled(true);
        key = chatMessage.replace("Your new API key is ", "");
    }
}
