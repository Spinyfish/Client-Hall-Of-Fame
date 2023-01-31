package intent.AquaDev.aqua.config;

import de.Hero.settings.Setting;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.gui.ConfigScreen;
import intent.AquaDev.aqua.modules.Category;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public class ConfigOnline {
   public void loadConfigOnline(String name) {
      String url = "https://aquaclient.github.io/configs/" + name.toLowerCase() + ".txt";

      try (Scanner scanner = new Scanner(new URL(url).openConnection().getInputStream()).useDelimiter("\\A")) {
         while(scanner.hasNextLine()) {
            String[] args = scanner.nextLine().split(" ");
            if (args[0].equalsIgnoreCase("MODULE")) {
               if (args[1].equalsIgnoreCase("TOGGLE")) {
                  String modName = args[2];
                  boolean val = Boolean.parseBoolean(args[3]);

                  try {
                     Aqua.moduleManager.getModuleByName(modName).setState(val);
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
      } catch (IOException var23) {
         var23.printStackTrace();
      }
   }
}
