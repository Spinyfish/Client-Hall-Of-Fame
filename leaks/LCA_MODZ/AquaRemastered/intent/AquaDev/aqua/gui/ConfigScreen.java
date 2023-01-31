package intent.AquaDev.aqua.gui;

import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.config.Config;
import intent.AquaDev.aqua.config.ConfigOnline;
import intent.AquaDev.aqua.modules.visual.ShaderMultiplier;
import intent.AquaDev.aqua.utils.ChatUtil;
import intent.AquaDev.aqua.utils.FileUtil;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Objects;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class ConfigScreen extends GuiScreen {
   private int scrollAdd = 0;
   private ArrayList<Config> configList = new ArrayList<>();
   private ArrayList<String> configListName = new ArrayList<>();
   private ArrayList<String> onlineConfigList = new ArrayList<>();
   private ArrayList<String> onlineConfigDate = new ArrayList<>();
   private int configHeight = 0;
   private boolean onlineConfigs = false;
   public static boolean loadVisuals = false;

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      int hudColor = Aqua.setmgr.getSetting("HUDColor").getColor();
      ScaledResolution sr = new ScaledResolution(this.mc);
      if (Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
         ShaderMultiplier.drawGlowESP(
            () -> RenderUtil.drawRoundedRectSmooth(
                  (double)((float)sr.getScaledWidth() / 2.0F - 162.0F), 118.0, 300.0, 244.0, 3.0, new Color(Aqua.setmgr.getSetting("HUDColor").getColor())
               ),
            false
         );
      }

      RenderUtil.drawRoundedRectSmooth(
         (double)((float)sr.getScaledWidth() / 2.0F - 162.0F), 118.0, 300.0, 244.0, 3.0, new Color(Aqua.setmgr.getSetting("HUDColor").getColor())
      );
      RenderUtil.drawRoundedRect2Alpha((double)((float)sr.getScaledWidth() / 2.0F - 160.0F), 120.0, 296.0, 240.0, 2.0, new Color(40, 40, 40, 255));
      RenderUtil.drawRoundedRect2Alpha((double)((float)sr.getScaledWidth() / 2.0F - 140.0F), 150.0, 256.0, 195.0, 3.0, new Color(35, 35, 35));
      Aqua.INSTANCE.comfortaa3.drawString("Load Visuals ", (float)sr.getScaledWidth() / 2.0F + 30.0F, 133.0F, -1);
      if (this.mouseOver(mouseX, mouseY, (int)((float)sr.getScaledWidth() / 2.0F + 100.0F), 132, 13, 12)) {
         if (Aqua.moduleManager.getModuleByName("ShaderMultiplier").isToggled()) {
            ShaderMultiplier.drawGlowESP(
               () -> RenderUtil.drawRoundedRect2Alpha(
                     (double)((float)sr.getScaledWidth() / 2.0F + 138.0F), 98.0, 105.0, 14.0, 3.0, new Color(Aqua.setmgr.getSetting("HUDColor").getColor())
                  ),
               false
            );
         }

         RenderUtil.drawRoundedRect2Alpha((double)((float)sr.getScaledWidth() / 2.0F + 138.0F), 98.0, 105.0, 14.0, 3.0, new Color(40, 40, 40, 100));
         Aqua.INSTANCE.comfortaa3.drawString("Right Click to Disable", (float)sr.getScaledWidth() / 2.0F + 140.0F, 100.0F, -1);
         if (Mouse.isButtonDown(0)) {
            loadVisuals = true;
         } else if (Mouse.isButtonDown(1)) {
            loadVisuals = false;
         }
      }

      if (loadVisuals) {
         RenderUtil.drawRoundedRect2Alpha((double)((float)sr.getScaledWidth() / 2.0F + 100.0F), 132.0, 13.0, 12.0, 3.0, new Color(hudColor));
      } else {
         RenderUtil.drawRoundedRect2Alpha((double)((float)sr.getScaledWidth() / 2.0F + 100.0F), 132.0, 13.0, 12.0, 3.0, new Color(29, 29, 29));
      }

      if (this.mouseOver(mouseX, mouseY, (int)((float)sr.getScaledWidth() / 2.0F - 140.0F), 133, 29, 12)) {
         Aqua.INSTANCE.comfortaa3.drawString("Local", (float)sr.getScaledWidth() / 2.0F - 140.0F, 133.0F, -1);
      } else {
         Aqua.INSTANCE.comfortaa3.drawString("Local", (float)sr.getScaledWidth() / 2.0F - 140.0F, 133.0F, hudColor);
      }

      if (this.mouseOver(mouseX, mouseY, (int)((float)sr.getScaledWidth() / 2.0F - 100.0F), 133, 32, 12)) {
         Aqua.INSTANCE.comfortaa3.drawString("Online", (float)sr.getScaledWidth() / 2.0F - 100.0F, 133.0F, -1);
      } else {
         Aqua.INSTANCE.comfortaa3.drawString("Online", (float)sr.getScaledWidth() / 2.0F - 100.0F, 133.0F, hudColor);
      }

      int currentY = this.scrollAdd;
      int dateY = this.scrollAdd;
      if (this.onlineConfigs) {
         this.configHeight = 25 * this.onlineConfigList.size();
         this.renderConfig(this.onlineConfigList, this.onlineConfigDate, this.configHeight, currentY, dateY, sr, mouseX, mouseY);
      } else {
         this.renderConfig(this.configListName, this.onlineConfigDate, this.configHeight, currentY, dateY, sr, mouseX, mouseY);
      }

      super.drawScreen(mouseX, mouseY, partialTicks);
   }

   private void renderConfig(
      ArrayList<String> configList, ArrayList<String> onlineConfigDate, int configHeight, int currentY, int dateY, ScaledResolution sr, int mouseX, int mouseY
   ) {
      GL11.glEnable(3089);
      RenderUtil.scissor((double)((float)sr.getScaledWidth() / 2.0F - 100.0F), 155.0, 190.0, 188.0);
      if (this.onlineConfigs) {
         for(String date : onlineConfigDate) {
            dateY += 25;
            Aqua.INSTANCE.comfortaa4.drawString("" + date, (float)sr.getScaledWidth() / 2.0F + 31.0F, (float)(136 + dateY), -1);
         }
      }

      for(String config : configList) {
         currentY += 25;
         RenderUtil.drawRoundedRect2Alpha(
            (double)((float)sr.getScaledWidth() / 2.0F - 97.0F), (double)(currentY + 127), 173.0, 25.0, 0.0, new Color(0, 0, 0, 30)
         );
         RenderUtil.drawRoundedRect2Alpha(
            (double)((float)sr.getScaledWidth() / 2.0F - 97.0F),
            (double)(currentY + 127),
            173.0,
            1.0,
            0.0,
            new Color(Aqua.setmgr.getSetting("HUDColor").getColor())
         );
         Aqua.INSTANCE.comfortaa4.drawString("" + config, (float)sr.getScaledWidth() / 2.0F - 90.0F, (float)(136 + currentY), -1);
      }

      if (this.mouseOver(mouseX, mouseY, (int)((float)sr.getScaledWidth() / 2.0F - 100.0F), 150, 190, 200) && configHeight > 200) {
         int mouseDelta = Aqua.INSTANCE.mouseWheelUtil.mouseDelta;
         this.scrollAdd += mouseDelta / 5;
         this.scrollAdd = MathHelper.clamp_int(this.scrollAdd, -configHeight + 188, 0);
      }

      GL11.glDisable(3089);
   }

   @Override
   public void initGui() {
      this.initConfigSystem();
   }

   private void initConfigSystem() {
      this.configList = this.findConfigs();
      this.configListName.clear();

      for(Config config : this.configList) {
         this.configListName.add(config.getConfigName());
      }

      this.configHeight = 25 * this.configList.size();
      this.onlineConfigList.clear();
      this.onlineConfigDate.clear();
      this.findOnlineConfig();
      this.findOnlineConfigDate();
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      ScaledResolution sr = new ScaledResolution(this.mc);
      int currentY = this.scrollAdd;
      if (this.onlineConfigs) {
         for(String config : this.onlineConfigList) {
            currentY += 25;
            if (this.mouseOver(mouseX, mouseY, (int)((float)sr.getScaledWidth() / 2.0F - 97.0F), currentY + 125, 183, 22)) {
               ConfigOnline skid = new ConfigOnline();
               skid.loadConfigOnline(config);
               break;
            }
         }
      } else {
         for(Config config : this.configList) {
            currentY += 25;
            if (this.mouseOver(mouseX, mouseY, (int)((float)sr.getScaledWidth() / 2.0F - 97.0F), currentY + 125, 183, 22)) {
               config.load();
               break;
            }
         }
      }

      if (this.mouseOver(mouseX, mouseY, (int)((float)sr.getScaledWidth() / 2.0F - 140.0F), 133, 29, 12)) {
         if (this.onlineConfigs) {
            this.initConfigSystem();
            this.onlineConfigs = false;
         }
      } else if (this.mouseOver(mouseX, mouseY, (int)((float)sr.getScaledWidth() / 2.0F - 100.0F), 133, 32, 12) && !this.onlineConfigs) {
         this.initConfigSystem();
         this.onlineConfigs = true;
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
   }

   private ArrayList<Config> findConfigs() {
      ArrayList<Config> configs = new ArrayList<>();
      File confDir = new File(FileUtil.DIRECTORY, "configs/");

      for(File file : Objects.requireNonNull(confDir.listFiles())) {
         Config config = new Config(file.getName().replaceAll(".json", ""));
         configs.add(config);
      }

      return configs;
   }

   private void findOnlineConfig() {
      Thread thread = new Thread(
         () -> {
            ArrayList<String> configs = new ArrayList<>();
   
            try {
               URLConnection urlConnection = new URL("https://raw.githubusercontent.com/aquaclient/aquaclient.github.io/main/configs/configs.json")
                  .openConnection();
               urlConnection.setConnectTimeout(10000);
               urlConnection.connect();
   
               String text;
               try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
                  while((text = bufferedReader.readLine()) != null) {
                     if (text.contains("404: Not Found")) {
                        ChatUtil.sendChatMessageWithPrefix("An error occurred while loading the configs from Github.");
                        return;
                     }
   
                     configs.add(text);
                  }
               }
            } catch (IOException var18) {
               ChatUtil.sendChatMessageWithPrefix("An error occurred while loading the configs from Github.");
               var18.printStackTrace();
            }
   
            this.onlineConfigList = configs;
         }
      );
      thread.start();
   }

   private void findOnlineConfigDate() {
      Thread thread = new Thread(() -> {
         ArrayList<String> date = new ArrayList<>();

         try {
            URLConnection urlConnection = new URL("https://raw.githubusercontent.com/aquaclient/aquaclient.github.io/main/ConfigDate.json").openConnection();
            urlConnection.setConnectTimeout(10000);
            urlConnection.connect();

            String text;
            try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()))) {
               while((text = bufferedReader.readLine()) != null) {
                  if (text.contains("404: Not Found")) {
                     ChatUtil.sendChatMessageWithPrefix("An error occurred while loading the configs from Github.");
                     return;
                  }

                  date.add(text);
               }
            }
         } catch (IOException var18) {
            ChatUtil.sendChatMessageWithPrefix("An error occurred while loading the configs from Github.");
            var18.printStackTrace();
         }

         this.onlineConfigDate = date;
      });
      thread.start();
   }

   private boolean mouseOver(int x, int y, int modX, int modY, int modWidth, int modHeight) {
      return x >= modX && x <= modX + modWidth && y >= modY && y <= modY + modHeight;
   }
}
