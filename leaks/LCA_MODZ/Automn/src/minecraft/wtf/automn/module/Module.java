package wtf.automn.module;

import com.google.gson.annotations.Expose;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.client.Minecraft;
import wtf.automn.Automn;
import wtf.automn.events.EventManager;
import wtf.automn.module.settings.Setting;
import wtf.automn.utils.interfaces.MC;
import wtf.automn.utils.interfaces.MM;
import wtf.automn.utils.interfaces.RM;
import wtf.automn.utils.io.FileUtil;
import wtf.automn.utils.io.JsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class Module implements MM, wtf.automn.utils.interfaces.MC, RM {
  private final String id;
  private final String display;
  private final Category category;

  @Expose
  private int keybinding;

  @Expose
  private boolean enabled = false;

  @Expose
  private List<Setting> settings = new ArrayList<>();

  public Module() {
    var info = getClass().getAnnotation(ModuleInfo.class);
    if (info == null) {
      throw new RuntimeException(String.format("Module %s is missing annotation", getClass().getSimpleName()));
    }
    this.id = info.name();
    this.display = info.displayName();
    this.category = info.category();
    this.keybinding = info.keybind();
    Automn.instance().manager().getModules().add(this);


    try {
      var fromJSON = loadFromJson(id);
      if (fromJSON != null) {
        if (fromJSON.enabled() && !this.enabled()) {
          toggle();
        }
        this.keybinding = fromJSON.keybinding;

        if (fromJSON.settings != null && !fromJSON.settings.isEmpty()) {
          for (var setting : fromJSON.settings) {
            if (getSetting(setting.id) == null) {
              this.settings.add(setting);
            } else {
              this.settings.forEach(setting2 -> {
                if (setting2.id.equals(setting.id)) {
                  setting2.value = setting.value;
                }
              });
            }
          }
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  /*
   * returns the value of a certain setting
   */


  /*
  public List<Setting> onSetup() {
    System.err.printf("Expected no super call from Module %s%n", getClass().getSimpleName());
    return List.of();
  }

   */

  public Module loadFromJson(String id) {
    File dir = FileUtil.getDirectory("modules");
    File f = new File(FileUtil.getDirectory("modules"), id + ".json");
    return JsonUtil.getObject(Module.class, f);
  }

  public void saveToFile(String id) {
    File f = new File(FileUtil.getDirectory("modules"), id + ".json");
    JsonUtil.writeObjectToFile(this, f);
  }

  public void toggle() {
    this.enabled(!this.enabled());
    if (this.enabled) {
      this.onEnable();
      EventManager.register(this);
    } else {
      this.onDisable();
      EventManager.unregister(this);
    }
    saveToFile(id);
  }

  protected void onEnable() {
  }

  protected void onDisable() {
  }

  /*
   * returns a setting by its ID
   */
  public Setting getSetting(String id) {
    for (Setting setting : this.settings) {
      if (setting.id.equals(id)) return setting;
    }
    return null;
  }

  /*
   * returns the settings of a module
   */
  public List<Setting> getSettings() {
    return settings;
  }
}
