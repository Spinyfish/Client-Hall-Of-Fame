/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.entity;

import net.minecraft.block.BlockAir;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.EnumChatFormatting;
import wtf.monsoon.Monsoon;
import wtf.monsoon.Wrapper;
import wtf.monsoon.api.util.Util;
import wtf.monsoon.api.util.misc.PacketUtil;

public class PlayerUtil
extends Util {
    public static final String chatPrefix = (Object)((Object)EnumChatFormatting.WHITE) + "<" + (Object)((Object)EnumChatFormatting.AQUA) + "monsoon" + (Object)((Object)EnumChatFormatting.WHITE) + "> " + (Object)((Object)EnumChatFormatting.RESET);
    public static final String debugPrefix = (Object)((Object)EnumChatFormatting.WHITE) + "<" + (Object)((Object)EnumChatFormatting.YELLOW) + "debug" + (Object)((Object)EnumChatFormatting.WHITE) + "> " + (Object)((Object)EnumChatFormatting.RESET);

    public static void sendClientMessage(String message) {
        Wrapper.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(chatPrefix + message));
    }

    public static void debug(String message) {
        if (Wrapper.isDebugModeEnabled() && Monsoon.DEBUG_MODE) {
            Wrapper.getMinecraft().thePlayer.addChatMessage(new ChatComponentText(debugPrefix + message));
        }
    }

    public static boolean isBlockAbovePlayer() {
        return PlayerUtil.mc.theWorld.getBlockState(PlayerUtil.mc.thePlayer.getPosition().add(0, 2, 0)).getBlock() != null && PlayerUtil.mc.theWorld.getBlockState(PlayerUtil.mc.thePlayer.getPosition().add(0, 2, 0)).getBlock().isCollidable() && !(PlayerUtil.mc.theWorld.getBlockState(PlayerUtil.mc.thePlayer.getPosition().add(0, 2, 0)).getBlock() instanceof BlockAir);
    }

    public static void oldNCPDamage() {
        for (int i = 0; i < 50; ++i) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY + 0.0625, PlayerUtil.mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY, PlayerUtil.mc.thePlayer.posZ, false));
        }
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY, PlayerUtil.mc.thePlayer.posZ, true));
    }

    public static void damageVerus() {
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY + 3.1001, PlayerUtil.mc.thePlayer.posZ, false));
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY, PlayerUtil.mc.thePlayer.posZ, false));
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY, PlayerUtil.mc.thePlayer.posZ, true));
        PlayerUtil.mc.thePlayer.jump();
    }

    public static void damageHypixel() {
        for (int i = 0; i < 50; ++i) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY + 0.0625, PlayerUtil.mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY, PlayerUtil.mc.thePlayer.posZ, false));
        }
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY, PlayerUtil.mc.thePlayer.posZ, true));
    }

    public static void damageSpartan() {
        for (int i = 0; i < 64; ++i) {
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY + 0.049, PlayerUtil.mc.thePlayer.posZ, false));
            PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY, PlayerUtil.mc.thePlayer.posZ, false));
        }
        PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(PlayerUtil.mc.thePlayer.posX, PlayerUtil.mc.thePlayer.posY + 0.1, PlayerUtil.mc.thePlayer.posZ, true));
        PacketUtil.sendPacketNoEvent(new C0APacketAnimation());
    }

    public static void damageCustomAmount(double damage) {
        Minecraft mc = Minecraft.getMinecraft();
        if (damage > (double)PlayerUtil.floor_double(mc.thePlayer.getMaxHealth())) {
            damage = PlayerUtil.floor_double(mc.thePlayer.getMaxHealth());
        }
        double offset = 0.0625;
        if (mc.thePlayer != null) {
            int i = 0;
            while ((double)i <= (3.0 + damage) / offset) {
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset / 2.0 * 1.0, mc.thePlayer.posZ, false));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + offset / 2.0 * 2.0, mc.thePlayer.posZ, false));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, (double)i == (3.0 + damage) / offset));
                PacketUtil.sendPacketNoEvent(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, (double)i == (3.0 + damage) / offset));
                i = (short)(i + 1);
            }
        }
    }

    public static int floor_double(double p_76128_0_) {
        int var2 = (int)p_76128_0_;
        return p_76128_0_ < (double)var2 ? var2 - 1 : var2;
    }

    public static double getPlayerSpeed() {
        return PlayerUtil.mc.thePlayer.getDistance(PlayerUtil.mc.thePlayer.lastTickPosX, PlayerUtil.mc.thePlayer.posY, PlayerUtil.mc.thePlayer.lastTickPosZ) * (double)(Minecraft.getMinecraft().getTimer().ticksPerSecond * Minecraft.getMinecraft().getTimer().timerSpeed);
    }
}

