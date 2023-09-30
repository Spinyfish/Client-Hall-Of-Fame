package com.alan.clients.util.player;

import com.alan.clients.Client;
import com.alan.clients.module.impl.player.Scaffold;
import com.alan.clients.newevent.impl.input.MoveInputEvent;
import com.alan.clients.newevent.impl.other.MoveEvent;
import com.alan.clients.util.interfaces.InstanceAccess;
import com.alan.clients.util.vector.Vector2d;
import lombok.experimental.UtilityClass;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

import java.util.Arrays;

/**
 * This is a motion util which can be used to do various things related to the players motion
 *
 * @author Dort, Auth, Patrick, Alan
 * @since 21/10/2021
 */
@UtilityClass
public class MoveUtil implements InstanceAccess {

    public static final double WALK_SPEED = 0.221;
    public static final double BUNNY_SLOPE = 0.66;
    public static final double MOD_SPRINTING = 1.3F;
    public static final double MOD_SNEAK = 0.3F;
    public static final double MOD_ICE = 2.5F;
    public static final double MOD_WEB = 0.105 / WALK_SPEED;
    public static final double JUMP_HEIGHT = 0.42F;
    public static final double BUNNY_FRICTION = 159.9F;
    public static final double Y_ON_GROUND_MIN = 0.00001;
    public static final double Y_ON_GROUND_MAX = 0.0626;

    public static final double AIR_FRICTION = 0.9800000190734863D;
    public static final double WATER_FRICTION = 0.800000011920929D;
    public static final double LAVA_FRICTION = 0.5D;
    public static final double MOD_SWIM = 0.115F / WALK_SPEED;
    public static final double[] MOD_DEPTH_STRIDER = {
            1.0F,
            0.1645F / MOD_SWIM / WALK_SPEED,
            0.1995F / MOD_SWIM / WALK_SPEED,
            1.0F / MOD_SWIM,
    };

    public static final double UNLOADED_CHUNK_MOTION = -0.09800000190735147;
    public static final double HEAD_HITTER_MOTION = -0.0784000015258789;

    /**
     * Checks if the player is moving
     *
     * @return player moving
     */
    public boolean isMoving() {
        return mc.thePlayer.moveForward != 0 || mc.thePlayer.moveStrafing != 0;
    }

    /**
     * Checks if the player has enough movement input for sprinting
     *
     * @return movement input enough for sprinting
     */
    public boolean enoughMovementForSprinting() {
        return Math.abs(mc.thePlayer.moveForward) >= 0.8F || Math.abs(mc.thePlayer.moveStrafing) >= 0.8F;
    }

    /**
     * Checks if the player is allowed to sprint
     *
     * @param legit should the player follow vanilla sprinting rules?
     * @return player able to sprint
     */
    public boolean canSprint(final boolean legit) {
        return (legit ? mc.thePlayer.moveForward >= 0.8F
                && !mc.thePlayer.isCollidedHorizontally
                && (mc.thePlayer.getFoodStats().getFoodLevel() > 6 || mc.thePlayer.capabilities.allowFlying)
                && !mc.thePlayer.isPotionActive(Potion.blindness)
                && !mc.thePlayer.isUsingItem()
                && !mc.thePlayer.isSneaking()
                : enoughMovementForSprinting());
    }

    /**
     * Returns the distance the player moved in the last tick
     *
     * @return last tick distance
     */
    public double movementDelta() {
        return Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ);
    }

    public double speedPotionAmp(final double amp) {
        return mc.thePlayer.isPotionActive(Potion.moveSpeed) ? ((mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1) * amp) : 0;
    }

    /**
     * Calculates the default player jump motion
     *
     * @return player jump motion
     */
    public double jumpMotion() {
        return jumpBoostMotion(JUMP_HEIGHT);
    }

    /**
     * Modifies a selected motion with jump boost
     *
     * @param motionY input motion
     * @return modified motion
     */
    public double jumpBoostMotion(final double motionY) {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            return motionY + (mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier() + 1) * 0.1F;
        }

        return motionY;
    }

    public static javax.vecmath.Vector2d getMotion(double moveSpeed) {
        MovementInput movementInput = mc.thePlayer.movementInput;

        double moveForward = movementInput.moveForward;
        double moveStrafe = movementInput.moveStrafe;
        double rotationYaw = mc.thePlayer.movementYaw;

        if (moveForward != 0.0D || moveStrafe != 0.0D) {
            if (moveStrafe > 0.0D) {
                moveStrafe = 1.0D;
            } else if (moveStrafe < 0.0D) {
                moveStrafe = -1.0D;
            }

            if (moveForward != 0.0D) {
                if (moveStrafe > 0.0D) {
                    rotationYaw += moveForward > 0.0D ? -45 : 45;
                } else if (moveStrafe < 0.0D) {
                    rotationYaw += moveForward > 0.0D ? 45 : -45;
                }

                moveStrafe = 0.0D;

                if (moveForward > 0.0D) {
                    moveForward = 1.0D;
                } else if (moveForward < 0.0D) {
                    moveForward = -1.0D;
                }
            }

            double cos = Math.cos(Math.toRadians(rotationYaw + 90.0F));
            double sin = Math.sin(Math.toRadians(rotationYaw + 90.0F));
            return new javax.vecmath.Vector2d(moveForward * moveSpeed * cos + moveStrafe * moveSpeed * sin, moveForward * moveSpeed * sin - moveStrafe * moveSpeed * cos);
        }
        return new javax.vecmath.Vector2d(0.0D, 0.0D);
    }

    /**
     * Gets the players' depth strider modifier
     *
     * @return depth strider modifier
     */
    public int depthStriderLevel() {
        return EnchantmentHelper.getDepthStriderModifier(mc.thePlayer);
    }

    public static float fallDistanceForDamage() {
        float fallDistanceReq = 3;

        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            fallDistanceReq += (float) (amplifier + 1);
        }

        return fallDistanceReq;
    }

    /**
     * Rounds the players' position to a valid ground position
     *
     * @return valid ground position
     */
    public double roundToGround(final double posY) {
        return Math.round(posY / 0.015625) * 0.015625;
    }

    /**
     * Gets the players predicted jump motion 1 tick ahead
     *
     * @return predicted jump motion
     */
    public double predictedMotion(final double motion) {
        return (motion - 0.08) * 0.98F;
    }

    public void forward(final double speed) {
        final double yaw = direction();

        mc.thePlayer.motionX = -Math.sin(yaw) * speed;
        mc.thePlayer.motionZ = Math.cos(yaw) * speed;
    }

    /**
     * Gets the players predicted jump motion the specified amount of ticks ahead
     *
     * @return predicted jump motion
     */
    public double predictedMotion(final double motion, final int ticks) {
        if (ticks == 0) return motion;
        double predicted = motion;

        for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08) * 0.98F;
        }

        return predicted;
    }

    /**
     * Basically calculates allowed horizontal distance just like NCP does
     *
     * @return allowed horizontal distance in one tick
     */
    public double getAllowedHorizontalDistance() {
        double horizontalDistance;
        boolean useBaseModifiers = false;

        if (mc.thePlayer.isInWeb) {
            horizontalDistance = MOD_WEB * WALK_SPEED;
        } else if (PlayerUtil.inLiquid()) {
            horizontalDistance = MOD_SWIM * WALK_SPEED;

            final int depthStriderLevel = depthStriderLevel();
            if (depthStriderLevel > 0) {
                horizontalDistance *= MOD_DEPTH_STRIDER[depthStriderLevel];
                useBaseModifiers = true;
            }

        } else if (mc.thePlayer.isSneaking()) {
            horizontalDistance = MOD_SNEAK * WALK_SPEED;
        } else {
            horizontalDistance = WALK_SPEED;
            useBaseModifiers = true;
        }

        if (useBaseModifiers) {
            if (canSprint(false)) {
                horizontalDistance *= MOD_SPRINTING;
            }

            final Scaffold scaffold = Client.INSTANCE.getModuleManager().get(Scaffold.class);

            if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).duration > 0 && !(scaffold.isEnabled() && scaffold.ignoreSpeed.getValue())) {
                horizontalDistance *= 1 + (0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
            }

            if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
                horizontalDistance = 0.29;
            }
        }

        return horizontalDistance;
    }

    /**
     * Sets the players' jump motion to the specified value with random to bypass value patches
     */
    public void jumpRandom(final double motion) {
        mc.thePlayer.motionY = motion + (Math.random() / 500);
    }

    /**
     * Makes the player strafe
     */
    public void strafe() {
        strafe(speed());
    }

    /**
     * Makes the player strafe at the specified speed
     */
    public void strafe(final double speed) {
        if (!isMoving()) {
            return;
        }

        final double yaw = direction();
        mc.thePlayer.motionX = -MathHelper.sin((float) yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos((float) yaw) * speed;
    }

    public void strafe(final double speed, float yaw) {
        if (!isMoving()) {
            return;
        }

        yaw = (float) Math.toRadians(yaw);
        mc.thePlayer.motionX = -MathHelper.sin(yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
    }

    /**
     * Stops the player from moving
     */
    public void stop() {
        mc.thePlayer.motionX = 0;
        mc.thePlayer.motionZ = 0;
    }

    /**
     * Gets the players' movement yaw
     */
    public double direction() {
        float rotationYaw = mc.thePlayer.movementYaw;

        if (mc.thePlayer.moveForward < 0) {
            rotationYaw += 180;
        }

        float forward = 1;

        if (mc.thePlayer.moveForward < 0) {
            forward = -0.5F;
        } else if (mc.thePlayer.moveForward > 0) {
            forward = 0.5F;
        }

        if (mc.thePlayer.moveStrafing > 0) {
            rotationYaw -= 70 * forward;
        }

        if (mc.thePlayer.moveStrafing < 0) {
            rotationYaw += 70 * forward;
        }

        return Math.toRadians(rotationYaw);
    }

    /**
     * Gets the players' movement yaw wrapped to 90
     */
    public double wrappedDirection() {
        float rotationYaw = mc.thePlayer.movementYaw;

        if (mc.thePlayer.moveForward < 0 && mc.thePlayer.moveStrafing == 0) {
            rotationYaw += 180;
        }

        if (mc.thePlayer.moveStrafing > 0) {
            rotationYaw -= 90;
        }

        if (mc.thePlayer.moveStrafing < 0) {
            rotationYaw += 90;
        }

        return Math.toRadians(rotationYaw);
    }

    /**
     * Gets the players' movement yaw
     */
    public double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    /**
     * Used to get the players speed
     */
    public double speed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public void setMoveEvent(final MoveEvent moveEvent, final double moveSpeed) {
        setMoveEvent(moveEvent, moveSpeed, mc.thePlayer.movementYaw, mc.thePlayer.movementInput.moveStrafe, mc.thePlayer.movementInput.moveForward);
    }

    public void setMoveEvent(final MoveEvent moveEvent, final double moveSpeed, final float pseudoYaw, final double pseudoStrafe, final double pseudoForward) {
        double forward = pseudoForward;
        double strafe = pseudoStrafe;
        float yaw = pseudoYaw;

        if (forward != 0.0D) {
            if (strafe > 0.0D) {
                yaw += ((forward > 0.0D) ? -45.0F : 45.0F);
            } else if (strafe < 0.0D) {
                yaw += ((forward > 0.0D) ? 45.0F : -45.0F);
            }

            strafe = 0.0D;

            if (forward > 0.0D) {
                forward = 1.0D;
            } else if (forward < 0.0D) {
                forward = -1.0D;
            }
        }

        final double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        final double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        moveEvent.setPosX(forward * moveSpeed * mx + strafe * moveSpeed * mz);
        moveEvent.setPosZ(forward * moveSpeed * mz - strafe * moveSpeed * mx);
    }

    /**
     * Fixes the players movement
     */
    public void fixMovement(final MoveInputEvent event, final float yaw) {
        final float forward = event.getForward();
        final float strafe = event.getStrafe();

        final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction(mc.thePlayer.rotationYaw, forward, strafe)));

        if (forward == 0 && strafe == 0) {
            return;
        }

        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;

                final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MoveUtil.direction(yaw, predictedForward, predictedStrafe)));
                final double difference = Math.abs(angle - predictedAngle);

                if (difference < closestDifference) {
                    closestDifference = (float) difference;
                    closestForward = predictedForward;
                    closestStrafe = predictedStrafe;
                }
            }
        }

        event.setForward(closestForward);
        event.setStrafe(closestStrafe);
    }

    public double getMCFriction() {
        float f = 0.91F;

        if (mc.thePlayer.onGround) {
            f = mc.theWorld.getBlockState(new BlockPos(MathHelper.floor_double(mc.thePlayer.posX), MathHelper.floor_double(mc.thePlayer.getEntityBoundingBox().minY) - 1, MathHelper.floor_double(mc.thePlayer.posZ))).getBlock().slipperiness * 0.91F;
        }

        return f;
    }

    public double[] moveFlying(float strafe, float forward, final boolean onGround, final float yaw, final boolean sprinting) {
        float friction = 0.02f;
        final float playerWalkSpeed = mc.thePlayer.getAIMoveSpeed();

        if (onGround) {
            final float f4 = 0.6f * 0.91f;
            final float f = 0.16277136F / (f4 * f4 * f4);
            friction = playerWalkSpeed / 2.0f * f;
        }

        if (sprinting) {
            friction = (float) ((double) friction + ((onGround) ? (playerWalkSpeed / 2.0f) : 0.02f) * 0.3D);
        }

        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = MathHelper.sqrt_float(f);

            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;

            final float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
            final float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0F);

            final double motionX = (strafe * f2 - forward * f1);
            final double motionZ = (forward * f2 + strafe * f1);

            return new double[]{motionX, motionZ};
        }

        return null;
    }

    public Vector2d moveFlyingVec(float strafe, float forward, final boolean onGround, final float yaw, final boolean sprinting) {
        double[] values = moveFlying(strafe, forward, onGround, yaw, sprinting);
        if (values == null) return null;
        return new Vector2d(values[0], values[1]);
    }

    public Double moveFlyingSpeed(float strafe, float forward, final boolean onGround, final float yaw, final boolean sprinting) {
        double[] speed = moveFlying(strafe, forward, onGround, yaw, sprinting);

        if (speed == null) return null;

        return Math.hypot(speed[0], speed[1]);
    }

    public Double moveFlyingSpeed(final boolean sprinting) {
        double[] speed = moveFlying(0.98f, 0.98f, mc.thePlayer.onGround, 180, sprinting);

        if (speed == null) return null;

        return Math.hypot(speed[0], speed[1]);
    }

    public void partialStrafeMax(double maxStrafe) {
        double motionX = mc.thePlayer.motionX;
        double motionZ = mc.thePlayer.motionZ;

        MoveUtil.strafe();

        mc.thePlayer.motionX = motionX + Math.max(-maxStrafe, Math.min(maxStrafe, mc.thePlayer.motionX - motionX));
        mc.thePlayer.motionZ = motionZ + Math.max(-maxStrafe, Math.min(maxStrafe, mc.thePlayer.motionZ - motionZ));
    }

    public void partialStrafePercent(double percentage) {
        percentage /= 100;
        percentage = Math.min(1, Math.max(0, percentage));

        double motionX = mc.thePlayer.motionX;
        double motionZ = mc.thePlayer.motionZ;

        MoveUtil.strafe();

        mc.thePlayer.motionX = motionX + (mc.thePlayer.motionX - motionX) * percentage;
        mc.thePlayer.motionZ = motionZ + (mc.thePlayer.motionZ - motionZ) * percentage;
    }

    public double moveMaxFlying(final boolean onGround) {
        float friction = 0.02f;
        final float playerWalkSpeed = mc.thePlayer.getAIMoveSpeed() / 2;
        float strafe = 0.98f;
        float forward = 0.98f;
        float yaw = 180;

        if (onGround) {
            final float f4 = 0.6f * 0.91f;
            final float f = 0.16277136F / (f4 * f4 * f4);
            friction = playerWalkSpeed * f;
        }

        friction = (float) ((double) friction + ((onGround) ? (playerWalkSpeed) : 0.02f) * 0.3D);

        float f = strafe * strafe + forward * forward;

        if (f >= 1.0E-4F) {
            f = (float) Math.sqrt(f);

            if (f < 1.0F) {
                f = 1.0F;
            }

            f = friction / f;
            strafe = strafe * f;
            forward = forward * f;

            final float f1 = MathHelper.sin(yaw * (float) Math.PI / 180.0F);
            final float f2 = MathHelper.cos(yaw * (float) Math.PI / 180.0F);

            final double motionX = (strafe * f2 - forward * f1);
            final double motionZ = (forward * f2 + strafe * f1);

            return Math.hypot(motionX, motionZ);
        }

        return 0;
    }

    public float simulationStrafeAngle(float currentMoveYaw, float maxAngle) {
        float workingYaw;
        float target = (float) Math.toDegrees(MoveUtil.direction());

        if (Math.abs(currentMoveYaw - target) <= maxAngle) {
            currentMoveYaw = target;
        } else if (currentMoveYaw > target) {
            currentMoveYaw -= maxAngle;
        } else {
            currentMoveYaw += maxAngle;
        }

        workingYaw = currentMoveYaw;

        MoveUtil.strafe(MoveUtil.speed(), workingYaw);

        return workingYaw;
    }

    public float simulationStrafe(float currentMoveYaw) {
        double moveFlying = 0.02599999835384377;
        double friction = 0.9100000262260437;

        double lastDeltaX = mc.thePlayer.lastMotionX * friction;
        double lastDeltaZ = mc.thePlayer.lastMotionZ * friction;

        float workingYaw = currentMoveYaw;

        float target = (float) Math.toDegrees(MoveUtil.direction());

        for (int i = 0; i <= 360; i++) {

            MoveUtil.strafe(MoveUtil.speed(), currentMoveYaw);

            double deltaX = Math.abs(mc.thePlayer.motionX);
            double deltaZ = Math.abs(mc.thePlayer.motionZ);

            double minDeltaX = lastDeltaX - moveFlying;
            double minDeltaZ = lastDeltaZ - moveFlying;

            if (currentMoveYaw == target || (deltaX < minDeltaX || deltaZ < minDeltaZ)) {
                break;
            }

            workingYaw = currentMoveYaw;

            if (Math.abs(currentMoveYaw - target) <= 1) {
                currentMoveYaw = target;
            } else if (currentMoveYaw > target) {
                currentMoveYaw -= 1;
            } else {
                currentMoveYaw += 1;
            }
        }

        MoveUtil.strafe(MoveUtil.speed(), workingYaw);

        return workingYaw;
    }

    public void useDiagonalSpeed() {
        KeyBinding[] gameSettings = new KeyBinding[]{mc.gameSettings.keyBindForward, mc.gameSettings.keyBindRight, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindLeft};

        final int[] down = {0};

        Arrays.stream(gameSettings).forEach(keyBinding -> {
            down[0] = down[0] + (keyBinding.isKeyDown() ? 1 : 0);
        });

        boolean active = down[0] == 1;

        if (!active) return;

        final double groundIncrease = (0.1299999676734952 - 0.12739998266255503) + 1E-7 - 1E-8;
        final double airIncrease = (0.025999999334873708 - 0.025479999685988748) - 1E-8;
        final double increase = mc.thePlayer.onGround ? groundIncrease : airIncrease;

        moveFlying(increase);
    }

    public void moveFlying(double increase) {
        if (!MoveUtil.isMoving()) return;
        final double yaw = MoveUtil.direction();
        mc.thePlayer.motionX += -MathHelper.sin((float) yaw) * increase;
        mc.thePlayer.motionZ += MathHelper.cos((float) yaw) * increase;
    }
}
