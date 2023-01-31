package gui.jello.settingScreen;

import de.Hero.settings.GuiColorChooser2;
import de.Hero.settings.Setting;
import gui.jello.ClickguiScreen;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.fr.lavache.anime.Animate;
import intent.AquaDev.aqua.modules.Module;
import intent.AquaDev.aqua.modules.visual.Blur;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.utils.ColorUtils;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.Translate;
import intent.AquaDev.aqua.utils.UnicodeFontRenderer;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

public class SettingScreen extends GuiScreen {
   private GuiScreen parent;
   private ResourceLocation resourceLocation;
   private int scrollAdd = 0;
   private int x;
   private int y;
   private Module module;
   private float scroll;
   private GuiColorChooser2 colorChooser2;
   private int modX;
   private int modY;
   private int modWidth;
   private int modHeight;
   private final Animate animate = new Animate();
   Translate translate;

   public SettingScreen(Module module, GuiScreen parent) {
      this.parent = parent;
      this.module = module;
      this.colorChooser2 = new GuiColorChooser2(this.modX, this.modY + 10);
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      if (this.resourceLocation != null) {
         ScaledResolution scaledRes = new ScaledResolution(this.mc);
         float posX = (float)Aqua.setmgr.getSetting("GuiElementsPosX").getCurrentNumber();
         float posY = (float)Aqua.setmgr.getSetting("GuiElementsPosY").getCurrentNumber();
         float width1 = (float)Aqua.setmgr.getSetting("GuiElementsWidth").getCurrentNumber();
         float height1 = (float)Aqua.setmgr.getSetting("GuiElementsHeight").getCurrentNumber();
         float alpha1 = (float)Aqua.setmgr.getSetting("GuiElementsBackgroundAlpha").getCurrentNumber();
         if (Aqua.moduleManager.getModuleByName("GuiElements").isToggled()) {
            if (Aqua.setmgr.getSetting("GuiElementsCustomPic").isState()) {
               RenderUtil.drawImage(
                  (int)((float)scaledRes.getScaledWidth() - this.animate.getValue() - posX),
                  (int)((float)scaledRes.getScaledHeight() - posY),
                  (int)width1,
                  (int)height1,
                  this.resourceLocation
               );
            }

            if (Aqua.moduleManager.getModuleByName("Blur").isToggled()) {
               int color = Aqua.setmgr.getSetting("HUDColor").getColor();
               Color colorAlpha = ColorUtils.getColorAlpha(color, (int)alpha1);
               if (Aqua.setmgr.getSetting("GuiElementsBackgroundColor").isState()) {
                  Gui.drawRect2(0.0, 0.0, (double)this.mc.displayWidth, (double)this.mc.displayHeight, colorAlpha.getRGB());
               }

               Blur.drawBlurred(() -> Gui.drawRect(0, 0, this.mc.displayWidth, this.mc.displayHeight, -1), false);
            }

            if (Aqua.setmgr.getSetting("GuiElementsCustomPic").isState()) {
               RenderUtil.drawImage(
                  (int)((float)scaledRes.getScaledWidth() - this.animate.getValue() - posX),
                  (int)((float)scaledRes.getScaledHeight() - posY),
                  (int)width1,
                  (int)height1,
                  this.resourceLocation
               );
            }
         }

         ScaledResolution sr = new ScaledResolution(this.mc);
         this.translate.interpolate((float)width, (float)height, 4.0);
         double xmod = (double)((float)width / 2.0F - this.translate.getX() / 2.0F);
         double ymod = (double)((float)height / 2.0F - this.translate.getY() / 2.0F);
         GlStateManager.translate(xmod, ymod, 0.0);
         GlStateManager.scale(this.translate.getX() / (float)width, this.translate.getY() / (float)height, 1.0F);
         int hudColor = Aqua.setmgr.getSetting("HUDColor").getColor();
         float leftWindowBorder = (float)sr.getScaledWidth() / 2.0F - 95.0F;
         float rightWindowBorder = leftWindowBorder + 190.0F;
         float windowWidth = 190.0F;
         float windowY = 180.0F;
         float windowHeight = 150.0F;
         RenderUtil.drawRoundedRect2Alpha((double)leftWindowBorder, (double)windowY, (double)windowWidth, (double)windowHeight, 4.0, new Color(0, 0, 0, 100));
         int textColor = Color.GRAY.getRGB();
         ArrayList<Setting> settings = Aqua.setmgr.getSettingsFromModule(this.module);
         GL11.glEnable(3089);
         GL11.glScissor(
            (int)(leftWindowBorder * (float)sr.getScaleFactor()),
            (int)(windowY * (float)sr.getScaleFactor()),
            (int)(windowWidth * (float)sr.getScaleFactor()),
            (int)(windowHeight * (float)sr.getScaleFactor())
         );
         float offset = 0.0F;

         for(Setting setting : settings) {
            float y = windowY + offset + 10.0F + this.scroll;
            boolean doRender = y > windowY && y < windowY + windowHeight;
            offset += this.drawSettingOwn(setting, leftWindowBorder + 10.0F, y, rightWindowBorder, mouseX, mouseY, doRender) + 3.0F;
         }

         if (Mouse.hasWheel()) {
            this.scroll = (float)((double)this.scroll + (double)Aqua.INSTANCE.mouseWheelUtil.mouseDelta / 9.0);
         }

         if (-offset - 10.0F + windowHeight > this.scroll) {
            this.scroll = -offset - 10.0F + windowHeight;
         }

         if (this.scroll > 0.0F || offset + 10.0F < windowHeight) {
            this.scroll = 0.0F;
         }

         GL11.glDisable(3089);
         super.drawScreen(mouseX, mouseY, partialTicks);
      }
   }

   @Override
   public void initGui() {
      try {
         File file = new File(
            System.getProperty("user.dir") + "/" + Aqua.name + "//pic/" + Aqua.setmgr.getSetting("GuiElementsMode").getCurrentMode() + ".png"
         );
         BufferedImage bi = ImageIO.read(file);
         this.resourceLocation = Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation("name", new DynamicTexture(bi));
      } catch (Exception var3) {
      }

      this.translate = new Translate(0.0F, 0.0F);
      new ScaledResolution(this.mc);
   }

   public float drawSettingOwn(Setting setting, float nameX, float nameY, float windowBorderX, int mouseX, int mouseY, boolean doRender) {
      int textColor = Color.WHITE.getRGB();
      String camelCase = setting.getDisplayName().substring(0, 1).toUpperCase() + setting.getDisplayName().substring(1);
      UnicodeFontRenderer font = Aqua.INSTANCE.comfortaa5;
      UnicodeFontRenderer font2 = Aqua.INSTANCE.comfortaa5;
      float height = font.getHeight(camelCase) + 3.0F;
      int distanceToBorder = 10;
      switch(setting.type) {
         case BOOLEAN:
            int rectWidth = 30;
            int rectHeight = 10;
            float checkBoxXmin = windowBorderX - (float)distanceToBorder - (float)rectWidth;
            boolean hovered = this.mouseOver(mouseX, mouseY, (int)checkBoxXmin, (int)nameY, rectWidth, rectHeight);
            setting.setHovered(hovered && doRender);
            Color color = setting.isState() ? Color.GREEN : Color.RED;
            if (hovered) {
               color = color.darker();
            }

            if (doRender) {
               Shadow.drawGlow(() -> font.drawString(camelCase, nameX, nameY, Color.WHITE.getRGB()), false);
               font.drawString(camelCase, nameX, nameY, textColor);
               Shadow.drawGlow(
                  () -> RenderUtil.drawRoundedRect(
                        (double)checkBoxXmin, (double)nameY, (double)rectWidth, (double)rectHeight, 1.0, new Color(0, 0, 0, 255).getRGB()
                     ),
                  false
               );
               if (setting.isState()) {
                  Shadow.drawGlow(
                     () -> RenderUtil.drawRoundedRect2Alpha(
                           (double)checkBoxXmin, (double)nameY, (double)((float)rectWidth / 2.0F), (double)rectHeight, 1.0, color
                        ),
                     false
                  );
                  RenderUtil.drawRoundedRect2Alpha((double)checkBoxXmin, (double)nameY, (double)((float)rectWidth / 2.0F), (double)rectHeight, 1.0, color);
                  font.drawString("On", checkBoxXmin + 1.3F, nameY, -1);
               } else {
                  Shadow.drawGlow(
                     () -> RenderUtil.drawRoundedRect2Alpha(
                           (double)(windowBorderX - (float)distanceToBorder - (float)rectWidth / 2.0F - 1.0F),
                           (double)nameY,
                           (double)((float)rectWidth / 2.0F),
                           (double)rectHeight,
                           1.0,
                           color
                        ),
                     false
                  );
                  RenderUtil.drawRoundedRect2Alpha(
                     (double)(windowBorderX - (float)distanceToBorder - (float)rectWidth / 2.0F - 1.0F),
                     (double)nameY,
                     (double)((float)rectWidth / 2.0F + 1.0F),
                     (double)rectHeight,
                     1.0,
                     color
                  );
                  font.drawString("OFF", windowBorderX - (float)distanceToBorder - (float)rectWidth / 2.0F, nameY, -1);
               }
            }

            height = Math.max((float)rectHeight, height);
            break;
         case NUMBER:
            double percentage = (setting.getCurrentNumber() - setting.getMin()) / (setting.getMax() - setting.getMin());
            int rectWidth = 100;
            int rectHeight = 6;
            float sliderMinX = windowBorderX - (float)distanceToBorder - (float)rectWidth;
            float sliderY = nameY + height / 2.0F - (float)(rectHeight / 2);
            setting.setHovered(this.mouseOver(mouseX, mouseY, (int)sliderMinX, (int)sliderY, rectWidth, rectHeight) && doRender);
            setting.setSliderMinX(sliderMinX);
            setting.setSliderMaxX(sliderMinX + (float)rectWidth);
            if (doRender) {
               Shadow.drawGlow(() -> font.drawString(camelCase, nameX, nameY, Color.WHITE.getRGB()), false);
               font.drawString(camelCase, nameX, nameY, textColor);
               Shadow.drawGlow(
                  () -> RenderUtil.drawRoundedRect((double)sliderMinX, (double)sliderY, (double)rectWidth, (double)rectHeight, 1.0, Color.black.getRGB()),
                  false
               );
               RenderUtil.drawRoundedRect2Alpha((double)sliderMinX, (double)sliderY, (double)rectWidth, (double)rectHeight, 1.0, new Color(0, 0, 0, 60));
               Color color = new Color(Aqua.setmgr.getSetting("HUDColor").getColor());
               if (setting.isHovered()) {
                  color = color.darker();
               }

               if (percentage > 0.0) {
                  Color finalColor = color;
                  Shadow.drawGlow(
                     () -> RenderUtil.drawRoundedRect(
                           (double)sliderMinX, (double)sliderY, (double)rectWidth * percentage, (double)rectHeight, 1.0, finalColor.getRGB()
                        ),
                     false
                  );
                  RenderUtil.drawRoundedRect2Alpha((double)sliderMinX, (double)sliderY, (double)rectWidth * percentage, (double)rectHeight, 1.0, color);
               }

               String value = round(setting.getCurrentNumber(), 2);
               Shadow.drawGlow(
                  () -> font2.drawString(
                        value,
                        sliderMinX + (float)rectWidth / 2.0F - font2.getWidth(value) / 2.0F,
                        sliderY + (float)rectHeight / 2.0F - font2.getHeight(value) / 2.0F,
                        Color.WHITE.getRGB()
                     ),
                  false
               );
               font2.drawString(
                  value,
                  sliderMinX + (float)rectWidth / 2.0F - font2.getWidth(value) / 2.0F,
                  sliderY + (float)rectHeight / 2.0F - font2.getHeight(value) / 2.0F,
                  Color.white.getRGB()
               );
            }

            height = Math.max(height, (float)rectHeight);
            break;
         case COLOR:
            this.colorChooser2.x = (double)(nameX - 5.0F);
            this.colorChooser2.y = (double)(nameY + 5.0F);
            height = (float)(10 + this.colorChooser2.getHeight());
            this.colorChooser2.draw(mouseX, mouseY);
            setting.color = this.colorChooser2.color;
            this.colorChooser2.setWidth(180);
            break;
         case STRING:
            if (setting.getModes() == null) {
               return 0.0F;
            }

            float rectHeight = font2.getHeight(setting.getCurrentMode()) + 3.0F;
            setting.setHovered(false);
            if (doRender) {
               Shadow.drawGlow(() -> font.drawString(camelCase, nameX, nameY, Color.WHITE.getRGB()), false);
               font.drawString(camelCase, nameX, nameY, textColor);
               float rectWidth = 0.0F;

               for(String s : setting.getModes()) {
                  rectWidth = Math.max(font2.getWidth(s), rectWidth) + 4.0F;
               }

               float minX = windowBorderX - (float)distanceToBorder - rectWidth;
               setting.setHovered(this.mouseOver(mouseX, mouseY, (int)minX, (int)nameY, (int)rectWidth, (int)rectHeight));
               Color color = new Color(0, 0, 0, 60);
               if (setting.isHovered()) {
                  color = color.darker();
               }

               float offset = 0.0F;
               if (setting.isComboExtended()) {
                  for(String mode : setting.getModes()) {
                     offset += font2.getHeight(mode) + 1.0F;
                  }
               }

               float finalOffset = offset;
               Color finalColor = color;
               Shadow.drawGlow(
                  () -> RenderUtil.drawRoundedRect(
                        (double)minX, (double)nameY, (double)rectWidth, (double)(rectHeight + finalOffset), 1.0, finalColor.getRGB()
                     ),
                  false
               );
               RenderUtil.drawRoundedRect2Alpha((double)minX, (double)nameY, (double)rectWidth, (double)(rectHeight + offset), 1.0, color);
               setting.setComboHoverIndex(-1);
               if (!setting.isComboExtended()) {
                  Shadow.drawGlow(
                     () -> font2.drawString(
                           setting.getCurrentMode(),
                           minX + 1.0F,
                           nameY + rectHeight / 2.0F - font2.getHeight(setting.getCurrentMode()) / 2.0F,
                           Color.WHITE.getRGB()
                        ),
                     false
                  );
                  font2.drawString(
                     setting.getCurrentMode(), minX + 1.0F, nameY + rectHeight / 2.0F - font2.getHeight(setting.getCurrentMode()) / 2.0F, Color.WHITE.getRGB()
                  );
               } else {
                  Color color1 = new Color(Aqua.setmgr.getSetting("HUDColor").getColor());
                  Shadow.drawGlow(() -> font2.drawString(setting.getCurrentMode(), minX + 1.0F, nameY, color1.getRGB()), false);
                  font2.drawString(setting.getCurrentMode(), minX + 1.0F, nameY, color1.getRGB());
                  offset = font2.getHeight(setting.getCurrentMode()) + 1.0F;

                  for(int i = 0; i < setting.getModes().length; ++i) {
                     String mode = setting.getModes()[i];
                     Color stringColor = Color.WHITE;
                     if (!setting.getCurrentMode().equalsIgnoreCase(mode)) {
                        boolean hovered = this.mouseOver(
                           mouseX, mouseY, (int)(minX + 1.0F), (int)(nameY + offset), (int)rectWidth, (int)(font2.getHeight(mode) + 1.0F)
                        );
                        if (hovered) {
                           setting.setComboHoverIndex(i);
                           stringColor = stringColor.darker();
                        }

                        float finalOffset1 = offset;
                        Color finalStringColor = stringColor;
                        Shadow.drawGlow(() -> font2.drawString(mode, minX + 1.0F, nameY + finalOffset1, finalStringColor.getRGB()), false);
                        font2.drawString(mode, minX + 1.0F, nameY + offset, Color.white.getRGB());
                        if (i + 1 < setting.getModes().length) {
                           offset += font2.getHeight(mode) + 1.0F;
                        }
                     }
                  }

                  rectHeight += offset;
               }
            }

            height = Math.max(height, rectHeight);
      }

      return height;
   }

   public static String round(double v, int places) {
      double f = Math.pow(10.0, (double)places);
      return String.valueOf((double)Math.round(v * f) / f);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      ScaledResolution sr = new ScaledResolution(this.mc);
      if (!this.mouseOver(mouseX, mouseY, (int)((float)sr.getScaledWidth() / 2.0F - 95.0F), 180, 190, 150) && mouseButton == 0) {
         this.mc.displayGuiScreen(new ClickguiScreen(null));
      } else {
         for(Setting setting : Aqua.setmgr.getSettingsFromModule(this.module)) {
            if (setting.isHovered()) {
               switch(setting.type) {
                  case BOOLEAN:
                     setting.setState(!setting.isState());
                  case NUMBER:
                  default:
                     break;
                  case COLOR:
                     setting.color = this.colorChooser2.color;
                     break;
                  case STRING:
                     setting.setComboExtended(!setting.isComboExtended());
               }
            }

            if (setting.type == Setting.Type.STRING && setting.isComboExtended() && setting.getComboHoverIndex() >= 0 && setting.getModes() != null) {
               setting.setCurrentMode(setting.getModes()[setting.getComboHoverIndex()]);
            }
         }
      }

      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      if (clickedMouseButton == 0) {
         for(Setting setting : Aqua.setmgr.getSettingsFromModule(this.module)) {
            if (setting.isHovered()) {
               switch(setting.type) {
                  case BOOLEAN:
                     setting.setState(!setting.isState());
                     break;
                  case NUMBER:
                     double percentage = (double)((float)mouseX - setting.getSliderMinX()) / (double)(setting.getSliderMaxX() - setting.getSliderMinX());
                     setting.setCurrentNumber(setting.getMin() + (setting.getMax() - setting.getMin()) * percentage);
               }
            }
         }
      }

      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode == 1) {
         this.mc.displayGuiScreen(this.parent);
      }
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
   }

   private boolean mouseOver(int x, int y, int modX, int modY, int modWidth, int modHeight) {
      return x >= modX && x <= modX + modWidth && y >= modY && y <= modY + modHeight;
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
}
