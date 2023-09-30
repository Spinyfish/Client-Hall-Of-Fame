package dev.eternal.client.ui.clickgui.eternal.component.impl;

import dev.eternal.client.Client;
import dev.eternal.client.module.Module;
import dev.eternal.client.property.Property;
import dev.eternal.client.ui.clickgui.eternal.component.Component;
import lombok.Getter;
import lombok.Setter;
import scheme.Scheme;

@Getter
@Setter
public abstract class SettingComponent extends Component {

  private final Module module;
  private final Property<?> property;

  public SettingComponent(double x, double y, Module module, Property<?> property) {
    super(x, y);
    this.module = module;
    this.property = property;
  }

  private boolean expanded;

  @SuppressWarnings("unchecked")
  public <T extends Property<?>> T getProperty() {
    return (T) this.property;
  }

}
