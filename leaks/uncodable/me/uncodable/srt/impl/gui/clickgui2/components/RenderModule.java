package me.uncodable.srt.impl.gui.clickgui2.components;

import java.util.ArrayList;
import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.modules.api.Module;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

public class RenderModule {
   private static final Minecraft MC = Minecraft.getMinecraft();
   private final RenderTab parentTab;
   private final Module module;
   private RenderSetting firstSetting;
   private RenderSetting lastSetting;
   private int startX;
   private int startY;
   private int endX;
   private int endY;
   private boolean dropped;
   private boolean binding;
   private boolean setup;
   private final ArrayList<RenderSetting> renderSettings = new ArrayList<>();
   private static final int TOGGLED_OFF_COLOR = -14079185;
   private static final int TOGGLED_ON_COLOR = -5435905;
   private static final int TEXT_COLOR = 16777215;
   private static final int BACKDROP = Integer.MIN_VALUE;

   public RenderModule(RenderTab parentTab, Module module, int startX, int startY, int endX, int endY) {
      this.parentTab = parentTab;
      this.module = module;
      this.startX = startX;
      this.startY = startY;
      this.endX = endX;
      this.endY = endY;
      this.setup();
   }

   public void setup() {
      if (!this.setup) {
         for(int settingIndex = 0; settingIndex < Ries.INSTANCE.getSettingManager().getAllSettings(this.module).size(); ++settingIndex) {
            this.renderSettings
               .add(
                  new RenderSetting(
                     this,
                     Ries.INSTANCE.getSettingManager().getAllSettings(this.module).get(settingIndex),
                     this.endX + 4,
                     this.startY + 16 * settingIndex + 2,
                     this.endX + 90 - 4,
                     this.endY + 16 * settingIndex - 2
                  )
               );
         }

         this.firstSetting = this.renderSettings.get(0);
         this.lastSetting = this.renderSettings.get(this.renderSettings.size() - 1);
         this.setup = true;
      }
   }

   public void render(int mouseX, int mouseY) {
      for(int settingIndex = 0; settingIndex < this.renderSettings.size(); ++settingIndex) {
         this.renderSettings.get(settingIndex).setStartX(this.endX + 4);
         this.renderSettings.get(settingIndex).setStartY(this.startY + 16 * settingIndex + 2);
         this.renderSettings.get(settingIndex).setEndX(this.endX + 90 - 4);
         this.renderSettings.get(settingIndex).setEndY(this.endY + 16 * settingIndex - 2);
      }

      Gui.drawRect(this.startX, this.startY, this.endX, this.endY, this.module.isEnabled() ? -5435905 : -14079185);
      if (this.binding) {
         MC.fontRendererObj.drawStringWithShadow("Press a key...", (float)(this.startX + 4), (float)(this.startY + 5), 16777215);
      } else {
         MC.fontRendererObj
            .drawStringWithShadow(
               String.format(
                  "%s %s",
                  this.module.getInfo().name(),
                  this.module.getPrimaryKey() == 0 ? "" : String.format("(%s)", Keyboard.getKeyName(this.module.getPrimaryKey()))
               ),
               (float)(this.startX + 4),
               (float)(this.startY + 5),
               16777215
            );
      }

      if (!Ries.INSTANCE.getSettingManager().getAllSettings(this.module).isEmpty()) {
         GL11.glPushMatrix();
         GL11.glTranslatef((float)(this.endX - 8), (float)(this.startY + 5), 0.0F);
         if (this.dropped) {
            GL11.glTranslatef(5.0F, 2.0F, 0.0F);
            GL11.glRotatef(90.0F, 0.0F, 0.0F, 1.0F);
         }

         MC.fontRendererObj.drawStringWithShadow(">", 0.0F, 0.0F, 16777215);
         GL11.glPopMatrix();
      }

      if (this.dropped) {
         Gui.drawRect(
            this.firstSetting.getStartX() - 2,
            this.firstSetting.getStartY() - 4,
            this.lastSetting.getEndX() + 2,
            this.lastSetting.getEndY() + 4,
            Integer.MIN_VALUE
         );
         this.renderSettings.forEach(renderSetting -> renderSetting.render(mouseX, mouseY));
      }
   }

   public void onClicked(int mouseX, int mouseY, int mouseButton) {
      if (this.hoverOver(mouseX, mouseY)) {
         switch(mouseButton) {
            case 0:
               this.module.toggle();
               break;
            case 1:
               this.dropped = !this.dropped;
               break;
            case 2:
               this.binding = true;
         }
      }

      if (this.dropped) {
         this.renderSettings.forEach(renderSetting -> renderSetting.onClicked(mouseX, mouseY, mouseButton));
      }
   }

   public boolean hoverOver(int mouseX, int mouseY) {
      return mouseX >= this.startX && mouseX <= this.endX && mouseY >= this.startY && mouseY <= this.endY;
   }

   public void keyTyped(int keyCode) {
      if (this.binding) {
         if (keyCode == 1) {
            this.module.setPrimaryKey(0);
         } else {
            this.module.setPrimaryKey(keyCode);
         }

         this.binding = false;
      }

      if (this.dropped) {
         this.renderSettings.forEach(renderSetting -> renderSetting.keyTyped(keyCode));
      }
   }

   public void mouseReleased() {
      this.renderSettings.forEach(RenderSetting::mouseReleased);
   }

   public RenderTab getParentTab() {
      return this.parentTab;
   }

   public Module getModule() {
      return this.module;
   }

   public RenderSetting getFirstSetting() {
      return this.firstSetting;
   }

   public RenderSetting getLastSetting() {
      return this.lastSetting;
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

   public boolean isBinding() {
      return this.binding;
   }

   public boolean isSetup() {
      return this.setup;
   }

   public ArrayList<RenderSetting> getRenderSettings() {
      return this.renderSettings;
   }

   public void setFirstSetting(RenderSetting firstSetting) {
      this.firstSetting = firstSetting;
   }

   public void setLastSetting(RenderSetting lastSetting) {
      this.lastSetting = lastSetting;
   }

   public void setStartX(int startX) {
      this.startX = startX;
   }

   public void setStartY(int startY) {
      this.startY = startY;
   }

   public void setEndX(int endX) {
      this.endX = endX;
   }

   public void setEndY(int endY) {
      this.endY = endY;
   }

   public void setDropped(boolean dropped) {
      this.dropped = dropped;
   }

   public void setBinding(boolean binding) {
      this.binding = binding;
   }

   public void setSetup(boolean setup) {
      this.setup = setup;
   }
}
