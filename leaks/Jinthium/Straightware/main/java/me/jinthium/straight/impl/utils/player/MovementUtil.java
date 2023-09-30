package me.jinthium.straight.impl.utils.player;

import me.jinthium.straight.api.util.MinecraftInstance;
import me.jinthium.straight.impl.event.game.MoveInputEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveEvent;
import me.jinthium.straight.impl.event.movement.PlayerMoveUpdateEvent;
import me.jinthium.straight.impl.modules.combat.TargetStrafe;
import net.minecraft.block.Block;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.potion.Potion;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MovementInput;

import javax.vecmath.Vector2f;
import java.util.Arrays;


public class MovementUtil implements MinecraftInstance {

    public static final double WALK_SPEED = 0.221;
    public static final float LILYPAD_VALUE = 0.015625F;
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

    public static void stop(){
        mc.thePlayer.motionX *= 0;
        mc.thePlayer.motionZ *= 0;
    }

    public static boolean canFall() {
        return mc.thePlayer.isEntityAlive() && mc.theWorld != null && !mc.thePlayer.isInWater() && !mc.thePlayer.isInLava()
                && PlayerUtil.isBlockUnderNoCollisions();
    }


    public static boolean isOnGround(double height) {
        return !mc.theWorld.getCollidingBoundingBoxes(mc.thePlayer, mc.thePlayer.getEntityBoundingBox().offset(0, -height, 0)).isEmpty();
    }

    public static double direction(float rotationYaw, final double moveForward, final double moveStrafing) {
        if (moveForward < 0F) rotationYaw += 180F;

        float forward = 1F;

        if (moveForward < 0F) forward = -0.5F;
        else if (moveForward > 0F) forward = 0.5F;

        if (moveStrafing > 0F) rotationYaw -= 90F * forward;
        if (moveStrafing < 0F) rotationYaw += 90F * forward;

        return Math.toRadians(rotationYaw);
    }

    public static void fixMovement(final MoveInputEvent event, final float yaw) {
        final float forward = event.getForward();
        final float strafe = event.getStrafe();

        final double angle = MathHelper.wrapAngleTo180_double(Math.toDegrees(MovementUtil.direction(mc.thePlayer.rotationYaw, forward, strafe)));

        if (forward == 0 && strafe == 0) {
            return;
        }

        float closestForward = 0, closestStrafe = 0, closestDifference = Float.MAX_VALUE;

        for (float predictedForward = -1F; predictedForward <= 1F; predictedForward += 1F) {
            for (float predictedStrafe = -1F; predictedStrafe <= 1F; predictedStrafe += 1F) {
                if (predictedStrafe == 0 && predictedForward == 0) continue;

                final double predictedAngle = MathHelper.wrapAngleTo180_double(Math.toDegrees(direction(yaw, predictedForward, predictedStrafe)));
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

    public static double[] getXZ(final double moveSpeed) {
        double forward = mc.thePlayer.movementInput.moveForward;
        double strafe = mc.thePlayer.movementInput.moveStrafe;
        float yaw = mc.thePlayer.rotationYaw;

        if (forward != 0.0) {
            if (strafe > 0.0) {
                yaw += ((forward > 0.0) ? -45 : 45);
            } else if (strafe < 0.0) {
                yaw += ((forward > 0.0) ? 45 : -45);
            }
            strafe = 0.0F;
            if (forward > 0.0) {
                forward = 1F;
            } else if (forward < 0.0) {
                forward = -1F;
            }
        }

        if (strafe > 0.0) {
            strafe = 1F;
        } else if (strafe < 0.0) {
            strafe = -1F;
        }
        double mx = Math.cos(Math.toRadians((yaw + 90.0F)));
        double mz = Math.sin(Math.toRadians((yaw + 90.0F)));
        double x = (forward * moveSpeed * mx + strafe * moveSpeed * mz);
        double z = (forward * moveSpeed * mz - strafe * moveSpeed * mx);
        return new double[]{x, z};
    }

    public static void setMotionCustomStrafe(PlayerMoveUpdateEvent handler, double hDist, double strafeComponent, boolean safe) {
        float remainder = 1.0F - (float) strafeComponent;
        if (safe && mc.thePlayer.isMoving() && mc.thePlayer.onGround)
            setMotion(hDist);
        else {
            mc.thePlayer.motionX *= strafeComponent;
            mc.thePlayer.motionZ *= strafeComponent;
            handler.setFriction((float) (hDist * remainder));
        }
    }
    
    public static Block block(BlockPos blockPos){
        return mc.theWorld.getBlockState(blockPos).getBlock();
    }

    public static Block block(double x, double y, double z){
        return mc.theWorld.getBlockState(new BlockPos(x, y, z)).getBlock();
    }

    public static void setMotionSmoothStrafe(PlayerMoveUpdateEvent handler, double hDist, double strafeSmoothing) {
        float remainder = (float) (1F - strafeSmoothing);
        if (mc.thePlayer.onGround) {
            setMotion(hDist);
        } else {
            mc.thePlayer.motionX *= strafeSmoothing;
            mc.thePlayer.motionZ *= strafeSmoothing;
            handler.setFriction((float) (hDist * remainder));
        }
    }

    public static void setMotion(double hDist) {
        EntityPlayerSP playerInstance = mc.thePlayer;

        MovementInput movementInput = playerInstance.movementInput;
        double moveForward = movementInput.moveForward;
        double moveStrafe = movementInput.moveStrafe;

        Motion motion = getMotion(hDist, moveForward, moveStrafe, playerInstance.rotationYaw);
        playerInstance.motionX = motion.getMotionX();
        playerInstance.motionZ = motion.getMotionZ();
    }

    public static Motion getMotion(double hDist, double moveForward, double moveStrafe, float rotationYaw) {
        if (moveForward != 0.0D || moveStrafe != 0.0D) {
            if (moveStrafe > 0) {
                moveStrafe = 1;
            } else if (moveStrafe < 0) {
                moveStrafe = -1;
            }
            if (moveForward != 0.0D) {
                if (moveStrafe > 0.0D) {
                    rotationYaw += moveForward > 0.0D ? -35 : 35;
                } else if (moveStrafe < 0.0D) {
                    rotationYaw += moveForward > 0.0D ? 35 : -35;
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
            return new Motion(moveForward * hDist * cos
                    + moveStrafe * hDist * sin, 0, moveForward * hDist * sin
                    - moveStrafe * hDist * cos);
        }
        return new Motion(0, 0, 0);
    }

    public static float getMoveYaw(float yaw) {
        Vector2f from = new Vector2f((float) mc.thePlayer.lastTickPosX, (float) mc.thePlayer.lastTickPosZ),
                to = new Vector2f((float) mc.thePlayer.posX, (float) mc.thePlayer.posZ),
                diff = new Vector2f(to.x - from.x, to.y - from.y);

        double x = diff.x, z = diff.y;
        if (x != 0 && z != 0) {
            yaw = (float) Math.toDegrees((Math.atan2(-x, z) + MathHelper.PI2) % MathHelper.PI2);
        }
        return yaw;
    }

    public final double UNLOADED_CHUNK_MOTION = -0.09800000190735147;
    public final double HEAD_HITTER_MOTION = -0.0784000015258789;

    public static double getBPS() {
        double bps = (Math.hypot(mc.thePlayer.posX - mc.thePlayer.prevPosX, mc.thePlayer.posZ - mc.thePlayer.prevPosZ) * mc.timer.timerSpeed) * 20;
        return Math.round(bps * 100.0) / 100.0;
    }

    public static boolean inLiquid() {
        return mc.thePlayer.isInWater() || mc.thePlayer.isInLava();
    }

    public static Block blockRelativeToPlayer(final double offsetX, final double offsetY, final double offsetZ) {
        return mc.theWorld.getBlockState(new BlockPos(mc.thePlayer).add(offsetX, offsetY, offsetZ)).getBlock();
    }

    public static double predictedMotion(final double motion) {
        return (motion - 0.08) * 0.98F;
    }

    public static double predictedMotion(final double motion, final int ticks) {
        if (ticks == 0) return motion;
        double predicted = motion;

        for (int i = 0; i < ticks; i++) {
            predicted = (predicted - 0.08) * 0.98F;
        }

        return predicted;
    }

    public static double getAllowedHorizontalDistance() {
        double horizontalDistance;
        boolean useBaseModifiers = false;

        if (mc.thePlayer.isInWeb) {
            horizontalDistance = MOD_WEB * WALK_SPEED;
        } else if (MovementUtil.inLiquid()) {
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
            if (mc.thePlayer.isSprinting()) {
                horizontalDistance *= MOD_SPRINTING;
            }
            
            if (mc.thePlayer.isPotionActive(Potion.moveSpeed) && mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getDuration() > 0) {
                horizontalDistance *= 1 + (0.2 * (mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier() + 1));
            }

            if (mc.thePlayer.isPotionActive(Potion.moveSlowdown)) {
                horizontalDistance = 0.29;
            }
        }

        return horizontalDistance;
    }

    public static int depthStriderLevel() {
        return EnchantmentHelper.getDepthStriderModifier(mc.thePlayer);
    }


    public static void useDiagonalSpeed() {
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

    public static void useDiagonalSpeed(PlayerMoveUpdateEvent event) {
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

//        mc.thePlayer.motionX *= mc.thePlayer.motionZ *= 0;
        event.setFriction((float) (event.getFriction() - increase));
    }

    public static void moveFlying(double increase) {
        if (!mc.thePlayer.isMoving()) return;
        final double yaw = getMovementDirection();
        mc.thePlayer.motionX += -MathHelper.sin((float) yaw) * increase;
        mc.thePlayer.motionZ += MathHelper.cos((float) yaw) * increase;
    }

    public static double getMovementDirection() {
        return getMovementDirection(mc.thePlayer.rotationYaw);
    }

    public static float getMovementDirection1() {
        return getMovementDirection1(mc.thePlayer.rotationYaw);
    }

    public static float getMovementDirection1(final float yaw) {
        final float forward = mc.thePlayer.moveForward;
        final float strafe = mc.thePlayer.moveStrafing;
        final boolean forwards = forward > 0;
        final boolean backwards = forward < 0;
        final boolean right = strafe > 0;
        final boolean left = strafe < 0;
        float direction = 0;
        if (backwards)
            direction += 180;
        direction += forwards ? (right ? -45 : left ? 45 : 0) : backwards ? (right ? 45 : left ? -45 : 0) : (right ? -90 : left ? 90 : 0);
        direction += yaw;
        return MathHelper.wrapAngleTo180_float(direction);
    }

    public static double getMovementDirection(final float yaw) {
            float rotationYaw = yaw;

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

    public static double getBaseMoveSpeed() {
        return getBaseMoveSpeed(true);
    }

    public static double getBaseMoveSpeed(boolean sprint) {
        double baseSpeed = (sprint) ? 0.2873 : 0.22;
        if ((mc.thePlayer != null && mc.thePlayer.isPotionActive(Potion.moveSpeed)) && sprint) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.moveSpeed).getAmplifier();
            baseSpeed *= 1.0 + 0.2 * (amplifier + 1);
        }
        return baseSpeed;
    }

    public static double getJumpHeight(double height) {
        if (mc.thePlayer.isPotionActive(Potion.jump)) {
            int amplifier = mc.thePlayer.getActivePotionEffect(Potion.jump).getAmplifier();
            return height + (amplifier + 1) * 0.1F;
        }
        return height;
    }

    public static float simulationStrafeAngle(float currentMoveYaw, float maxAngle) {
        float workingYaw;
        float target = (float) Math.toDegrees(MovementUtil.getMovementDirection());

        if (Math.abs(currentMoveYaw - target) <= maxAngle) {
            currentMoveYaw = target;
        } else if (currentMoveYaw > target) {
            currentMoveYaw -= maxAngle;
        } else {
            currentMoveYaw += maxAngle;
        }

        workingYaw = currentMoveYaw;

        MovementUtil.strafe(MovementUtil.getSpeed(), workingYaw);

        return workingYaw;
    }

    public static float simulationStrafe(float currentMoveYaw) {
        double moveFlying = 0.02599999835384377;
        double friction = 0.9100000262260437;

        double lastDeltaX = mc.thePlayer.lastMotionX * friction;
        double lastDeltaZ = mc.thePlayer.lastMotionZ * friction;

        float workingYaw = currentMoveYaw;

        float target = (float) Math.toDegrees(MovementUtil.getMovementDirection());

        for (int i = 0; i <= 360; i++) {

            MovementUtil.strafe(MovementUtil.getSpeed(), currentMoveYaw);

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

        MovementUtil.strafe(MovementUtil.getSpeed(), workingYaw);

        return workingYaw;
    }
    
    public static void setSpeed(double speed) {
        EntityPlayerSP player = mc.thePlayer;
        setSpeed( speed, player.moveForward, player.moveStrafing, player.rotationYaw);
    }

    public static void strafe() {
        strafe(getSpeed());
    }

    /**
     * Makes the player strafe at the specified speed
     */
    public static void strafe(final double speed) {
        if (!mc.thePlayer.isMoving()) {
            return;
        }

        final double yaw = getMovementDirection();
        mc.thePlayer.motionX = -MathHelper.sin((float) yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos((float) yaw) * speed;
    }

    public static void strafe(final double speed, float yaw) {
        if (!mc.thePlayer.isMoving()) {
            return;
        }

        yaw = (float) Math.toRadians(yaw);
        mc.thePlayer.motionX = -MathHelper.sin(yaw) * speed;
        mc.thePlayer.motionZ = MathHelper.cos(yaw) * speed;
    }

    public static void setSpeed(double speed, float forward, float strafing, float yaw) {
        if (forward == 0.0F && strafing == 0.0F) return;

        boolean reversed = forward < 0.0f;
        float strafingYaw = 90.0f *
                (forward > 0.0f ? 0.5f : reversed ? -0.5f : 1.0f);

        if (reversed)
            yaw += 180.0f;
        if (strafing > 0.0f)
            yaw -= strafingYaw;
        else if (strafing < 0.0f)
            yaw += strafingYaw;

        double x = Math.cos(Math.toRadians(yaw + 90.0f));
        double z = Math.cos(Math.toRadians(yaw));

        mc.thePlayer.motionX = x * speed;
        mc.thePlayer.motionZ = z * speed;
    }

    public static void setSpeed(final PlayerMoveEvent event, double speed) {
        EntityPlayerSP player = mc.thePlayer;
        setSpeed(event, speed, player.moveForward, player.moveStrafing, player.rotationYaw);
    }

    public static void setSpeed(double speed, float yaw) {
        EntityPlayerSP player = mc.thePlayer;
        setSpeed(speed, player.moveForward, player.moveStrafing, yaw);
    }

    public static double getSpeed() {
        return Math.hypot(mc.thePlayer.motionX, mc.thePlayer.motionZ);
    }

    public static void setSpeed(PlayerMoveEvent event, double speed, float forward, float strafing, float yaw) {
        if (forward == 0.0F && strafing == 0.0F) return;

        boolean reversed = forward < 0.0f;
        float strafingYaw = 90.0f *
                (forward > 0.0f ? 0.5f : reversed ? -0.5f : 1.0f);

        if (reversed)
            yaw += 180.0f;
        if (strafing > 0.0f)
            yaw -= strafingYaw;
        else if (strafing < 0.0f)
            yaw += strafingYaw;

        double x = Math.cos(Math.toRadians(yaw + 90.0f));
        double z = Math.cos(Math.toRadians(yaw));

        event.setX(x * speed);
        event.setZ(z * speed);
    }
    
    public static class Motion {
        private double motionX, motionY, motionZ;

        public Motion(double x, double y, double z){
            this.motionX = x;
            this.motionY = y;
            this.motionZ = z;
        }
        
        public void setMotionX(double motionX) {
            this.motionX = motionX;
        }

        public void setMotionY(double motionY) {
            this.motionY = motionY;
        }

        public void setMotionZ(double motionZ) {
            this.motionZ = motionZ;
        }

        public double getMotionX() {
            return motionX;
        }

        public double getMotionY() {
            return motionY;
        }

        public double getMotionZ() {
            return motionZ;
        }
    }
}
