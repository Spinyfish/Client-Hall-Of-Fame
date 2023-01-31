package gui.clickgui.ownClickgui;

import gui.clickgui.ownClickgui.components.CategoryPaneOwn;
import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.fr.lavache.anime.Animate;
import intent.AquaDev.aqua.fr.lavache.anime.Easing;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.visual.Blur;
import intent.AquaDev.aqua.modules.visual.Shadow;
import intent.AquaDev.aqua.utils.ColorUtils;
import intent.AquaDev.aqua.utils.RenderUtil;
import intent.AquaDev.aqua.utils.Translate;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiButton2;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;

public class ClickguiScreen extends GuiScreen {
   public static boolean awaitingClose = true;
   private final List<CategoryPaneOwn> categoryPanes = new ArrayList<>();
   private final GuiScreen parentScreen;
   private final Animate animate = new Animate();
   public CategoryPaneOwn current = null;
   Translate translate;
   private ResourceLocation resourceLocation;

   public ClickguiScreen(GuiScreen parentScreen) {
      this.parentScreen = null;
   }

   @Override
   public void initGui() {
      try {
         File file = new File(
            System.getProperty("user.dir") + "/" + Aqua.name + "//pic/" + Aqua.setmgr.getSetting("GuiElementsMode").getCurrentMode() + ".png"
         );
         BufferedImage bi = ImageIO.read(file);
         this.resourceLocation = Minecraft.getMinecraft().getRenderManager().renderEngine.getDynamicTextureLocation("name", new DynamicTexture(bi));
      } catch (Exception var8) {
      }

      this.translate = new Translate(0.0F, 0.0F);
      this.categoryPanes.clear();
      int x = 10;
      ScaledResolution sr = new ScaledResolution(this.mc);
      this.buttonList.add(new GuiButton2(2, 0, sr.getScaledHeight() - 55, 200, 200, "Ghost"));

      for(Category category : Category.values()) {
         CategoryPaneOwn categoryPane = new CategoryPaneOwn(x, 10, 100, 20, category, this);
         Aqua.INSTANCE.fileUtil.loadClickGuiOwn(categoryPane);
         this.categoryPanes.add(categoryPane);
         x += 110;
      }

      awaitingClose = false;
      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution scaledRes = new ScaledResolution(this.mc);
      float posX = (float)Aqua.setmgr.getSetting("GuiElementsPosX").getCurrentNumber();
      float posY = (float)Aqua.setmgr.getSetting("GuiElementsPosY").getCurrentNumber();
      float width1 = (float)Aqua.setmgr.getSetting("GuiElementsWidth").getCurrentNumber();
      float height1 = (float)Aqua.setmgr.getSetting("GuiElementsHeight").getCurrentNumber();
      float alpha1 = (float)Aqua.setmgr.getSetting("GuiElementsBackgroundAlpha").getCurrentNumber();
      if (Aqua.moduleManager.getModuleByName("GuiElements").isToggled()) {
         if (Aqua.setmgr.getSetting("GuiElementsCustomPic").isState()) {
            try {
               RenderUtil.drawImage(
                  (int)((float)scaledRes.getScaledWidth() - this.animate.getValue() - posX),
                  (int)((float)scaledRes.getScaledHeight() - posY),
                  (int)width1,
                  (int)height1,
                  this.resourceLocation
               );
            } catch (Exception var19) {
            }
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
            try {
               RenderUtil.drawImage(
                  (int)((float)scaledRes.getScaledWidth() - this.animate.getValue() - posX),
                  (int)((float)scaledRes.getScaledHeight() - posY),
                  (int)width1,
                  (int)height1,
                  this.resourceLocation
               );
            } catch (Exception var18) {
            }
         }
      }

      this.translate.interpolate((float)width, (float)height, 4.0);
      double xmod = (double)((float)width / 2.0F - this.translate.getX() / 2.0F);
      double ymod = (double)((float)height / 2.0F - this.translate.getY() / 2.0F);
      GlStateManager.translate(xmod, ymod, 0.0);
      GlStateManager.scale(this.translate.getX() / (float)width, this.translate.getY() / (float)height, 1.0F);
      if (awaitingClose && (int)this.animate.getValue() == (int)this.animate.getMin()) {
         this.mc.displayGuiScreen(this.parentScreen);
      }

      ScaledResolution sr = new ScaledResolution(this.mc);
      if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
         Shadow.drawGlow(() -> RenderUtil.drawRoundedRect(5.0, (double)(sr.getScaledHeight() - 55), 35.0, 35.0, 3.0, new Color(0, 0, 0, 100).getRGB()), false);
      }

      RenderUtil.drawRoundedRect2Alpha(5.0, (double)(sr.getScaledHeight() - 55), 35.0, 35.0, 3.0, new Color(0, 0, 0, 100));
      Aqua.INSTANCE.comfortaa4.drawStringWithShadow("Config", 7.5F, (float)(sr.getScaledHeight() - 43), -1);
      this.animate.setEase(Easing.BACK_IN_OUT).setMin(1.0F).setMax(10.0F).setSpeed(25.0F).setReversed(awaitingClose).update();
      GlStateManager.pushMatrix();
      float scale = this.animate.getValue() / 10.0F * 0.06F;
      if (this.parentScreen != null) {
         this.parentScreen.drawScreen(mouseX, mouseY, partialTicks);
      }

      for(CategoryPaneOwn categoryPane : this.categoryPanes) {
         if (Aqua.moduleManager.getModuleByName("Shadow").isToggled()) {
            Shadow.drawGlow(() -> categoryPane.draw(categoryPane.getX(), categoryPane.getY(), mouseX, mouseY), false);
         }

         categoryPane.draw(categoryPane.getX(), categoryPane.getY(), mouseX, mouseY);
      }

      for(CategoryPaneOwn categoryPane : this.categoryPanes) {
         if (Mouse.isButtonDown(0)
            && (
               this.mouseOver(mouseX, mouseY, categoryPane.getX(), categoryPane.getY(), categoryPane.getWidth(), categoryPane.getHeight())
                  || this.current == categoryPane
            )
            && (this.current == categoryPane || this.current == null)) {
            this.current = categoryPane;
            categoryPane.setX(mouseX - categoryPane.getWidth() / 2);
            categoryPane.setY(mouseY - categoryPane.getHeight() / 2);
         }
      }

      if (!Mouse.isButtonDown(0)) {
         this.current = null;
      }

      GlStateManager.popMatrix();
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);

      for(CategoryPaneOwn categoryPane : this.categoryPanes) {
         categoryPane.clickMouse(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

      for(CategoryPaneOwn categoryPane : this.categoryPanes) {
         categoryPane.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
      }
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);

      for(CategoryPaneOwn categoryPane : this.categoryPanes) {
         categoryPane.mouseReleased(mouseX, mouseY, state);
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      awaitingClose = true;
      super.keyTyped(typedChar, keyCode);
   }

   @Override
   public void onGuiClosed() {
      Aqua.INSTANCE.fileUtil.saveClickGuiOwn(this);
      super.onGuiClosed();
   }

   @Override
   public boolean doesGuiPauseGame() {
      return false;
   }

   private boolean mouseOver(int x, int y, int modX, int modY, int modWidth, int modHeight) {
      return x >= modX && x <= modX + modWidth && y >= modY && y <= modY + modHeight;
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
      if (button.id == 2) {
      }
   }

   public List<CategoryPaneOwn> getCategoryPanes() {
      return this.categoryPanes;
   }
}
