package byron.Mono.module.impl.combat;

import byron.Mono.event.impl.EventUpdate;
import byron.Mono.interfaces.ModuleInterface;
import byron.Mono.module.Category;
import byron.Mono.module.Module;
import com.google.common.eventbus.Subscribe;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MathHelper;

import java.util.List;

@ModuleInterface(name = "Aimbot", description = "aim op", category = Category.Combat)
public class Aimbot extends Module {

    @Subscribe
    public void onUpdate(EventUpdate e) {
            List list = mc.theWorld.playerEntities;

            for(int k = 0; k < list.size(); ++k) {
                if (((EntityPlayer)list.get(k)).getName() != mc.thePlayer.getName()) {
                    EntityPlayer entityplayer = (EntityPlayer)list.get(1);
                    if (mc.thePlayer.getDistanceToEntity(entityplayer) > mc.thePlayer.getDistanceToEntity((Entity)list.get(k))) {
                        entityplayer = (EntityPlayer)list.get(k);
                    }

                    float f = mc.thePlayer.getDistanceToEntity(entityplayer);
                    if (f < 8.0F && mc.thePlayer.canEntityBeSeen(entityplayer)) {
                        faceEntity(entityplayer);
                    }
                }
            }

        }


    public static synchronized void faceEntity(EntityLivingBase entity) {
        float[] rotations = getRotationsNeeded(entity);
        if (rotations != null) {
            mc.thePlayer.rotationYaw = rotations[0];
            mc.thePlayer.rotationPitch = rotations[1] + 1.0F;
        }

    }

    public static float[] getRotationsNeeded(Entity entity) {
        if (entity == null) {
            return null;
        } else {
            double diffX = entity.posX - mc.thePlayer.posX;
            double diffZ = entity.posZ - mc.thePlayer.posZ;
            double diffY;
            if (entity instanceof EntityLivingBase) {
                EntityLivingBase entityLivingBase = (EntityLivingBase)entity;
                diffY = entityLivingBase.posY + (double)entityLivingBase.getEyeHeight() - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
            } else {
                diffY = (entity.boundingBox.minY + entity.boundingBox.maxY) / 2.0D - (mc.thePlayer.posY + (double)mc.thePlayer.getEyeHeight());
            }

            double dist = (double) MathHelper.sqrt_double(diffX * diffX + diffZ * diffZ);
            float yaw = (float)(Math.atan2(diffZ, diffX) * 180.0D / 3.141592653589793D) - 90.0F;
            float pitch = (float)(-(Math.atan2(diffY, dist) * 180.0D / 3.141592653589793D));
            return new float[]{mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw), mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)};
        }
    }
    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

}
