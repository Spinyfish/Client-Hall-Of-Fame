package intent.AquaDev.aqua.config;

import de.Hero.settings.Setting;
import de.Hero.settings.SettingsManager;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.gui.ConfigScreen;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.notifications.Notification;
import intent.AquaDev.aqua.notifications.NotificationManager;
import intent.AquaDev.aqua.utils.FileUtil;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

public class Config {
   private static final String FILE_ENDING = ".txt";
   private final String fileName;
   private boolean github = false;
   private boolean defaultSettings = false;

   public Config(String fileName) {
      this(fileName, false);
   }

   public Config(String fileName, boolean github) {
      this(fileName, github, false);
   }

   public Config(String fileName, boolean github, boolean defaultSettings) {
      this.fileName = fileName;
      this.github = github;
      this.defaultSettings = defaultSettings;
   }

   public boolean load() {
      File confDir = new File(FileUtil.DIRECTORY, this.defaultSettings ? "" : "configs/");
      if (!confDir.exists()) {
         confDir.mkdirs();
         return false;
      } else {
         File confFile = new File(confDir, this.fileName.endsWith(".txt") ? this.fileName : this.fileName + ".txt");
         String configName = this.fileName;
         NotificationManager.addNotificationToQueue(new Notification("Config", "loaded §a" + this.fileName, 1000L, Notification.NotificationType.INFO));
         sendChatMessageWithPrefix("Loaded Config : " + this.fileName);
         List<String> lines = FileUtil.readFile(confFile);
         if (this.github) {
            try {
               URLConnection urlConnection = new URL(
                     "https://raw.githubusercontent.com/LCAMODZ/FantaX-Configs/main/"
                        + (this.fileName.endsWith(".txt") ? this.fileName : this.fileName + ".txt")
                  )
                  .openConnection();
               urlConnection.setConnectTimeout(10000);
               urlConnection.connect();

               String text;
               try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                  while((text = bufferedReader.readLine()) != null) {
                     lines.add(text);
                  }
               }
            } catch (Exception var23) {
            }
         } else if (!confFile.exists()) {
            return false;
         }

         for(String string : lines) {
            String[] args = string.split(" ");
            if (args[0].equalsIgnoreCase("MODULE")) {
               if (args[1].equalsIgnoreCase("TOGGLE")) {
                  String modName = args[2];
                  boolean val = Boolean.parseBoolean(args[3]);

                  try {
                     Module m = Aqua.moduleManager.getModuleByName(modName);
                     if (m.getCategory() != Category.Visual) {
                        m.setState(val);
                     }
                  } catch (Exception var19) {
                  }
               } else if (args[1].equalsIgnoreCase("SET")) {
                  String settingName = args[2];

                  try {
                     Setting.Type settingType = Setting.Type.valueOf(args[3]);
                     String value = args[4];
                     if (ConfigScreen.loadVisuals || Aqua.setmgr.getSetting(settingName).getModule().getCategory() != Category.Visual) {
                        switch(settingType) {
                           case BOOLEAN:
                              Aqua.setmgr.getSetting(settingName).setState(Boolean.parseBoolean(value));
                           case NUMBER:
                              Aqua.setmgr.getSetting(settingName).setCurrentNumber(Double.parseDouble(value));
                           case STRING:
                              Aqua.setmgr.getSetting(settingName).setCurrentMode(value);
                           case COLOR:
                              Aqua.setmgr.getSetting(settingName).color = Integer.parseInt(value);
                        }
                     }
                  } catch (Exception var20) {
                  }
               }
            }
         }

         return lines != null && !lines.isEmpty();
      }
   }

   public boolean loadOnStart() {
      File confDir = new File(FileUtil.DIRECTORY, this.defaultSettings ? "" : "configs/");
      if (!confDir.exists()) {
         confDir.mkdirs();
         return false;
      } else {
         File confFile = new File(confDir, this.fileName.endsWith(".txt") ? this.fileName : this.fileName + ".txt");
         String configName = this.fileName;
         NotificationManager.addNotificationToQueue(new Notification("Config", "loaded §a" + this.fileName, 1000L, Notification.NotificationType.INFO));
         sendChatMessageWithPrefix("Loaded Config : " + this.fileName);
         List<String> lines = FileUtil.readFile(confFile);
         if (this.github) {
            try {
               URLConnection urlConnection = new URL(
                     "https://raw.githubusercontent.com/LCAMODZ/FantaX-Configs/main/"
                        + (this.fileName.endsWith(".txt") ? this.fileName : this.fileName + ".txt")
                  )
                  .openConnection();
               urlConnection.setConnectTimeout(10000);
               urlConnection.connect();

               String text;
               try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                  while((text = bufferedReader.readLine()) != null) {
                     lines.add(text);
                  }
               }
            } catch (Exception var25) {
            }
         } else if (!confFile.exists()) {
            return false;
         }

         for(String string : lines) {
            String[] args = string.split(" ");
            if (args[0].equalsIgnoreCase("MODULE")) {
               if (args[1].equalsIgnoreCase("TOGGLE")) {
                  String modName = args[2];
                  boolean val = Boolean.parseBoolean(args[3]);

                  try {
                     Aqua.moduleManager.getModuleByName(modName).setState(val);
                  } catch (Exception var21) {
                  }
               } else if (args[1].equalsIgnoreCase("SET")) {
                  String settingName = args[2];

                  try {
                     Setting.Type settingType = Setting.Type.valueOf(args[3]);
                     String value = args[4];

                     for(Module module : Aqua.moduleManager.getModules()) {
                        switch(settingType) {
                           case BOOLEAN:
                              Aqua.setmgr.getSetting(settingName).setState(Boolean.parseBoolean(value));
                           case NUMBER:
                              Aqua.setmgr.getSetting(settingName).setCurrentNumber(Double.parseDouble(value));
                           case STRING:
                              Aqua.setmgr.getSetting(settingName).setCurrentMode(value);
                           case COLOR:
                              Aqua.setmgr.getSetting(settingName).color = Integer.parseInt(value);
                        }
                     }
                  } catch (Exception var22) {
                  }
               }
            }
         }

         return lines != null && !lines.isEmpty();
      }
   }

   public boolean saveCurrent() {
      File confDir = new File(FileUtil.DIRECTORY, this.defaultSettings ? "" : "configs/");
      if (!confDir.exists()) {
         confDir.mkdirs();
      }

      File confFile = new File(confDir, this.fileName.endsWith(".txt") ? this.fileName : this.fileName + ".txt");
      String configName = this.fileName;
      sendChatMessageWithPrefix("Saved Config : " + this.fileName);
      NotificationManager.addNotificationToQueue(new Notification("Config", "saved §a" + this.fileName, 1000L, Notification.NotificationType.INFO));
      if (!confFile.exists()) {
         try {
            confFile.createNewFile();
         } catch (Exception var11) {
         }
      }

      List<String> lines = new ArrayList<>();

      for(Module module : Aqua.moduleManager.getModules()) {
         String TOGGLE = "MODULE TOGGLE " + module.getName() + " " + module.isToggled();
         lines.add(TOGGLE);

         for(Setting setting : SettingsManager.getSettings()) {
            if (setting.getModule() == module) {
               String SET = "MODULE SET " + setting.getName() + " " + setting.getType() + " " + setting.getValue();
               lines.add(SET);
            }
         }
      }

      FileUtil.writeFile(confFile, lines);
      return true;
   }

   public String getConfigName() {
      return this.fileName.replaceAll(".txt", "");
   }

   public static void sendChatMessageWithPrefix(String message) {
   }
}
