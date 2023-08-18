/* November.lol © 2023 */
package lol.november.feature.module.config;

import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import lol.november.November;
import lol.november.feature.module.Module;
import lol.november.feature.module.ModuleRegistry;
import lol.november.utility.fs.FileUtils;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
@Getter
public class ModuleConfig {

  /**
   * A {@link SimpleDateFormat} to format dates for the config metadata
   */
  private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(
    "MM/dd/y"
  );

  /**
   * The default config name
   */
  private static final String DEFAULT_CONFIG = "default";

  /**
   * The config extension
   */
  private static final String EXTENSION = ".november";

  /**
   * A {@link File} of the module configs
   */
  private final File directory;

  /**
   * A {@link File} to the default config
   */
  private final File defaultConfig;

  /**
   * The {@link ModuleRegistry} to configure
   */
  @Getter(AccessLevel.NONE)
  private final ModuleRegistry modules;

  public ModuleConfig(ModuleRegistry modules)
    throws IOException, IllegalStateException {
    this.modules = modules;

    directory = new File(FileUtils.directory, "configs");
    if (!directory.exists()) {
      boolean result = directory.mkdir();
      log.info(
        "Created {} {}",
        directory,
        result ? "successfully" : "unsuccessfully"
      );
    }

    defaultConfig = resolve(DEFAULT_CONFIG + EXTENSION);
    if (!defaultConfig.exists()) {
      boolean result = defaultConfig.createNewFile();
      log.info(
        "Created {} {}",
        defaultConfig,
        result ? "successfully" : "unsuccessfully"
      );

      return;
    }

    // load the default config
    load(DEFAULT_CONFIG);
  }

  /**
   * Loads a module config
   *
   * @param name the name of the config
   */
  public void load(String name)
    throws FileNotFoundException, IllegalStateException {
    File file = resolve(name);
    if (!file.exists()) throw new FileNotFoundException(
      "File " + file + " does not exist"
    );

    // read from file & validate
    String content = FileUtils.readFile(file);
    if (content == null || content.isEmpty()) throw new IllegalStateException(
      "File " + file + " should not be empty"
    );

    JsonObject object = FileUtils.jsonParser.parse(content).getAsJsonObject();
    for (Module module : modules.values()) {
      if (!object.has(module.name())) {
        log.warn("{} did not have a {} entry", file, module.name());
        continue;
      }

      module.fromJson(object.get(module.name()));
    }
  }

  /**
   * Saves the default config
   */
  public void saveDefault() throws IOException {
    save(DEFAULT_CONFIG);
  }

  /**
   * Saves a module config
   *
   * @param name the name of the config
   */
  public void save(String name) throws IOException {
    File file = resolve(name);
    if (!file.exists()) {
      boolean result = file.createNewFile();
      log.info(
        "Created {} {}",
        file,
        result ? "successfully" : "unsuccessfully"
      );
    }

    JsonObject object = new JsonObject();
    object.add("meta", metadata(System.currentTimeMillis()));

    for (Module module : modules.values()) {
      object.add(module.name(), module.toJson());
    }

    // write to file
    FileUtils.writeFile(file, FileUtils.gson.toJson(object));
  }

  /**
   * Gets the config metadata
   *
   * @param timeMs the creation time in milliseconds
   * @return a {@link JsonObject} containing the meta data
   */
  private JsonObject metadata(long timeMs) {
    JsonObject meta = new JsonObject();
    meta.addProperty("version", November.fullVersion());
    meta.addProperty("username", "DEVELOPER_CFG");
    meta.addProperty("date", DATE_FORMAT.format(timeMs));
    return meta;
  }

  /**
   * Resolves the {@link File} where the config is
   *
   * @param name the name of the config
   * @return the {@link File} object pointing to the config
   */
  private File resolve(String name) {
    if (!name.endsWith(EXTENSION)) name += EXTENSION;
    return new File(directory, name);
  }
}
