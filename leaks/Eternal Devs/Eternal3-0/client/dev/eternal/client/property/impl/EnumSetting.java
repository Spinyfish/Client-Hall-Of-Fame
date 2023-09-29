package dev.eternal.client.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.Property;
import dev.eternal.client.property.impl.interfaces.INameable;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class EnumSetting<T extends INameable> extends Property<T> {

  private int index;
  private final List<T> enumList;

  @SafeVarargs
  public EnumSetting(IToggleable owner, String name, T... modes) {
    super(owner, name, modes[0], PropertyType.ENUM);
    this.enumList = Arrays.asList(modes);
    index = 0;
  }

  public void setValue(String value) {
    T val = enumList.stream()
        .filter(mode -> mode.getName().equalsIgnoreCase(value))
        .findFirst()
        .orElse(this.enumList.get(0));
    index = enumList.indexOf(val);
    value(val);
  }

  @Override
  public EnumSetting<T> value(T value) {
    super.value(value);
    index = enumList().indexOf(value());
    return this;
  }

  public void cycle() {
    if (index++ >= enumList.size() - 1) index = 0;
    this.value(enumList.get(index));
  }

  @Override
  public JsonElement getJsonElement() {
    return new JsonPrimitive(index);
  }

  @Override
  public void loadJsonElement(JsonElement jsonElement) {
    index = jsonElement.getAsInt();
    value(enumList.get(index));
  }

}
