package net.optifine.util;

import java.util.HashMap;
import java.util.Map;

public class TimedEvent {
    private static final Map<String, Long> mapEventTimes = new HashMap();

    public static boolean isActive(final String name, final long timeIntervalMs) {
        synchronized (mapEventTimes) {
            final long i = System.currentTimeMillis();
            Long olong = mapEventTimes.get(name);

            if (olong == null) {
                olong = new Long(i);
                mapEventTimes.put(name, olong);
            }

            final long j = olong.longValue();

            if (i < j + timeIntervalMs) {
                return false;
            } else {
                mapEventTimes.put(name, new Long(i));
                return true;
            }
        }
    }
}
