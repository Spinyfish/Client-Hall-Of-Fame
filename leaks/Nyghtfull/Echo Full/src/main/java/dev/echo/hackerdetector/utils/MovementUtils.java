package dev.echo.hackerdetector.utils;

import net.minecraft.entity.player.EntityPlayer;

public class MovementUtils {

    public static boolean isMoving(EntityPlayer player) {
        return player.moveForward != 0F || player.moveStrafing != 0F;
    }

    public static boolean isFalseFlaggable(EntityPlayer player) {
        return player.isInWater() || player.isInLava() || player.isOnLadder() || player.ticksExisted < 10;
    }

}
