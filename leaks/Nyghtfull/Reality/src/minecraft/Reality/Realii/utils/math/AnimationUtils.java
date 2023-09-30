package Reality.Realii.utils.math;

import Reality.Realii.utils.cheats.world.TimerUtil;

public class AnimationUtils {
    private static float defaultSpeed = 0.125f;
    private TimerUtil timerUtil = new TimerUtil();
    

    public static float calculateCompensation(float target, float current, long delta, double speed) {
        float diff = current - target;
        if (delta < 1L) {
            delta = 1L;
        }
        if (delta > 1000L) {
            delta = 16L;
        }
        if ((double) diff > speed) {
            double xD = speed * (double) delta / 16.0 < 0.5 ? 0.5 : speed * (double) delta / 16.0;
            if ((current = (float) ((double) current - xD)) < target) {
                current = target;
            }
        } else if ((double) diff < -speed) {
            double xD = speed * (double) delta / 16.0 < 0.5 ? 0.5 : speed * (double) delta / 16.0;
            if ((current = (float) ((double) current + xD)) > target) {
                current = target;
            }
        } else {
            current = target;
        }
        return current;
    }

    public float mvoeUD(float current, float end, float minSpeed) {
        return moveUD(current, end, defaultSpeed, minSpeed);
    }

    public double animate(double target, double current, double speed) {
        if(timerUtil.delay(4)) {
            boolean larger;
            boolean bl = larger = target > current;
            if (speed < 0.0) {
                speed = 0.0;
            } else if (speed > 1.0) {
                speed = 1.0;
            }
            double dif = Math.max(target, current) - Math.min(target, current);
            double factor = dif * speed;
            if (factor < 0.1) {
                factor = 0.1;
            }

            current = larger ? (current += factor) : (current -= factor);
            timerUtil.reset();
        }
        return current;
    }

    public float animate(float target, float current, float speed) {
        if(timerUtil.delay(4)) {
            boolean larger;
            boolean bl = larger = target > current;
            if (speed < 0.0f) {
                speed = 0.0f;
            } else if (speed > 1.0) {
                speed = 1.0f;
            }
            float dif = Math.max(target, current) - Math.min(target, current);
            float factor = dif * speed;
            if (factor < 0.1f) {
                factor = 0.1f;
            }
            current = larger ? (current += factor) : (current -= factor);

            timerUtil.reset();
        }
        if (Math.abs(current - target) < 0.2) {
            return target;
        } else {
            return current;
        }
    }

    public float moveUD(float current, float end, float smoothSpeed, float minSpeed) {
        float movement = 0;
        if (timerUtil.delay(20)) {
            movement = (end - current) * smoothSpeed;
            if (movement > 0.0f) {
                movement = Math.max((float) minSpeed, (float) movement);
                movement = Math.min((float) (end - current), (float) movement);
            } else if (movement < 0.0f) {
                movement = Math.min((float) (-minSpeed), (float) movement);
                movement = Math.max((float) (end - current), (float) movement);
            }
            timerUtil.reset();
        }
        return current + movement;
    }

}
