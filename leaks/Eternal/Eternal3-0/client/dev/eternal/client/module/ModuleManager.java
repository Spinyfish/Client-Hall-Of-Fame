package dev.eternal.client.module;

import dev.eternal.client.Client;
import dev.eternal.client.event.Subscribe;
import dev.eternal.client.event.events.EventKeyTyped;
import dev.eternal.client.manager.AbstractManager;
import dev.eternal.client.module.api.ModuleInfo;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.reflections.scanners.Scanners.SubTypes;

public class ModuleManager extends AbstractManager<Module> {

  @Override
  public void init() {
    Client.singleton().eventBus().register(this);
    new Reflections("dev")
        .getTypesAnnotatedWith(ModuleInfo.class)
        .stream()
        .map(this::createInstance)
        .forEach(this::add);

    this.forEach(Module::init);
  }

  private Module createInstance(Class<?> clazz) {
    try {
      return (Module) clazz.getConstructor().newInstance();
    } catch (InstantiationException | IllegalAccessException | NoSuchMethodException |
        InvocationTargetException e) {
      throw new RuntimeException(e);
    }
  }

  public Stream<Module> getEnabledModules() {
    return stream().filter(Module::isEnabled);
  }

  public Stream<Module> getModulesByCategory(Module.Category category) {
    return stream().filter(module -> module.moduleInfo().category() == category);
  }

  @SuppressWarnings("unchecked")
  public <T extends Module> T getByName(String name) {
    Optional<Module> optional = this.stream().filter(module -> module.moduleInfo().name().equalsIgnoreCase(name)).findFirst();
    return (T) optional.orElse(null);
  }

  @SuppressWarnings("unchecked")
  public <T extends Module> T getByClass(Class<? extends Module> clazz) {
    Optional<Module> optional = this.stream().filter(module -> module.getClass().isAssignableFrom(clazz)).findFirst();
    return (T) optional.orElse(null);
  }

  public Stream<Module> getIf(Predicate<Module> modulePredicate) {
    return this.stream().filter(modulePredicate);
  }

  @Subscribe
  public void handleKeyTyped(EventKeyTyped eventKeyTyped) {
    stream()
        .filter(module -> module.keyBind() == eventKeyTyped.key())
        .forEach(Module::toggle);
  }
}
