package me.uncodable.srt.impl.gui.clickgui2.components;

import java.util.ArrayList;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.modules.api.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.opengl.GL11;

public class RenderTab {
   private static final Minecraft MC = Minecraft.getMinecraft();
   private final Module.Category category;
   private RenderModule firstModule;
   private RenderModule lastModule;
   private int startX;
   private int startY;
   private int endX;
   private int endY;
   private boolean dropped;
   private boolean setup;
   private boolean dragging;
   private final ArrayList<RenderModule> renderModules = new ArrayList<>();
   private static final int TEXT_COLOR = 16777215;
   private static final int CATEGORY_COLOR = -5435905;
   private static final int BACKDROP = Integer.MIN_VALUE;
   private static final int SCROLL_BORDER_MAX = 2000;

   public RenderTab(Module.Category category, int startX, int startY, int endX, int endY) {
      this.category = category;
      this.startX = startX;
      this.startY = startY;
      this.endX = endX;
      this.endY = endY;
      this.setup();
   }

   public void setup() {
      if (!this.setup) {
         for(int moduleIndex = 0; moduleIndex < Ries.INSTANCE.getModuleManager().getModulesInCategory(this.category).size(); ++moduleIndex) {
            this.renderModules
               .add(
                  new RenderModule(
                     this,
                     Ries.INSTANCE.getModuleManager().getModulesInCategory(this.category).get(moduleIndex),
                     this.startX + 2,
                     this.startY + 20 * (moduleIndex + 1) + 2,
                     this.endX - 2,
                     this.endY + 20 * (moduleIndex + 1) - 2
                  )
               );
         }

         this.firstModule = this.renderModules.get(0);
         this.lastModule = this.renderModules.get(this.renderModules.size() - 1);
         this.setup = true;
      }
   }

   public void render(int mouseX, int mouseY, int width, int height) {
      if (this.dragging) {
         this.startX = mouseX;
         this.startY = mouseY;
         this.endX = this.startX + 90;
         this.endY = this.startY + 20;
         this.updatePositions();
      }

      Gui.drawRect(this.startX, this.startY, this.endX, this.endY, -5435905);
      MC.fontRendererObj.drawStringWithShadow(this.category.getCategoryName(), (float)(this.startX + 4), (float)(this.startY + 6), 16777215);
      GL11.glPushMatrix();
      GL11.glTranslatef((float)(this.endX - 8), (float)(this.startY + 6), 0.0F);
      if (this.dropped) {
         GL11.glTranslatef(5.0F, 2.0F, 0.0F);
         GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
      }

      MC.fontRendererObj.drawStringWithShadow(">", 0.0F, 0.0F, 16777215);
      GL11.glPopMatrix();
      if (this.dropped) {
         Gui.drawRect(
            this.firstModule.getStartX() - 2,
            this.firstModule.getStartY() - 2,
            this.lastModule.getEndX() + 2,
            this.lastModule.getEndY() + 2,
            Integer.MIN_VALUE
         );
         this.renderModules.forEach(renderModule -> renderModule.render(mouseX, mouseY));
      }
   }

   public void onClicked(int mouseX, int mouseY, int mouseButton) {
      switch(mouseButton) {
         case 0:
            if (this.hoverOver(mouseX, mouseY)) {
               this.dragging = !this.dragging;
            }
            break;
         case 1:
            if (this.hoverOver(mouseX, mouseY)) {
               this.dropped = !this.dropped;
            }
      }

      if (this.dropped) {
         this.renderModules.forEach(renderModule -> renderModule.onClicked(mouseX, mouseY, mouseButton));
      }
   }

   public boolean hoverOver(int mouseX, int mouseY) {
      return mouseX >= this.startX && mouseX <= this.endX && mouseY >= this.startY && mouseY <= this.endY;
   }

   public void keyTyped(int keyCode) {
      this.renderModules.forEach(renderModule -> renderModule.keyTyped(keyCode));
   }

   public void mouseReleased() {
      this.dragging = false;
      this.renderModules.forEach(RenderModule::mouseReleased);
   }

   private void updatePositions() {
      for(int moduleIndex = 0; moduleIndex < this.renderModules.size(); ++moduleIndex) {
         this.renderModules.get(moduleIndex).setStartX(this.startX + 2);
         this.renderModules.get(moduleIndex).setStartY(this.startY + 20 * (moduleIndex + 1) + 2);
         this.renderModules.get(moduleIndex).setEndX(this.endX - 2);
         this.renderModules.get(moduleIndex).setEndY(this.endY + 20 * (moduleIndex + 1) - 2);
      }
   }

   public Module.Category getCategory() {
      return this.category;
   }

   public RenderModule getFirstModule() {
      return this.firstModule;
   }

   public RenderModule getLastModule() {
      return this.lastModule;
   }

   public int getStartX() {
      return this.startX;
   }

   public int getStartY() {
      return this.startY;
   }

   public int getEndX() {
      return this.endX;
   }

   public int getEndY() {
      return this.endY;
   }

   public boolean isDropped() {
      return this.dropped;
   }

   public boolean isSetup() {
      return this.setup;
   }

   public boolean isDragging() {
      return this.dragging;
   }

   public ArrayList<RenderModule> getRenderModules() {
      return this.renderModules;
   }
}
