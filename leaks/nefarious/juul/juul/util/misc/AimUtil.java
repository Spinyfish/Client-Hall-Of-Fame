package juul.util.misc;

import juul.Juul;
import juul.inject.wrappers.EntityLivingBaseWrapper;
import juul.inject.wrappers.EntityPlayerSPWrapper;
import juul.inject.wrappers.EntityWrapper;
import juul.inject.wrappers.MinecraftWrapper;
import juul.inject.wrappers.RenderManagerWrapper;
import juul.inject.wrappers.TimerWrapper;
import juul.util.Utils;

public class AimUtil implements Utils {

    public float[] getRotationsDelta(Object entity) {
        if (entity == null)
            return null;

        EntityLivingBaseWrapper entityLivingBase = new EntityLivingBaseWrapper(entity);


        TimerWrapper timer = new TimerWrapper(mc.timer());
        RenderManagerWrapper renderManager = new RenderManagerWrapper(mc.renderManager());

        double diffX = (entityLivingBase.lastTickPosX() + (entityLivingBase.posX() - entityLivingBase.lastTickPosX()) * timer.renderPartialTicks()) - renderManager.viewerPosX(),
                diffZ = (entityLivingBase.lastTickPosZ() + (entityLivingBase.posZ() - entityLivingBase.lastTickPosZ()) * timer.renderPartialTicks()) - renderManager.viewerPosZ(),
                diffY = ((entityLivingBase.lastTickPosY() + (entityLivingBase.posY() - entityLivingBase.lastTickPosY()) * timer.renderPartialTicks()) + entityLivingBase.getEyeHeight()) - (renderManager.viewerPosY() + thePlayerSP.getEyeHeight()),
                dist = sqrt_double(diffX * diffX + diffZ * diffZ);

        float yaw = (float) (Math.atan2(diffZ, diffX) * 180d / Math.PI) - 90f,
                pitch = (float) -(Math.atan2(diffY, dist) * 180d / Math.PI);

        return new float[]{
                wrapAngleTo180_float(thePlayerSP.rotationYaw() - yaw),
                wrapAngleTo180_float(thePlayerSP.rotationPitch() - pitch)
        };
    }

    public float[] getEntityRotations(Object e) {
        EntityPlayerSPWrapper player = new EntityPlayerSPWrapper(mc.player());
        EntityLivingBaseWrapper target = new EntityLivingBaseWrapper(e);
        TimerWrapper timer = new TimerWrapper(mc.timer());
        RenderManagerWrapper renderManager = new RenderManagerWrapper(mc.renderManager());

        double posX = (target.lastTickPosX() + (target.posX() - target.lastTickPosX()) * timer.renderPartialTicks()) - renderManager.viewerPosX(),
                posZ = (target.lastTickPosZ() + (target.posZ() - target.lastTickPosZ()) * timer.renderPartialTicks()) - renderManager.viewerPosZ(),
                posY = (target.lastTickPosY() + (target.posY() - target.lastTickPosY()) * timer.renderPartialTicks()) - 3.5 + target.getEyeHeight() - renderManager.viewerPosY() + player.getEyeHeight(),
                distance = sqrt_double((posX * posX) + (posZ * posZ));

        float newYaw = (float) Math.toDegrees(-Math.atan(posX / posZ)),
                newPitch = (float) -Math.toDegrees(Math.atan(posY / distance));

        if (posZ < 0 && posX < 0)
            newYaw = (float) (90 + Math.toDegrees(Math.atan(posZ / posX)));
        else if (posZ < 0 && posX > 0)
            newYaw = (float) (-90 + Math.toDegrees(Math.atan(posZ / posX)));

        return new float[]{newYaw, newPitch};
    }

    public static float sqrt_double(double n) {
        return (float) Math.sqrt(n);
    }

    public static float wrapAngleTo180_float(float n) {
        n %= 360.0F;

        if (n >= 180.0F) {
            n -= 360.0F;
        }

        if (n < -180.0F) {
            n += 360.0F;
        }

        return n;
    }

}
