package intent.AquaDev.aqua.gui.hero.components;

import de.Hero.settings.Setting;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.gui.hero.ClickguiScreenHero;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.utils.MouseClicker;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.Translate;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class CategoryPaneHero extends Gui {
   private final Integer CATEGORY_PANE_TOP_COLOR = new Color(0, 0, 0, 195).getRGB();
   private final Integer CATEGORY_MODULE_COLOR = new Color(0, 0, 0, 180).getRGB();
   private final Integer INACTIVATED_TEXT_COLOR = Color.decode("#FEFEFF").getRGB();
   Minecraft mc = Minecraft.getMinecraft();
   Translate translate;
   private int x;
   private int y;
   private final int width;
   private final int height;
   private final Category category;
   private final ClickguiScreenHero novoline;
   private Integer ACTIVATED_TEXT_COLOR;
   private final MouseClicker checker = new MouseClicker();
   private int scrollAdd = 0;
   private int currHeight;
   private final ArrayList<Setting> settings = new ArrayList<>();

   public CategoryPaneHero(int x, int y, int width, int height, Category category, ClickguiScreenHero novoline) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.category = category;
      this.novoline = novoline;
   }

   public void draw(int posX, int posY, int mouseX, int mouseY) {
      this.ACTIVATED_TEXT_COLOR = Aqua.setmgr.getSetting("HUDColor").getColor();
      if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
      }

      Gui.drawRect(this.x + 10, this.y + 5, this.x + this.width - 8, this.y + this.height + 2, new Color(65, 255, 67).getRGB());
      RenderUtil.drawTriangleFilledReversed((float)this.x + (float)this.width / 2.0F + 27.0F, (float)(this.y + 16), 5.0F, 5.0F, -1);
      Aqua.INSTANCE.robotoBold.drawStringWithShadow(this.category.name(), (float)(this.x + 14), (float)(this.y + 7), this.INACTIVATED_TEXT_COLOR);
      int add = this.height + this.scrollAdd;
      List<Module> moduleList = Aqua.moduleManager.getModulesOrdered(this.category, true, Aqua.INSTANCE.comfortaa4);
      int allModHeight = 0;

      for(Module module : moduleList) {
         if (module.getCategory() == this.category) {
            allModHeight += 11;
         }
      }

      int iiiii = 0;

      for(Category cc : Category.values()) {
         int mcSize = 0;

         for(Module module : Aqua.moduleManager.modules) {
            if (module.getCategory() == cc) {
               mcSize = (int)((float)mcSize + (float)Aqua.setmgr.getSetting("GUIGUIScizzor").getCurrentNumber());
            }
         }

         if (mcSize > iiiii) {
            iiiii = mcSize;
         }
      }

      GL11.glEnable(3089);
      RenderUtil.scissor((double)this.x, (double)(this.y + this.height), (double)(this.width - 8), (double)iiiii);
      allModHeight += this.height;
      int settingsHeight = 0;

      for(Module module : Aqua.moduleManager.getModulesOrderedRoboto(this.category, false, Aqua.INSTANCE.robotoPanel)) {
         if (module.getCategory() == this.category) {
            if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
            }

            if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
            }

            drawRect(this.x + 10, this.y + add + 1, this.x + this.width - 8, this.y + add + 12, new Color(35, 35, 35, 195).getRGB());
            if (module.isToggled()) {
            }

            Aqua.INSTANCE
               .robotoPanel
               .drawCenteredString(
                  module.getName(),
                  (float)this.x + (float)this.width / 2.0F,
                  (float)(this.y + add + 2),
                  module.isToggled() ? Color.decode("#00FF45").getRGB() : -1
               );
            int modX = this.x + 2;
            int modY = this.y + add;
            int modWidth = this.width - 4;
            int modHeight = 11;
            ArrayList<Setting> settings1 = Aqua.setmgr.getSettingsFromModule(module);
            if (!settings1.isEmpty() && !module.isOpen()) {
            }

            if (!settings1.isEmpty() && module.isOpen()) {
            }

            if (module.isOpen()) {
               ArrayList<Setting> settings = Aqua.setmgr.getSettingsFromModule(module);
               int i = 0;

               for(Setting setting : settings) {
                  i += setting.getHeight();
                  if (setting.type.equals(Setting.Type.COLOR)) {
                  }

                  setting.setMouseX(mouseX);
                  setting.setMouseY(mouseY);
                  setting.drawSettingHero(modX, modY + i, modWidth, modHeight, this.CATEGORY_MODULE_COLOR, this.ACTIVATED_TEXT_COLOR);
                  if (setting.type.equals(Setting.Type.STRING) && setting.isComboExtended()) {
                     i = (int)((double)i + setting.getBoxHeight());
                  }
               }

               settingsHeight += i;
               add += i;
            }

            add += 11;
         }
      }

      int currHeight = MathHelper.clamp_int(add, 0, Math.max(allModHeight - this.height, iiiii));
      this.currHeight = currHeight;
      if (this.mouseOver(mouseX, mouseY, this.x, this.y, this.width, currHeight)) {
         int mouseDelta = Aqua.INSTANCE.mouseWheelUtil.mouseDelta;
         int oldadd = this.scrollAdd;
         this.scrollAdd += mouseDelta / 5;
         this.scrollAdd = MathHelper.clamp_int(this.scrollAdd, Math.min(0, -add + this.height + oldadd + iiiii), 0);
      }

      GL11.glDisable(3089);
      int finalAdd = currHeight - 1;
      this.checker.release(0);
   }

   public void clickMouse(int mouseX, int mouseY, int mouseButton) {
      int add = this.height + this.scrollAdd;
      List<Module> moduleList = Aqua.moduleManager.getModulesOrdered(this.category, true, Aqua.INSTANCE.comfortaa4);
      int allModHeight = 0;

      for(Module module : Aqua.moduleManager.modules) {
         if (module.getCategory() == this.category) {
            allModHeight += 11;
         }
      }

      for(Module module : Aqua.moduleManager.modules) {
         if (module.getCategory() == this.category) {
            int modX = this.x + 2;
            int modY = this.y + add;
            int modWidth = this.width - 4;
            int modHeight = 11;
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

            add += 11;
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

   public void setX(int x) {
      this.x = x;
   }

   public int getX() {
      return this.x;
   }

   public void setY(int y) {
      this.y = y;
   }

   public int getY() {
      return this.y;
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public Category getCategory() {
      return this.category;
   }
}
