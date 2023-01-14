package me.uncodable.srt.impl.utils;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Set;
import java.util.Map.Entry;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.modules.api.Module;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

@Exclude({Strategy.NAME_REMAPPING, Strategy.STRING_ENCRYPTION, Strategy.FLOW_OBFUSCATION, Strategy.NUMBER_OBFUSCATION, Strategy.REFERENCE_OBFUSCATION, Strategy.PARAMETER_OBFUSCATION})
public class ConfigUtils {
   public static boolean loadConfiguration(String inConfig, boolean loadBinds) {
      File config = new File(FileUtils.SRT_CONFIG_DIR, inConfig.concat(".json"));
      JsonParser parser = new JsonParser();

      FileReader reader;
      try {
         reader = new FileReader(config);
      } catch (FileNotFoundException var17) {
         Ries.INSTANCE
            .msg(
               String.format(
                  "An I/O error occurred while attempting to load the selected configuration. Failed to locate the configuration file \"%s.\"",
                  config.getName()
               )
            );
         var17.printStackTrace();
         return false;
      }

      JsonElement element = parser.parse(reader);

      Set<Entry<String, JsonElement>> entrySet;
      try {
         entrySet = element.getAsJsonObject().entrySet();
      } catch (IllegalStateException var16) {
         Ries.INSTANCE
            .msg(
               String.format(
                  "An I/O error occurred while attempting to load the selected configuration. The \"%s\" configuration file appears to be malformed.",
                  config.getName()
               )
            );
         var16.printStackTrace();
         return false;
      }

      for(Entry<String, JsonElement> entry : entrySet) {
         for(Module module : Ries.INSTANCE.getModuleManager().getModules()) {
            if (module.getInfo().internalName().equalsIgnoreCase(entry.getKey())) {
               JsonObject jsonModule = ((JsonElement)entry.getValue()).getAsJsonObject();
               if (jsonModule.get("toggled").getAsBoolean() != module.isEnabled()) {
                  module.toggle();
               }

               if (loadBinds) {
                  module.setPrimaryKey(jsonModule.get("primary_bind").getAsInt());
               }

               if (jsonModule.has("settings")) {
                  JsonObject jsonSetting = jsonModule.getAsJsonObject("settings");

                  for(Setting setting : Ries.INSTANCE.getSettingManager().getAllSettings(module)) {
                     String lowercase = setting.getInternalName().toLowerCase();
                     if (jsonSetting.has(lowercase)) {
                        switch(setting.getSettingType()) {
                           case CHECKBOX:
                              setting.setTicked(jsonSetting.get(lowercase).getAsBoolean());
                              break;
                           case SLIDER:
                              setting.setCurrentValue(jsonSetting.get(lowercase).getAsNumber().doubleValue());
                              break;
                           case COMBO_BOX:
                              setting.setCurrentCombo(jsonSetting.get(lowercase).getAsString());
                              break;
                           default:
                              Ries.INSTANCE.msg(String.format("Unknown setting type parsed: %s", setting.getSettingType().name()));
                        }
                     }
                  }
               }
            }
         }
      }

      Ries.INSTANCE.msg(String.format("Loaded configuration file \"%s.\"", config.getName()));
      return true;
   }

   public static boolean saveConfiguration(String outConfig) {
      File config = new File(FileUtils.SRT_CONFIG_DIR, outConfig.concat(".json"));
      JsonObject jsonModules = new JsonObject();

      PrintWriter writer;
      try {
         writer = new PrintWriter(config);
      } catch (FileNotFoundException var12) {
         Ries.INSTANCE.msg("An I/O error occurred while attempting to save the current configuration.");
         var12.printStackTrace();
         return false;
      }

      for(Module module : Ries.INSTANCE.getModuleManager().getModules()) {
         ArrayList<Setting> settings = Ries.INSTANCE.getSettingManager().getAllSettings(module);
         JsonObject jsonModule = new JsonObject();
         jsonModule.addProperty("toggled", module.isEnabled());
         jsonModule.addProperty("primary_bind", module.getPrimaryKey());
         if (!settings.isEmpty()) {
            JsonObject jsonSetting = new JsonObject();

            for(Setting setting : settings) {
               String lowercase = setting.getInternalName().toLowerCase();
               switch(setting.getSettingType()) {
                  case CHECKBOX:
                     jsonSetting.addProperty(lowercase, setting.isTicked());
                     break;
                  case SLIDER:
                     jsonSetting.addProperty(lowercase, setting.getCurrentValue());
                     break;
                  case COMBO_BOX:
                     jsonSetting.addProperty(lowercase, setting.getCurrentCombo());
                     break;
                  default:
                     Ries.INSTANCE.msg(String.format("Unknown setting type parsed: %s", setting.getSettingType().name()));
               }
            }

            jsonModule.add("settings", jsonSetting);
         }

         jsonModules.add(module.getInfo().internalName().toLowerCase(), jsonModule);
      }

      writer.println(new GsonBuilder().setPrettyPrinting().create().toJson(jsonModules));
      writer.close();
      Ries.INSTANCE.msg(String.format("Saved configuration file \"%s.\"", config.getName()));
      return true;
   }
}
