package gui.jello.components;

import de.Hero.settings.Setting;
import gui.jello.ClickguiScreen;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.visual.ShaderMultiplier;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.utils.MouseClicker;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class CategoryPaneOwn extends Gui {
   public static CategoryPaneOwn INSTANCE;
   private final Integer CATEGORY_PANE_TOP_COLOR = new Color(0, 0, 0, 195).getRGB();
   private final Integer CATEGORY_MODULE_COLOR = new Color(0, 0, 0, 180).getRGB();
   private final Integer INACTIVATED_TEXT_COLOR = Color.decode("#FEFEFF").getRGB();
   Minecraft mc = Minecraft.getMinecraft();
   private int x;
   private int y;
   private final int width;
   private final int height;
   private final Category category;
   private final ClickguiScreen novoline;
   private Integer ACTIVATED_TEXT_COLOR;
   private final MouseClicker checker = new MouseClicker();
   private int scrollAdd = 0;
   private int currHeight;

   public CategoryPaneOwn(int x, int y, int width, int height, Category category, ClickguiScreen novoline) {
      INSTANCE = this;
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.category = category;
      this.novoline = novoline;
   }

   public void draw(int posX, int posY, int mouseX, int mouseY) {
      this.ACTIVATED_TEXT_COLOR = Aqua.setmgr.getSetting("HUDColor").getColor();
      Shadow.drawGlow(() -> Aqua.INSTANCE.comfortaa3.drawString(this.category.name(), (float)(this.x + 14), (float)(this.y + 3), Color.white.getRGB()), false);
      int add = this.height + this.scrollAdd;
      List<Module> moduleList = Aqua.moduleManager.getModulesOrdered2(this.category, true, Aqua.INSTANCE.jelloClickguiPanelBottom);
      int allModHeight = 0;

      for(Module module : moduleList) {
         if (module.getCategory() == this.category) {
            allModHeight += 15;
         }
      }

      int iiiii = 0;

      for(Category cc : Category.values()) {
         int mcSize = 0;

         for(Module module : Aqua.moduleManager.modules) {
            if (module.getCategory() == cc) {
               mcSize += 15;
            }
         }

         if (mcSize > iiiii) {
            iiiii = mcSize;
         }
      }

      int maxPanelHeight = 127;
      ShaderMultiplier.drawGlowESP(
         () -> RenderUtil.drawRoundedRect2Alpha(
               (double)this.x, (double)this.y, (double)this.width, (double)(maxPanelHeight + this.height), 0.0, new Color(0, 0, 0, 200)
            ),
         false
      );
      RenderUtil.drawRoundedRect2Alpha(
         (double)this.x, (double)this.y, (double)this.width, (double)(maxPanelHeight + this.height), 0.0, new Color(250, 250, 250, 255)
      );
      ShaderMultiplier.drawGlowESP(
         () -> RenderUtil.drawRoundedRect2Alpha(
               (double)this.x, (double)(this.y - 5), (double)this.width, (double)(this.height + 5), 0.0, new Color(0, 0, 0, 200)
            ),
         false
      );
      RenderUtil.drawRoundedRect2Alpha((double)this.x, (double)(this.y - 5), (double)this.width, (double)(this.height + 5), 0.0, new Color(244, 244, 244, 255));
      Aqua.INSTANCE.jelloClickguiPanelTop.drawString(this.category.name(), (float)(this.x + 9), (float)this.y, Color.gray.getRGB());
      if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
      }

      GL11.glEnable(3089);
      RenderUtil.scissor((double)this.x, (double)(this.y + this.height), (double)this.width, (double)maxPanelHeight);
      allModHeight += this.height;
      int settingsHeight = 0;

      for(Module module : moduleList) {
         if (module.getCategory() == this.category) {
            if (module.isToggled()) {
               ShaderMultiplier.drawGlowESP(
                  () -> Gui.drawRect(this.x, this.y + add, this.x + this.width, this.y + add + 15, new Color(45, 165, 251).getRGB()), false
               );
               Gui.drawRect(this.x, this.y + add, this.x + this.width, this.y + add + 15, new Color(45, 165, 251).getRGB());
               Aqua.INSTANCE
                  .jelloClickguiPanelBottom
                  .drawString(
                     module.getName(),
                     (float)this.x + (float)this.width / 2.0F - 37.0F,
                     (float)(this.y + add + 1),
                     module.isToggled() ? Color.white.getRGB() : Color.darkGray.getRGB()
                  );
            } else {
               Aqua.INSTANCE
                  .jelloClickguiPanelBottom
                  .drawString(
                     module.getName(),
                     (float)this.x + (float)this.width / 2.0F - 41.0F,
                     (float)(this.y + add + 1),
                     module.isToggled() ? Color.white.getRGB() : Color.darkGray.getRGB()
                  );
            }

            int modX = this.x + 2;
            int modY = this.y + add;
            int modWidth = this.width - 4;
            int modHeight = 15;
            if (module.isOpen()) {
               ArrayList<Setting> settings = Aqua.setmgr.getSettingsFromModule(module);
               int i = 0;

               for(Setting setting : settings) {
                  i += setting.getHeight();
                  if (setting.type.equals(Setting.Type.COLOR)) {
                  }

                  setting.setMouseX(mouseX);
                  setting.setMouseY(mouseY);
                  setting.drawSettingJello(modX, modY + i, modWidth, modHeight, this.CATEGORY_MODULE_COLOR, this.ACTIVATED_TEXT_COLOR);
                  if (setting.type.equals(Setting.Type.STRING) && setting.isComboExtended()) {
                     i = (int)((double)i + setting.getBoxHeight());
                  }
               }

               settingsHeight += i;
               add += i;
            }

            add += 15;
         }
      }

      int currHeight = MathHelper.clamp_int(add, 0, Math.max(allModHeight - this.height, maxPanelHeight));
      this.currHeight = currHeight;
      if (this.mouseOver(mouseX, mouseY, this.x, this.y, this.width, currHeight)) {
         int mouseDelta = Aqua.INSTANCE.mouseWheelUtil.mouseDelta;
         int oldadd = this.scrollAdd;
         this.scrollAdd += mouseDelta / 5;
         this.scrollAdd = MathHelper.clamp_int(this.scrollAdd, Math.min(0, -add + this.height + oldadd + maxPanelHeight), 0);
      }

      GL11.glDisable(3089);
      this.checker.release(0);
   }

   public void clickMouse(int mouseX, int mouseY, int mouseButton) {
      int add = this.height + this.scrollAdd;

      for(Module module : Aqua.moduleManager.getModulesOrdered2(this.category, true, Aqua.INSTANCE.jelloClickguiPanelBottom)) {
         if (module.getCategory() == this.category) {
            int modX = this.x + 2;
            int modY = this.y + add;
            int modWidth = this.width - 4;
            int modHeight = 15;
            if (this.mouseOver(mouseX, mouseY, modX, modY, modWidth, modHeight)
               && this.novoline.current == null
               && this.mouseOver(mouseX, mouseY, this.x, this.y + this.height, this.width, this.currHeight)) {
               if (mouseButton == 0) {
                  module.toggle();
                  this.checker.stop();
               }

               if (mouseButton == 1) {
                  module.toggleOpen();
               }

               if (mouseButton == 1) {
               }
            }

            if (module.isOpen()) {
               for(Setting setting : Aqua.setmgr.getSettingsFromModule(module)) {
                  add += setting.getHeight();
                  if (setting.type.equals(Setting.Type.STRING) && setting.isComboExtended()) {
                     add = (int)((double)add + setting.getBoxHeight());
                  }

                  if (this.mouseOver(mouseX, mouseY, this.x, this.y + this.height, this.width, this.currHeight)) {
                     setting.clickMouse(mouseX, mouseY, mouseButton);
                  }
               }
            }

            add += 15;
         }
      }
   }

   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
   }

   public void mouseReleased(int mouseX, int mouseY, int state) {
   }

   private boolean mouseOver(int x, int y, int modX, int modY, int modWidth, int modHeight) {
      return x >= modX && x <= modX + modWidth && y >= modY && y <= modY + modHeight;
   }

   public int getX() {
      return this.x;
   }

   public void setX(int x) {
      this.x = x;
   }

   public int getY() {
      return this.y;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getHeight() {
      return this.height;
   }

   public int getWidth() {
      return this.width;
   }

   public Category getCategory() {
      return this.category;
   }
}
