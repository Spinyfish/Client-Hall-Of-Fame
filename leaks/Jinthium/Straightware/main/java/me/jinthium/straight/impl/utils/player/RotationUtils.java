package me.jinthium.straight.impl.utils.player;

import com.google.common.base.Predicates;
import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.impl.event.movement.PlayerUpdateEvent;
import me.jinthium.straight.impl.utils.math.MathUtils;
import me.jinthium.straight.impl.utils.vector.Vector2f;
import me.jinthium.straight.impl.utils.vector.Vector3d;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.util.*;

import java.util.List;

public class RotationUtils implements MinecraftInstance {
    private static final double RAD_TO_DEG = 180.0 / Math.PI;
    private static final double DEG_TO_RAD = Math.PI / 180.0;

    public static Vec3 getVecRotations(float yaw, float pitch) {
        double d = Math.cos(Math.toRadians(-yaw) - Math.PI);
        double d1 = Math.sin(Math.toRadians(-yaw) - Math.PI);
        double d2 = -Math.cos(Math.toRadians(-pitch));
        double d3 = Math.sin(Math.toRadians(-pitch));
        return new Vec3(d1 * d2, d3, d * d2);
    }

    public float[] getCurrentRotations(){
        return new float[] {mc.thePlayer.currentEvent.getYaw(), mc.thePlayer.currentEvent.getPitch()};
    }

    public static Vector2f getCurrentRotationsv(){
        return new Vector2f(mc.thePlayer.currentEvent.getYaw(), mc.thePlayer.currentEvent.getPitch());
    }

    public static boolean overBlock(final Vector2f rotation, final EnumFacing enumFacing, final BlockPos pos, final boolean strict) {
        final MovingObjectPosition movingObjectPosition = mc.thePlayer.rayTraceCustom(4.5f, rotation.x, rotation.y);

        if (movingObjectPosition == null) return false;

        final Vec3 hitVec = movingObjectPosition.hitVec;
        if (hitVec == null) return false;

        return movingObjectPosition.getBlockPos().equals(pos) && (!strict || movingObjectPosition.sideHit == enumFacing);
    }


    public static void setVisualRotations(float yaw, float pitch){
        mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYawHead = yaw;
        mc.thePlayer.rotationPitchHead = pitch;
    }

    public static void setVisualRotations(float[] rotations){
        setVisualRotations(rotations[0], rotations[1]);
    }

    public static void setRotations(PlayerUpdateEvent event, float yaw, float pitch, double rotationSpeed, boolean allowInvalidPitch){
        Vector2f smoothedRots = smooth(mc.thePlayer.getPreviousRotation(), new Vector2f(yaw, pitch), rotationSpeed, allowInvalidPitch);
        event.setYaw(smoothedRots.x);
        event.setPitch(smoothedRots.y);
        setVisualRotations(smoothedRots.x, smoothedRots.y);
    }

    public static void setRotations(PlayerUpdateEvent event, float[] rotations, double rotationSpeed){
        setRotations(event, rotations[0], rotations[1], rotationSpeed, false);
    }

    public static void setRotations(PlayerUpdateEvent event, Vector2f rotations, double rotationSpeed){
        setRotations(event, rotations.x, rotations.y, rotationSpeed, false);
    }

    public static void setRotationsNoLimitsv(PlayerUpdateEvent event, Vector2f rotations, double rotationSpeed){
        setRotations(event, rotations.getX(), rotations.getY(), rotationSpeed, true);
    }

    public static void setRotationsNoLimitsf(PlayerUpdateEvent event, float[] rotations, double rotationSpeed){
        setRotations(event, rotations[0], rotations[1], rotationSpeed, true);
    }

    public static void setVisualRotations(Vector2f rotations) {
        setVisualRotations(rotations.x, rotations.y);
    }

    public static Vector2f resetRotation(final Vector2f rotation) {
        if (rotation == null) {
            return null;
        }

        final float yaw = rotation.x + MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationYaw - rotation.x);
        final float pitch = mc.thePlayer.rotationPitch;
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f applySensitivityPatch(final Vector2f rotation, final Vector2f previousRotation) {
        final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F + 0.2F);
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
        final float yaw = previousRotation.x + (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
        final float pitch = previousRotation.y + (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
        return new Vector2f(yaw, MathHelper.clamp_float(pitch, -90, 90));
    }

    public static Vector2f applySensitivityPatch(final Vector2f rotation) {
        final Vector2f previousRotation = mc.thePlayer.getPreviousRotation();
        final float mouseSensitivity = (float) (mc.gameSettings.mouseSensitivity * (1 + Math.random() / 10000000) * 0.6F + 0.2F);
        final double multiplier = mouseSensitivity * mouseSensitivity * mouseSensitivity * 8.0F * 0.15D;
        final float yaw = previousRotation.x + (float) (Math.round((rotation.x - previousRotation.x) / multiplier) * multiplier);
        final float pitch = previousRotation.y + (float) (Math.round((rotation.y - previousRotation.y) / multiplier) * multiplier);
        return new Vector2f(yaw, pitch);
    }
    public static Vector2f smooth(final Vector2f lastRotation, final Vector2f targetRotation, final double speed, boolean allowInvalidPitch) {
        float yaw = targetRotation.x;
        float pitch = targetRotation.y;
        final float lastYaw = lastRotation.x;
        final float lastPitch = lastRotation.y;

        if (speed != 0) {
            final float rotationSpeed = (float) speed;

            final double deltaYaw = MathHelper.wrapAngleTo180_float(targetRotation.x - lastRotation.x);
            final double deltaPitch = pitch - lastPitch;

            final double distance = Math.sqrt(deltaYaw * deltaYaw + deltaPitch * deltaPitch);
            final double distributionYaw = Math.abs(deltaYaw / distance);
            final double distributionPitch = Math.abs(deltaPitch / distance);

            final double maxYaw = rotationSpeed * distributionYaw;
            final double maxPitch = rotationSpeed * distributionPitch;

            final float moveYaw = (float) Math.max(Math.min(deltaYaw, maxYaw), -maxYaw);
            final float movePitch = (float) Math.max(Math.min(deltaPitch, maxPitch), -maxPitch);

            yaw = lastYaw + moveYaw;
            pitch = lastPitch + movePitch;

            for (int i = 1; i <= (int) (Minecraft.getDebugFPS() / 20f + Math.random() * 10); ++i) {

                if (Math.abs(moveYaw) + Math.abs(movePitch) > 1) {
                    yaw += (Math.random() - 0.5) / 1000;
                    pitch -= Math.random() / 200;
                }

                /*
                 * Fixing GCD
                 */
                final Vector2f rotations = new Vector2f(yaw, pitch);
                final Vector2f fixedRotations = applySensitivityPatch(rotations);

                /*
                 * Setting rotations
                 */
                yaw = fixedRotations.x;
                pitch = allowInvalidPitch ? fixedRotations.y : Math.max(-90, Math.min(90, fixedRotations.y));
            }
        }

        return new Vector2f(yaw, pitch);
    }

    public static Vector2f calculate(final Vec3 to, final EnumFacing enumFacing) {
        return calculate(new Vector3d(to.xCoord, to.yCoord, to.zCoord), enumFacing);
    }

    public static Vector2f calculate(final Vec3 to) {
        return calculate(mc.thePlayer.getCustomPositionVector().add(0, mc.thePlayer.getEyeHeight(), 0), new Vector3d(to.xCoord, to.yCoord, to.zCoord));
    }

    public static Vector2f calculate(final Entity entity) {
        return calculate(entity.getCustomPositionVector().add(0, Math.max(0, Math.min(mc.thePlayer.posY - entity.posY +
                mc.thePlayer.getEyeHeight(), (entity.getEntityBoundingBox().maxY - entity.getEntityBoundingBox().minY) * 0.9)), 0));
    }

    public static Vector2f calculate(final Vector3d to) {
        return calculate(mc.thePlayer.getCustomPositionVector().add(0, mc.thePlayer.getEyeHeight(), 0), to);
    }

    public static Vector2f calculate(final Vector3d from, final Vector3d to) {
        final Vector3d diff = to.subtract(from);
        final double distance = Math.hypot(diff.getX(), diff.getZ());
        final float yaw = (float) (MathHelper.atan2(diff.getZ(), diff.getX()) * MathUtils.TO_DEGREES) - 90.0F;
        final float pitch = (float) (-(MathHelper.atan2(diff.getY(), distance) * MathUtils.TO_DEGREES));
        return new Vector2f(yaw, pitch);
    }

    public static Vector2f calculate(final Vector3d position, final EnumFacing enumFacing) {
        double x = position.getX() + 0.5D;
        double y = position.getY() + 0.5D;
        double z = position.getZ() + 0.5D;

        x += (double) enumFacing.getDirectionVec().getX() * 0.5D;
        y += (double) enumFacing.getDirectionVec().getY() * 0.5D;
        z += (double) enumFacing.getDirectionVec().getZ() * 0.5D;
        return calculate(new Vector3d(x, y, z));
    }

    public static float getYaw(Vec3 to) {
        float x = (float) (to.xCoord - mc.thePlayer.posX);
        float z = (float) (to.zCoord - mc.thePlayer.posZ);
        float var1 = (float) (StrictMath.atan2(z, x) * 180.0D / StrictMath.PI) - 90.0F;
        float rotationYaw = mc.thePlayer.rotationYaw;
        return rotationYaw + MathHelper.wrapAngleTo180_float(var1 - rotationYaw);
    }

    public static MovingObjectPosition rayTraceBlocks(final Minecraft mc,
                                               final Vec3 src,
                                               final double reach,
                                               final float yaw,
                                               final float pitch) {
        return mc.theWorld.rayTraceBlocks(src,
                getDstVec(src, yaw, pitch, reach),
                false,
                false,
                true);
    }


    public static MovingObjectPosition rayTraceBlocks(final Minecraft mc,
                                               final float yaw,
                                               final float pitch) {
        return rayTraceBlocks(mc, getHitOrigin(mc.thePlayer), mc.playerController.getBlockReachDistance(), yaw, pitch);
    }

    public static MovingObjectPosition rayCast(final Vector2f rotation, final double range) {
        return rayCast(rotation, range, 0);
    }

    public static MovingObjectPosition rayCast(final Vector2f rotation, final double range, final float expand) {
        return rayCast(rotation, range, expand, mc.thePlayer);
    }

    public static MovingObjectPosition rayCast(final Vector2f rotation, final double range, final float expand, Entity entity) {
        final float partialTicks = mc.timer.renderPartialTicks;
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

    public float calculateYawFromSrcToDst(final float yaw,
                                          final double srcX,
                                          final double srcZ,
                                          final double dstX,
                                          final double dstZ) {
        final double xDist = dstX - srcX;
        final double zDist = dstZ - srcZ;
        final float var1 = (float) (Math.atan2(zDist, xDist) * 180.0 / Math.PI) - 90.0F;
        return yaw + MathHelper.wrapAngleTo180_float(var1 - yaw);
    }

    public static Vec3 getAttackHitVec(final Minecraft mc,
                                final Vec3 src,
                                final AxisAlignedBB boundingBox,
                                final Vec3 desiredHitVec,
                                final boolean ignoreBlocks,
                                final int maxRayTraces) {
        // Validate that closest hit vec is legit
        if (validateHitVec(mc, src, desiredHitVec, ignoreBlocks))
            return desiredHitVec;

        // If not find a better hit vec
        double closestDist = Double.MAX_VALUE;
        Vec3 bone = null;

        final double xWidth = boundingBox.maxX - boundingBox.minX;
        final double zWidth = boundingBox.maxZ - boundingBox.minZ;
        final double height = boundingBox.maxY - boundingBox.minY;

        int passes = 0;

        for (double x = 0.0; x < 1.0; x += 0.2) {
            for (double y = 0.0; y < 1.0; y += 0.2) {
                for (double z = 0.0; z < 1.0; z += 0.2) {
                    if (maxRayTraces != -1 && passes > maxRayTraces) return null;

                    final Vec3 hitVec = new Vec3(boundingBox.minX + xWidth * x,
                            boundingBox.minY + height * y,
                            boundingBox.minZ + zWidth * z);

                    final double dist;
                    if (validateHitVec(mc, src, hitVec, ignoreBlocks) &&
                            (dist = src.distanceTo(hitVec)) < closestDist) {

                        closestDist = dist;
                        bone = hitVec;
                    }

                    passes++;
                }
            }
        }

        return bone;
    }


    public static boolean validateHitVec(final Minecraft mc,
                                  final Vec3 src,
                                  final Vec3 dst,
                                  final boolean ignoreBlocks) {
        // Max vanilla penetration range
        return validateHitVec(mc, src, dst, ignoreBlocks, 2.8);
    }


    public static boolean validateHitVec(final Minecraft mc,
                                  final Vec3 src,
                                  final Vec3 dst,
                                  final boolean ignoreBlocks,
                                  final double penetrationDist) {
        final Vec3 blockHitVec = rayTraceHitVec(mc, src, dst);
        // If not return closest
        if (blockHitVec == null) return true;
        // Get the distance of the hit (a.k.a the reach)
        final double distance = src.distanceTo(dst);
        // If ignoreBlocks & distance passed < penetrationDist use the closest
        return ignoreBlocks && distance < penetrationDist;
    }

    public static Vec3 rayTraceHitVec(final Minecraft mc,
                               final Vec3 src,
                               final Vec3 dst) {
        final MovingObjectPosition rayTraceResult = mc.theWorld.rayTraceBlocks(src, dst,
                false,
                false,
                false);

        return rayTraceResult != null ? rayTraceResult.hitVec : null;
    }


    public static Vec3 getCenterPointOnBB(final AxisAlignedBB hitBox,
                                   final double progressToTop) {
        final double xWidth = hitBox.maxX - hitBox.minX;
        final double zWidth = hitBox.maxZ - hitBox.minZ;
        final double height = hitBox.maxY - hitBox.minY;
        return new Vec3(hitBox.minX + xWidth / 2.0, hitBox.minY + height * progressToTop, hitBox.minZ + zWidth / 2.0);
    }

    public static AxisAlignedBB getHittableBoundingBox(final Entity entity,
                                                final double boundingBoxScale) {
        if(entity == null)
            return mc.thePlayer.getEntityBoundingBox().expand(boundingBoxScale, boundingBoxScale, boundingBoxScale);
        return entity.getEntityBoundingBox().expand(boundingBoxScale, boundingBoxScale, boundingBoxScale);
    }

    public static AxisAlignedBB getHittableBoundingBox(final Entity entity) {
        return getHittableBoundingBox(entity, entity.getCollisionBorderSize());
    }

    public static MovingObjectPosition calculateIntercept(final AxisAlignedBB boundingBox,
                                                   final Vec3 src,
                                                   final float yaw,
                                                   final float pitch,
                                                   final double reach) {
        return boundingBox.calculateIntercept(src, getDstVec(src, yaw, pitch, reach));
    }

    public static Vec3 getPointedVec(final float yaw,
                              final float pitch) {
        final double theta = -Math.cos(-pitch * DEG_TO_RAD);

        return new Vec3(Math.sin(-yaw * DEG_TO_RAD - Math.PI) * theta,
                Math.sin(-pitch * DEG_TO_RAD),
                Math.cos(-yaw * DEG_TO_RAD - Math.PI) * theta);
    }

    public static Vec3 getDstVec(final Vec3 src,
                          final float yaw,
                          final float pitch,
                          final double reach) {
        final Vec3 rotationVec = getPointedVec(yaw, pitch);
        return src.addVector(rotationVec.xCoord * reach,
                rotationVec.yCoord * reach,
                rotationVec.zCoord * reach);
    }


    public static float[] calculateAverageRotations(final List<float[]> rotations) {
        final int n = rotations.size();

        final float[] tRots = new float[2];

        for (final float[] rotation : rotations) {
            tRots[0] += rotation[0];
            tRots[1] += rotation[1];
        }

        tRots[0] /= n;
        tRots[1] /= n;

        return tRots;
    }

    public static Vec3 getHitOrigin(final Entity entity) {
        return new Vec3(entity.posX, entity.posY + 1.62F, entity.posZ);
    }

    public static Vec3 getClosestPoint(final Vec3 start,
                                final AxisAlignedBB boundingBox) {
        final double closestX = start.xCoord >= boundingBox.maxX ? boundingBox.maxX :
                start.xCoord <= boundingBox.minX ? boundingBox.minX :
                        boundingBox.minX + (start.xCoord - boundingBox.minX);

        final double closestY = start.yCoord >= boundingBox.maxY ? boundingBox.maxY :
                start.yCoord <= boundingBox.minY ? boundingBox.minY :
                        boundingBox.minY + (start.yCoord - boundingBox.minY);

        final double closestZ = start.zCoord >= boundingBox.maxZ ? boundingBox.maxZ :
                start.zCoord <= boundingBox.minZ ? boundingBox.minZ :
                        boundingBox.minZ + (start.zCoord - boundingBox.minZ);

        return new Vec3(closestX, closestY, closestZ);
    }

    public static double getMouseGCD() {
        final float sens = Minecraft.getMinecraft().gameSettings.mouseSensitivity * 0.6F + 0.2F;
        final float pow = sens * sens * sens * 8.0F;
        return (double) pow * 0.15;
    }


    public static float[] getRotations(double posX, double posY, double posZ) {
        double x = posX - mc.thePlayer.posX, z = posZ - mc.thePlayer.posZ, y = posY - (mc.thePlayer.getEyeHeight() + mc.thePlayer.posY);
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (MathHelper.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(y, d3) * 180.0D / Math.PI));
        return new float[]{yaw, pitch};
    }


    public static float[] getRotations(BlockPos blockPos, EnumFacing enumFacing) {
        double d = (double) blockPos.getX() + 0.5 - mc.thePlayer.posX + (double) enumFacing.getFrontOffsetX() * 0.25;
        double d2 = (double) blockPos.getZ() + 0.5 - mc.thePlayer.posZ + (double) enumFacing.getFrontOffsetZ() * 0.25;
        double d3 = mc.thePlayer.posY + (double) mc.thePlayer.getEyeHeight() - blockPos.getY() - (double) enumFacing.getFrontOffsetY() * 0.25;
        double d4 = MathHelper.sqrt_double(d * d + d2 * d2);
        float f = (float) (Math.atan2(d2, d) * 180.0 / Math.PI) - 90.0f;
        float f2 = (float) (Math.atan2(d3, d4) * 180.0 / Math.PI);
        return new float[]{MathHelper.wrapAngleTo180_float(f), f2};
    }

    public static float getEnumRotations(EnumFacing facing) {
        float yaw = 0;
        if (facing == EnumFacing.NORTH) {
            yaw = 0;
        }
        if (facing == EnumFacing.EAST) {
            yaw = 90;
        }
        if (facing == EnumFacing.WEST) {
            yaw = -90;
        }
        if (facing == EnumFacing.SOUTH) {
            yaw = 180;
        }
        return yaw;
    }

    public static float[] getRotations(final float[] lastRotations,
                                final float smoothing,
                                final Vec3 start,
                                final Vec3 dst) {
        // Get rotations from start - dst
        final float[] rotations = getRotations(start, dst);
        // Apply smoothing to them
        Vector2f rots = smooth(mc.thePlayer.getPreviousRotation(), new Vector2f(rotations[0], rotations[1]), smoothing, false);
        return new float[]{rots.x, rots.y};
    }


    public static Vector2f getBlockRotations(Vec3 vector) {
        if (vector == null || mc.thePlayer == null) return new Vector2f(0, 0);

        Vec3 eyesPos = new Vec3(
                mc.thePlayer.posX,
                mc.thePlayer.posY + mc.thePlayer.getEyeHeight(),
                mc.thePlayer.posZ
        );

        double diffX = vector.xCoord - eyesPos.xCoord;
        double diffY = vector.yCoord - eyesPos.yCoord;
        double diffZ = vector.zCoord - eyesPos.zCoord;

        double diffXZ = Math.sqrt(diffX * diffX + diffZ * diffZ);

        return new Vector2f(
                (float) MathHelper.wrapAngleTo180_double(Math.toDegrees(Math.atan2(diffZ, diffX)) - 90f),
                (float) MathHelper.wrapAngleTo180_double(-Math.toDegrees(Math.atan2(diffY, diffXZ)))
        );
    }

    public static Vector2f getBlockRotations(BlockPos blockPos) {
        if (blockPos == null || mc.thePlayer == null) {
            return new Vector2f(0, 0);
        }
        double deltaX = (blockPos.getX() + 0.5) - mc.thePlayer.posX;
        double deltaY = (blockPos.getY() + 0.5) - (1.6D + 0.2125) - 0.4D - mc.thePlayer.posY;
        double deltaZ = (blockPos.getZ() + 0.5) - mc.thePlayer.posZ;

        double distanceXZ = MathHelper.sqrt_double(deltaX * deltaX + deltaZ * deltaZ);
        double pitchToEntity = -Math.toDegrees(Math.atan(deltaY / distanceXZ));

        double yawToEntity;
        double v = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if ((deltaZ < 0.0D) && (deltaX < 0.0D)) {
            yawToEntity = 90.0D + v;
        } else {
            if ((deltaZ < 0.0D) && (deltaX > 0.0D)) {
                yawToEntity = -90.0D + v;
            } else {
                yawToEntity = Math.toDegrees(-Math.atan(deltaX / deltaZ));
            }
        }
        float yawChangeToEntity = MathHelper.wrapAngleTo180_float(-(mc.thePlayer.rotationYaw - (float) yawToEntity));
        float pitchChangeToEntity = -MathHelper.wrapAngleTo180_float(mc.thePlayer.rotationPitch - (float) pitchToEntity);

        return new Vector2f(yawChangeToEntity + mc.thePlayer.rotationYaw, pitchChangeToEntity + mc.thePlayer.rotationPitch);
    }

    public static void applySmoothing(final float[] lastRotations,
                               final float smoothing,
                               final float[] dstRotation) {
        if (smoothing > 0.0F) {
            final float yawChange = MathHelper.wrapAngleTo180_float(dstRotation[0] - lastRotations[0]);
            final float pitchChange = MathHelper.wrapAngleTo180_float(dstRotation[1] - lastRotations[1]);

            final float smoothingFactor = Math.max(1.0F, smoothing / 10.0F);

            dstRotation[0] = lastRotations[0] + yawChange / smoothingFactor;
            dstRotation[1] = Math.max(Math.min(90.0F, lastRotations[1] + pitchChange / smoothingFactor), -90.0F);
        }
    }

    public static float[] getRotations(final Vec3 start,
                                final Vec3 dst) {
        final double xDif = dst.xCoord - start.xCoord;
        final double yDif = dst.yCoord - start.yCoord;
        final double zDif = dst.zCoord - start.zCoord;

        final double distXZ = Math.sqrt(xDif * xDif + zDif * zDif);

        return new float[]{
                (float) (Math.atan2(zDif, xDif) * RAD_TO_DEG) - 90.0F,
                (float) (-(Math.atan2(yDif, distXZ) * RAD_TO_DEG))
        };
    }

    public float[] getFacingRotations2(final int paramInt1, final double d, final int paramInt3) {
        final EntitySnowball localEntityPig = new EntitySnowball(Minecraft.getMinecraft().theWorld);
        localEntityPig.posX = paramInt1 + 0.5;
        localEntityPig.posY = d + 0.5;
        localEntityPig.posZ = paramInt3 + 0.5;
        return getRotationsNeeded(localEntityPig);
    }

    public float[] getRotationsNeeded(final Entity entity) {
        if (entity == null) {
            return null;
        }

        final double xSize = entity.posX - mc.thePlayer.posX;
        final double ySize = entity.posY + entity.getEyeHeight() / 2 - (mc.thePlayer.posY + mc.thePlayer.getEyeHeight());
        final double zSize = entity.posZ - mc.thePlayer.posZ;
        final double theta = MathHelper.sqrt_double(xSize * xSize + zSize * zSize);
        final float yaw = (float) (Math.atan2(zSize, xSize) * 180 / Math.PI) - 90;
        final float pitch = (float) (-(Math.atan2(ySize, theta) * 180 / Math.PI));
        return new float[]{(mc.thePlayer.rotationYaw + MathHelper.wrapAngleTo180_float(yaw - mc.thePlayer.rotationYaw)) % 360, (mc.thePlayer.rotationPitch + MathHelper.wrapAngleTo180_float(pitch - mc.thePlayer.rotationPitch)) % 360.0f};
    }

    public float smoothRotation(float from, float to, float speed) {
        float f = MathHelper.wrapAngleTo180_float(to - from);

        if (f > speed) {
            f = speed;
        }

        if (f < -speed) {
            f = -speed;
        }

        return from + f;
    }

    public float[] getSmoothRotations(EntityLivingBase entity) {
        float f1 = mc.gameSettings.mouseSensitivity * 0.6F + 0.2F;
        float fac = f1 * f1 * f1 * 256.0F;

        double x = entity.posX - mc.thePlayer.posX;
        double z = entity.posZ - mc.thePlayer.posZ;
        double y = entity.posY + entity.getEyeHeight()
                - (mc.thePlayer.getEntityBoundingBox().minY
                + (mc.thePlayer.getEntityBoundingBox().maxY
                - mc.thePlayer.getEntityBoundingBox().minY));

        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float) (MathHelper.atan2(z, x) * 180.0 / Math.PI) - 90.0F;
        float pitch = (float) (-(MathHelper.atan2(y, d3) * 180.0 / Math.PI));
        yaw = smoothRotation(mc.thePlayer.prevRotationYawHead, yaw, fac * MathUtils.getRandomFloat(0.9F, 1));
        pitch = smoothRotation(mc.thePlayer.prevRotationPitchHead, pitch, fac * MathUtils.getRandomFloat(0.7F, 1));

        return new float[]{yaw, pitch};
    }

    public static boolean isMouseOver(final float yaw, final float pitch, final Entity target, final float range) {
        final float partialTicks = mc.timer.renderPartialTicks;
        final Entity entity = mc.getRenderViewEntity();
        MovingObjectPosition objectMouseOver;
        Entity mcPointedEntity = null;

        if (entity != null && mc.theWorld != null) {

            mc.mcProfiler.startSection("pick");
            final double d0 = mc.playerController.getBlockReachDistance();
            objectMouseOver = entity.rayTrace(d0, partialTicks);
            double d1 = d0;
            final Vec3 vec3 = entity.getPositionEyes(partialTicks);
            final boolean flag = d0 > (double) range;

            if (objectMouseOver != null) {
                d1 = objectMouseOver.hitVec.distanceTo(vec3);
            }

            final Vec3 vec31 = mc.thePlayer.getVectorForRotation(pitch, yaw);
            final Vec3 vec32 = vec3.addVector(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0);
            Entity pointedEntity = null;
            Vec3 vec33 = null;
            final float f = 1.0F;
            final List<Entity> list = mc.theWorld.getEntitiesInAABBexcluding(entity, entity.getEntityBoundingBox().addCoord(vec31.xCoord * d0, vec31.yCoord * d0, vec31.zCoord * d0).expand(f, f, f), Predicates.and(EntitySelectors.NOT_SPECTATING, Entity::canBeCollidedWith));
            double d2 = d1;

            for (final Entity entity1 : list) {
                final float f1 = entity1.getCollisionBorderSize();
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

            if (pointedEntity != null && flag && vec3.distanceTo(vec33) > (double) range) {
                pointedEntity = null;
                objectMouseOver = new MovingObjectPosition(MovingObjectPosition.MovingObjectType.MISS, vec33, null, new BlockPos(vec33));
            }

            if (pointedEntity != null && (d2 < d1 || objectMouseOver == null)) {
                if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
                    mcPointedEntity = pointedEntity;
                }
            }

            mc.mcProfiler.endSection();

            return mcPointedEntity == target;
        }

        return false;
    }
}
