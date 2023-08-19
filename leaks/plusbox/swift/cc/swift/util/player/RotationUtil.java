/**
 * @project hakarware
 * @author CodeMan
 * @at 24.07.23, 20:52
 */

package cc.swift.util.player;

import cc.swift.util.IMinecraft;
import com.google.common.base.Predicates;
import lombok.experimental.UtilityClass;
import net.minecraft.entity.Entity;
import net.minecraft.util.*;
import net.optifine.reflect.Reflector;

import java.util.List;

@UtilityClass
public class RotationUtil implements IMinecraft {

    public float[] getRotationsToVector(Vec3 from, Vec3 to) {
        double d0 = to.xCoord - from.xCoord;
        double d1 = to.yCoord - from.yCoord;
        double d2 = to.zCoord - from.zCoord;
        double d3 = MathHelper.sqrt_double(d0 * d0 + d2 * d2);
        float f = (float) (MathHelper.atan2(d2, d0) * 180.0D / Math.PI) - 90.0F;
        float f1 = (float) (-(MathHelper.atan2(d1, d3) * 180.0D / Math.PI));
        return new float[]{f, f1};
    }

    public Vec3 getEyePosition() {
        return new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);
    }

    public Vec3 getBestVector(AxisAlignedBB axisAlignedBB, Vec3 eyePosition) {
        double x = MathHelper.clamp_double(eyePosition.xCoord, axisAlignedBB.minX, axisAlignedBB.maxX);
        double y = MathHelper.clamp_double(eyePosition.yCoord, axisAlignedBB.minY, axisAlignedBB.maxY);
        double z = MathHelper.clamp_double(eyePosition.zCoord, axisAlignedBB.minZ, axisAlignedBB.maxZ);
        return new Vec3(x, y, z);
    }

    public void fixGCD(float[] rotations, float[] prevRotations) {
        float deltaYaw = rotations[0] - prevRotations[0];
        float deltaPitch = rotations[1] - prevRotations[1];
        float f = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float f1 = f * f * f * 8.0F * 0.15F;
        rotations[0] -= deltaYaw % f1;
        rotations[1] -= deltaPitch % f1;
        rotations[1] = MathHelper.clamp_float(rotations[1], -90, 90);
    }

    public Vec3 getVectorForRotation(float pitch, float yaw) {
        float f = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
        float f1 = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
        float f2 = -MathHelper.cos(-pitch * 0.017453292F);
        float f3 = MathHelper.sin(-pitch * 0.017453292F);
        return new Vec3(f1 * f2, f3, f * f2);
    }

    public Entity raycast(double range, float yaw, float pitch) {
        Vec3 vec3 = mc.thePlayer.getPositionEyes(1.0f);
        Vec3 vec31 = getVectorForRotation(pitch, yaw);
        Vec3 vec32 = vec3.addVector(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range);
        Entity pointedEntity = null;
        float f = 1.0F;
        List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(mc.getRenderViewEntity(), mc.getRenderViewEntity().getEntityBoundingBox().addCoord(vec31.xCoord * range, vec31.yCoord * range, vec31.zCoord * range).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
        double d2 = range;
        for (Entity entity : list) {
            float f1 = (entity).getCollisionBorderSize();
            AxisAlignedBB axisalignedbb = (entity).getEntityBoundingBox().expand(f1, f1, f1);
            MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
            if (axisalignedbb.isVecInside(vec3)) {
                if (d2 >= 0.0D) {
                    pointedEntity = entity;
                    d2 = 0.0D;
                }
            } else if (movingobjectposition != null) {
                double d3 = vec3.distanceTo(movingobjectposition.hitVec);

                if (d3 < d2 || d2 == 0.0D) {
                    boolean flag2 = false;

                    if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                        flag2 = Reflector.callBoolean(entity, Reflector.ForgeEntity_canRiderInteract);
                    }

                    if (entity == mc.getRenderViewEntity().ridingEntity && !flag2) {
                        if (d2 == 0.0D) {
                            pointedEntity = entity;
                        }
                    } else {
                        pointedEntity = entity;
                        d2 = d3;
                    }
                }
            }
        }

        return pointedEntity;
    }

}
