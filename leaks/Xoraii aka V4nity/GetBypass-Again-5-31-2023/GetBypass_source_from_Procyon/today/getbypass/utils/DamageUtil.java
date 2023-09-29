// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.utils;

import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.client.Minecraft;

public class DamageUtil
{
    private static double randomNumber(final double max, final double min) {
        return Math.random() * (max - min) + min;
    }
    
    public static void advancedDamage() {
        final Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.jump();
        final double x = mc.thePlayer.posX;
        final double y = mc.thePlayer.posY;
        final double z = mc.thePlayer.posZ;
        final float minValue = 3.1f;
        for (int i = 0; i < (int)(minValue / (randomNumber(0.089, 0.0849) - 0.001 - Math.random() * 1.9999999494757503E-4 - Math.random() * 1.9999999494757503E-4) + 18.0); ++i) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + randomNumber(0.0655, 0.0625) - randomNumber(0.001, 0.01) - Math.random() * 1.9999999494757503E-4, z, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(x, y + Math.random() * 1.9999999494757503E-4, z, false));
        }
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer(true));
    }
    
    public static void simpleDamage() {
        final Minecraft mc = Minecraft.getMinecraft();
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3.5, mc.thePlayer.posZ, false));
        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
    }
}
