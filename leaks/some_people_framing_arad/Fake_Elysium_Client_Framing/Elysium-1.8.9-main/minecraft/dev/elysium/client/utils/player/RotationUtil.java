package dev.elysium.client.utils.player;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.MathHelper;

public class RotationUtil {
    public static float getYawChange(float yaw, double posX, double posZ) {
        Minecraft mc = Minecraft.getMinecraft();
        double deltaX = posX - mc.thePlayer.posX;
        double deltaZ = posZ - mc.thePlayer.posZ;
        double yawToEntity = 0;
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            if(deltaX != 0)
                yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
            if(deltaX != 0)
                yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            if(deltaZ != 0)
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }

        return MathHelper.wrapAngleTo180_float(-(yaw- (float) yawToEntity));
    }

    public static float getYawChange(float yaw, float desiredyaw) {
        return MathHelper.wrapAngleTo180_float(-(yaw-desiredyaw));
    }

    public static float getPitchChange(float pitch, double posX, double posY, double posZ) {
        Minecraft mc = Minecraft.getMinecraft();
        double deltaX = posX - mc.thePlayer.posX;
        double deltaZ = posZ - mc.thePlayer.posZ;
        double deltaY = posY + mc.thePlayer.getEyeHeight() - mc.thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity;
        deltaY = posY - Minecraft.getMinecraft().thePlayer.posY - (double)Minecraft.getMinecraft().thePlayer.getEyeHeight()*1.3185-.5;
        double distance = Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaZ, 2.0));

        pitchToEntity = (float) ((float)(-Math.toDegrees(Math.atan(deltaY / distance)))-2+((mc.thePlayer.getDistance(posX,posY,posZ)*0.2))) + 1;
        return -MathHelper.wrapAngleTo180_float(pitch - (float) pitchToEntity);
    }

    public static float getPitchChange(float pitch, Entity entity, double posY) {
        double deltaX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double deltaY = posY - 2.2D + entity.getEyeHeight() - Minecraft.getMinecraft().thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity;
        deltaY = entity.posY - 3.5 + (entity.posY - entity.lastTickPosY) + (double)entity.getEyeHeight()*1.3185 - Minecraft.getMinecraft().thePlayer.posY + (double)Minecraft.getMinecraft().thePlayer.getEyeHeight()-0.25592;
        double distance = Math.sqrt(Math.pow(deltaX, 2.0) + Math.pow(deltaZ, 2.0));

        pitchToEntity = (float) ((float)(-Math.toDegrees(Math.atan(deltaY / distance)))-2+((entity.getDistanceToEntity(Minecraft.getMinecraft().thePlayer)*0.2))) + 1;
        return -MathHelper.wrapAngleTo180_float(pitch - (float) pitchToEntity);
    }
}
