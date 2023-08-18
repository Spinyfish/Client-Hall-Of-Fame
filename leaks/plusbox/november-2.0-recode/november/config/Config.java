/* November.lol © 2023 */
package lol.november.config;

import java.io.File;
import lombok.Getter;
import lombok.SneakyThrows;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Getter
public abstract class Config {

  /**
   * The {@link File} object pointing to where to save to
   */
  private final File file;

  public Config(File file) {
    this.file = file;
  }

  /**
   * Creates the {@link File} if it does not exist
   *
   * @return the result of creating the file
   */
  @SneakyThrows
  protected boolean createIfNotExists() {
    if (!file.exists()) {
      return file.createNewFile();
    }

    return true;
  }

  /**
   * Saves this config
   */
  public abstract void save();

  /**
   * Loads this config
   */
  public abstract void load();
}
