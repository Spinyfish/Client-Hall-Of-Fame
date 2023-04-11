package byron.Mono.utils;

import byron.Mono.interfaces.MinecraftInterface;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

public final class RotationUtil implements MinecraftInterface {



    public static float[] getRotations(EntityLivingBase target) {
        float yaw, pitch;
        Vec3 targetPos = new Vec3(target.posX, target.posY, target.posZ);
        Vec3 playerPos = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        double d0 = targetPos.xCoord - playerPos.xCoord;
        double d1 = targetPos.yCoord - playerPos.yCoord;
        double d2 = targetPos.zCoord - playerPos.zCoord;
        double d3 = (double) MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f = (float)(MathHelper.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
        float f1 = (float)(-(MathHelper.atan2(d1, d3) * 180.0D / Math.PI));
        yaw = f;
        pitch = f1;
      //  this.entity.rotationPitch = this.updateRotation(this.entity.rotationPitch, f1, this.deltaLookPitch);
      //  this.entity.rotationYawHead = this.updateRotation(this.entity.rotationYawHead, f, this.deltaLookYaw);
        return new float[] {yaw, pitch};
    }

    public static float[] fixRotation(float[] previous, float[] now) {
        //Simulate the vanilla sensitivity handling
        float sensitivity = mc.gameSettings.mouseSensitivity;
        float f = sensitivity * 0.6F + 0.2F;
        float gcd = f * f * f * 1.2F;

        //get real Yaw
        float dYaw = now[0] - previous[0];
        dYaw -= dYaw % gcd;
        float yaw = previous[0] + dYaw;

        //get real Pitch
        float dPitch = now[1] - previous[1];
        dPitch -= dPitch % gcd;
        float pitch = previous[1] + dPitch;
        return new float[] {yaw, pitch};
    }

}
