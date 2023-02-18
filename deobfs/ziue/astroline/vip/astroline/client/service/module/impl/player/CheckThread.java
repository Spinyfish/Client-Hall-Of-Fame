/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.minecraft.client.Minecraft
 *  net.minecraft.util.ChatComponentText
 *  net.minecraft.util.EnumChatFormatting
 *  net.minecraft.util.IChatComponent
 *  vip.astroline.client.Astroline
 *  vip.astroline.client.service.module.impl.player.BanQuantityListJSON
 *  vip.astroline.client.service.module.impl.player.StaffAnalyser
 *  vip.astroline.client.storage.utils.other.HttpUtil
 */
package vip.astroline.client.service.module.impl.player;

import com.google.gson.Gson;
import java.net.URL;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import vip.astroline.client.Astroline;
import vip.astroline.client.service.module.impl.player.BanQuantityListJSON;
import vip.astroline.client.service.module.impl.player.StaffAnalyser;
import vip.astroline.client.storage.utils.other.HttpUtil;

class CheckThread
extends Thread {
    int lastBannedCount = 0;

    CheckThread() {
    }

    @Override
    public void run() {
        while (true) {
            try {
                while (true) {
                    if (StaffAnalyser.key == null) {
                        CheckThread.sleep(1000L);
                        continue;
                    }
                    CheckThread.sleep((long)StaffAnalyser.delay.getValue().intValue() * 1000L);
                    String result = HttpUtil.performGetRequest((URL)new URL("https://api.hypixel.net/watchdogStats?key=" + StaffAnalyser.key));
                    Gson gson = new Gson();
                    BanQuantityListJSON banQuantityListJSON = gson.fromJson(result, BanQuantityListJSON.class);
                    int staffTotal = banQuantityListJSON.getStaffTotal();
                    if (this.lastBannedCount == 0) {
                        this.lastBannedCount = staffTotal;
                        continue;
                    }
                    int banned = staffTotal - this.lastBannedCount;
                    this.lastBannedCount = staffTotal;
                    if (banned > 1) {
                        if (!StaffAnalyser.notificationType.isCurrentMode("Notification")) {
                            CheckThread.tellPlayer("\u00a7cStaff banned " + banned + " players in " + StaffAnalyser.delay.getValue().intValue() + "s.");
                        }
                    } else if (!StaffAnalyser.hideNoBan.getValue().booleanValue()) {
                        if (StaffAnalyser.notificationType.isCurrentMode("Notification")) {
                            // empty if block
                        }
                    } else {
                        CheckThread.tellPlayer("\u00a7aStaff didn't ban any player in " + StaffAnalyser.delay.getValue().intValue() + "s.");
                    }
                    StaffAnalyser.lastBanned = banned;
                }
            }
            catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            break;
        }
    }

    public static void tellPlayer(String string) {
        if (string == null) return;
        if (Minecraft.getMinecraft().thePlayer == null) return;
        Minecraft.getMinecraft().thePlayer.addChatMessage((IChatComponent)new ChatComponentText(EnumChatFormatting.GRAY + "[" + EnumChatFormatting.WHITE + Astroline.INSTANCE.CLIENT + EnumChatFormatting.GRAY + "]: " + EnumChatFormatting.GRAY + string));
    }
}
