package me.uncodable.srt.impl.utils;

import java.io.File;
import java.io.IOException;
import me.uncodable.srt.Ries;
import store.intent.intentguard.annotation.Exclude;
import store.intent.intentguard.annotation.Strategy;

@Exclude({Strategy.NAME_REMAPPING, Strategy.STRING_ENCRYPTION, Strategy.FLOW_OBFUSCATION, Strategy.NUMBER_OBFUSCATION, Strategy.REFERENCE_OBFUSCATION, Strategy.PARAMETER_OBFUSCATION})
public class FileUtils {
   public static final File SRT_ROOT_DIR = new File(String.format("%s\\SRT\\", System.getProperty("user.dir")));
   public static final File SRT_CONFIG_DIR = new File(SRT_ROOT_DIR, "Configurations");
   public static final File LAST_SAVE = new File(SRT_CONFIG_DIR, "Last Session Save.json");

   public static void init() {
      if (!SRT_ROOT_DIR.exists()) {
         Ries.INSTANCE.console("Failed to discover the SRT root directory. Creating the root directory...");
         if (!SRT_ROOT_DIR.mkdir()) {
            Ries.INSTANCE.console("Failed to create the SRT root directory. Does the user have access to the path?");
            return;
         }
      }

      if (!SRT_CONFIG_DIR.exists()) {
         Ries.INSTANCE.console("Failed to discover the SRT configuration directory. Creating the configuration directory...");
         if (!SRT_CONFIG_DIR.mkdir()) {
            Ries.INSTANCE.console("Failed to create the SRT configuration directory. Does the user have access to the path?");
            return;
         }
      }

      if (!LAST_SAVE.exists()) {
         Ries.INSTANCE.console("Failed to discover the last client save configuration. Creating a new one...");

         try {
            LAST_SAVE.createNewFile();
         } catch (IOException var1) {
            Ries.INSTANCE.console("An I/O exception occurred while creating the last save configuration file.");
            var1.printStackTrace();
         }
      } else {
         ConfigUtils.loadConfiguration(LAST_SAVE.getName().replace(".json", ""), true);
         Ries.INSTANCE.console("Loaded last session save configuration.");
      }
   }
}
