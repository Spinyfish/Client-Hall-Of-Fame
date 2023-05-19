package club.marsh.bloom.impl.utils.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MathHelper;
import net.minecraft.util.Vec3;

import java.util.List;

import com.google.common.base.Predicates;
import net.minecraft.util.*;

public class RaycastUtil {
	
	public static Minecraft mc = Minecraft.getMinecraft();
    
    public static Entity raycastEntity(final double range, final float yaw, final float pitch) {
        final Entity renderViewEntity = mc.getRenderViewEntity();
        if (renderViewEntity != null && mc.theWorld != null) {
            double blockReachDistance = range;
            final Vec3 eyePosition = renderViewEntity.getPositionEyes(1.0f);
            final float yawCos = MathHelper.cos(-yaw * 0.017453292f - 3.1415927f);
            final float yawSin = MathHelper.sin(-yaw * 0.017453292f - 3.1415927f);
            final float pitchCos = -MathHelper.cos(-pitch * 0.017453292f);
            final float pitchSin = MathHelper.sin(-pitch * 0.017453292f);
            final Vec3 entityLook = new Vec3((double)(yawSin * pitchCos), (double)pitchSin, (double)(yawCos * pitchCos));
            final Vec3 vector = eyePosition.addVector(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance);
            final List<Entity> entityList = (List<Entity>)mc.theWorld.getEntitiesInAABBexcluding(renderViewEntity, renderViewEntity.getEntityBoundingBox().addCoord(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance).expand(1.0, 1.0, 1.0), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            Entity pointedEntity = null;
            for (final Entity entity : entityList) {
                
                final float collisionBorderSize = entity.getCollisionBorderSize();
                final AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().expand((double)collisionBorderSize, (double)collisionBorderSize, (double)collisionBorderSize);
                final MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(eyePosition, vector);
                if (axisAlignedBB.isVecInside(eyePosition)) {
                    if (blockReachDistance < 0.0) {
                        continue;
                    }
                    pointedEntity = entity;
                    blockReachDistance = 0.0;
                }
                else {
                    if (movingObjectPosition == null) {
                        continue;
                    }
                    final double eyeDistance = eyePosition.distanceTo(movingObjectPosition.hitVec);
                    if (eyeDistance >= blockReachDistance && blockReachDistance != 0.0) {
                        continue;
                    }
                    pointedEntity = entity;
                    blockReachDistance = eyeDistance;
                }
            }
            return pointedEntity;
        }
        return null;
    }
}
