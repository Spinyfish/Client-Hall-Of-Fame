package org.crystalpvp.anticheat.api.util;

public class TimesUtil {

    public static long differenceTimeMillis(long st ,long et){
        long timeDifference = et - st;
        return timeDifference;
    }

    public static long differenceTimeSecond(long st ,long et){
        long timeDifference = et - st;
        return timeDifference / 1000;
    }

    public static long differenceTimeMinutes(long st ,long et){
        long timeDifference = et - st;
        return timeDifference / 1000 / 60;
    }

    public static long differenceTimeHour(long st ,long et){
        long timeDifference = et - st;
        return timeDifference / 1000 / 60 / 24;
    }

}
