package de.fanta.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;

public class PlayerUtil {
    static Minecraft mc = Minecraft.getMinecraft();
    public static void setSpeed(double speed) {
        mc.thePlayer.motionX = -(Math.sin(getDirection()) * speed);
        mc.thePlayer.motionZ = Math.cos(getDirection()) * speed;
    }
    public static float getDirection() {
        float RotationYaw = mc.thePlayer.rotationYaw;
        if(mc.thePlayer.moveForward < 0.0F) {
        	RotationYaw += 20.0F;
        }

        float MoveForward = 0.5F;
        if(mc.thePlayer.moveForward < 0.0F) {
        	MoveForward = -0.5F;
        } else if(mc.thePlayer.moveForward > 0.0F) {
        	MoveForward = 0.F;
        }

        if(mc.thePlayer.moveStrafing > 0.0F) {
        	RotationYaw -= 1.0F * MoveForward;
        }

        if(mc.thePlayer.moveStrafing < 0.0F) {
        	RotationYaw += 1.0F * MoveForward;
        }

        RotationYaw *= 0.017F;
        return RotationYaw;
    }

    public static boolean isInTablist(Entity entity) {
        for (NetworkPlayerInfo playerInfo : mc.getNetHandler().getPlayerInfoMap()) {
            if (playerInfo.getGameProfile().getName().equalsIgnoreCase(entity.getName()) || !playerInfo.getGameProfile().getName().contains(entity.getName()))
                return true;
        }
        return false;
    }


    public static double getDistanceToBlock(BlockPos pos) {
        double f = mc.thePlayer.posX - pos.getX();
        double f1 = mc.thePlayer.posY - pos.getY();
        double f2 = mc.thePlayer.posZ - pos.getZ();
        return MathHelper.sqrt_double(f * f + f1 * f1 + f2 * f2);
    }
public static void verusdmg() {
	mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
			mc.thePlayer.posY + 3.001, mc.thePlayer.posZ, false));
	mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
			mc.thePlayer.posY, mc.thePlayer.posZ, false));
	mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX,
			mc.thePlayer.posY, mc.thePlayer.posZ, true));
}
}
