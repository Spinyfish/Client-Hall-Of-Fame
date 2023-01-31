package intent.AquaDev.aqua.gui.aqua.components;

import de.Hero.settings.Setting;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.gui.aqua.ClickguiScreenNovoline;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.visual.Blur;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.utils.ColorUtils;
import intent.AquaDev.aqua.utils.MouseClicker;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.StencilUtil;
import intent.AquaDev.aqua.utils.Translate;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.opengl.GL11;

public class CategoryPaneNovoline extends Gui {
   private final Integer CATEGORY_PANE_TOP_COLOR = new Color(0, 0, 0, 195).getRGB();
   private final Integer CATEGORY_MODULE_COLOR = new Color(0, 0, 0, 180).getRGB();
   private final Integer INACTIVATED_TEXT_COLOR = Color.decode("#FEFEFF").getRGB();
   Minecraft mc = Minecraft.getMinecraft();
   Translate translate;
   private int x;
   private int y;
   private final int width;
   private final int height;
   public static boolean last = false;
   public static boolean first = false;
   private final Category category;
   private final ClickguiScreenNovoline novoline;
   private Integer ACTIVATED_TEXT_COLOR;
   private final MouseClicker checker = new MouseClicker();
   private int scrollAdd = 0;
   private int currHeight;
   private final ArrayList<Setting> settings = new ArrayList<>();

   public CategoryPaneNovoline(int x, int y, int width, int height, Category category, ClickguiScreenNovoline novoline) {
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

      Color alphaColor = ColorUtils.getColorAlpha(Aqua.setmgr.getSetting("HUDColor").getColor(), 200);
      if (Aqua.moduleManager.getModuleByName("Shadow").isToggled() && !ClickguiScreenNovoline.isDragging) {
         Shadow.drawGlow(
            () -> RenderUtil.drawRoundedRect3(
                  (double)(this.x + 2),
                  (double)(this.y + 4),
                  (double)(this.width - 3),
                  (double)(this.height - 4),
                  5.0,
                  true,
                  true,
                  false,
                  false,
                  new Color(36, 36, 36, 255)
               ),
            false
         );
      }

      StencilUtil.write(false);
      RenderUtil.drawRoundedRect3(
         (double)(this.x + 2), (double)(this.y + 4), (double)(this.width - 3), (double)(this.height - 4), 5.0, true, true, false, false, alphaColor
      );
      StencilUtil.erase(true);
      Aqua.INSTANCE.shaderBackground.renderShader();
      StencilUtil.dispose();
      if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
         Blur.drawBlurred(
            () -> RenderUtil.drawRoundedRect3(
                  (double)(this.x + 2), (double)(this.y + 5), (double)(this.width - 3), (double)(this.height - 5), 5.0, true, true, false, false, alphaColor
               ),
            false
         );
      }

      Aqua.INSTANCE
         .novlineBigger
         .drawCenteredString(this.category.name(), (float)this.x + (float)this.width / 2.0F, (float)(this.y + 6), this.INACTIVATED_TEXT_COLOR);
      int add = this.height + this.scrollAdd;
      List<Module> moduleList = Aqua.moduleManager.getModulesOrdered(this.category, false, Aqua.INSTANCE.comfortaa4);
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
               mcSize = (int)((float)mcSize + (float)Aqua.setmgr.getSetting("GUIGUIScizzor").getCurrentNumber());
            }
         }

         if (mcSize > iiiii) {
            iiiii = mcSize;
         }
      }

      RenderUtil.drawRoundedRect3((double)(this.x + 2), (double)(this.y + 19), (double)(this.width - 3), 1.0, 0.0, true, true, false, false, Color.white);
      GL11.glEnable(3089);
      RenderUtil.scissor((double)this.x, (double)(this.y + this.height), (double)this.width, (double)iiiii);
      allModHeight += this.height;
      int settingsHeight = 0;
      int c = 0;
      int cc = 0;

      for(Module module : moduleList) {
         if (module.getCategory() == this.category) {
            ++c;
            if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
            }

            if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
            }

            last = c == moduleList.size();
            first = cc == moduleList.size();
            if (Aqua.setmgr.getSetting("GUIDarkMode").isState()) {
               if (Aqua.moduleManager.getModuleByName("Shadow").isToggled() && !ClickguiScreenNovoline.isDragging) {
                  Shadow.drawGlow(
                     () -> RenderUtil.drawRoundedRect3(
                           (double)(this.x + 2),
                           (double)(this.y + add),
                           (double)(this.width - 3),
                           last ? 15.0 : 15.0,
                           3.0,
                           false,
                           false,
                           last,
                           last,
                           new Color(36, 36, 36, 255)
                        ),
                     false
                  );
               }

               RenderUtil.drawRoundedRect3(
                  (double)(this.x + 2),
                  (double)(this.y + add),
                  (double)(this.width - 3),
                  last ? 15.0 : 15.0,
                  3.0,
                  false,
                  false,
                  last,
                  last,
                  new Color(20, 20, 20, 60)
               );
            } else if (Aqua.moduleManager.getModuleByName("Shadow").isToggled() && !ClickguiScreenNovoline.isDragging) {
               Shadow.drawGlow(
                  () -> RenderUtil.drawRoundedRect3(
                        (double)(this.x + 2),
                        (double)(this.y + add),
                        (double)(this.width - 3),
                        last ? 15.0 : 15.0,
                        3.0,
                        false,
                        false,
                        last,
                        last,
                        new Color(255, 255, 255, 180)
                     ),
                  false
               );
            }

            if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
               Blur.drawBlurred(
                  () -> RenderUtil.drawRoundedRect3(
                        (double)(this.x + 2),
                        (double)(this.y + add),
                        (double)(this.width - 3),
                        last ? 15.0 : 15.0,
                        3.0,
                        false,
                        false,
                        last,
                        last,
                        new Color(36, 36, 36, 180)
                     ),
                  false
               );
            }

            if (module.isToggled()) {
               if (module.isOpen()) {
                  StencilUtil.write(false);
                  RenderUtil.drawRoundedRect2Alpha((double)(this.x + 2), (double)(this.y + add), (double)(this.width - 3), 15.0, 0.0, alphaColor);
                  StencilUtil.erase(true);
                  Aqua.INSTANCE.shaderBackground.renderShader();
                  StencilUtil.dispose();
               } else {
                  StencilUtil.write(false);
                  RenderUtil.drawRoundedRect3(
                     (double)(this.x + 2), (double)(this.y + add), (double)(this.width - 3), 15.0, last ? 3.0 : 0.0, false, false, true, true, alphaColor
                  );
                  StencilUtil.erase(true);
                  Aqua.INSTANCE.shaderBackground.renderShader();
                  StencilUtil.dispose();
               }

               if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
                  Blur.drawBlurred(
                     () -> RenderUtil.drawRoundedRect(
                           (double)(this.x + 4),
                           (double)(this.y + add + 1),
                           (double)(this.width - 7),
                           13.0,
                           3.0,
                           new Color(Aqua.setmgr.getSetting("HUDColor").getColor()).getRGB()
                        ),
                     false
                  );
               }
            } else {
               RenderUtil.drawRoundedRect2Alpha(
                  (double)(this.x + 4), (double)(this.y + add + 1), (double)(this.width - 7), 13.0, 3.0, new Color(76, 76, 76, 0)
               );
               if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
                  Blur.drawBlurred(
                     () -> RenderUtil.drawRoundedRect(
                           (double)(this.x + 4), (double)(this.y + add + 1), (double)(this.width - 7), 13.0, 3.0, new Color(255, 255, 255, 150).getRGB()
                        ),
                     false
                  );
               }
            }

            if (module.isToggled()) {
               Aqua.INSTANCE
                  .novoline
                  .drawCenteredString(module.getName(), (float)this.x + (float)this.width / 2.0F, (float)(this.y + add + 2), Color.white.getRGB());
            } else if (Aqua.setmgr.getSetting("GUIDarkMode").isState()) {
               Aqua.INSTANCE
                  .novoline
                  .drawCenteredString(module.getName(), (float)this.x + (float)this.width / 2.0F, (float)(this.y + add + 2), Color.white.getRGB());
            } else {
               Aqua.INSTANCE
                  .novoline
                  .drawCenteredString(module.getName(), (float)this.x + (float)this.width / 2.0F, (float)(this.y + add + 2), Color.gray.getRGB());
            }

            int modX = this.x + 2;
            int modY = this.y + add;
            int modWidth = this.width - 4;
            int modHeight = 15;
            ArrayList<Setting> settings1 = Aqua.setmgr.getSettingsFromModule(module);
            if (!settings1.isEmpty() && !module.isOpen()) {
               if (module.isToggled()) {
                  Aqua.INSTANCE.novoline.drawString("+", (float)this.x + (float)this.width / 2.0F + 40.0F, (float)(this.y + add + 2), -1);
               } else {
                  Aqua.INSTANCE.novoline.drawString("+", (float)this.x + (float)this.width / 2.0F + 40.0F, (float)(this.y + add + 2), Color.gray.getRGB());
               }
            }

            if (!settings1.isEmpty() && module.isOpen()) {
               if (module.isToggled()) {
                  Aqua.INSTANCE.novoline.drawString("-", (float)this.x + (float)this.width / 2.0F + 41.0F, (float)(this.y + add + 2), -1);
               } else {
                  Aqua.INSTANCE.novoline.drawString("-", (float)this.x + (float)this.width / 2.0F + 41.0F, (float)(this.y + add + 2), Color.gray.getRGB());
               }
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
                  setting.drawSettingAqua(modX, modY + i, modWidth, modHeight, this.CATEGORY_MODULE_COLOR, this.ACTIVATED_TEXT_COLOR);
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
            allModHeight += 15;
         }
      }

      for(Module module : Aqua.moduleManager.modules) {
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
