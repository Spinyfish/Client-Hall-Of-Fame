/* November.lol © 2023 */
package lol.november.feature.module;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

import lol.november.feature.module.config.ModuleConfig;
import lol.november.feature.registry.Registry;
import lol.november.scripting.wrapper.module.LuaModule;
import lol.november.utility.reflect.Reflections;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;

/**
 * @author Gavin
 * @since 2.0.0
 */
@Log4j2
@Getter
public class ModuleRegistry implements Registry<Module> {

  /**
   * An array of features not in the client by default, but can be added later via the november websocket
   */
  private static final String[] ADDITIONAL_FEATURES = {
    "staffDetector",
    "aprilFools",
  };

  /**
   * The package representation of where modules should be
   */
  private static final String MODULE_PACKAGE =
    "lol.november.feature.module.impl";

  /**
   * A {@link LinkedHashMap} of the module class & its instance
   */
  @Getter(AccessLevel.NONE)
  private final Map<Class<? extends Module>, Module> moduleInstanceMap =
    new LinkedHashMap<>();

  /**
   * An {@link ArrayList} of {@link LuaModule} created by a {@link lol.november.scripting.Script}
   */
  private final List<LuaModule> luaModules = new ArrayList<>();

  /**
   * The {@link ModuleConfig} to load and save specifically module configs
   */
  private ModuleConfig configs;

  @Override
  @SneakyThrows
  public void init() {
    // reflect all module classes in the package
    Reflections
      .getAllClassesInPackage(MODULE_PACKAGE)
      .forEach(clazz -> {
        if (!Module.class.isAssignableFrom(clazz)) return;

        try {
          // create new instance & add to this registry
          add((Module) clazz.getConstructors()[0].newInstance());
        } catch (
          InstantiationException
          | IllegalAccessException
          | InvocationTargetException e
        ) {
          log.error("Failed to create module instance of " + clazz);
          e.printStackTrace();
        }
      });

    configs = new ModuleConfig(this);

    log.info("Discovered {} modules", size());
    // get additional features
    // November.socket().requestFeatures(ADDITIONAL_FEATURES);
  }

  @Override
  public void add(Module... elements) {
    for (Module module : elements) {
      module.init();
      moduleInstanceMap.put(module.getClass(), module);
    }
  }

  @Override
  public void remove(Module... elements) {
    for (Module module : elements) {
      moduleInstanceMap.remove(module.getClass());
    }
  }

  public <T extends Module> T get(Class<T> clazz) {
    return (T) moduleInstanceMap.get(clazz);
  }

  @Override
  public int size() {
    return moduleInstanceMap.size();
  }

  @Override
  public Collection<Module> values() {
    return moduleInstanceMap.values();
  }

  public Collection<Module> valuesInCategory(Category category) {
    return moduleInstanceMap.values().stream().filter(module -> module.getCategory() == category).collect(Collectors.toList());
  }

  /**
   * Adds a {@link LuaModule} to the module map
   *
   * @param luaModule the {@link LuaModule} object
   */
  public void addLuaModule(LuaModule luaModule) {
    if (!luaModules.contains(luaModule)) {
      add(luaModule);
      luaModules.add(luaModule);
    }
  }

  /**
   * Removes a {@link LuaModule} from the module map
   *
   * @param luaModule the {@link LuaModule} object
   */
  public void removeLuaModule(LuaModule luaModule) {
    luaModule.setState(false);
    remove(luaModule);
    luaModules.remove(luaModule);
  }
}
