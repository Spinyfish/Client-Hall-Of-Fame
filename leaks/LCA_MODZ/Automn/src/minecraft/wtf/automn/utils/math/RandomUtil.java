package wtf.automn.utils.math;

import java.security.SecureRandom;
import java.util.concurrent.ThreadLocalRandom;

public class RandomUtil {
    public static int nextInt(int origin, int bound) {
        if (origin == bound) {
            return origin;
        }
        return ThreadLocalRandom.current().nextInt(origin, bound);
    }
    public static long nextLong(long origin, long bound) {
        if (origin == bound) {
            return origin;
        }
        return ThreadLocalRandom.current().nextLong(origin, bound);
    }
    public static float nextFloat(double origin, double bound) {
        if (origin == bound) {
            return (float) origin;
        }
        return (float) ThreadLocalRandom.current().nextDouble((float)origin, (float)bound);
    }
    public static float nextFloat(float origin, float bound) {
        if (origin == bound) {
            return origin;
        }
        return (float) ThreadLocalRandom.current().nextDouble(origin, bound);
    }
    public static double nextDouble(double origin, double bound) {
        if (origin == bound) {
            return origin;
        }
        return ThreadLocalRandom.current().nextDouble(origin, bound);
    }
    public static double nextSecureInt(int origin, int bound) {
        if (origin == bound) {
            return origin;
        }
        SecureRandom secureRandom = new SecureRandom();
        int difference = bound - origin;
        return origin + secureRandom.nextInt(difference);
    }
    public static double nextSecureDouble(double origin, double bound) {
        if (origin == bound) {
            return origin;
        }
        SecureRandom secureRandom = new SecureRandom();
        double difference = bound - origin;
        return origin + secureRandom.nextDouble() * difference;
    }
    public static float nextSecureFloat(double origin, double bound) {
        if (origin == bound) {
            return (float) origin;
        }
        SecureRandom secureRandom = new SecureRandom();
        float difference = (float) (bound - origin);
        return (float) (origin + secureRandom.nextFloat() * difference);
    }

    public static double randomSin() {
        return Math.sin(RandomUtil.nextDouble(0, 2*Math.PI));
    }
}
