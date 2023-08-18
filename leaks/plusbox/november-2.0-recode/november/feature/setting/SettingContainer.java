/* November.lol © 2023 */
package lol.november.feature.setting;

import java.util.Collection;

/**
 * @author Gavin
 * @since 2.0.0
 */
public interface SettingContainer {
  /**
   * Loads settings into this {@link SettingContainer}
   */
  void init();

  /**
   * Gets a setting by name
   *
   * @param name the name of the setting
   * @param <T>  the type the {@link Setting} holds
   * @return the {@link Setting} object or null
   */
  <T> Setting<T> get(String name);

  /**
   * A {@link Collection} of {@link Setting}
   *
   * @return the {@link Collection} of {@link Setting}
   */
  Collection<Setting<?>> settings();
}
