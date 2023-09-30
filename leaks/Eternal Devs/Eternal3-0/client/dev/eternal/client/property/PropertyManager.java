package dev.eternal.client.property;

import dev.eternal.client.manager.AbstractManager;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.impl.ModeSetting;
import dev.eternal.client.property.impl.mode.Mode;

import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

public class PropertyManager extends AbstractManager<Property<?>> {

  @Override
  public void init() {
    this.stream()
        .filter(property -> property instanceof ModeSetting)
        .map(property -> (ModeSetting) property)
        .forEach(modeSetting -> modeSetting.modeList()
            .forEach(Mode::init));
  }

  @Override
  @Deprecated
  public <U extends Property<?>> U getByName(String name) {
    throw new UnsupportedOperationException("Use PropertyManager#get(IToggleable, String) instead.");
  }

  public List<Property<?>> getOfType(IToggleable toggleable, Property.PropertyType propertyType) {
    return stream().filter(property -> property.owner().equals(toggleable) && property.propertyType().equals(propertyType)).toList();
  }

  public Property<?> get(IToggleable toggleable, String name) {
    return this.getStream(toggleable)
        .filter(property -> property.name().equalsIgnoreCase(name))
        .findFirst()
        .orElse(null);
  }

  public Property<?>[] get(IToggleable toggleable) {
    return this.getStream(toggleable)
        .filter(property -> Objects.equals(property.owner(), toggleable))
        .toArray(Property[]::new);
  }

  private Property<?>[] getDisplayableSettings(IToggleable toggleable) {
    return this.getStream(toggleable)
        .filter(option -> option.visible().getAsBoolean())
        .toArray(Property[]::new);
  }

  public Stream<Property<?>> getStream(IToggleable toggleable) {
    return this.stream().filter(property -> Objects.equals(property.owner(), toggleable));
  }

}
