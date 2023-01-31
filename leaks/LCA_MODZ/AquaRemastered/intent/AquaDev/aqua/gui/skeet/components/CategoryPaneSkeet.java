package intent.AquaDev.aqua.gui.skeet.components;

import intent.AquaDev.aqua.gui.skeet.ClickGuiScreenSkeet;
import intent.AquaDev.aqua.modules.Category;
import intent.AquaDev.aqua.utils.MouseClicker;
import net.minecraft.client.gui.Gui;

public class CategoryPaneSkeet extends Gui {
   private int x;
   private int y;
   private final int width;
   private final int height;
   private final Category category;
   private final MouseClicker checker = new MouseClicker();

   public CategoryPaneSkeet(int x, int y, int width, int height, Category category, ClickGuiScreenSkeet novoline) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
      this.category = category;
   }

   public void draw(int posX, int posY, int mouseX, int mouseY) {
      Gui.drawRect2((double)this.x, (double)this.y, (double)this.width, (double)this.height, Integer.MIN_VALUE);
   }

   public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
   }

   public void mouseReleased(int mouseX, int mouseY, int state) {
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
