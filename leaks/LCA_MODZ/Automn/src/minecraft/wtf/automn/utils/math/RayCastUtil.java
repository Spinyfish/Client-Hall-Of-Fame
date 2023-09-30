package wtf.automn.utils.math;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.*;
import net.optifine.reflect.Reflector;
import viamcp.ViaMCP;
import viamcp.gui.GuiProtocolSelector;
import wtf.automn.utils.interfaces.MC;

import java.util.List;

public class RayCastUtil implements wtf.automn.utils.interfaces.MC {

    public static void getMouseOver(float partialTicks, double range, boolean rayCast, Entity target) {
        Entity entity = mc.getRenderViewEntity();
        if (entity != null && mc.theWorld != null) {
            mc.mcProfiler.startSection("pick");
            mc.pointedEntity = null;
            double d0 = (double) mc.playerController.getBlockReachDistance();
            d0 = Math.max(d0, range);
            mc.objectMouseOver = entity.rayTrace(d0, partialTicks);

            double d1 = d0;
            Vec3 vec3 = entity.getPositionEyes(partialTicks);
            boolean flag = false;
            boolean flag1 = true;

            if (d0 > 3.0D) {
                flag = true;
            }
            d0 = d0;
            if (mc.objectMouseOver != null) {
                d1 = mc.objectMouseOver.hitVec.distanceTo(vec3);
            }
            d0 = Math.max(d0, range);
            d1 = Math.max(d1, range);

            Vec3 vec31 = entity.getLook(partialTicks);
            Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            mc.entityRenderer.pointedEntity = null;
            Vec3 vec33 = null;
            float f = 1.0F;
            List list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand((double) f, (double) f, (double) f), Predicates.and(EntitySelectors.NOT_SPECTATING));
            double d2 = d1;
            if (!rayCast) {
                float f1 = target.getCollisionBorderSize();
                AxisAlignedBB axisalignedbb = target.getEntityBoundingBox().expand((double) f1, (double) f1, (double) f1);
                MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                if (axisalignedbb.isVecInside(vec3)) {
                    if (d2 >= 0.0D) {
                        mc.entityRenderer.pointedEntity = target;
                        vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                        d2 = 0.0D;
                    }
                } else if (movingobjectposition != null) {
                    double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                    if (d3 < d2 || d2 == 0.0D) {
                        boolean flag2 = false;
                        if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                            flag2 = Reflector.callBoolean(target, Reflector.ForgeEntity_canRiderInteract, new Object[0]);
                        }
                        if (target == entity.ridingEntity && !flag2) {
                            if (d2 == 0.0D) {
                                mc.entityRenderer.pointedEntity = target;
                                vec33 = movingobjectposition.hitVec;
                            }
                        } else {
                            mc.entityRenderer.pointedEntity = target;
                            vec33 = movingobjectposition.hitVec;
                            d2 = d3;
                        }
                    }
                }
            } else {
                for (int i = 0; i < list.size(); ++i) {
                    Entity entity1 = (Entity) list.get(i);
                    float f1 = entity1.getCollisionBorderSize();
                    AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().expand((double) f1, (double) f1, (double) f1);
                    MovingObjectPosition movingobjectposition = axisalignedbb.calculateIntercept(vec3, vec32);
                    if (axisalignedbb.isVecInside(vec3)) {
                        if (d2 >= 0.0D) {
                            mc.entityRenderer.pointedEntity = entity1;
                            vec33 = movingobjectposition == null ? vec3 : movingobjectposition.hitVec;
                            d2 = 0.0D;
                        }
                    } else if (movingobjectposition != null) {
                        double d3 = vec3.distanceTo(movingobjectposition.hitVec);
                        if (d3 < d2 || d2 == 0.0D) {
                            boolean flag2 = false;
                            if (Reflector.ForgeEntity_canRiderInteract.exists()) {
                                flag2 = Reflector.callBoolean(entity1, Reflector.ForgeEntity_canRiderInteract, new Object[0]);
                            }
                            if (entity1 == entity.ridingEntity && !flag2) {
                                if (d2 == 0.0D) {
                                    mc.entityRenderer.pointedEntity = entity1;
                                    vec33 = movingobjectposition.hitVec;
                                }
                            } else {
                                mc.entityRenderer.pointedEntity = entity1;
                                vec33 = movingobjectposition.hitVec;
                                d2 = d3;
                            }
                        }
                    }
                }
            }

            if (mc.entityRenderer.pointedEntity != null && flag && vec3.distanceTo(vec33) > range) {
                mc.entityRenderer.pointedEntity = null;
                mc.objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, (EnumFacing) null, new BlockPos(vec33));
            }
            if (mc.entityRenderer.pointedEntity != null && (d2 < d1 || mc.objectMouseOver == null)) {
                mc.objectMouseOver = new MovingObjectPosition(mc.entityRenderer.pointedEntity, vec33);

                if (mc.entityRenderer.pointedEntity instanceof EntityLivingBase || mc.entityRenderer.pointedEntity instanceof EntityItemFrame) {
                    mc.pointedEntity = mc.entityRenderer.pointedEntity;
                }
            }
            mc.mcProfiler.endSection();
        }
    }
}
