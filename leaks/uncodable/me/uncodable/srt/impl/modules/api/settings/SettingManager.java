package me.uncodable.srt.impl.modules.api.settings;

import java.util.ArrayList;
import java.util.concurrent.CopyOnWriteArrayList;
import me.uncodable.srt.impl.modules.api.Module;

public class SettingManager {
   private final CopyOnWriteArrayList<Setting> settings = new CopyOnWriteArrayList<>();

   public void addComboBox(Module module, String internalName, String settingName, String... modes) {
      this.settings.add(new Setting(module, internalName, settingName, modes));
   }

   public void addCheckbox(Module module, String internalName, String settingName) {
      this.addCheckbox(module, internalName, settingName, false);
   }

   public void addCheckbox(Module module, String internalName, String settingName, boolean ticked) {
      this.settings.add(new Setting(module, internalName, settingName, ticked));
   }

   public void addSlider(Module module, String internalName, String settingName, double defaultValue, double minimumValue, double maximumValue) {
      this.addSlider(module, internalName, settingName, defaultValue, minimumValue, maximumValue, false);
   }

   public void addSlider(
      Module module, String internalName, String settingName, double defaultValue, double minimumValue, double maximumValue, boolean truncate
   ) {
      this.settings.add(new Setting(module, internalName, settingName, defaultValue, minimumValue, maximumValue, truncate));
   }

   public Setting getSetting(Module module, String internalName, Setting.Type type) {
      for(Setting setting : this.settings) {
         if (setting.getModule() == module && setting.getInternalName().equals(internalName) && setting.getSettingType() == type) {
            return setting;
         }
      }

      return null;
   }

   public ArrayList<Setting> getAllSettings(Module module) {
      ArrayList<Setting> returnSettings = new ArrayList<>();

      for(Setting setting : this.settings) {
         if (setting.getModule() == module) {
            returnSettings.add(setting);
         }
      }

      return returnSettings;
   }

   public CopyOnWriteArrayList<Setting> getSettings() {
      return this.settings;
   }
}
