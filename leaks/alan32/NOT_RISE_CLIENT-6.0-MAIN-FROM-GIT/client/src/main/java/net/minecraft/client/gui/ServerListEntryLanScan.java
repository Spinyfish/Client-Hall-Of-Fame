package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

public class ServerListEntryLanScan implements GuiListExtended.IGuiListEntry {
    private final Minecraft mc = Minecraft.getMinecraft();

    public void drawEntry(final int slotIndex, final int x, final int y, final int listWidth, final int slotHeight, final int mouseX, final int mouseY, final boolean isSelected) {
        final int i = y + slotHeight / 2 - this.mc.fontRendererObj.FONT_HEIGHT / 2;
        this.mc.fontRendererObj.drawString(I18n.format("lanServer.scanning"), this.mc.currentScreen.width / 2 - this.mc.fontRendererObj.width(I18n.format("lanServer.scanning")) / 2, i, 16777215);
        final String s;

        switch ((int) (Minecraft.getSystemTime() / 300L % 4L)) {
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

        this.mc.fontRendererObj.drawString(s, this.mc.currentScreen.width / 2 - this.mc.fontRendererObj.width(s) / 2, i + this.mc.fontRendererObj.FONT_HEIGHT, 8421504);
    }

    public void setSelected(final int p_178011_1_, final int p_178011_2_, final int p_178011_3_) {
    }

    /**
     * Returns true if the mouse has been pressed on this control.
     */
    public boolean mousePressed(final int slotIndex, final int p_148278_2_, final int p_148278_3_, final int p_148278_4_, final int p_148278_5_, final int p_148278_6_) {
        return false;
    }

    /**
     * Fired when the mouse button is released. Arguments: index, x, y, mouseEvent, relativeX, relativeY
     */
    public void mouseReleased(final int slotIndex, final int x, final int y, final int mouseEvent, final int relativeX, final int relativeY) {
    }
}
