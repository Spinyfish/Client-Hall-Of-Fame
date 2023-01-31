package intent.AquaDev.aqua.utils;

import gui.clickgui.ownClickgui.ClickguiScreen;
import gui.clickgui.ownClickgui.components.CategoryPaneOwn;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.gui.novoline.ClickguiScreenNovoline;
import intent.AquaDev.aqua.gui.novoline.components.CategoryPaneNovoline;
import intent.AquaDev.aqua.modules.Module;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;

public class FileUtil {
   public static final File DIRECTORY = new File(Minecraft.getMinecraft().mcDataDir, Aqua.name);
   public static final File PIC = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/pic");
   public static final File CONFIGS = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/configs");

   public void createFolder() {
      if (!DIRECTORY.exists() || !DIRECTORY.isDirectory()) {
         DIRECTORY.mkdirs();
         DIRECTORY.mkdir();
      }
   }

   public void createPicFolder() {
      if (!PIC.exists() || !PIC.isDirectory()) {
         PIC.mkdir();
         PIC.mkdirs();
      }
   }

   public void createConfigFolder() {
      if (CONFIGS.exists() && CONFIGS.isDirectory()) {
         System.out.println("Folder exist");
      } else {
         System.out.println("Created Folder");
         CONFIGS.mkdir();
         CONFIGS.mkdirs();
      }
   }

   public void loadKeys() {
      try {
         Path path1 = Paths.get(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name);
         if (!Files.exists(path1)) {
            try {
               Files.createDirectory(path1);
            } catch (IOException var10) {
               var10.printStackTrace();
            }
         }

         Path path = Paths.get(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/keys.txt");
         if (!Files.exists(path)) {
            try {
               Files.createFile(path);
            } catch (IOException var9) {
               var9.printStackTrace();
            }
         }

         File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/keys.txt");
         if (!file.exists()) {
            file.createNewFile();
         } else if (file.exists()) {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String readString;
            while((readString = bufferedReader.readLine()) != null) {
               String[] split = readString.split(":");
               Module mod = Aqua.moduleManager.getModuleByName(split[0]);
               if (mod != null) {
                  int key = Integer.parseInt(split[1]);
                  mod.setKeyBind(key);
               }
            }
         }
      } catch (Exception var11) {
         var11.printStackTrace();
      }
   }

   public void saveKeys() {
      File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/keys.txt");
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException var8) {
            var8.printStackTrace();
         }
      }

      try {
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));

         for(Module module : Aqua.moduleManager.modules) {
            String moduleName = module.getName();
            int moduleKey = module.getKeyBind();
            String endstring = moduleName + ":" + moduleKey + "\n";
            writer.write(endstring);
         }

         writer.close();
      } catch (Exception var9) {
      }
   }

   public void saveModules() {
      File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/modules.txt");
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

      try {
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));

         for(Module module : Aqua.moduleManager.modules) {
            String modName = module.getName();
            String string = modName + ":" + module.toggeld + "\n";
            writer.write(string);
         }

         writer.close();
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   public void loadModules() {
      try {
         File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/modules.txt");
         if (!file.exists()) {
            file.createNewFile();
         } else {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String readString;
            while((readString = bufferedReader.readLine()) != null) {
               String[] split = readString.split(":");
               Module mod = Aqua.moduleManager.getModuleByName(split[0]);
               boolean enabled = Boolean.parseBoolean(split[1]);
               if (mod != null) {
                  mod.setState(enabled);
               }
            }
         }
      } catch (Exception var7) {
         var7.printStackTrace();
      }
   }

   public void loadClickGuiOwn(CategoryPaneOwn categoryPanel) {
      try {
         File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/gui.txt");
         if (!file.exists()) {
            file.createNewFile();
         } else {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String readString;
            while((readString = bufferedReader.readLine()) != null) {
               String[] split = readString.split(":");
               if (split.length == 3 && categoryPanel.getCategory().toString().equals(split[0])) {
                  categoryPanel.setX(Integer.parseInt(split[1]));
                  categoryPanel.setY(Integer.parseInt(split[2]));
                  return;
               }
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   public void loadClickGuiJello(gui.jello.components.CategoryPaneOwn categoryPanel) {
      try {
         File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/gui.txt");
         if (!file.exists()) {
            file.createNewFile();
         } else {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String readString;
            while((readString = bufferedReader.readLine()) != null) {
               String[] split = readString.split(":");
               if (split.length == 3 && categoryPanel.getCategory().toString().equals(split[0])) {
                  categoryPanel.setX(Integer.parseInt(split[1]));
                  categoryPanel.setY(Integer.parseInt(split[2]));
                  return;
               }
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   public void saveClickGuiOwn(ClickguiScreen clickGui) {
      File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/gui.txt");
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

      try {
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));

         for(CategoryPaneOwn categoryPanel : clickGui.getCategoryPanes()) {
            String string = categoryPanel.getCategory() + ":" + categoryPanel.getX() + ":" + categoryPanel.getY() + "\n";
            writer.write(string);
         }

         writer.close();
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   public void saveClickGuiJello(gui.jello.ClickguiScreen clickGui) {
      File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/gui.txt");
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

      try {
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));

         for(gui.jello.components.CategoryPaneOwn categoryPanel : clickGui.getCategoryPanes()) {
            String string = categoryPanel.getCategory() + ":" + categoryPanel.getX() + ":" + categoryPanel.getY() + "\n";
            writer.write(string);
         }

         writer.close();
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   public void saveClickGui(ClickguiScreenNovoline clickGui) {
      File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/gui.txt");
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

      try {
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));

         for(CategoryPaneNovoline categoryPanel : clickGui.getCategoryPanes()) {
            String string = categoryPanel.getCategory() + ":" + categoryPanel.getX() + ":" + categoryPanel.getY() + "\n";
            writer.write(string);
         }

         writer.close();
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   public void saveClickGuiTenacity(intent.AquaDev.aqua.gui.tenacity.ClickguiScreenNovoline clickGui) {
      File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/gui.txt");
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

      try {
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));

         for(intent.AquaDev.aqua.gui.tenacity.components.CategoryPaneNovoline categoryPanel : clickGui.getCategoryPanes()) {
            String string = categoryPanel.getCategory() + ":" + categoryPanel.getX() + ":" + categoryPanel.getY() + "\n";
            writer.write(string);
         }

         writer.close();
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   public void saveClickGuiSuicide(intent.AquaDev.aqua.gui.suicideX.ClickguiScreenNovoline clickGui) {
      File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/gui.txt");
      if (!file.exists()) {
         try {
            file.createNewFile();
         } catch (IOException var7) {
            var7.printStackTrace();
         }
      }

      try {
         BufferedWriter writer = new BufferedWriter(new FileWriter(file));

         for(intent.AquaDev.aqua.gui.suicideX.components.CategoryPaneNovoline categoryPanel : clickGui.getCategoryPanes()) {
            String string = categoryPanel.getCategory() + ":" + categoryPanel.getX() + ":" + categoryPanel.getY() + "\n";
            writer.write(string);
         }

         writer.close();
      } catch (Exception var8) {
         var8.printStackTrace();
      }
   }

   public void loadClickGui(CategoryPaneNovoline categoryPanel) {
      try {
         File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/gui.txt");
         if (!file.exists()) {
            file.createNewFile();
         } else {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String readString;
            while((readString = bufferedReader.readLine()) != null) {
               String[] split = readString.split(":");
               if (split.length == 3 && categoryPanel.getCategory().toString().equals(split[0])) {
                  categoryPanel.setX(Integer.parseInt(split[1]));
                  categoryPanel.setY(Integer.parseInt(split[2]));
                  return;
               }
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   public void loadClickGuiTenacity(intent.AquaDev.aqua.gui.tenacity.components.CategoryPaneNovoline categoryPanel) {
      try {
         File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/gui.txt");
         if (!file.exists()) {
            file.createNewFile();
         } else {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String readString;
            while((readString = bufferedReader.readLine()) != null) {
               String[] split = readString.split(":");
               if (split.length == 3 && categoryPanel.getCategory().toString().equals(split[0])) {
                  categoryPanel.setX(Integer.parseInt(split[1]));
                  categoryPanel.setY(Integer.parseInt(split[2]));
                  return;
               }
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   public void loadClickGuiSuicde(intent.AquaDev.aqua.gui.suicideX.components.CategoryPaneNovoline categoryPanel) {
      try {
         File file = new File(Minecraft.getMinecraft().mcDataDir + "/" + Aqua.name + "/gui.txt");
         if (!file.exists()) {
            file.createNewFile();
         } else {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String readString;
            while((readString = bufferedReader.readLine()) != null) {
               String[] split = readString.split(":");
               if (split.length == 3 && categoryPanel.getCategory().toString().equals(split[0])) {
                  categoryPanel.setX(Integer.parseInt(split[1]));
                  categoryPanel.setY(Integer.parseInt(split[2]));
                  return;
               }
            }
         }
      } catch (Exception var6) {
         var6.printStackTrace();
      }
   }

   public static ArrayList<String> readFile(File file) {
      try {
         if (!file.exists()) {
            return null;
         } else {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            ArrayList<String> strings = new ArrayList<>();

            String curr;
            while((curr = reader.readLine()) != null) {
               strings.add(curr);
            }

            reader.close();
            return strings;
         }
      } catch (Exception var4) {
         return null;
      }
   }

   public static boolean writeFile(File file, List<String> lines) {
      try {
         if (!file.exists()) {
            return false;
         } else {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));

            for(String line : lines) {
               writer.write(line.endsWith("\n") ? line : line + "\n");
            }

            writer.close();
            writer.flush();
            return true;
         }
      } catch (Exception var5) {
         return false;
      }
   }
}
