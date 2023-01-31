package gui.clickgui.ownClickgui.components;

import gui.clickgui.ownClickgui.ClickguiScreen;
import gui.clickgui.ownClickgui.settingScreen.SettingScreen;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.utils.MouseClicker;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
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
      Aqua.INSTANCE.comfortaa3.drawString(this.category.name(), (float)(this.x + 14), (float)(this.y + 3), Color.white.getRGB());
      int add = this.height + this.scrollAdd;
      List<Module> moduleList = Aqua.moduleManager.getModulesOrdered(this.category, true, Aqua.INSTANCE.comfortaa4);
      int allModHeight = 0;

      for(Module module : moduleList) {
         if (module.getCategory() == this.category) {
            allModHeight += 15;
         }
      }

      int maxPanelHeight = 225;
      int finalAllModHeight = allModHeight;
      RenderUtil.drawRoundedRect2Alpha(
         (double)this.x, (double)this.y, (double)this.width, (double)(finalAllModHeight + this.height), 4.0, new Color(0, 0, 0, 100)
      );
      if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
         Shadow.drawGlow(
            () -> RenderUtil.drawRoundedRect(
                  (double)this.x, (double)this.y, (double)this.width, (double)(finalAllModHeight + this.height), 4.0, Color.black.getRGB()
               ),
            false
         );
      }

      GL11.glEnable(3089);
      RenderUtil.scissor((double)this.x, (double)(this.y + this.height), (double)this.width, (double)(allModHeight + this.height + 5));
      allModHeight += this.height;
      int settingsHeight = 0;

      for(Module module : moduleList) {
         if (module.getCategory() == this.category) {
            Aqua.INSTANCE
               .comfortaa4
               .drawCenteredString(
                  module.getName(),
                  (float)this.x + (float)this.width / 2.0F,
                  (float)(this.y + add),
                  module.isToggled() ? this.ACTIVATED_TEXT_COLOR : this.INACTIVATED_TEXT_COLOR
               );
            int modX = this.x + 2;
            int modY = this.y + add;
            int modWidth = this.width - 4;
            int modHeight = 15;
            add += 15;
         }
      }

      int currHeight = MathHelper.clamp_int(add, 0, Math.max(allModHeight - this.height, maxPanelHeight));
      this.currHeight = currHeight;
      if (this.mouseOver(mouseX, mouseY, this.x, this.y, this.width, currHeight)) {
         int mouseDelta = Aqua.INSTANCE.mouseWheelUtil.mouseDelta;
         this.scrollAdd += mouseDelta / 5;
         this.scrollAdd = MathHelper.clamp_int(this.scrollAdd, -settingsHeight + (currHeight - allModHeight), 0);
      }

      GL11.glDisable(3089);
      int finalAdd = currHeight - 1;
      this.checker.release(0);
   }

   public void clickMouse(int mouseX, int mouseY, int mouseButton) {
      int add = this.height + this.scrollAdd;
      List<Module> moduleList = Aqua.moduleManager.getModulesOrdered(this.category, true, Aqua.INSTANCE.comfortaa4);
      int allModHeight = 0;

      for(Module module : moduleList) {
         if (module.getCategory() == this.category) {
            allModHeight += 15;
         }
      }

      for(Module module : moduleList) {
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
                  Aqua.INSTANCE.fileUtil.saveClickGuiOwn(this.novoline);

                  try {
                     this.mc.displayGuiScreen(new SettingScreen(module, this.novoline));
                  } catch (Exception var14) {
                  }
               }
            }

            if (module.isOpen()) {
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
