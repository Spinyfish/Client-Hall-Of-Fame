/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import viamcp.ViaMCP;
import wtf.monsoon.api.util.Util;

public class PacketUtil
extends Util {
    public static void sendPacket(Packet<?> p) {
        mc.getNetHandler().addToSendQueue(p);
    }

    public static void sendPacketNoEvent(Packet<?> p) {
        mc.getNetHandler().addToSendQueueNoEvent(p);
    }

    public static void sendFunnyPacket() {
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(Minecraft.getMinecraft().thePlayer.posX, 3.141592653589793E8, Minecraft.getMinecraft().thePlayer.posZ, false));
    }

    public static float fixRightClick() {
        if (ViaMCP.getInstance().getVersion() == 47) {
            return 16.0f;
        }
        return 1.0f;
    }
}

