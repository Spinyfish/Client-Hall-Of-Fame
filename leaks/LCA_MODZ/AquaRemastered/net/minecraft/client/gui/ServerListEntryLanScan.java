package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanScan implements GuiListExtended.IGuiListEntry {
   private final Minecraft mc = Minecraft.getMinecraft();

   @Override
   public void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected) {
      int i = y + slotHeight / 2 - FontRenderer.FONT_HEIGHT / 2;
      this.mc
         .fontRendererObj
         .drawString(
            I18n.format("lanServer.scanning"),
            GuiScreen.width / 2 - this.mc.fontRendererObj.getStringWidth(I18n.format("lanServer.scanning")) / 2,
            i,
            16777215
         );
      String s;
      switch((int)(Minecraft.getSystemTime() / 300L % 4L)) {
         case 0:
         default:
            s = "O o o";
            break;
         case 1:
         case 3:
            s = "o O o";
            break;
         case 2:
            s = "o o O";
      }

      this.mc.fontRendererObj.drawString(s, GuiScreen.width / 2 - this.mc.fontRendererObj.getStringWidth(s) / 2, i + FontRenderer.FONT_HEIGHT, 8421504);
   }

   @Override
   public void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_) {
   }

   @Override
   public boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_) {
      return false;
   }

   @Override
   public void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY) {
   }
}
