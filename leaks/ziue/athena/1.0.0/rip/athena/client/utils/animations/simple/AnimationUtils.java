package rip.athena.client.utils.animations.simple;

public class AnimationUtils
{
    public static float calculateCompensation(final float target, float current, final double speed, final long delta) {
        final float diff = current - target;
        final double add = delta * (speed / 50.0);
        if (diff > speed) {
            if (current - add > target) {
                current -= (float)add;
            }
            else {
                current = target;
            }
        }
        else if (diff < -speed) {
            if (current + add < target) {
                current += (float)add;
            }
            else {
                current = target;
            }
        }
        else {
            current = target;
        }
        return current;
    }
}
