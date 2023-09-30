package wtf.automn.events.types;

public final class Priority {

  public static final byte
    /**
     * Highest priority, called first.
     */
    HIGHEST = 0,
  /**
   * High priority, called after the highest priority.
   */
  HIGH = 1,
  /**
   * Medium priority, called after the high priority.
   */
  MEDIUM = 2,
  /**
   * Low priority, called after the medium priority.
   */
  LOW = 3,
  /**
   * Lowest priority, called after all the other priorities.
   */
  LOWEST = 4;

  /**
   * Array containing all the prioriy values.
   */
  public static final byte[] VALUE_ARRAY;

  static {
    VALUE_ARRAY = new byte[]{
      HIGHEST,
      HIGH,
      MEDIUM,
      LOW,
      LOWEST
    };
  }

}
