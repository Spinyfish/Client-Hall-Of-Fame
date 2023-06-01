package frapppyz.cutefurry.pics.util;

import frapppyz.cutefurry.pics.Wrapper;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.BlockPos;

public class RotUtil {
    static Minecraft mc = Minecraft.getMinecraft();
    public static double yaw = 0;
    public static float[] getRots(Entity e) {
        double deltaX = e.posX - mc.thePlayer.posX,
                deltaY = e.posY - 3.7 + e.getEyeHeight() + Math.random()/2 - mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - Math.random(),
                deltaZ = e.posZ - mc.thePlayer.posZ,
                distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
                pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

        if(deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }else if(deltaX > 0 && deltaZ < 0) {
            yaw = (float) (-90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }

        return new float[] {yaw, pitch};
    }

    public static float[] getRots(BlockPos p) {
        Minecraft mc = Minecraft.getMinecraft();
        double deltaX = p.getX() - mc.thePlayer.posX+0.3+Math.random()*0.2,
                deltaY = -p.getY() + mc.thePlayer.getEyeHeight(),
                deltaZ = p.getZ() - mc.thePlayer.posZ,
                distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
                pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));

        if(deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }else if(deltaX > 0 && deltaZ < 0) {
            yaw = (float) (-90 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }

        return new float[] {yaw, pitch};
    }
    public static float[] getRandomisedRots(Entity e) {

        double deltaX = e.posX - mc.thePlayer.posX + Math.random()/10,
                deltaY = e.posY - 3.7 + e.getEyeHeight() + Math.random()/2 - mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - Math.random(),
                deltaZ = e.posZ - mc.thePlayer.posZ,
                distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)),
                pitch = (float) ((float) Math.round(-Math.toDegrees(Math.atan(deltaY / distance)))*Math.random()*2);

        if(deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 - Math.random()/10 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }else if(deltaX > 0 && deltaZ < 0) {
            yaw = (float) (-90 + Math.random()/10 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }

        return new float[] {yaw, pitch};
    }

    public static float[] getRandomisedRots2(Entity e) {

        double deltaX = e.posX - mc.thePlayer.posX + Math.random()/25,
                deltaZ = e.posZ - mc.thePlayer.posZ;

        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ));

        if(deltaX < 0 && deltaZ < 0) {
            yaw = (float) (90 - Math.random()/25 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }else if(deltaX > 0 && deltaZ < 0) {
            yaw = (float) (-90 + Math.random()/25 + Math.toDegrees(Math.atan(deltaZ / deltaX)));
        }

        if(!(yaw-RotUtil.yaw >= 45 || yaw-RotUtil.yaw <= -45)){
            yaw = (float) RotUtil.yaw;
        }

        return new float[] {yaw, 0};
    }

    public static void renderRots(float yaw, float pitch) {
        Minecraft.getMinecraft().thePlayer.renderYawOffset = yaw;
        Minecraft.getMinecraft().thePlayer.rotationYawHead = yaw;
    }
}
