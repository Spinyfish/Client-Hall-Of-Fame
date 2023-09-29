package wtf.automn.module.settings;

import wtf.automn.module.Module;

public class SettingBoolean extends Setting<Boolean> {

  public SettingBoolean(String id, Boolean value, String display, Module parent, String description) {
    super(id, value, display, parent, description);
    parent.getSettings().add(this);
  }

  public boolean getBoolean() {
    return value;
  }

}
