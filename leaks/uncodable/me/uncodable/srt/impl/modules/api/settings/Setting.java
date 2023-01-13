package me.uncodable.srt.impl.modules.api.settings;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.modules.api.Module;

public class Setting {
   private final Module module;
   private final String internalName;
   private final String settingName;
   private final Setting.Type settingType;
   private boolean ticked;
   private boolean truncate;
   private double defaultValue;
   private double minimumValue;
   private double maximumValue;
   private double currentValue;
   private String[] combos;
   private String currentCombo;
   private int comboIndex;

   public Setting(Module module, String internalName, String settingName, boolean ticked) {
      this.module = module;
      this.internalName = internalName;
      this.settingName = settingName;
      this.ticked = ticked;
      this.settingType = Setting.Type.CHECKBOX;
   }

   public Setting(Module module, String internalName, String settingName, double defaultValue, double minimumValue, double maximumValue, boolean truncate) {
      this.module = module;
      this.internalName = internalName;
      this.settingName = settingName;
      this.defaultValue = defaultValue;
      this.minimumValue = minimumValue;
      this.maximumValue = maximumValue;
      this.truncate = truncate;
      this.settingType = Setting.Type.SLIDER;
      this.currentValue = defaultValue;
   }

   public Setting(Module module, String internalName, String settingName, String... combos) {
      this.module = module;
      this.internalName = internalName;
      this.settingName = settingName;
      this.combos = combos;
      this.settingType = Setting.Type.COMBO_BOX;
      this.currentCombo = combos[this.comboIndex];
   }

   public void setCurrentCombo(String comboName) {
      this.currentCombo = comboName;
      Ries.INSTANCE.getModuleManager().sort();
   }

   public void setCurrentCombo(int comboIndex) {
      this.currentCombo = this.combos[comboIndex];
      Ries.INSTANCE.getModuleManager().sort();
   }

   public void setTicked(boolean ticked) {
      this.ticked = ticked;
   }

   public void setTruncate(boolean truncate) {
      this.truncate = truncate;
   }

   public void setDefaultValue(double defaultValue) {
      this.defaultValue = defaultValue;
   }

   public void setMinimumValue(double minimumValue) {
      this.minimumValue = minimumValue;
   }

   public void setMaximumValue(double maximumValue) {
      this.maximumValue = maximumValue;
   }

   public void setCurrentValue(double currentValue) {
      this.currentValue = currentValue;
   }

   public void setCombos(String[] combos) {
      this.combos = combos;
   }

   public void setComboIndex(int comboIndex) {
      this.comboIndex = comboIndex;
   }

   public Module getModule() {
      return this.module;
   }

   public String getInternalName() {
      return this.internalName;
   }

   public String getSettingName() {
      return this.settingName;
   }

   public Setting.Type getSettingType() {
      return this.settingType;
   }

   public boolean isTicked() {
      return this.ticked;
   }

   public boolean isTruncate() {
      return this.truncate;
   }

   public double getDefaultValue() {
      return this.defaultValue;
   }

   public double getMinimumValue() {
      return this.minimumValue;
   }

   public double getMaximumValue() {
      return this.maximumValue;
   }

   public double getCurrentValue() {
      return this.currentValue;
   }

   public String[] getCombos() {
      return this.combos;
   }

   public String getCurrentCombo() {
      return this.currentCombo;
   }

   public int getComboIndex() {
      return this.comboIndex;
   }

   public static enum Type {
      CHECKBOX,
      COMBO_BOX,
      SLIDER;
   }
}
