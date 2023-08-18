/* November.lol © 2023 */
package lol.november.feature.module;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Register {
  /**
   * The name of the {@link Module}
   *
   * @return the {@link Module} name
   */
  String name();

  /**
   * The description for the {@link Module}
   *
   * @return the description of the {@link Module}
   */
  String description() default "No description was provided";

  /**
   * The {@link Category} for the {@link Module}
   *
   * @return the {@link Category} for the {@link Module}
   */
  Category category();
}
