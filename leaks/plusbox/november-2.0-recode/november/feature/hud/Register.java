/* November.lol © 2023 */
package lol.november.feature.hud;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Register {
  /**
   * The name of the {@link HUDElement}
   *
   * @return the name
   */
  String value();

  /**
   * The default state of this {@link HUDElement}
   *
   * @return the default state
   */
  boolean state() default false;
}
