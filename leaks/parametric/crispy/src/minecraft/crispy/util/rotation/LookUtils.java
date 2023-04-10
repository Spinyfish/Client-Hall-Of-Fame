package crispy.util.rotation;

import crispy.Crispy;
import crispy.features.event.impl.movement.EventStrafe;
import crispy.features.hacks.impl.combat.Aura;
import crispy.util.MinecraftUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Random;

public class LookUtils implements MinecraftUtil {

    static Minecraft mc = Minecraft.getMinecraft();

    public static float[] getRotations(Entity ent) {


        double x = ent.posX;
        double z = ent.posZ;
        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }


    public static float[] getPredictedRotations(EntityLivingBase ent) {
        double x = ent.posX + (ent.posX - ent.lastTickPosX);
        double z = ent.posZ + (ent.posZ - ent.lastTickPosZ);

        double y = ent.posY + ent.getEyeHeight() / 2.0F;
        return getRotationFromPosition(x, z, y);
    }

    public static float[] getAverageRotations(List<EntityLivingBase> targetList) {
        double posX = 0.0D;
        double posY = 0.0D;
        double posZ = 0.0D;
        for (Entity ent : targetList) {
            posX += ent.posX;
            posY += ent.boundingBox.maxY - 2.0D;
            posZ += ent.posZ;
        }
        posX /= targetList.size();
        posY /= targetList.size();
        posZ /= targetList.size();

        return new float[]{getRotationFromPosition(posX, posZ, posY)[0], getRotationFromPosition(posX, posZ, posY)[1]};
    }

    public static double round(final double value, final int places) {
        if (places < 0) {
            throw new IllegalArgumentException();
        }
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }


    public static float getStraitYaw() {
        float YAW = MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw);
        if (YAW < 45 && YAW > -45) {
            YAW = 0;
        } else if (YAW > 45 && YAW < 135) {
            YAW = 90f;
        } else if (YAW > 135 || YAW < -135) {
            YAW = 180;
        } else {
            YAW = -90f;
        }
        return YAW;
    }

    public static float getHorizontalAngleToLookVec(Vec3i vec) {
        Rotation needed = getNeededRotations(vec);
        return MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw) - needed.getYaw();
    }

    public static Rotation getNeededRotations(Vec3i vec) {
        Vec3 eyesPos = getEyesPos();

        double diffX = vec.getX() - eyesPos.getXCoord();
        double diffY = vec.getY() - eyesPos.getYCoord();
        double diffZ = vec.getZ() - eyesPos.getZCoord();

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        float yaw = (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F;
        float pitch = (float) -Math.toDegrees(Math.atan2(diffY, diffXZ));

        return new Rotation(yaw, pitch);
    }

    public static Vec3 getEyesPos() {
        EntityPlayerSP player = mc.thePlayer;

        return new Vec3(player.posX,
                player.posY + player.getEyeHeight(),
                player.posZ);
    }


    public static float[] getBowAngles(final Entity entity) {
        final double xDelta = (entity.posX - entity.lastTickPosX) * 0.4;
        final double zDelta = (entity.posZ - entity.lastTickPosZ) * 0.4;
        double d = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
        d -= d % 0.8;
        double xMulti = 1.0;
        double zMulti = 1.0;
        final boolean sprint = entity.isSprinting();
        xMulti = d / 0.8 * xDelta * (sprint ? 1.25 : 1.0);
        zMulti = d / 0.8 * zDelta * (sprint ? 1.25 : 1.0);
        final double x = entity.posX + xMulti - Minecraft.getMinecraft().thePlayer.posX;
        final double z = entity.posZ + zMulti - Minecraft.getMinecraft().thePlayer.posZ;
        final double y = Minecraft.getMinecraft().thePlayer.posY + Minecraft.getMinecraft().thePlayer.getEyeHeight() - (entity.posY + entity.getEyeHeight());
        final double dist = Minecraft.getMinecraft().thePlayer.getDistanceToEntity(entity);
        final float yaw = (float) Math.toDegrees(Math.atan2(z, x)) - 90.0f;
        double d1 = MathHelper.sqrt_double(x * x + z * z);
        final float pitch = (float) -(Math.atan2(y, d1) * 180.0D / Math.PI) + (float) dist * 0.11f;

        return new float[]{yaw, -pitch};
    }

    public static float[] getRotationFromPosition(double x, double z, double y) {
        double xDiff = x - Minecraft.getMinecraft().thePlayer.posX;
        double zDiff = z - Minecraft.getMinecraft().thePlayer.posZ;
        double yDiff = y - Minecraft.getMinecraft().thePlayer.posY - 1.2;

        double dist = MathHelper.sqrt_double(xDiff * xDiff + zDiff * zDiff);
        float yaw = (float) (Math.atan2(zDiff, xDiff) * 180.0D / 3.141592653589793D) - 90.0F;
        float pitch = (float) -(Math.atan2(yDiff, dist) * 180.0D / 3.141592653589793D);
        return new float[]{yaw, pitch};
    }

    public static float[] fixedSensitivity(float sensitivity, float yaw, float pitch) {
        float f = sensitivity * 0.6f + 0.2f;
        float gcd = f * f * f * 1.2f;
        Rotation rotation = Crispy.INSTANCE.getServerRotation();
        float deltaYaw = yaw - rotation.getYaw();
        deltaYaw -= deltaYaw % gcd;
        float fixedYaw = rotation.getYaw() + deltaYaw;
        float deltaPitch = pitch - rotation.getPitch();
        deltaPitch -= deltaPitch % gcd;
        float fixedPitch = rotation.getPitch() + deltaPitch;
        return new float[]{fixedYaw, fixedPitch};
    }

    public static Vec3 getVectorForRotation(final Rotation rotation) {
        float yawCos = (float) Math.cos(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float yawSin = (float) Math.sin(-rotation.getYaw() * 0.017453292F - (float) Math.PI);
        float pitchCos = (float) -Math.cos(-rotation.getPitch() * 0.017453292F);

        float pitchSin = (float) Math.sin(-rotation.getPitch() * 0.017453292F);
        return new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
    }

    /**
     * Apply strafe to player
     *
     * @author bestnub
     */
    public static void applyStrafeToPlayer(EventStrafe event, float yaw) {
        EntityPlayerSP player = mc.thePlayer;
        int dif = (int) ((MathHelper.wrapAngleTo180_float(player.rotationYaw - yaw - 23.5f - 135) + 180) / 45);

        float strafe = event.getStrafe();
        float forward = event.getFward();
        float friction = event.getFric();

        float calcForward = 0f;
        float calcStrafe = 0f;

        switch (dif) {
            case 0: {

                calcForward = forward;
                calcStrafe = strafe;
                break;
            }
            case 1: {

                calcForward += forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe += strafe;
                break;
            }
            case 2: {

                calcForward = strafe;
                calcStrafe = -forward;
                break;
            }
            case 3: {

                calcForward -= forward;
                calcStrafe -= forward;
                calcForward += strafe;
                calcStrafe -= strafe;
                break;
            }
            case 4: {

                calcForward = -forward;
                calcStrafe = -strafe;
                break;
            }
            case 5: {

                calcForward -= forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe -= strafe;
                break;
            }
            case 6: {

                calcForward = -strafe;
                calcStrafe = forward;
                break;
            }
            case 7: {

                calcForward += forward;
                calcStrafe += forward;
                calcForward -= strafe;
                calcStrafe += strafe;
                break;
            }
        }

        if (calcForward > 1f || calcForward < 0.9f && calcForward > 0.3f || calcForward < -1f || calcForward > -0.9f && calcForward < -0.3f) {
            calcForward *= 0.5f;
        }
        if (calcStrafe > 1f || calcStrafe < 0.9f && calcStrafe > 0.3f || calcStrafe < -1f || calcStrafe > -0.9f && calcStrafe < -0.3f) {
            calcStrafe *= 0.5f;
        }

        double d = calcStrafe * calcStrafe + calcForward * calcForward;

        if (d >= 1.0E-4f) {
            d = Math.sqrt(d);
            if (d < 1.0f) d = 1.0f;
            d = friction / d;
            calcStrafe *= d;
            calcForward *= d;
            double yawSin = Math.sin((yaw * Math.PI / 180f));
            double yawCos = Math.cos((yaw * Math.PI / 180f));
            player.motionX += calcStrafe * yawCos - calcForward * yawSin;
            player.motionZ += calcForward * yawCos + calcStrafe * yawSin;
        }
    }

    public static float getTrajAngleSolutionLow(float d3, float d1, float velocity) {
        float g = 0.006F;
        float sqrt = velocity * velocity * velocity * velocity - g * (g * (d3 * d3) + 2.0F * d1 * (velocity * velocity));
        return (float) Math.toDegrees(Math.atan((velocity * velocity - Math.sqrt(sqrt)) / (g * d3)));
    }

    public static float getYawChange(float yaw, double posX, double posZ) {
        double deltaX = posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double yawToEntity = 0;
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            if (deltaX != 0)
                yawToEntity = 90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
            if (deltaX != 0)
                yawToEntity = -90.0D + Math.toDegrees(Math.atan(deltaZ / deltaX));
        } else {
            if (deltaZ != 0)
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
        }

        return MathHelper.wrapAngleTo180_float(-(yaw - (float) yawToEntity));
    }

    public static boolean isFaced(final Entity targetEntity, double blockReachDistance) {
        return raycastEntity(blockReachDistance, entity -> entity == targetEntity) != null;
    }

    public static boolean isFaced2(final Entity targetEntity, double blockReachDistance, float yaw, float pitch) {
        return raycastEntity(blockReachDistance, entity -> entity == targetEntity) != null;
    }

    public static boolean isFaced3(final Entity targetEntity, double blockReachDistance) {
        return raycastEntity3(blockReachDistance, entity -> entity == targetEntity) != null;
    }

    public static float getPitchChange(float pitch, Entity entity, double posY) {
        double deltaX = entity.posX - Minecraft.getMinecraft().thePlayer.posX;
        double deltaZ = entity.posZ - Minecraft.getMinecraft().thePlayer.posZ;
        double deltaY = posY - 2.2D + entity.getEyeHeight() - Minecraft.getMinecraft().thePlayer.posY;
        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));
        return -MathHelper.wrapAngleTo180_float(pitch - (float) pitchToEntity) - 2.5F;
    }

    public static Entity raycastEntity(final double range, final IEntityFilter entityFilter) {

        return raycastEntity(range, Aura.targetRotation.getYaw(), Aura.targetRotation.getPitch(),
                entityFilter);
    }

    public static Entity raycastEntity3(final double range, final IEntityFilter entityFilter) {

        return raycastEntity2(range, Aura.targetRotation.getYaw(), Aura.targetRotation.getPitch(),
                entityFilter);

    }

    public static Entity raycastEntity2(final double range, final IEntityFilter entityFilter, float yaw, float pitch) {

        return raycastEntity(range, yaw, pitch,
                entityFilter);

    }

    private static Entity raycastEntity2(final double range, final float yaw, final float pitch, final IEntityFilter entityFilter) {
        final Entity renderViewEntity = mc.getRenderViewEntity();

        if (renderViewEntity != null && Minecraft.theWorld != null) {
            double blockReachDistance = range;
            final Vec3 eyePosition = renderViewEntity.getPositionEyes(1F);

            final float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
            final float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
            final float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
            final float pitchSin = MathHelper.sin(-pitch * 0.017453292F);

            final Vec3 entityLook = new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
            final Vec3 vector = eyePosition.addVector(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance);
            final List<Entity> entityList = Minecraft.theWorld.getEntitiesInAABBexcluding(renderViewEntity, renderViewEntity.getEntityBoundingBox().addCoord(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance).expand(1D, 1D, 1D), IEntitySelector.NOT_SPECTATING);

            Entity pointedEntity = null;

            for (final Entity entity : entityList) {
                if (!entityFilter.canRaycast(entity))
                    continue;

                final float collisionBorderSize = entity.getCollisionBorderSize() / 6.5f;
                final AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                final MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(eyePosition, vector);

                if (axisAlignedBB.isVecInside(eyePosition)) {
                    if (blockReachDistance >= 0.0D) {
                        pointedEntity = entity;
                        blockReachDistance = 0.0D;
                    }
                } else if (movingObjectPosition != null) {
                    final double eyeDistance = eyePosition.distanceTo(movingObjectPosition.hitVec);

                    if (eyeDistance < blockReachDistance || blockReachDistance == 0.0D) {
                        if (entity == renderViewEntity.ridingEntity && !renderViewEntity.canAttackWithItem()) {
                            if (blockReachDistance == 0.0D)
                                pointedEntity = entity;
                        } else {
                            pointedEntity = entity;
                            blockReachDistance = eyeDistance;
                        }
                    }
                }
            }

            return pointedEntity;
        }

        return null;
    }

    private static Entity raycastEntity(final double range, final float yaw, final float pitch, final IEntityFilter entityFilter) {
        final Entity renderViewEntity = mc.getRenderViewEntity();

        if (renderViewEntity != null && Minecraft.theWorld != null) {
            double blockReachDistance = range;
            final Vec3 eyePosition = renderViewEntity.getPositionEyes(1F);

            final float yawCos = MathHelper.cos(-yaw * 0.017453292F - (float) Math.PI);
            final float yawSin = MathHelper.sin(-yaw * 0.017453292F - (float) Math.PI);
            final float pitchCos = -MathHelper.cos(-pitch * 0.017453292F);
            final float pitchSin = MathHelper.sin(-pitch * 0.017453292F);

            final Vec3 entityLook = new Vec3(yawSin * pitchCos, pitchSin, yawCos * pitchCos);
            final Vec3 vector = eyePosition.addVector(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance);
            final List<Entity> entityList = Minecraft.theWorld.getEntitiesInAABBexcluding(renderViewEntity, renderViewEntity.getEntityBoundingBox().addCoord(entityLook.xCoord * blockReachDistance, entityLook.yCoord * blockReachDistance, entityLook.zCoord * blockReachDistance).expand(1D, 1D, 1D), IEntitySelector.NOT_SPECTATING);

            Entity pointedEntity = null;

            for (final Entity entity : entityList) {
                if (!entityFilter.canRaycast(entity))
                    continue;

                final float collisionBorderSize = entity.getCollisionBorderSize();
                final AxisAlignedBB axisAlignedBB = entity.getEntityBoundingBox().expand(collisionBorderSize, collisionBorderSize, collisionBorderSize);
                final MovingObjectPosition movingObjectPosition = axisAlignedBB.calculateIntercept(eyePosition, vector);

                if (axisAlignedBB.isVecInside(eyePosition)) {
                    if (blockReachDistance >= 0.0D) {
                        pointedEntity = entity;
                        blockReachDistance = 0.0D;
                    }
                } else if (movingObjectPosition != null) {
                    final double eyeDistance = eyePosition.distanceTo(movingObjectPosition.hitVec);

                    if (eyeDistance < blockReachDistance || blockReachDistance == 0.0D) {
                        if (entity == renderViewEntity.ridingEntity && !renderViewEntity.canAttackWithItem()) {
                            if (blockReachDistance == 0.0D)
                                pointedEntity = entity;
                        } else {
                            pointedEntity = entity;
                            blockReachDistance = eyeDistance;
                        }
                    }
                }
            }

            return pointedEntity;
        }

        return null;
    }

    public static float getNewAngle(float angle) {
        angle %= 360.0F;
        if (angle >= 180.0F) {
            angle -= 360.0F;
        }
        if (angle < -180.0F) {
            angle += 360.0F;
        }
        return angle;
    }

    public static boolean canEntityBeSeen(Entity e) {
        Vec3 vec1 = new Vec3(mc.thePlayer.posX, mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), mc.thePlayer.posZ);

        AxisAlignedBB box = e.getEntityBoundingBox();
        Vec3 vec2 = new Vec3(e.posX, e.posY + (e.getEyeHeight() / 1.32F), e.posZ);
        double minx = e.posX - 0.25;
        double maxx = e.posX + 0.25;
        double miny = e.posY;
        double maxy = e.posY + Math.abs(e.posY - box.maxY);
        double minz = e.posZ - 0.25;
        double maxz = e.posZ + 0.25;
        boolean see = Minecraft.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, miny, minz);
        see = Minecraft.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(minx, miny, minz);
        see = Minecraft.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if (see)
            return true;
        vec2 = new Vec3(minx, miny, maxz);
        see = Minecraft.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, miny, maxz);
        see = Minecraft.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;

        vec2 = new Vec3(maxx, maxy, minz);
        see = Minecraft.theWorld.rayTraceBlocks(vec1, vec2) == null;

        if (see)
            return true;
        vec2 = new Vec3(minx, maxy, minz);

        see = Minecraft.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(minx, maxy, maxz - 0.1);
        see = Minecraft.theWorld.rayTraceBlocks(vec1, vec2) == null;
        if (see)
            return true;
        vec2 = new Vec3(maxx, maxy, maxz);
        see = Minecraft.theWorld.rayTraceBlocks(vec1, vec2) == null;
        return see;
    }

    private static float getAngleDifference(final float a, final float b) {
        return ((((a - b) % 360F) + 540F) % 360F) - 180F;
    }

    /**
     * @param bb       AxisAligned hitbox
     * @param random   random offsets using java.util.random
     * @param predict  predicts the targets position
     * @param distance distance from the target
     * @return
     * @credit https://github.com/CCBlueX/LiquidBounce/tree/master, great rotation has some flaws but they're fixed
     */
    public static VecRotation searchCenter(final AxisAlignedBB bb, final boolean random, final boolean predict, final float distance) {
        Random random2 = new Random();
        double x = random2.nextDouble() * 0.1;
        double y = random2.nextDouble() * 0.1;
        double z = random2.nextDouble() * 0.1;
        final Vec3 randomVec = new Vec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * x * 0.1, bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * y * 0.1, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * z * 1.5);
        final Rotation randomRotation = toRotation(randomVec, predict);
        final Vec3 eyes = mc.thePlayer.getPositionEyes(1F);
        VecRotation vecRotation = null;
        for (double xSearch = 0.15D; xSearch < 0.85D; xSearch += 0.1D) {
            for (double ySearch = 0.15D; ySearch < 1D; ySearch += 0.1D) {
                for (double zSearch = 0.15D; zSearch < 0.85D; zSearch += 0.1D) {
                    final Vec3 vec3 = new Vec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * xSearch,
                            bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * ySearch, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * zSearch);
                    final Rotation rotation = toRotation(vec3, predict);
                    final double vecDist = eyes.distanceTo(vec3);
                    if (vecDist > distance)
                        continue;
                    final VecRotation currentVec = new VecRotation(vec3, rotation);
                    if (vecRotation == null || (random ? getRotationDifference(currentVec.getRotation(), randomRotation) < getRotationDifference(vecRotation.getRotation(), randomRotation) : getRotationDifference(currentVec.getRotation()) < getRotationDifference(vecRotation.getRotation())))
                        vecRotation = currentVec;
                }
            }
        }

        return vecRotation;
    }

    public static double getRotationDifference(final Rotation rotation) {
        return Aura.targetRotation == null ? 0D : getRotationDifference(rotation, Aura.targetRotation);
    }

    public static double getRotationDifference(final Rotation a, final Rotation b) {
        return Math.hypot(getAngleDifference(a.getYaw(), b.getYaw()), a.getPitch() - b.getPitch());
    }

    public static double getRotationDifference(final Entity entity) {
        final Rotation rotation = toRotation(getCenter(entity.getEntityBoundingBox()), true);

        return getRotationDifference(rotation, new Rotation(mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch));
    }

    public static Vec3 getCenter(final AxisAlignedBB bb) {
        return new Vec3(bb.getMinX() + (bb.getMaxX() - bb.getMinX()) * 0.5, bb.getMinY() + (bb.getMaxY() - bb.getMinY()) * 0.5, bb.getMinZ() + (bb.getMaxZ() - bb.getMinZ()) * 0.5);
    }

    public static Rotation toRotation(final Vec3 vec, final boolean predict) {
        final Vec3 eyesPos = new Vec3(Minecraft.getMinecraft().thePlayer.posX, Minecraft.getMinecraft().thePlayer.getEntityBoundingBox().minY +
                Minecraft.getMinecraft().thePlayer.getEyeHeight(), Minecraft.getMinecraft().thePlayer.posZ);

        if (predict)
            eyesPos.addVector(Minecraft.getMinecraft().thePlayer.motionX, Minecraft.getMinecraft().thePlayer.motionY, Minecraft.getMinecraft().thePlayer.motionZ);

        final double diffX = vec.xCoord - eyesPos.xCoord;
        final double diffY = vec.yCoord - eyesPos.yCoord;
        final double diffZ = vec.zCoord - eyesPos.zCoord;

        return new Rotation(MathHelper.wrapAngleTo180_float(
                (float) Math.toDegrees(Math.atan2(diffZ, diffX)) - 90F
        ), MathHelper.wrapAngleTo180_float(
                (float) (-Math.toDegrees(Math.atan2(diffY, Math.sqrt(diffX * diffX + diffZ * diffZ))))
        ));
    }

    public static float getDistanceBetweenAngles(float angle1, float angle2) {
        float angle = Math.abs(angle1 - angle2) % 360.0F;
        if (angle > 180.0F) {
            angle = 360.0F - angle;
        }
        return angle;
    }

    public static Rotation limitAngleChange(final Rotation currentRotation, final Rotation targetRotation, final float turnSpeed) {
        final float yawDifference = getAngleDifference(targetRotation.getYaw(), currentRotation.getYaw());
        final float pitchDifference = getAngleDifference(targetRotation.getPitch(), currentRotation.getPitch());

        return new Rotation(
                currentRotation.getYaw() + (yawDifference > turnSpeed ? turnSpeed : Math.max(yawDifference, -turnSpeed)),
                currentRotation.getPitch() + (pitchDifference > turnSpeed ? turnSpeed : Math.max(pitchDifference, -turnSpeed)
                ));
    }

    public interface IEntityFilter {
        boolean canRaycast(final Entity entity);
    }
}
