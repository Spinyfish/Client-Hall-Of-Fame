package intent.AquaDev.aqua.utils;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;
import net.optifine.reflect.Reflector;

public class RayTraceUtil {
   public static MovingObjectPosition rayCast(float partialTicks, float[] rots) {
      Minecraft mc = Minecraft.getMinecraft();
      MovingObjectPosition objectMouseOver = null;
      Entity entity = mc.getRenderViewEntity();
      if (entity != null && mc.theWorld != null) {
         mc.mcProfiler.startSection("pick");
         mc.pointedEntity = null;
         double d0 = (double)mc.playerController.getBlockReachDistance();
         objectMouseOver = entity.customRayTrace(d0, partialTicks, rots[0], rots[1]);
         double d1 = d0;
         Vec3 vec3 = entity.getPositionEyes(partialTicks);
         boolean flag = false;
         boolean flag1 = true;
         if (mc.playerController.extendedReach()) {
            d0 = 6.0;
            d1 = 6.0;
         } else {
            if (d0 > 3.0) {
               flag = true;
            }

            d0 = d0;
         }

         if (objectMouseOver != null) {
            d1 = objectMouseOver.hitVec.distanceTo(vec3);
         }

         Vec3 vec31 = entity.getCustomLook(partialTicks, rots[0], rots[1]);
         Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
         Entity pointedEntity = null;
         Vec3 vec33 = null;
         float f = 1.0F;
         List<Entity> list = mc.theWorld
            .getEntitiesInAABBexcluding(
               entity,
               entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double)f, (double)f, (double)f),
               Predicates.and(EntitySelectors.NOT_SPECTATING, new Predicate<Entity>() {
                  public boolean apply(Entity p_apply_1_) {
                     return p_apply_1_.canBeCollidedWith();
                  }
               })
            );
         double d2 = d1;
         AxisAlignedBB realBB = null;

         for(int i = 0; i < list.size(); ++i) {
            Entity entity1 = list.get(i);
            float f1 = entity1.getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double)f1, (double)f1, (double)f1);
            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
            if (axisalignedbb.isVecInside(vec3)) {
               if (d2 >= 0.0) {
                  pointedEntity = entity1;
                  vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                  d2 = 0.0;
               }
            } else if (movingobjectposition != null) {
               double d3 = vec3.distanceTo(movingobjectposition.hitVec);
               if (d3 < d2 || d2 == 0.0) {
                  boolean flag2 = false;
                  if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                     flag2 = Reflector.callBoolean(entity1, Reflector.ForgeEntity_canRiderInteract);
                  }

                  if (entity1 != entity.ridingEntity || flag2) {
                     pointedEntity = entity1;
                     vec33 = movingobjectposition.hitVec;
                     d2 = d3;
                  } else if (d2 == 0.0) {
                     pointedEntity = entity1;
                     vec33 = movingobjectposition.hitVec;
                  }
               }
            }
         }

         if (pointedEntity != null && flag && vec3.distanceTo(vec33) > 3.0) {
            pointedEntity = null;
            objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
         }

         if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
            objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
               ;
            }
         }
      }

      return objectMouseOver;
   }
}
