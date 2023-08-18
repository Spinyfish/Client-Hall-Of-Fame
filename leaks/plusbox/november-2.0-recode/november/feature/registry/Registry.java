/* November.lol © 2023 */
package lol.november.feature.registry;

import java.util.Collection;

/**
 * @param <T> the type this registry will hold
 * @author Gavin
 * @since 2.0.0
 */
public interface Registry<T> {
  /**
   * Initializes this {@link Registry}
   */
  void init();

  /**
   * Adds elements to this {@link Registry}
   *
   * @param elements the elements of {@link T}
   */
  void add(T... elements);

  /**
   * Adds elements to this {@link Registry}
   *
   * @param elements the elements of {@link T}
   */
  void remove(T... elements);

  /**
   * Gets the size of this {@link Registry}
   *
   * @return the size of the {@link Registry}
   */
  int size();

  /**
   * A {@link Collection} of {@link T}
   *
   * @return a {@link Collection} of {@link T}
   */
  Collection<T> values();
}
