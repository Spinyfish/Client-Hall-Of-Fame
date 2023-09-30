package dev.eternal.client.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.Property;

public class BooleanSetting extends Property<Boolean> {

  public BooleanSetting(IToggleable owner, String name, String description, Boolean value) {
    super(owner, name, description, value, PropertyType.BOOLEAN);
  }

  @Deprecated(forRemoval = true)
  public BooleanSetting(IToggleable owner, String name, Boolean value) {
    super(owner, name, value, PropertyType.BOOLEAN);
  }

  @Override
  public JsonElement getJsonElement() {
    return new JsonPrimitive(value());
  }

  @Override
  public void loadJsonElement(JsonElement jsonElement) {
    value(jsonElement.getAsBoolean());
  }
}
