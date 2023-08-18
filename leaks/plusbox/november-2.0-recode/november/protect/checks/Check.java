/* November.lol © 2023 */
package lol.november.protect.checks;

/**
 * @author Gavin
 * @since 2.0.0
 */
public interface Check {
  /**
   * Checks for whatever
   *
   * @return if the check passed or not
   */
  boolean check();

  /**
   * The name of this check
   *
   * @return the check name
   */
  String name();
}
