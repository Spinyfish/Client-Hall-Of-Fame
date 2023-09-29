package dev.eternal.client.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.Property;

public class TextSetting extends Property<String> {

  public TextSetting(IToggleable owner, String name, String value) {
    super(owner, name, value, PropertyType.TEXT);
  }

  @Override
  public JsonElement getJsonElement() {
    return new JsonPrimitive(value());
  }

  @Override
  public void loadJsonElement(JsonElement jsonElement) {
    value(jsonElement.getAsString());
  }

}
