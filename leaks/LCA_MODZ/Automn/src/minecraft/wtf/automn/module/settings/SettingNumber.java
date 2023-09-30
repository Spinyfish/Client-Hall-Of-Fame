package wtf.automn.module.settings;

import wtf.automn.module.Module;

public class SettingNumber extends Setting<Double> {

  public Double min, max;

  public SettingNumber(String id, Double value, Double min, Double max, String display, Module parent, String description) {
    super(id, value, display, parent, description);
    this.min = min;
    this.max = max;
    parent.getSettings().add(this);
  }

  public double getValue() {
    return this.value;
  }

}
