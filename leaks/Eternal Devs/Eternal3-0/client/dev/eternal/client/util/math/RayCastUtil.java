package dev.eternal.client.util.math;

import com.google.common.base.Predicates;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;
import net.optifine.reflect.Reflector;

import java.util.List;

import static dev.eternal.client.util.math.MathUtil.DEG_TO_RAD;

public final class RayCastUtil {

  private static final Minecraft MC = Minecraft.getMinecraft();

  /**
   * @see Entity
   */
  public static Vec3 getDirectionVector(float yaw, float pitch) {
    float delZ = MathHelper.cos(-yaw * DEG_TO_RAD - (float) Math.PI);
    float delX = MathHelper.sin(-yaw * DEG_TO_RAD - (float) Math.PI);
    float delHorizontal = -MathHelper.cos(-pitch * DEG_TO_RAD);
    float delY = MathHelper.sin(-pitch * DEG_TO_RAD);
    return new Vec3(delX * delHorizontal, delY, delZ * delHorizontal);
  }

  public static MovingObjectPosition rayTraceBlocks(
      Vec3 origin, float yaw, float pitch, float range) {
    final Vec3 direction = getDirectionVector(yaw, pitch);
    return rayTraceBlocks(origin, direction, range);
  }

  public static MovingObjectPosition rayTrace(
      float yaw, float pitch, double blockReachDistance, float partialTicks) {
    Vec3 vec3 = Minecraft.getMinecraft().thePlayer.getPositionEyes(partialTicks);
    Vec3 vec31 = getDirectionVector(yaw, pitch);
    Vec3 vec32 =
        vec3.addVector(
            vec31.xCoord * blockReachDistance,
            vec31.yCoord * blockReachDistance,
            vec31.zCoord * blockReachDistance);
    return Minecraft.getMinecraft().theWorld.rayTraceBlocks(vec3, vec32, false, false, true);
  }

  public static MovingObjectPosition rayTraceBlocks(Vec3 origin, Vec3 direction, float range) {
    return rayTraceBlocks(origin, origin.add(direction.normalize().scale(range)));
  }

  public static MovingObjectPosition rayTraceBlocks(Vec3 from, Vec3 to) {
    return MC.theWorld.rayTraceBlocks(from, to, false, false, false);
  }

  public static Entity rayCast(double boundingBoxInset, float yaw, float pitch, double range) {
    Vec3 look = getDirectionVector(yaw, pitch);
    Vec3 vec3 = MC.thePlayer.getPositionVector().addVector(0, MC.thePlayer.getEyeHeight(), 0);
    Vec3 vec31 = vec3.addVector(look.xCoord * range, look.yCoord * range, look.zCoord * range);
    if (vec31 == look) return MC.thePlayer;
    Vec3 sub = vec31.subtract(vec3);
    double d0 = range;
    AxisAlignedBB a =
        MC.getRenderViewEntity()
            .getEntityBoundingBox()
            .addCoord(sub.xCoord, sub.yCoord, sub.zCoord)
            .expand(1F - boundingBoxInset, 1F - boundingBoxInset, 1F - boundingBoxInset);
    List<Entity> list =
        MC.theWorld.getEntitiesWithinAABBExcludingEntity(MC.getRenderViewEntity(), a);
    double var13 = d0 + 0.5f;
    Entity returnEnt = null;
    for (int j = 0; j < list.size(); ++j) {
      Entity entity1 = list.get(j);
      if (entity1.canBeCollidedWith()) {
        float f1 = entity1.getCollisionBorderSize();
        AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec31);
        double var20 = 0;
        if (axisalignedbb.isVecInside(vec3) && 0.0D < var13 || var13 == 0.0D) {
          returnEnt = entity1;
          var13 = 0.0D;
        } else if ((movingobjectposition != null)
            && (var20 = vec3.distanceTo(movingobjectposition.hitVec)) < var13
            || var13 == 0) {
          returnEnt = entity1;
          var13 = var20;
        }
      }
    }
    return returnEnt;
  }

  public static MovingObjectPosition rayCast(float yaw, float pitch, float reach) {
    return rayCast(yaw, pitch, reach, 1.0f);
  }

  public static MovingObjectPosition rayCast(
      float yaw, float pitch, float reach, float partialTicks) {
    final Vec3 from = Minecraft.getMinecraft().thePlayer.getPositionEyes(partialTicks);

    MovingObjectPosition movingObjectPosition;
    Entity pointedEntity = null;

    movingObjectPosition = rayTraceBlocks(from, yaw, pitch, reach);
    double distanceToObject = reach;

    if (movingObjectPosition != null) {
      distanceToObject = movingObjectPosition.hitVec.distanceTo(from);
    }

    final Vec3 lookVector = getDirectionVector(yaw, pitch);
    final Vec3 lookRay =
        from.addVector(
            lookVector.xCoord * reach, lookVector.yCoord * reach, lookVector.zCoord * reach);
    Vec3 hitVec = null;

    final float expansion = 1.0F;
    List entities =
        Minecraft.getMinecraft()
            .theWorld
            .getEntitiesInAABBexcluding(
                Minecraft.getMinecraft().thePlayer,
                Minecraft.getMinecraft()
                    .thePlayer
                    .getEntityBoundingBox()
                    .addCoord(
                        lookVector.xCoord * reach,
                        lookVector.yCoord * reach,
                        lookVector.zCoord * reach)
                    .expand(expansion, expansion, expansion),
                Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
    double smallestDistance = distanceToObject;

    for (Object ent : entities) {
      final Entity entity = (Entity) ent;
      final float collisionBorderSize = entity.getCollisionBorderSize();
      final AxisAlignedBB collisionBox =
          entity
              .getEntityBoundingBox()
              .expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
      final MovingObjectPosition movingobjectposition =
          collisionBox.calculateIntercept(from, lookRay);

      if (collisionBox.isVecInside(from)) {
        if (smallestDistance >= 0.0D) {
          pointedEntity = entity;
          hitVec = movingobjectposition == null ? from : movingobjectposition.hitVec;
          smallestDistance = 0.0D;
        }
      } else if (movingobjectposition != null) {
        final double dist = from.distanceTo(movingobjectposition.hitVec);

        if (dist < smallestDistance || smallestDistance == 0.0D) {
          if (entity == entity.ridingEntity) {
            if (smallestDistance == 0.0D) {
              pointedEntity = entity;
              hitVec = movingobjectposition.hitVec;
            }
          } else {
            pointedEntity = entity;
            hitVec = movingobjectposition.hitVec;
            smallestDistance = dist;
          }
        }
      }
    }

    if (pointedEntity != null
        && (smallestDistance < distanceToObject || movingObjectPosition == null)) {
      movingObjectPosition = new MovingObjectPosition(pointedEntity, hitVec);
    }

    return movingObjectPosition;
  }

  public static MovingObjectPosition getOver(float yaw, float pitch) {
    Entity entity = Minecraft.getMinecraft().getRenderViewEntity();

    Entity pointedEntity;
    MovingObjectPosition movingObjectPosition = MC.objectMouseOver;

    if (entity != null && Minecraft.getMinecraft().theWorld != null) {
      pointedEntity = null;
      double d0 = Minecraft.getMinecraft().playerController.getBlockReachDistance();
      movingObjectPosition = rayTrace(yaw, pitch, d0, 1.0F);
      double d1 = d0;
      Vec3 vec3 = entity.getPositionEyes(1.0F);
      boolean flag = false;
      int i = 3;

      if (Minecraft.getMinecraft().playerController.extendedReach()) {
        d0 = 6.0D;
        d1 = 6.0D;
      } else if (d0 > 3.0D) {
        flag = true;
      }

      if (movingObjectPosition != null) {
        d1 = movingObjectPosition.hitVec.distanceTo(vec3);
      }

      Vec3 vec31 = getDirectionVector(yaw, pitch);
      Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
      pointedEntity = null;
      Vec3 vec33 = null;
      float f = 1.0F;
      List<Entity> list =
          Minecraft.getMinecraft()
              .theWorld
              .getEntitiesInAABBexcluding(
                  entity,
                  entity
                      .getEntityBoundingBox()
                      .addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0)
                      .expand(f, f, f),
                  Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
      double d2 = d1;

      for (int j = 0; j < list.size(); ++j) {
        Entity entity1 = list.get(j);
        float f1 = entity1.getCollisionBorderSize();
        AxisAlignedBB axisalignedbb =
            entity1.getEntityBoundingBox().expand(f1, f1, f1);
        MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

        if (axisalignedbb.isVecInside(vec3)) {
          if (d2 >= 0.0D) {
            pointedEntity = entity1;
            vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
            d2 = 0.0D;
          }
        } else if (movingobjectposition != null) {
          double d3 = vec3.distanceTo(movingobjectposition.hitVec);

          if (d3 < d2 || d2 == 0.0D) {
            boolean flag1 = false;

            if (Reflector.ForgeEntity_canRiderInteract.exists()) {
              flag1 =
                  Reflector.callBoolean(
                      entity1, Reflector.ForgeEntity_canRiderInteract);
            }

            if (!flag1 && entity1 == entity.ridingEntity) {
              if (d2 == 0.0D) {
                pointedEntity = entity1;
                vec33 = movingobjectposition.hitVec;
              }
            } else {
              pointedEntity = entity1;
              vec33 = movingobjectposition.hitVec;
              d2 = d3;
            }
          }
        }
      }

      if (pointedEntity != null && flag && vec3.distanceTo(vec33) > 3.0D) {
        pointedEntity = null;
        movingObjectPosition =
            new MovingObjectPosition(
                MovingObjectPosition.MovingObjectType.MISS,
                vec33,
                null,
                new BlockPos(vec33));
      }

      if (pointedEntity != null && (d2 < d1 || movingObjectPosition == null)) {
        movingObjectPosition = new MovingObjectPosition(pointedEntity, vec33);

        if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
          movingObjectPosition.entityHit = pointedEntity;
        }
      }

      if (movingObjectPosition == null) {
        System.out.println("kekW");
      }
    }
    return movingObjectPosition;
  }
}
