package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;

public abstract class GuiListExtended extends GuiSlot {
    public GuiListExtended(final Minecraft mcIn, final int widthIn, final int heightIn, final int topIn, final int bottomIn, final int slotHeightIn) {
        super(mcIn, widthIn, heightIn, topIn, bottomIn, slotHeightIn);
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected void elementClicked(final int slotIndex, final boolean isDoubleClick, final int mouseX, final int mouseY) {
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(final int slotIndex) {
        return false;
    }

    protected void drawBackground() {
    }

    protected void drawSlot(final int entryID, final int p_180791_2_, final int p_180791_3_, final int p_180791_4_, final int mouseXIn, final int mouseYIn) {
        this.getListEntry(entryID).drawEntry(entryID, p_180791_2_, p_180791_3_, this.getListWidth(), p_180791_4_, mouseXIn, mouseYIn, this.getSlotIndexFromScreenCoords(mouseXIn, mouseYIn) == entryID);
    }

    protected void func_178040_a(final int p_178040_1_, final int p_178040_2_, final int p_178040_3_) {
        this.getListEntry(p_178040_1_).setSelected(p_178040_1_, p_178040_2_, p_178040_3_);
    }

    public boolean mouseClicked(final int mouseX, final int mouseY, final int mouseEvent) {
        if (this.isMouseYWithinSlotBounds(mouseY)) {
            final int i = this.getSlotIndexFromScreenCoords(mouseX, mouseY);

            if (i >= 0) {
                final int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
                final int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
                final int l = mouseX - j;
                final int i1 = mouseY - k;

                if (this.getListEntry(i).mousePressed(i, mouseX, mouseY, mouseEvent, l, i1)) {
                    this.setEnabled(false);
                    return true;
                }
            }
        }

        return false;
    }

    public boolean mouseReleased(final int p_148181_1_, final int p_148181_2_, final int p_148181_3_) {
        for (int i = 0; i < this.getSize(); ++i) {
            final int j = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            final int k = this.top + 4 - this.getAmountScrolled() + i * this.slotHeight + this.headerPadding;
            final int l = p_148181_1_ - j;
            final int i1 = p_148181_2_ - k;
            this.getListEntry(i).mouseReleased(i, p_148181_1_, p_148181_2_, p_148181_3_, l, i1);
        }

        this.setEnabled(true);
        return false;
    }

    /**
     * Gets the IGuiListEntry object for the given index
     */
    public abstract GuiListExtended.IGuiListEntry getListEntry(int index);

    public interface IGuiListEntry {
        void setSelected(int p_178011_1_, int p_178011_2_, int p_178011_3_);

        void drawEntry(int slotIndex, int x, int y, int listWidth, int slotHeight, int mouseX, int mouseY, boolean isSelected);

        boolean mousePressed(int slotIndex, int p_148278_2_, int p_148278_3_, int p_148278_4_, int p_148278_5_, int p_148278_6_);

        void mouseReleased(int slotIndex, int x, int y, int mouseEvent, int relativeX, int relativeY);
    }
}
