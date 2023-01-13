package me.uncodable.srt.impl.gui.clickgui2.components;

import me.uncodable.srt.Ries;
import me.uncodable.srt.impl.modules.api.settings.Setting;
import me.uncodable.srt.impl.utils.MathUtils;
import me.uncodable.srt.impl.utils.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Keyboard;

public class RenderSetting {
   private static final Minecraft MC = Minecraft.getMinecraft();
   private final RenderModule parentModule;
   private final Setting setting;
   private boolean dragging;
   private boolean typing;
   private int startX;
   private int startY;
   private int endX;
   private int endY;
   private int draggedX;
   private StringBuilder builder = new StringBuilder();
   private static final int TEXT_COLOR = 16777215;
   private static final int BACKGROUND = -13290187;
   private static final int SETTING_COLOR = -9164133;

   public RenderSetting(RenderModule parentModule, Setting setting, int startX, int startY, int endX, int endY) {
      this.parentModule = parentModule;
      this.setting = setting;
      this.startX = startX;
      this.startY = startY;
      this.endX = endX;
      this.endY = endY;
   }

   public void render(int mouseX, int mouseY) {
      switch(this.setting.getSettingType()) {
         case CHECKBOX:
            Gui.drawRect(this.startX, this.startY, this.endX, this.endY, this.setting.isTicked() ? -9164133 : -13290187);
            RenderUtils.drawTextWithScale(this.setting.getSettingName(), (double)(this.startX + 3), (double)(this.startY + 3), 0.75F, 16777215);
            break;
         case SLIDER:
            double multiplier = (this.setting.getCurrentValue() - this.setting.getMinimumValue())
               / (this.setting.getMinimumValue() + this.setting.getMaximumValue());
            double sliderWidth = (double)(this.endX - this.startX);
            int result = this.draggedX != 0
               ? this.draggedX
               : (int)((double)this.startX + Math.pow(this.setting.getMinimumValue(), 2.0) + sliderWidth * multiplier);
            Gui.drawRect(this.startX, this.startY, this.endX, this.endY, -13290187);
            if (this.dragging && !this.typing) {
               if (mouseX >= this.startX && mouseX <= this.endX) {
                  this.draggedX = mouseX;
               }

               result = MathHelper.clamp_int(mouseX, this.startX, this.endX);
               double newValue = MathHelper.clamp_double(
                  MathUtils.round(
                     (double)(mouseX - this.startX) * (this.setting.getMaximumValue() - this.setting.getMinimumValue()) / sliderWidth
                        + this.setting.getMinimumValue(),
                     2
                  ),
                  this.setting.getMinimumValue(),
                  this.setting.getMaximumValue()
               );
               this.setting.setCurrentValue(this.setting.isTruncate() ? (double)((int)newValue) : newValue);
            }

            if (this.setting.getCurrentValue() >= this.setting.getMaximumValue()) {
               Gui.drawRect(this.startX, this.startY, this.endX, this.endY, -9164133);
            } else {
               Gui.drawRect(this.startX, this.startY, MathHelper.clamp_int(result, this.startX, this.endX), this.endY, -9164133);
               Gui.drawRect(MathHelper.clamp_int(result, this.startX, this.endX), this.startY, this.endX, this.endY, -13290187);
            }

            try {
               RenderUtils.drawTextWithScale(
                  String.format(
                     "%s: %s",
                     this.setting.getSettingName(),
                     this.typing
                        ? (this.builder.length() > 0 ? Double.parseDouble(this.builder.toString()) : "Type in a value...")
                        : this.setting.getCurrentValue()
                  ),
                  (double)(this.startX + 3),
                  (double)(this.startY + 3),
                  0.75F,
                  16777215
               );
            } catch (NumberFormatException var10) {
               RenderUtils.drawTextWithScale(
                  String.format("%s: %s", this.setting.getSettingName(), this.setting.getCurrentValue()),
                  (double)(this.startX + 3),
                  (double)(this.startY + 3),
                  0.75F,
                  16777215
               );
            }
            break;
         case COMBO_BOX:
            Gui.drawRect(this.startX, this.startY, this.endX, this.endY, -13290187);
            RenderUtils.drawTextWithScale(
               String.format("%s: %s", this.setting.getSettingName(), this.setting.getCurrentCombo()),
               (double)(this.startX + 3),
               (double)(this.startY + 3),
               0.75F,
               16777215
            );
      }
   }

   public void onClicked(int mouseX, int mouseY, int mouseButton) {
      switch(this.setting.getSettingType()) {
         case CHECKBOX:
            if (this.hoverOver(mouseX, mouseY) && mouseButton == 0) {
               this.setting.setTicked(!this.setting.isTicked());
            }
            break;
         case SLIDER:
            if (this.hoverOver(mouseX, mouseY)) {
               switch(mouseButton) {
                  case 0:
                     this.dragging = true;
                     break;
                  case 2:
                     this.typing = true;
               }
            }
            break;
         case COMBO_BOX:
            if (this.hoverOver(mouseX, mouseY)) {
               switch(mouseButton) {
                  case 0:
                     if (this.setting.getComboIndex() < this.setting.getCombos().length - 1) {
                        this.setting.setComboIndex(this.setting.getComboIndex() + 1);
                        this.setting.setCurrentCombo(this.setting.getCombos()[this.setting.getComboIndex()]);
                     }
                     break;
                  case 1:
                     if (this.setting.getComboIndex() > 0) {
                        this.setting.setComboIndex(this.setting.getComboIndex() - 1);
                        this.setting.setCurrentCombo(this.setting.getCombos()[this.setting.getComboIndex()]);
                     }
               }
            }
      }
   }

   public boolean hoverOver(int mouseX, int mouseY) {
      return mouseX >= this.startX && mouseX <= this.endX && mouseY >= this.startY && mouseY <= this.endY;
   }

   public void keyTyped(int keyCode) {
      if (this.typing) {
         if (keyCode >= 2 && keyCode <= 10) {
            this.builder.append(keyCode - 1);
         } else if (keyCode != 11 && keyCode != 28) {
            Ries.INSTANCE.msg(String.format("Invalid key supplied. Key: %s (%d)", Keyboard.getKeyName(keyCode), keyCode));
         }

         switch(keyCode) {
            case 11:
               this.builder.append(0);
               break;
            case 28:
               double value = Double.parseDouble(this.builder.toString());
               if (!MC.gameSettings.expertMode) {
                  if (value > this.setting.getMaximumValue()) {
                     Ries.INSTANCE
                        .msg(
                           "The value supplied is greater than the set maximum allowed value. The value has been rounded downwards to the maximum allowed. To bypass this limit, enable expert mode."
                        );
                     value = this.setting.getMaximumValue();
                  } else if (value < this.setting.getMinimumValue()) {
                     Ries.INSTANCE
                        .msg(
                           "The value supplied is less than the set minimum allowed value. The value has been rounded upwards to the minimum allowed. To bypass this limit, enable expert mode."
                        );
                     value = this.setting.getMinimumValue();
                  }
               }

               this.setting.setCurrentValue(value);
               this.builder = new StringBuilder();
               this.draggedX = 0;
               this.typing = false;
         }
      }
   }

   public void mouseReleased() {
      this.dragging = false;
   }

   public RenderModule getParentModule() {
      return this.parentModule;
   }

   public Setting getSetting() {
      return this.setting;
   }

   public boolean isDragging() {
      return this.dragging;
   }

   public boolean isTyping() {
      return this.typing;
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

   public int getDraggedX() {
      return this.draggedX;
   }

   public StringBuilder getBuilder() {
      return this.builder;
   }

   public void setDragging(boolean dragging) {
      this.dragging = dragging;
   }

   public void setTyping(boolean typing) {
      this.typing = typing;
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

   public void setDraggedX(int draggedX) {
      this.draggedX = draggedX;
   }

   public void setBuilder(StringBuilder builder) {
      this.builder = builder;
   }
}
