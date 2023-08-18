/* November.lol © 2023 */
package lol.november.listener.bus;

/**
 * @author aesthetical
 * @since 06/11/23
 */
@FunctionalInterface
public interface Listener<T> {
  void invoke(T event);
}
