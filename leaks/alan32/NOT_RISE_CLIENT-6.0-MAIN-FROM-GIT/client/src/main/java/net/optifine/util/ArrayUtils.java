package net.optifine.util;

public class ArrayUtils {
    public static boolean contains(final Object[] arr, final Object val) {
        if (arr == null) {
            return false;
        } else {
            for (int i = 0; i < arr.length; ++i) {
                final Object object = arr[i];

                if (object == val) {
                    return true;
                }
            }

            return false;
        }
    }
}
