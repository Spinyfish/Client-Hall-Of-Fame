package intent.AquaDev.aqua.gui.skeet;

import intent.AquaDev.aqua.gui.skeet.components.CategoryPaneSkeet;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.utils.RenderUtil;
import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ClickGuiScreenSkeet extends GuiScreen {
   private final List<CategoryPaneSkeet> categoryPanes = new ArrayList<>();

   @Override
   public void initGui() {
      int x = 120;

      for(Category category : Category.values()) {
         CategoryPaneSkeet categoryPane = new CategoryPaneSkeet(x, 10, 100, 20, category, this);
         this.categoryPanes.add(categoryPane);
         x += 60;
      }

      super.initGui();
   }

   @Override
   public void drawScreen(int mouseX, int mouseY, float partialTicks) {
      ScaledResolution sr = new ScaledResolution(this.mc);
      int posX = (int)((float)sr.getScaledWidth() / 2.0F - 170.0F);
      int posY = 120;
      int width = (int)((float)sr.getScaledWidth() / 2.0F + 170.0F);
      int height = (int)((float)sr.getScaledHeight() / 2.0F + 150.0F);
      Gui.drawRect2((double)(posX - 2), (double)(posY - 2), (double)(width + 2), (double)height + 1.5, new Color(60, 60, 60, 255).getRGB());
      GL11.glEnable(3089);
      RenderUtil.scissor((double)posX, (double)posY, (double)(width - posX), (double)(height - posY));
      Gui.drawRect2((double)posX, (double)posY, (double)width, (double)height, new Color(20, 20, 20, 255).getRGB());
      RenderUtil.drawImageDarker(0, 0, this.mc.displayWidth, this.mc.displayHeight, new ResourceLocation("Aqua/gui/skeet.png"));
      RenderUtil.drawImage(posX + 5, posY + 5, 40, 40, new ResourceLocation("Aqua/gui/1.png"));
      RenderUtil.drawImage(posX + 2, posY + 85, 40, 40, new ResourceLocation("Aqua/gui/2.png"));
      RenderUtil.drawImage(posX + 9, posY + 135, 30, 30, new ResourceLocation("Aqua/gui/4.png"));
      RenderUtil.drawImage(posX + 9, posY + 175, 30, 30, new ResourceLocation("Aqua/gui/3.png"));
      GL11.glDisable(3089);
      RenderUtil.drawRGBLineHorizontal((double)((float)posX + 0.5F), (double)(posY + 1), (double)(width - posX - 1), 2.0F, 0.2F, true);
   }

   @Override
   protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
      super.mouseClicked(mouseX, mouseY, mouseButton);

      for(CategoryPaneSkeet var5 : this.categoryPanes) {
         ;
      }
   }

   @Override
   protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
      super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);

      for(CategoryPaneSkeet categoryPane : this.categoryPanes) {
         categoryPane.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
      }
   }

   @Override
   protected void mouseReleased(int mouseX, int mouseY, int state) {
      super.mouseReleased(mouseX, mouseY, state);
   }

   @Override
   public void onGuiClosed() {
      super.onGuiClosed();
   }

   private boolean mouseOver(int x, int y, int modX, int modY, int modWidth, int modHeight) {
      return x >= modX && x <= modX + modWidth && y >= modY && y <= modY + modHeight;
   }

   @Override
   protected void actionPerformed(GuiButton button) throws IOException {
   }

   public List<CategoryPaneSkeet> getCategoryPanes() {
      return this.categoryPanes;
   }
}
