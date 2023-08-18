/* November.lol © 2023 */
package lol.november.config;

import java.io.IOException;
import java.util.List;
import lol.november.November;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin
 * @since 1.0.0
 */
@Log4j2
public class ConfigShutdownThread extends Thread {

  private final ConfigManager configs;

  public ConfigShutdownThread(ConfigManager configs) {
    this.configs = configs;
    setName("November Config Saver");
  }

  @Override
  public void run() {
    List<Config> cfgs = configs.getConfigs();
    if (cfgs.isEmpty()) {
      log.info("No configs to save");
      return;
    }

    for (Config config : cfgs) {
      try {
        config.save();
        log.info("Saved {}", config.getFile());
      } catch (Exception e) {
        log.error("Failed to save {}", config.getFile());
        e.printStackTrace();
      }
    }

    try {
      November.instance().modules().getConfigs().saveDefault();
      log.info(
        "Saved {}",
        November.instance().modules().getConfigs().getDefaultConfig()
      );
    } catch (IOException e) {
      log.error("Failed to save default config");
      e.printStackTrace();
    }
  }
}
