/* November.lol © 2023 */
package lol.november.feature.trait;

/**
 * @author Gavin
 * @since 2.0.0
 */
public interface Feature {
  /**
   * The name of this feature
   *
   * @return the name of this feature
   */
  String name();

  /**
   * Gets the aliases for this feature
   *
   * @return the aliases for this feature
   */
  default String[] aliases() {
    return new String[0];
  }
}
