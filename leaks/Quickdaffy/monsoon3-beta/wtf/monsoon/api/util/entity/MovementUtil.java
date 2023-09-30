/*
 * Decompiled with CFR 0.152.
 */
package wtf.monsoon.api.util.entity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.util.MathHelper;
import wtf.monsoon.api.util.Util;

public class MovementUtil
extends Util {
    public static final double WALK_SPEED = 0.221;
    private static final List<Double> frictionValues = new ArrayList<Double>();
    private static final double MIN_DIF = 0.01;
    public static final double BUNNY_DIV_FRICTION = 159.99;
    private static final double AIR_FRICTION = 0.98;
    private static final double WATER_FRICTION = 0.89;
    private static final double LAVA_FRICTION = 0.535;

    public static float getDirection() {
        float yaw = MathHelper.wrapAngleTo180_float(MovementUtil.mc.thePlayer.rotationYaw);
        double moveForward = MovementUtil.mc.thePlayer.moveForward;
        double moveStrafing = MovementUtil.mc.thePlayer.moveStrafing;
        if (moveForward < 0.0) {
            yaw += 180.0f;
        }
        if (moveStrafing > 0.0) {
            yaw += (float)(moveForward == 0.0 ? -90 : (moveForward > 0.0 ? -45 : 45));
        }
        if (moveStrafing < 0.0) {
            yaw += (float)(moveForward == 0.0 ? 90 : (moveForward > 0.0 ? 45 : -45));
        }
        return yaw;
    }

    public static boolean isGoingDiagonally() {
        return Math.abs(MovementUtil.mc.thePlayer.motionX) > 0.08 && Math.abs(MovementUtil.mc.thePlayer.motionZ) > 0.08;
    }

    public static double calculateFriction(double moveSpeed, double lastDist, double baseMoveSpeedRef) {
        frictionValues.clear();
        frictionValues.add(lastDist - lastDist / 159.99);
        frictionValues.add(lastDist - (moveSpeed - lastDist) / 33.3);
        double materialFriction = MovementUtil.mc.thePlayer.isInWater() ? 0.89 : (MovementUtil.mc.thePlayer.isInLava() ? 0.535 : 0.98);
        frictionValues.add(lastDist - baseMoveSpeedRef * (1.0 - materialFriction));
        return Collections.min(frictionValues);
    }
}

