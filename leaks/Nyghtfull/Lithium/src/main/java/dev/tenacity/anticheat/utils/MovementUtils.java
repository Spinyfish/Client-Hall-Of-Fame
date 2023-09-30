package dev.tenacity.hackerdetector.utils;

import net.minecraft.entity.player.EntityPlayer;

public class MovementUtils {

    public static boolean isMoving(EntityPlayer player) {
        return player.moveForward != 0F || player.moveStrafing != 0F;
    }

}
