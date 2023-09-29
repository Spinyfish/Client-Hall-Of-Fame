package v4n1ty.utils.misc;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;

/**
 * TimeHelper, used to time stuff using real life time, like killaura cps
 *
 * @author Alan
 * @since 13/02/2021
 */

public final class TimeUtil {

    public long lastMS = 0L;

    /**
     * Devides 1000 / d
     */
    public int convertToMS(final int d) {
        return 1000 / d;
    }

    /***
     * Gets current system time
     */
    public long getCurrentMS() {
        return System.nanoTime() / 1000000L;
    }

    public long getElapsedTime() {
        return System.currentTimeMillis() - this.lastMS;
    }

    /***
     * Checks if a timer has reached an amount of time
     */
    public boolean hasReached(final long milliseconds) {
        return getCurrentMS() - lastMS >= milliseconds;
    }

    /***
     * Gets the current amount of time that has passed since last reset
     */
    public long getDelay() {
        return System.currentTimeMillis() - lastMS;
    }

    /***
     * Resets timer
     */
    public void reset() {
        lastMS = getCurrentMS();
    }

    /***
     * Tbh I skidded this from intent and don't remember what this does
     */
    public void setLastMS() {
        lastMS = System.currentTimeMillis();
    }

    /***
     * Tbh I skidded this from intent and don't remember what this does
     */
    public void setLastMS(final long lastMS) {
        this.lastMS = lastMS;
    }

}