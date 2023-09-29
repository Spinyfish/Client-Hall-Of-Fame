package dev.eternal.client.property.impl;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import dev.eternal.client.Client;
import dev.eternal.client.module.interfaces.IToggleable;
import dev.eternal.client.property.Property;
import dev.eternal.client.property.impl.mode.Mode;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

@Getter
public class ModeSetting extends Property<Mode> {

  private int index;
  private final List<Mode> modeList;

  public ModeSetting(IToggleable owner, String name, Mode... modes) {
    super(owner, name, modes[0], PropertyType.MODE);
    this.modeList = Arrays.asList(modes);
    index = 0;
    for (Mode mode : modes) {
      mode.property(this);
    }
  }

  @Override
  public ModeSetting value(Mode value) {
    if (owner().isEnabled())
      Client.singleton().eventBus().unregister(this.value());
    Mode newMode = modeList.stream().filter(mode -> mode == value).findFirst().orElse(this.modeList.get(0));
    index = modeList.indexOf(newMode);
    super.value(newMode);
    if (owner().isEnabled())
      Client.singleton().eventBus().register(this.value());
    return this;
  }

  public void setValue(String value) {
    if (owner().isEnabled())
      Client.singleton().eventBus().unregister(this.value());
    Mode newMode = modeList.stream().filter(mode -> mode.name().equalsIgnoreCase(value)).findFirst().orElse(this.modeList.get(0));
    index = modeList.indexOf(newMode);
    super.value(newMode);
    if (owner().isEnabled())
      Client.singleton().eventBus().register(this.value());
  }

  public void cycle() {
    if (index++ >= modeList.size() - 1) index = 0;
    this.value(modeList.get(index));
  }

  @Override
  public JsonElement getJsonElement() {
    return new JsonPrimitive(index());
  }

  @Override
  public void loadJsonElement(JsonElement jsonElement) {
    value(modeList.get(jsonElement.getAsInt()));
  }
}
