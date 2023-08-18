/* November.lol © 2023 */
package lol.november.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
@Log4j2
public class ConfigManager {

  /**
   * An {@link ArrayList} of {@link Config}
   */
  private final List<Config> configs = new ArrayList<>();

  private boolean loading = false;

  public ConfigManager() {
    Runtime.getRuntime().addShutdownHook(new ConfigShutdownThread(this));
  }

  /**
   * Loads configs
   */
  public void load() {
    if (loading) {
      log.warn("Attempted load configs while already loading!");
      return;
    }

    loading = true;

    for (Config config : configs) {
      try {
        config.load();
      } catch (Exception e) {
        log.error("Failed to load {}", config.getFile());
        e.printStackTrace();
      }
    }

    loading = false;
  }

  /**
   * Adds a {@link Config} object
   *
   * @param config the {@link Config} object
   */
  public void add(Config config) {
    configs.add(config);
  }
}
