package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;
import org.lwjgl.input.Mouse;

public abstract class GuiSlot {
    protected final Minecraft mc;
    protected int width;
    protected int height;

    /**
     * The top of the slot container. Affects the overlays and scrolling.
     */
    protected int top;

    /**
     * The bottom of the slot container. Affects the overlays and scrolling.
     */
    protected int bottom;
    protected int right;
    protected int left;

    /**
     * The height of a slot.
     */
    protected final int slotHeight;

    /**
     * The buttonID of the button used to scroll up
     */
    private int scrollUpButtonID;

    /**
     * The buttonID of the button used to scroll down
     */
    private int scrollDownButtonID;
    protected int mouseX;
    protected int mouseY;
    protected boolean field_148163_i = true;

    /**
     * Where the mouse was in the window when you first clicked to scroll
     */
    protected int initialClickY = -2;

    /**
     * What to multiply the amount you moved your mouse by (used for slowing down scrolling when over the items and not
     * on the scroll bar)
     */
    protected float scrollMultiplier;

    /**
     * How far down this slot has been scrolled
     */
    protected float amountScrolled;

    /**
     * The element in the list that was selected
     */
    protected int selectedElement = -1;

    /**
     * The time when this button was last clicked.
     */
    protected long lastClicked;
    protected boolean field_178041_q = true;

    /**
     * Set to true if a selected element in this gui will show an outline box
     */
    protected boolean showSelectionBox = true;
    protected boolean hasListHeader;
    protected int headerPadding;
    private boolean enabled = true;

    public GuiSlot(final Minecraft mcIn, final int width, final int height, final int topIn, final int bottomIn, final int slotHeightIn) {
        this.mc = mcIn;
        this.width = width;
        this.height = height;
        this.top = topIn;
        this.bottom = bottomIn;
        this.slotHeight = slotHeightIn;
        this.left = 0;
        this.right = width;
    }

    public void setDimensions(final int widthIn, final int heightIn, final int topIn, final int bottomIn) {
        this.width = widthIn;
        this.height = heightIn;
        this.top = topIn;
        this.bottom = bottomIn;
        this.left = 0;
        this.right = widthIn;
    }

    public void setShowSelectionBox(final boolean showSelectionBoxIn) {
        this.showSelectionBox = showSelectionBoxIn;
    }

    /**
     * Sets hasListHeader and headerHeight. Params: hasListHeader, headerHeight. If hasListHeader is false headerHeight
     * is set to 0.
     */
    protected void setHasListHeader(final boolean hasListHeaderIn, final int headerPaddingIn) {
        this.hasListHeader = hasListHeaderIn;
        this.headerPadding = headerPaddingIn;

        if (!hasListHeaderIn) {
            this.headerPadding = 0;
        }
    }

    protected abstract int getSize();

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected abstract void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY);

    /**
     * Returns true if the element passed in is currently selected
     */
    protected abstract boolean isSelected(int slotIndex);

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight() {
        return this.getSize() * this.slotHeight + this.headerPadding;
    }

    protected abstract void drawBackground();

    protected void func_178040_a(final int p_178040_1_, final int p_178040_2_, final int p_178040_3_) {
    }

    protected abstract void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn);

    /**
     * Handles drawing a list's header row.
     */
    protected void drawListHeader(final int p_148129_1_, final int p_148129_2_, final Tessellator p_148129_3_) {
    }

    protected void func_148132_a(final int p_148132_1_, final int p_148132_2_) {
    }

    protected void func_148142_b(final int p_148142_1_, final int p_148142_2_) {
    }

    public int getSlotIndexFromScreenCoords(final int p_148124_1_, final int p_148124_2_) {
        final int i = this.left + this.width / 2 - this.getListWidth() / 2;
        final int j = this.left + this.width / 2 + this.getListWidth() / 2;
        final int k = p_148124_2_ - this.top - this.headerPadding + (int) this.amountScrolled - 4;
        final int l = k / this.slotHeight;
        return p_148124_1_ < this.getScrollBarX() && p_148124_1_ >= i && p_148124_1_ <= j && l >= 0 && k >= 0 && l < this.getSize() ? l : -1;
    }

    /**
     * Registers the IDs that can be used for the scrollbar's up/down buttons.
     */
    public void registerScrollButtons(final int scrollUpButtonIDIn, final int scrollDownButtonIDIn) {
        this.scrollUpButtonID = scrollUpButtonIDIn;
        this.scrollDownButtonID = scrollDownButtonIDIn;
    }

    /**
     * Stop the thing from scrolling out of bounds
     */
    protected void bindAmountScrolled() {
        this.amountScrolled = MathHelper.clamp_float(this.amountScrolled, 0.0F, (float) this.func_148135_f());
    }

    public int func_148135_f() {
        return Math.max(0, this.getContentHeight() - (this.bottom - this.top - 4));
    }

    /**
     * Returns the amountScrolled field as an integer.
     */
    public int getAmountScrolled() {
        return (int) this.amountScrolled;
    }

    public boolean isMouseYWithinSlotBounds(final int p_148141_1_) {
        return p_148141_1_ >= this.top && p_148141_1_ <= this.bottom && this.mouseX >= this.left && this.mouseX <= this.right;
    }

    /**
     * Scrolls the slot by the given amount. A positive value scrolls down, and a negative value scrolls up.
     */
    public void scrollBy(final int amount) {
        this.amountScrolled += (float) amount;
        this.bindAmountScrolled();
        this.initialClickY = -2;
    }

    public void actionPerformed(final GuiButton button) {
        if (button.enabled) {
            if (button.id == this.scrollUpButtonID) {
                this.amountScrolled -= (float) (this.slotHeight * 2 / 3);
                this.initialClickY = -2;
                this.bindAmountScrolled();
            } else if (button.id == this.scrollDownButtonID) {
                this.amountScrolled += (float) (this.slotHeight * 2 / 3);
                this.initialClickY = -2;
                this.bindAmountScrolled();
            }
        }
    }

    public void drawScreen(final int mouseXIn, final int mouseYIn, final float p_148128_3_) {
        if (this.field_178041_q) {
            this.mouseX = mouseXIn;
            this.mouseY = mouseYIn;
            this.drawBackground();
            final int i = this.getScrollBarX();
            final int j = i + 6;
            this.bindAmountScrolled();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            final Tessellator tessellator = Tessellator.getInstance();
            final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            this.drawContainerBackground(tessellator);
            final int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            final int l = this.top + 4 - (int) this.amountScrolled;

            if (this.hasListHeader) {
                this.drawListHeader(k, l, tessellator);
            }

            this.drawSelectionBox(k, l, mouseXIn, mouseYIn);
            GlStateManager.disableDepth();
            final int i1 = 4;
            this.overlayBackground(0, this.top, 255, 255);
            this.overlayBackground(this.bottom, this.height, 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
            worldrenderer.pos(this.left, this.top + i1, 0.0D).tex(0.0D, 1.0D).func_181669_b(0, 0, 0, 0).endVertex();
            worldrenderer.pos(this.right, this.top + i1, 0.0D).tex(1.0D, 1.0D).func_181669_b(0, 0, 0, 0).endVertex();
            worldrenderer.pos(this.right, this.top, 0.0D).tex(1.0D, 0.0D).func_181669_b(0, 0, 0, 255).endVertex();
            worldrenderer.pos(this.left, this.top, 0.0D).tex(0.0D, 0.0D).func_181669_b(0, 0, 0, 255).endVertex();
            tessellator.draw();
            worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
            worldrenderer.pos(this.left, this.bottom, 0.0D).tex(0.0D, 1.0D).func_181669_b(0, 0, 0, 255).endVertex();
            worldrenderer.pos(this.right, this.bottom, 0.0D).tex(1.0D, 1.0D).func_181669_b(0, 0, 0, 255).endVertex();
            worldrenderer.pos(this.right, this.bottom - i1, 0.0D).tex(1.0D, 0.0D).func_181669_b(0, 0, 0, 0).endVertex();
            worldrenderer.pos(this.left, this.bottom - i1, 0.0D).tex(0.0D, 0.0D).func_181669_b(0, 0, 0, 0).endVertex();
            tessellator.draw();
            final int j1 = this.func_148135_f();

            if (j1 > 0) {
                int k1 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                k1 = MathHelper.clamp_int(k1, 32, this.bottom - this.top - 8);
                int l1 = (int) this.amountScrolled * (this.bottom - this.top - k1) / j1 + this.top;

                if (l1 < this.top) {
                    l1 = this.top;
                }

                worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
                worldrenderer.pos(i, this.bottom, 0.0D).tex(0.0D, 1.0D).func_181669_b(0, 0, 0, 255).endVertex();
                worldrenderer.pos(j, this.bottom, 0.0D).tex(1.0D, 1.0D).func_181669_b(0, 0, 0, 255).endVertex();
                worldrenderer.pos(j, this.top, 0.0D).tex(1.0D, 0.0D).func_181669_b(0, 0, 0, 255).endVertex();
                worldrenderer.pos(i, this.top, 0.0D).tex(0.0D, 0.0D).func_181669_b(0, 0, 0, 255).endVertex();
                tessellator.draw();
                worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
                worldrenderer.pos(i, l1 + k1, 0.0D).tex(0.0D, 1.0D).func_181669_b(128, 128, 128, 255).endVertex();
                worldrenderer.pos(j, l1 + k1, 0.0D).tex(1.0D, 1.0D).func_181669_b(128, 128, 128, 255).endVertex();
                worldrenderer.pos(j, l1, 0.0D).tex(1.0D, 0.0D).func_181669_b(128, 128, 128, 255).endVertex();
                worldrenderer.pos(i, l1, 0.0D).tex(0.0D, 0.0D).func_181669_b(128, 128, 128, 255).endVertex();
                tessellator.draw();
                worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
                worldrenderer.pos(i, l1 + k1 - 1, 0.0D).tex(0.0D, 1.0D).func_181669_b(192, 192, 192, 255).endVertex();
                worldrenderer.pos(j - 1, l1 + k1 - 1, 0.0D).tex(1.0D, 1.0D).func_181669_b(192, 192, 192, 255).endVertex();
                worldrenderer.pos(j - 1, l1, 0.0D).tex(1.0D, 0.0D).func_181669_b(192, 192, 192, 255).endVertex();
                worldrenderer.pos(i, l1, 0.0D).tex(0.0D, 0.0D).func_181669_b(192, 192, 192, 255).endVertex();
                tessellator.draw();
            }

            this.func_148142_b(mouseXIn, mouseYIn);
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public void handleMouseInput() {
        if (this.isMouseYWithinSlotBounds(this.mouseY)) {
            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
                final int i = (this.width - this.getListWidth()) / 2;
                final int j = (this.width + this.getListWidth()) / 2;
                final int k = this.mouseY - this.top - this.headerPadding + (int) this.amountScrolled - 4;
                final int l = k / this.slotHeight;

                if (l < this.getSize() && this.mouseX >= i && this.mouseX <= j && l >= 0 && k >= 0) {
                    this.elementClicked(l, false, this.mouseX, this.mouseY);
                    this.selectedElement = l;
                } else if (this.mouseX >= i && this.mouseX <= j && k < 0) {
                    this.func_148132_a(this.mouseX - i, this.mouseY - this.top + (int) this.amountScrolled - 4);
                }
            }

            if (Mouse.isButtonDown(0) && this.getEnabled()) {
                if (this.initialClickY != -1) {
                    if (this.initialClickY >= 0) {
                        this.amountScrolled -= (float) (this.mouseY - this.initialClickY) * this.scrollMultiplier;
                        this.initialClickY = this.mouseY;
                    }
                } else {
                    boolean flag1 = true;

                    if (this.mouseY >= this.top && this.mouseY <= this.bottom) {
                        final int j2 = (this.width - this.getListWidth()) / 2;
                        final int k2 = (this.width + this.getListWidth()) / 2;
                        final int l2 = this.mouseY - this.top - this.headerPadding + (int) this.amountScrolled - 4;
                        final int i1 = l2 / this.slotHeight;

                        if (i1 < this.getSize() && this.mouseX >= j2 && this.mouseX <= k2 && i1 >= 0 && l2 >= 0) {
                            final boolean flag = i1 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                            this.elementClicked(i1, flag, this.mouseX, this.mouseY);
                            this.selectedElement = i1;
                            this.lastClicked = Minecraft.getSystemTime();
                        } else if (this.mouseX >= j2 && this.mouseX <= k2 && l2 < 0) {
                            this.func_148132_a(this.mouseX - j2, this.mouseY - this.top + (int) this.amountScrolled - 4);
                            flag1 = false;
                        }

                        final int i3 = this.getScrollBarX();
                        final int j1 = i3 + 6;

                        if (this.mouseX >= i3 && this.mouseX <= j1) {
                            this.scrollMultiplier = -1.0F;
                            int k1 = this.func_148135_f();

                            if (k1 < 1) {
                                k1 = 1;
                            }

                            int l1 = (int) ((float) ((this.bottom - this.top) * (this.bottom - this.top)) / (float) this.getContentHeight());
                            l1 = MathHelper.clamp_int(l1, 32, this.bottom - this.top - 8);
                            this.scrollMultiplier /= (float) (this.bottom - this.top - l1) / (float) k1;
                        } else {
                            this.scrollMultiplier = 1.0F;
                        }

                        if (flag1) {
                            this.initialClickY = this.mouseY;
                        } else {
                            this.initialClickY = -2;
                        }
                    } else {
                        this.initialClickY = -2;
                    }
                }
            } else {
                this.initialClickY = -1;
            }

            int i2 = Mouse.getEventDWheel();

            if (i2 != 0) {
                if (i2 > 0) {
                    i2 = -1;
                } else if (i2 < 0) {
                    i2 = 1;
                }

                this.amountScrolled += (float) (i2 * this.slotHeight / 2);
            }
        }
    }

    public void setEnabled(final boolean enabledIn) {
        this.enabled = enabledIn;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    /**
     * Gets the width of the list
     */
    public int getListWidth() {
        return 220;
    }

    /**
     * Draws the selection box around the selected slot element.
     */
    protected void drawSelectionBox(final int p_148120_1_, final int p_148120_2_, final int mouseXIn, final int mouseYIn) {
        final int i = this.getSize();
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        for (int j = 0; j < i; ++j) {
            final int k = p_148120_2_ + j * this.slotHeight + this.headerPadding;
            final int l = this.slotHeight - 4;

            if (k > this.bottom || k + l < this.top) {
                this.func_178040_a(j, p_148120_1_, k);
            }

            if (this.showSelectionBox && this.isSelected(j)) {
                final int i1 = this.left + (this.width / 2 - this.getListWidth() / 2);
                final int j1 = this.left + this.width / 2 + this.getListWidth() / 2;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableTexture2D();
                worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
                worldrenderer.pos(i1, k + l + 2, 0.0D).tex(0.0D, 1.0D).func_181669_b(128, 128, 128, 255).endVertex();
                worldrenderer.pos(j1, k + l + 2, 0.0D).tex(1.0D, 1.0D).func_181669_b(128, 128, 128, 255).endVertex();
                worldrenderer.pos(j1, k - 2, 0.0D).tex(1.0D, 0.0D).func_181669_b(128, 128, 128, 255).endVertex();
                worldrenderer.pos(i1, k - 2, 0.0D).tex(0.0D, 0.0D).func_181669_b(128, 128, 128, 255).endVertex();
                worldrenderer.pos(i1 + 1, k + l + 1, 0.0D).tex(0.0D, 1.0D).func_181669_b(0, 0, 0, 255).endVertex();
                worldrenderer.pos(j1 - 1, k + l + 1, 0.0D).tex(1.0D, 1.0D).func_181669_b(0, 0, 0, 255).endVertex();
                worldrenderer.pos(j1 - 1, k - 1, 0.0D).tex(1.0D, 0.0D).func_181669_b(0, 0, 0, 255).endVertex();
                worldrenderer.pos(i1 + 1, k - 1, 0.0D).tex(0.0D, 0.0D).func_181669_b(0, 0, 0, 255).endVertex();
                tessellator.draw();
                GlStateManager.enableTexture2D();
            }

            if (!(this instanceof GuiResourcePackList) || k >= this.top - this.slotHeight && k <= this.bottom) {
                this.drawSlot(j, p_148120_1_, k, l, mouseXIn, mouseYIn);
            }
        }
    }

    protected int getScrollBarX() {
        return this.width / 2 + 124;
    }

    /**
     * Overlays the background to hide scrolled items
     */
    protected void overlayBackground(final int startY, final int endY, final int startAlpha, final int endAlpha) {
        final Tessellator tessellator = Tessellator.getInstance();
        final WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        final float f = 32.0F;
        worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
        worldrenderer.pos(this.left, endY, 0.0D).tex(0.0D, (float) endY / 32.0F).func_181669_b(64, 64, 64, endAlpha).endVertex();
        worldrenderer.pos(this.left + this.width, endY, 0.0D).tex((float) this.width / 32.0F, (float) endY / 32.0F).func_181669_b(64, 64, 64, endAlpha).endVertex();
        worldrenderer.pos(this.left + this.width, startY, 0.0D).tex((float) this.width / 32.0F, (float) startY / 32.0F).func_181669_b(64, 64, 64, startAlpha).endVertex();
        worldrenderer.pos(this.left, startY, 0.0D).tex(0.0D, (float) startY / 32.0F).func_181669_b(64, 64, 64, startAlpha).endVertex();
        tessellator.draw();
    }

    /**
     * Sets the left and right bounds of the slot. Param is the left bound, right is calculated as left + width.
     */
    public void setSlotXBoundsFromLeft(final int leftIn) {
        this.left = leftIn;
        this.right = leftIn + this.width;
    }

    public int getSlotHeight() {
        return this.slotHeight;
    }

    protected void drawContainerBackground(final Tessellator p_drawContainerBackground_1_) {
        final WorldRenderer worldrenderer = p_drawContainerBackground_1_.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        final float f = 32.0F;
        worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
        worldrenderer.pos(this.left, this.bottom, 0.0D).tex((float) this.left / f, (float) (this.bottom + (int) this.amountScrolled) / f).func_181669_b(32, 32, 32, 255).endVertex();
        worldrenderer.pos(this.right, this.bottom, 0.0D).tex((float) this.right / f, (float) (this.bottom + (int) this.amountScrolled) / f).func_181669_b(32, 32, 32, 255).endVertex();
        worldrenderer.pos(this.right, this.top, 0.0D).tex((float) this.right / f, (float) (this.top + (int) this.amountScrolled) / f).func_181669_b(32, 32, 32, 255).endVertex();
        worldrenderer.pos(this.left, this.top, 0.0D).tex((float) this.left / f, (float) (this.top + (int) this.amountScrolled) / f).func_181669_b(32, 32, 32, 255).endVertex();
        p_drawContainerBackground_1_.draw();
    }
}
