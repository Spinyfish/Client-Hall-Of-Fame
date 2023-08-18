/* November.lol © 2023 */
package lol.november.feature.trait;

/**
 * @author Gavin
 * @since 2.0.0
 */
public interface Toggle {
  void enable();

  void disable();

  boolean toggled();

  void setState(boolean state);
}
