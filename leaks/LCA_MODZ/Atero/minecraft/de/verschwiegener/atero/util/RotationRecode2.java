package de.verschwiegener.atero.util;



import de.verschwiegener.atero.module.modules.world.Scaffold;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.Random;

public class RotationRecode2 {

    public static Minecraft mc = Minecraft.getMinecraft();

    public static float[] rotationrecode7(Scaffold.BlockData blockData) {
        double x = blockData.getPos().getX() + 0.5D - mc.thePlayer.posX + blockData.getFacing().getFrontOffsetX() / 2.0D;
        double z = blockData.getPos().getZ() + 0.5D - mc.thePlayer.posZ + blockData.getFacing().getFrontOffsetZ() / 2.0D;
        double y = blockData.getPos().getY() + 0.5D;
        double ymax = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double allmax = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(ymax + 10, allmax) * 180.0D / Math.PI);

        final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float f3 = f2 * f2 * f2 * 1.2F;
        yaw -= yaw % f3;
        pitch -= pitch % (f3 * f2);
        if (yaw < 0.0F) yaw += 360.0F;
        return new float[]{yaw, pitch};
    }
    public static float[] rotationrecodeWatchdog(Scaffold.BlockData blockData) {
        double x = blockData.getPos().getX() + 0.5D - mc.thePlayer.posX + blockData.getFacing().getFrontOffsetX() / 2.0D;
        double z = blockData.getPos().getZ() + 0.5D - mc.thePlayer.posZ + blockData.getFacing().getFrontOffsetZ() / 2.0D;
        double y = blockData.getPos().getY() + 0.5D;
        double ymax = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double allmax = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (Math.atan2(ymax + 5, allmax) * 180.0D / 3.14);

        final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float f3 = f2 * f2 * f2 * 1.2F;
        yaw -= yaw % f3;
        pitch -= pitch % (f3 * f2);
        if (yaw < 0.0F) yaw += 360.0F;
        return new float[]{yaw, MathHelper.clamp_float(pitch, -90, 90) };
    }


    public static float[] rotationrecodeMEME(Scaffold.BlockData blockData) {
        double x = blockData.getPos().getX() + 0.5D - mc.thePlayer.posX + blockData.getFacing().getFrontOffsetX() / 2.0D;
        double z = blockData.getPos().getZ() + 0.5D - mc.thePlayer.posZ + blockData.getFacing().getFrontOffsetZ() / 2.0D;
        final float MEME = (float) MathHelper.getRandomDoubleInRange(new Random(), 0, 360);
        final float MEM2 = (float) MathHelper.getRandomDoubleInRange(new Random(), 0, 360);
        double y = blockData.getPos().getY() + 0.5D;
        double ymax = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double allmax = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (Math.atan2(z, x) * MEME / Math.PI) - MEM2;
        float pitch = (float) (Math.atan2(ymax + 10, allmax) * 180 / Math.PI);

        final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float f3 = f2 * f2 * f2 * 1.2F;
        yaw -= yaw % f3;
        pitch -= pitch % (f3 * f2);
        if (yaw < 0.0F) yaw += 360.0F;
        return new float[]{yaw, pitch};
    }

    public static float[] rotationrecode(Scaffold.BlockData blockData) {
        double x = blockData.getPos().getX() + 0.5D - mc.thePlayer.posX + blockData.getFacing().getFrontOffsetX() / 2.0D;
        double z = blockData.getPos().getZ() + 0.5D - mc.thePlayer.posZ + blockData.getFacing().getFrontOffsetZ() / 2.0D;
        double y = blockData.getPos().getY() + 0.5D;
        double ymax = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() - y;
        double allmax = MathHelper.sqrt_double(x * x + z * z);
        float yaw = mc.thePlayer.rotationYawHead +180;
        float pitch = (float) (Math.atan2(ymax, allmax) * 180.0D / Math.PI);

        final float f2 = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float f3 = f2 * f2 * f2 * 1.2F;
        yaw -= yaw % f3;
        pitch -= pitch % (f3 * f2);
        if (yaw < 0.0F) yaw += 360.0F;
        return new float[]{yaw, pitch};
    }


}

