/*
 * Copyright (c) 2023. Syncinus / Liticane
 * You cannot redistribute these files
 * without my written permission.
 */

package io.github.raze.utilities.collection.math;

public class TimeUtil {

    public long lastMS = System.currentTimeMillis();

    public void reset()
    {
        lastMS = System.currentTimeMillis();
    }

    public boolean elapsed(long time, boolean reset) {

        if (System.currentTimeMillis() - lastMS > time) {
            if (reset) {
                reset();
            }

            return true;
        }

        return false;
    }

    public boolean elapsed(long time) {
        return System.currentTimeMillis() - lastMS > time;
    }

}
