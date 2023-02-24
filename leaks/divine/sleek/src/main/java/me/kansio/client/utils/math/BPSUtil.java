package me.kansio.client.utils.math;

import me.kansio.client.utils.Util;
import net.minecraft.entity.player.EntityPlayer;

public class BPSUtil extends Util {

    public static double getBPS() {
        return mc.thePlayer.getDistance(mc.thePlayer.lastTickPosX, mc.thePlayer.lastTickPosY, mc.thePlayer.lastTickPosZ) * (mc.timer.ticksPerSecond * mc.timer.timerSpeed);
    }

    public static double getBPS(EntityPlayer player) {
        return player.getDistance(player.lastTickPosX, player.posY, player.lastTickPosZ) * (mc.timer.ticksPerSecond * mc.timer.timerSpeed);
    }

}
