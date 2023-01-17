package com.alan.clients.util;

import com.alan.clients.util.chat.ChatUtil;
import com.alan.clients.util.rotation.RotationUtil;
import com.google.common.base.Predicates;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2f;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.util.*;

import java.util.List;

/**
 * @author Patrick
 * @since 11/17/2021
 */
public final class RayCastUtil implements InstanceAccess {

    public static MovingObjectPosition rayCast(final Vector2f rotation, final double range) {
        return rayCast(rotation, range, 0);
    }

    public static boolean inView(final Entity entity) {
        int renderDistance = 16 * mc.gameSettings.renderDistanceChunks;

        if (entity.threadDistance > 60) {
            MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(RotationUtil.calculate(entity), renderDistance, 0.2f);
            return movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY;
        } else {
            for (double yPercent = 1; yPercent >= 0; yPercent -= 0.5) {
                for (double xPercent = 1; xPercent >= -1; xPercent -= 1) {
                    for (double zPercent = 1; zPercent >= -1; zPercent -= 1) {
                        Vector2f rotations = RotationUtil.calculate(entity.getCustomPositionVector().add(
                                (entity.getEntityBoundingBox().maxX - entity.getEntityBoundingBox().minX) * xPercent,
                                (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * yPercent,
                                (entity.getEntityBoundingBox().maxZ - entity.getEntityBoundingBox().minZ) * zPercent));

                        MovingObjectPosition movingObjectPosition = RayCastUtil.rayCast(rotations, renderDistance, 0.2f);
                        if (movingObjectPosition != null && movingObjectPosition.typeOfHit == MovingObjectPosition.MovingObjectType.ENTITY) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    public static MovingObjectPosition rayCast(final Vector2f rotation, final double range, final float expand) {
        final float partialTicks = mc.timer.renderPartialTicks;
        final Entity entity = mc.getRenderViewEntity();
        MovingObjectPosition objectMouseOver;

        if (entity != null && mc.theWorld != null) {
            objectMouseOver = entity.rayTraceCustom(range, rotation.x, rotation.y);
            double d1 = range;
            final Vec3 vec3 = entity.getPositionEyes(partialTicks);

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }

            final Vec3 vec31 = mc.thePlayer.getVectorForRotation(rotation.y, rotation.x);
            final Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (final Entity entity1 : list) {
                final float f1 = entity1.getCollisionBorderSize() + expand;
                final AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand(f1, f1, f1);
                final MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);

                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    final double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                    if (d3 < d2 || d2 == 0.0D) {
                        pointedEntity = entity1;
                        vec33 = movingobjectposition.hitVec;
                        d2 = d3;
                    }
                }
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                objectMouseOver = new MovingObjectPosition(pointedEntity, vec33);
            }

            return objectMouseOver;
        }

        return null;
    }

    public static boolean overBlock(final Vector2f rotation, final EnumFacing enumFacing, final BlockPos pos, final boolean strict) {
        final MovingObjectPosition movingObjectPosition = mc.thePlayer.rayTraceCustom(4.5f, rotation.x, rotation.y);

        if (movingObjectPosition == null) return false;

        final Vec3 hitVec = movingObjectPosition.hitVec;
        if (hitVec == null) return false;

        return movingObjectPosition.getBlockPos().equals(pos) && (!strict || movingObjectPosition.sideHit == enumFacing);
    }

    public static boolean overBlock(final EnumFacing enumFacing, final BlockPos pos, final boolean strict) {
        final MovingObjectPosition movingObjectPosition = mc.objectMouseOver;

        if (movingObjectPosition == null) return false;

        final Vec3 hitVec = movingObjectPosition.hitVec;
        if (hitVec == null) return false;

        return movingObjectPosition.getBlockPos().equals(pos) && (!strict || movingObjectPosition.sideHit == enumFacing);
    }

    public static Boolean overBlock(final Vector2f rotation, final BlockPos pos) {
        return overBlock(rotation, EnumFacing.UP, pos, false);
    }

    public static Boolean overBlock(final Vector2f rotation, final BlockPos pos, final EnumFacing enumFacing) {
        return overBlock(rotation, enumFacing, pos, true);
    }
}