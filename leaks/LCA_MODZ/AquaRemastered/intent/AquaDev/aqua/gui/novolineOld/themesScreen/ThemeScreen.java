package intent.AquaDev.aqua.gui.novolineOld.themesScreen;

import intent.AquaDev.aqua.Aqua;
import intent.AquaDev.aqua.modules.visual.Blur;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
import java.io.IOException;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class ThemeScreen extends GuiScreen {
   public static boolean themeLoaded;
   public static boolean themeAqua;
   public static boolean themeHero;
   public static boolean themeSigma;
   public static boolean themeJello;
   public static boolean themeXave;
   public static boolean themeRise;
   public static boolean themeRise6;
   public static boolean themeAqua2;
   public static boolean themeTenacity;
   public static boolean legitTheme;

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution scaledResolution = new ScaledResolution(this.mc);
      int posX = scaledResolution.getScaledWidth() / 4;
      int posY = 80;
      int width = scaledResolution.getScaledWidth() / 2 + 10;
      int height = scaledResolution.getScaledHeight() / 2 + 60;
      int cornerRadius = 3;
      Blur.drawBlurred(
         () -> RenderUtil.drawRoundedRect2Alpha((double)posX, (double)posY, (double)width, (double)height, (double)cornerRadius, new Color(0, 0, 0, 100)),
         false
      );
      RenderUtil.drawRoundedRect2Alpha((double)posX, (double)posY, (double)width, (double)height, (double)cornerRadius, new Color(0, 0, 0, 100));
      RenderUtil.drawRoundedRect2Alpha((double)(posX + 3), (double)(posY + 3), 70.0, 40.0, 3.0, new Color(0, 0, 0, 60));
      RenderUtil.drawRoundedRect2Alpha((double)(posX + 3), (double)(posY + 45), 60.0, 40.0, 3.0, new Color(0, 0, 0, 60));
      RenderUtil.drawRoundedRect2Alpha((double)(posX + 80), (double)(posY + 45), 87.0, 40.0, 3.0, new Color(0, 0, 0, 60));
      RenderUtil.drawRoundedRect2Alpha((double)(posX + 285), (double)(posY + 45), 117.0, 40.0, 3.0, new Color(0, 0, 0, 60));
      RenderUtil.drawRoundedRect2Alpha((double)(posX + 180), (double)(posY + 45), 97.0, 40.0, 3.0, new Color(0, 0, 0, 60));
      RenderUtil.drawRoundedRect2Alpha((double)(posX + 80), (double)(posY + 3), 87.0, 40.0, 3.0, new Color(0, 0, 0, 60));
      RenderUtil.drawRoundedRect2Alpha((double)(posX + 178), (double)(posY + 3), 80.0, 40.0, 3.0, new Color(0, 0, 0, 60));
      RenderUtil.drawRoundedRect2Alpha((double)(posX + 268), (double)(posY + 3), 65.0, 40.0, 3.0, new Color(0, 0, 0, 60));
      RenderUtil.drawRoundedRect2Alpha((double)(posX + 340), (double)(posY + 3), 80.0, 40.0, 3.0, new Color(0, 0, 0, 60));
      Aqua.INSTANCE.roboto.drawString("Hero", (float)(posX + 3), (float)(posY + 3), Color.GREEN.getRGB());
      Aqua.INSTANCE.roboto.drawString("Sigma", (float)(posX + 80), (float)(posY + 3), new Color(4, 154, 248, 255).getRGB());
      Aqua.INSTANCE.roboto.drawString("Aqua", (float)(posX + 180), (float)(posY + 3), new Color(0, 255, 245, 255).getRGB());
      Aqua.INSTANCE.roboto.drawString("Jello", (float)(posX + 270), (float)(posY + 3), new Color(163, 163, 163, 255).getRGB());
      Aqua.INSTANCE.roboto.drawString("Xave", (float)(posX + 345), (float)(posY + 3), Color.blue.getRGB());
      Aqua.INSTANCE.roboto.drawString("Rise", (float)(posX + 3), (float)(posY + 45), Color.cyan.getRGB());
      Aqua.INSTANCE.roboto.drawString("Rise 6", (float)(posX + 80), (float)(posY + 45), Color.cyan.getRGB());
      Aqua.INSTANCE.roboto.drawString("Aqua 2", (float)(posX + 180), (float)(posY + 45), Color.blue.getRGB());
      Aqua.INSTANCE.roboto.drawString("Tenacity", (float)(posX + 285), (float)(posY + 45), Color.green.getRGB());
      if (this.mouseOver(mouseX, mouseY, posX + 3, posY + 3, 70, 40) && Mouse.isButtonDown(0)) {
         Display.setTitle("Hero v0.9.18");
         themeLoaded = true;
         themeHero = true;
         themeSigma = false;
         themeJello = false;
         themeXave = false;
         themeRise = false;
         themeRise6 = false;
         themeAqua2 = false;
         themeTenacity = false;
      }

      if (this.mouseOver(mouseX, mouseY, posX + 80, posY + 3, 87, 40) && Mouse.isButtonDown(0)) {
         Display.setTitle("" + Aqua.name + " b" + Aqua.build + " by " + Aqua.dev);
         themeLoaded = true;
         themeSigma = true;
         themeHero = false;
         themeJello = false;
         themeXave = false;
         themeRise = false;
         themeRise6 = false;
         themeAqua2 = false;
         themeTenacity = false;
      }

      if (this.mouseOver(mouseX, mouseY, posX + 180, posY + 3, 80, 40) && Mouse.isButtonDown(0)) {
         Display.setTitle("" + Aqua.name + " b" + Aqua.build + " by " + Aqua.dev);
         themeLoaded = false;
         themeSigma = false;
         themeHero = false;
         themeJello = false;
         themeXave = false;
         themeRise = false;
         themeRise6 = false;
         themeAqua2 = false;
         themeTenacity = false;
      }

      if (this.mouseOver(mouseX, mouseY, posX + 268, posY + 3, 65, 40) && Mouse.isButtonDown(0)) {
         Display.setTitle("" + Aqua.name + " b" + Aqua.build + " by " + Aqua.dev);
         themeLoaded = true;
         themeHero = false;
         themeSigma = false;
         themeJello = true;
         themeXave = false;
         themeRise = false;
         themeRise6 = false;
         themeAqua2 = false;
         themeTenacity = false;
      }

      if (this.mouseOver(mouseX, mouseY, posX + 340, posY + 3, 80, 40) && Mouse.isButtonDown(0)) {
         Display.setTitle("" + Aqua.name + " b" + Aqua.build + " by " + Aqua.dev);
         themeLoaded = true;
         themeHero = false;
         themeSigma = false;
         themeJello = false;
         themeXave = true;
         themeRise = false;
         themeRise6 = false;
         themeAqua2 = false;
         themeTenacity = false;
      }

      if (this.mouseOver(mouseX, mouseY, posX + 3, posY + 45, 60, 40) && Mouse.isButtonDown(0)) {
         Display.setTitle("" + Aqua.name + " b" + Aqua.build + " by " + Aqua.dev);
         themeLoaded = true;
         themeHero = false;
         themeSigma = false;
         themeJello = false;
         themeXave = false;
         themeRise = true;
         themeRise6 = false;
         themeAqua2 = false;
         themeTenacity = false;
      }

      if (this.mouseOver(mouseX, mouseY, posX + 80, posY + 45, 87, 40) && Mouse.isButtonDown(0)) {
         Display.setTitle("" + Aqua.name + " b" + Aqua.build + " by " + Aqua.dev);
         themeLoaded = true;
         themeHero = false;
         themeSigma = false;
         themeJello = false;
         themeXave = false;
         themeRise = false;
         themeRise6 = true;
         themeAqua2 = false;
         themeTenacity = false;
      }

      if (this.mouseOver(mouseX, mouseY, posX + 180, posY + 45, 97, 40) && Mouse.isButtonDown(0)) {
         Display.setTitle("" + Aqua.name + " b" + Aqua.build + " by " + Aqua.dev);
         themeLoaded = true;
         themeHero = false;
         themeSigma = false;
         themeJello = false;
         themeXave = false;
         themeRise = false;
         themeRise6 = false;
         themeAqua2 = true;
         themeTenacity = false;
      }

      if (this.mouseOver(mouseX, mouseY, posX + 285, posY + 45, 117, 40) && Mouse.isButtonDown(0)) {
         Display.setTitle("" + Aqua.name + " b" + Aqua.build + " by " + Aqua.dev);
         themeLoaded = true;
         themeHero = false;
         themeSigma = false;
         themeJello = false;
         themeXave = false;
         themeRise = false;
         themeRise6 = false;
         themeAqua2 = false;
         themeTenacity = true;
      }
   }

   @Override
   public void initGui() {
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
   }

   private boolean mouseOver(int x, int y, int modX, int modY, int modWidth, int modHeight) {
      return x >= modX && x <= modX + modWidth && y >= modY && y <= modY + modHeight;
   }
}
