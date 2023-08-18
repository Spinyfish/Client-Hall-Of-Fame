/* November.lol © 2023 */
package lol.november.utility.math.timer;

import org.apache.logging.log4j.Logger;

/**
 * @author Gavin
 * @since 2.0.0
 */
public class Timer {

  private static final long NS_TO_MS = 1_000_000L;

  /**
   * The last time set by {@link System#nanoTime()}
   */
  private long lastSetTime;

  public Timer() {
    // reset the timer
    reset();
  }

  /**
   * Checks if time in milliseconds has passed
   *
   * @param ms    the time in milliseconds
   * @param reset if to automatically reset the timer
   * @return if the time in milliseconds has passed
   */
  public boolean passed(long ms, boolean reset) {
    boolean passed = elapsedTimeMs() > ms;
    if (passed && reset) reset();
    return passed;
  }

  /**
   * Checks if time in milliseconds has passed
   *
   * @param ms the time in milliseconds
   * @return if the time in milliseconds has passed
   */
  public boolean passed(long ms) {
    return passed(ms, false);
  }

  /**
   * Resets this timer to the current {@link System#nanoTime()}
   */
  public void reset() {
    lastSetTime = System.nanoTime();
  }

  /**
   * Gets the elapsed time in nanoseconds
   *
   * @return the elapsed time in nanoseconds
   */
  public long elapsedTime() {
    return System.nanoTime() - lastSetTime;
  }

  /**
   * Gets the elapsed time in milliseconds
   *
   * @return the elapsed time in milliseconds
   */
  public long elapsedTimeMs() {
    return elapsedTime() / NS_TO_MS;
  }

  public static void measure(Runnable runnable, Logger logger, String format) {
    Timer timer = new Timer();
    runnable.run();
    long time = timer.elapsedTimeMs();
    logger.info(format, time + "ms");
  }
}
