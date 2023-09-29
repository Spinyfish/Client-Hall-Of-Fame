package dev.eternal.client.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.Property;
import lombok.Getter;

@Getter
public class NumberSetting extends Property<Double> {

  private final double min, max, increment;

  public NumberSetting(IToggleable owner, String name, String desc, double value, double min, double max, double increment) {
    super(owner, name, desc, value, PropertyType.NUMBER);
    this.min = min;
    this.max = max;
    this.increment = increment;
  }

  @Deprecated(forRemoval = true)
  public NumberSetting(IToggleable owner, String name, double value, double min, double max, double increment) {
    super(owner, name, value, PropertyType.NUMBER);
    this.min = min;
    this.max = max;
    this.increment = increment;
  }

  public void setValue(double value) {
    value = Math.max(min, Math.min(max, value));
    super.value(value);
  }

  @Override
  public JsonElement getJsonElement() {
    return new JsonPrimitive(value());
  }

  @Override
  public void loadJsonElement(JsonElement jsonElement) {
    setValue(jsonElement.getAsDouble());
  }


}
