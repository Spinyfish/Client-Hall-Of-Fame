package intent.AquaDev.aqua.gui.aqua;

import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.fr.lavache.anime.Animate;
import intent.AquaDev.aqua.gui.ConfigScreen;
import intent.AquaDev.aqua.gui.aqua.components.CategoryPaneNovoline;
import intent.AquaDev.aqua.gui.novolineOld.themesScreen.ThemeScreen;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.modules.visual.Blur;
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

public class ClickguiScreenNovoline extends GuiScreen {
   Translate translate;
   private ResourceLocation resourceLocation;
   public static boolean awaitingClose = true;
   public static boolean isDragging;
   private final List<CategoryPaneNovoline> categoryPanes = new ArrayList<>();
   private final GuiScreen parentScreen;
   public CategoryPaneNovoline current = null;
   private final Animate animate = new Animate();

   public ClickguiScreenNovoline(GuiScreen parentScreen) {
      this.parentScreen = parentScreen;
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
      int x = 10;
      ScaledResolution sr = new ScaledResolution(this.mc);
      this.buttonList.add(new GuiButton2(2, 0, sr.getScaledHeight() - 55, 31, 200, "Ghost"));
      this.buttonList.add(new GuiButton2(911, 30, sr.getScaledHeight() - 55, 55, 200, "Ghost"));

      for(Category category : Category.values()) {
         CategoryPaneNovoline categoryPane = new CategoryPaneNovoline(x, 10, 120, 20, category, this);
         this.categoryPanes.add(categoryPane);
         x += 120;
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
            } catch (Exception var18) {
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
            } catch (Exception var17) {
            }
         }
      }

      this.translate.interpolate((float)width, (float)height, 4.0);
      double xmod = (double)((float)width / 2.0F - this.translate.getX() / 2.0F);
      double ymod = (double)((float)height / 2.0F - this.translate.getY() / 2.0F);
      GlStateManager.translate(xmod, ymod, 0.0);
      GlStateManager.scale(this.translate.getX() / (float)width, this.translate.getY() / (float)height, 1.0F);
      if (awaitingClose) {
         this.mc.displayGuiScreen(this.parentScreen);
      }

      ScaledResolution sr = new ScaledResolution(this.mc);
      RenderUtil.drawImage(30, sr.getScaledHeight() - 57, 50, 50, new ResourceLocation("Aqua/gui/themes.png"));
      RenderUtil.drawImage(1, sr.getScaledHeight() - 49, 35, 35, new ResourceLocation("Aqua/gui/configs.png"));
      GlStateManager.pushMatrix();
      if (this.parentScreen != null) {
         this.parentScreen.drawScreen(mouseX, mouseY, partialTicks);
      }

      for(CategoryPaneNovoline categoryPane : this.categoryPanes) {
         categoryPane.draw(categoryPane.getX(), categoryPane.getY(), mouseX, mouseY);
      }

      for(CategoryPaneNovoline categoryPane : this.categoryPanes) {
         if (Mouse.isButtonDown(0)
            && (
               this.mouseOver(mouseX, mouseY, categoryPane.getX(), categoryPane.getY(), categoryPane.getWidth(), categoryPane.getHeight())
                  || this.current == categoryPane
            )
            && (this.current == categoryPane || this.current == null)) {
            this.current = categoryPane;
            categoryPane.setX(mouseX - categoryPane.getWidth() / 2);
            categoryPane.setY(mouseY - categoryPane.getHeight() / 2);
            isDragging = true;
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

      for(CategoryPaneNovoline categoryPane : this.categoryPanes) {
         categoryPane.clickMouse(mouseX, mouseY, mouseButton);
      }
   }

   @Override
   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

      for(CategoryPaneNovoline categoryPane : this.categoryPanes) {
         categoryPane.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
      }
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);

      for(CategoryPaneNovoline categoryPane : this.categoryPanes) {
         categoryPane.mouseReleased(mouseX, mouseY, state);
         isDragging = false;
      }
   }

   @Override
   protected void keyTyped(char typedChar, int keyCode) throws IOException {
      if (keyCode != 1 && keyCode != Aqua.moduleManager.getModuleByName("GUI").getKeyBind()) {
         super.keyTyped(typedChar, keyCode);
      } else {
         awaitingClose = true;
      }
   }

   @Override
   public void onGuiClosed() {
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
         this.mc.displayGuiScreen(new ConfigScreen());
      }

      if (button.id == 911) {
         this.mc.displayGuiScreen(new ThemeScreen());
      }
   }

   public List<CategoryPaneNovoline> getCategoryPanes() {
      return this.categoryPanes;
   }
}
