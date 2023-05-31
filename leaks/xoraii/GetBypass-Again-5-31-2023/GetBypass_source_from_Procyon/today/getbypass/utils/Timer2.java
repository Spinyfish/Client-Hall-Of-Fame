// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.utils;

public class Timer2
{
    private static long lastMS;
    
    static {
        Timer2.lastMS = 0L;
    }
    
    public boolean isDelayComplete(final float f) {
        return System.currentTimeMillis() - Timer2.lastMS >= f;
    }
    
    public static long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }
    
    public void setLastMS(final long lastMS) {
        Timer2.lastMS = System.currentTimeMillis();
    }
    
    public int convertToMS(final int perSecond) {
        return 1000 / perSecond;
    }
    
    public static boolean hasReached(final long milliseconds) {
        return getCurrentMS() - Timer2.lastMS >= milliseconds;
    }
    
    public static void reset() {
        Timer2.lastMS = getCurrentMS();
    }
}
