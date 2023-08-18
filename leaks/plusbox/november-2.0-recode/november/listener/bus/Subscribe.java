/* November.lol © 2023 */
package lol.november.listener.bus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author aesthetical
 * @since 06/11/23
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Subscribe {
  /**
   * The priority of this event
   *
   * @return a integer between {@link Integer#MIN_VALUE} - {@link Integer#MAX_VALUE}
   */
  int priority() default DefaultEventPriority.DEFAULT;

  /**
   * If the {@link Listener} should ignore canceled events
   *
   * @return if the {@link Listener} should ignore events that have been canceled by other {@link Listener}s
   */
  boolean ignoreCanceled() default true;
}
