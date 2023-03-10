package store.femboy.spring.impl.util;

import java.util.Random;

public final class MathUtil {
    public static int getRandInt(final int min, final int max) {
        return new Random().nextInt(max - min + 1) + min;
    }
    public static int getRandFloat(final float min, final float max){
        return (int) (new Random().nextInt((int)max - (int)min + 1) + min);
    }
}
