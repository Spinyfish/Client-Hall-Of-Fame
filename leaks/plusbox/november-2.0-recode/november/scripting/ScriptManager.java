/* November.lol © 2023 */
package lol.november.scripting;

import java.io.File;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lol.november.feature.registry.Registry;
import lol.november.utility.fs.FileUtils;
import lol.november.utility.math.timer.Timer;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
public class ScriptManager implements Registry<Script> {

  /**
   * The API version for this scripting system
   */
  public static final String API_VERSION = "1.0.0";

  /**
   * A {@link ConcurrentHashMap} containing the {@link Script} name and its instance
   */
  private final Map<String, Script> scriptMap = new ConcurrentHashMap<>();

  /**
   * The {@link File} object pointing to the scripts folder
   */
  private File directory;

  @Override
  public void init() {
    directory = new File(FileUtils.directory, "scripts");
    if (!directory.exists()) {
      boolean result = directory.mkdir();
      log.info(
        "Created {} {}",
        directory,
        result ? "successfully" : "unsuccessfully"
      );
    }

    log.info("Loading scripts from {}", directory);
    loadScripts();
  }

  /**
   * Loads scripts from a directory
   */
  public void loadScripts() {
    File[] files = directory.listFiles();
    if (files == null || files.length == 0) {
      log.info("No scripts to load");
      return;
    }

    for (File file : files) {
      if (!file.getName().endsWith(".lua")) continue;
      loadScript(file);
    }
  }

  /**
   * Loads a specific script from a {@link File}
   *
   * @param file the {@link File} object of the script
   */
  public void loadScript(File file) {
    try {
      Timer.measure(
        () -> add(new Script(file)),
        log,
        "Script file " + file.getName() + " evaluated in {}"
      );
    } catch (Exception e) {
      log.error("Failed to load script from file {}", file);
      e.printStackTrace();
    }
  }

  /**
   * Unloads cached and running scripts
   */
  public void unloadScripts() {
    for (String scriptName : scriptMap.keySet()) {
      unloadScript(scriptMap.get(scriptName));
    }
  }

  /**
   * Unloads a specific script from a {@link File}
   *
   * @param script the {@link Script}
   */
  public void unloadScript(Script script) {
    Timer.measure(
      () -> remove(script),
      log,
      "Unloaded script file " + script.getFile() + " in {}"
    );
  }

  @Override
  public void add(Script... elements) {
    for (Script script : elements) {
      script.load();
      scriptMap.put(script.getName(), script);
    }
  }

  @Override
  public void remove(Script... elements) {
    for (Script script : elements) {
      script.unload();
      scriptMap.remove(script.getName());
    }
  }

  @Override
  public int size() {
    return scriptMap.size();
  }

  @Override
  public Collection<Script> values() {
    return scriptMap.values();
  }

  /**
   * Gets a {@link Script} via its name
   *
   * @param name the script name
   * @return the {@link Script} or null
   */
  public Script get(String name) {
    return scriptMap.getOrDefault(name, null);
  }
}
