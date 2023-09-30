package wtf.automn.module.settings;

import wtf.automn.module.Module;

public class SettingString extends Setting<String> {

  public String[] values;

  public SettingString(String id, String value, String[] values, String display, Module parent, String description) {
    super(id, value, display, parent, description);
    this.values = values;
    parent.getSettings().add(this);
  }

  public String getString() {
    return this.value;
  }

}
