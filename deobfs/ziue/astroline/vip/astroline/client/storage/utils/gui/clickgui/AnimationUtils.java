/*
 * Decompiled with CFR 0.152.
 */
package vip.astroline.client.storage.utils.gui.clickgui;

public class AnimationUtils {
    public static float rotateDirection = 0.0f;
    public static boolean animationDone = true;
    public static double delta;

    public static float getAnimationState(float animation, float finalState, float speed) {
        float add = (float)(delta * (double)(speed / 1000.0f));
        animation = animation < finalState ? (animation + add < finalState ? (animation += add) : finalState) : (animation - add > finalState ? (animation -= add) : finalState);
        return animation;
    }

    public static float smoothAnimation(float ani, float finalState, float speed, float scale) {
        return AnimationUtils.getAnimationState(ani, finalState, Math.max(10.0f, Math.abs(ani - finalState) * speed) * scale);
    }

    public static float getRotateDirection() {
        if (!((rotateDirection += (float)delta) > 360.0f)) return rotateDirection;
        rotateDirection = 0.0f;
        return rotateDirection;
    }
}
