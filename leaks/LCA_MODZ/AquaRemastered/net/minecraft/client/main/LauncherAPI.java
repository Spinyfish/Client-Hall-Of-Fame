package net.minecraft.client.main;

import java.io.File;
import java.util.Locale;

public class LauncherAPI {
   public static void main(String[] args) {
      String userHome = System.getProperty("user.home", ".");
      File workingDirectory;
      switch(LauncherAPI.OS.getOperatingSystem()) {
         case LINUX:
            workingDirectory = new File(userHome, ".minecraft/");
            break;
         case WINDOWS:
            String applicationData = System.getenv("APPDATA");
            String folder = applicationData != null ? applicationData : userHome;
            workingDirectory = new File(folder, ".minecraft/");
            break;
         case MACOS:
            workingDirectory = new File(userHome, "Library/Application Support/minecraft");
            break;
         default:
            workingDirectory = new File(userHome, "minecraft/");
      }

      try {
         Main.main(
            new String[]{
               "--version",
               "FantaX",
               "--accessToken",
               "0",
               "--gameDir",
               new File(workingDirectory, ".").getAbsolutePath(),
               "--assetsDir",
               new File(workingDirectory, "assets").getAbsolutePath(),
               "--assetIndex",
               "1.8",
               "--userProperties",
               "{}"
            }
         );
      } catch (Throwable var5) {
         var5.printStackTrace();
      }
   }

   public static enum OS {
      WINDOWS,
      MACOS,
      LINUX,
      OTHER;

      public static LauncherAPI.OS getOperatingSystem() {
         String operatingSystem = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
         if (operatingSystem.contains("mac") || operatingSystem.contains("darwin")) {
            return MACOS;
         } else if (operatingSystem.contains("win")) {
            return WINDOWS;
         } else {
            return operatingSystem.contains("nux") ? LINUX : OTHER;
         }
      }
   }
}
