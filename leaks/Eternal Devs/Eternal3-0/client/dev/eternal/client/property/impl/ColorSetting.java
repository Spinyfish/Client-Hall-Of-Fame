package dev.eternal.client.property.impl;


import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.Property;

import java.awt.*;

public class ColorSetting extends Property<Color> {

  public ColorSetting(IToggleable owner, String name, Color value) {
    super(owner, name, value, PropertyType.COLOUR);
  }

  public int getColor() {
    return this.value().getRGB();
  }

  @Override
  public JsonElement getJsonElement() {
    return new JsonPrimitive(value().getRGB());
  }

  @Override
  public void loadJsonElement(JsonElement jsonElement) {
    value(new Color(jsonElement.getAsInt()));
  }

}
