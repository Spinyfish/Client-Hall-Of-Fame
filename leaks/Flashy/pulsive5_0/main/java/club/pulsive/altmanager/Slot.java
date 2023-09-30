package club.pulsive.altmanager;

import org.lwjgl.input.Mouse;


import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer; 
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.MathHelper;


public abstract class Slot {
    protected final Minecraft mc;
    /**
     * The height of a slot.
     */
    protected final int slotHeight;
    protected int x;
    protected int slotWidth;
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
    /**
     * The buttonID of the button used to scroll up
     */
    private int scrollUpButtonID;
    /**
     * The buttonID of the button used to scroll down
     */
    private int scrollDownButtonID;
    private boolean enabled = true;

    protected boolean overlayBackground = true;

    public Slot(Minecraft mcIn, int x, int slotWidth, int slotHeight, int width, int height, int topIn, int bottomIn) {
        mc = mcIn;
        this.x = x;
        this.slotWidth = slotWidth;
        this.height = height;
        top = topIn;
        bottom = bottomIn;
        this.slotHeight = slotHeight;
        this.width = width;
    }

    public void setDimensions(int xIn, int widthIn, int heightIn, int topIn, int bottomIn) {
        x = xIn;
        height = heightIn;
        top = topIn;
        bottom = bottomIn;
        width = widthIn;
    }

    public void setShowSelectionBox(boolean showSelectionBoxIn) {
        showSelectionBox = showSelectionBoxIn;
    }

    /**
     * Sets hasListHeader and headerHeight. Params: hasListHeader, headerHeight. If hasListHeader is false headerHeight
     * is set to 0.
     */
    protected void setHasListHeader(boolean hasListHeaderIn, int headerPaddingIn) {
        hasListHeader = hasListHeaderIn;
        headerPadding = headerPaddingIn;

        if (!hasListHeaderIn) {
            headerPadding = 0;
        }
    }

    protected abstract int getSize();

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected abstract void elementClicked(int slotIndex, boolean isDoubleClick, int mouseX, int mouseY);

    protected void elementRightClicked(int slotHeight, boolean isDoubleClick, int mouseX, int mouseY) {

    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected abstract boolean isSelected(int slotIndex);

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight() {
        return getSize() * slotHeight + headerPadding;
    }

    protected abstract void drawBackground();

    protected void func_178040_a(int p_178040_1_, int p_178040_2_, int p_178040_3_) {
    }

    protected abstract void drawSlot(int entryID, int p_180791_2_, int p_180791_3_, int p_180791_4_, int mouseXIn, int mouseYIn);

    /**
     * Handles drawing a list's header row.
     */
    protected void drawListHeader(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_) {
    }

    protected void func_148132_a(int p_148132_1_, int p_148132_2_) {
    }

    protected void func_148142_b(int p_148142_1_, int p_148142_2_) {
    }

    public int getSlotIndexFromScreenCoords(int p_148124_1_, int p_148124_2_) {
        int i = x;
        int j = x + getSlotWidth();
        int k = p_148124_2_ - top - headerPadding + (int) amountScrolled - 4;
        int l = k / slotHeight;
        return p_148124_1_ < getScrollBarX() && p_148124_1_ >= i && p_148124_1_ <= j && l >= 0 && k >= 0 && l < getSize() ? l : -1;
    }

    /**
     * Registers the IDs that can be used for the scrollbar's up/down buttons.
     */
    public void registerScrollButtons(int scrollUpButtonIDIn, int scrollDownButtonIDIn) {
        scrollUpButtonID = scrollUpButtonIDIn;
        scrollDownButtonID = scrollDownButtonIDIn;
    }

    /**
     * Stop the thing from scrolling out of bounds
     */
    protected void bindAmountScrolled() {
        amountScrolled = MathHelper.clamp_float(amountScrolled, 0.0F, (float) func_148135_f());
    }

    public int func_148135_f() {
        return Math.max(0, getContentHeight() - (bottom - top - 4));
    }

    /**
     * Returns the amountScrolled field as an integer.
     */
    public int getAmountScrolled() {
        return (int) amountScrolled;
    }

    public boolean isMouseYWithinSlotBounds(int p_148141_1_) {
        return p_148141_1_ >= top && p_148141_1_ <= bottom && mouseX >= x && mouseX <= x + slotWidth;
    }

    /**
     * Scrolls the slot by the given amount. A positive value scrolls down, and a negative value scrolls up.
     */
    public void scrollBy(int amount) {
        amountScrolled += (float) amount;
        bindAmountScrolled();
        initialClickY = -2;
    }

    public void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == scrollUpButtonID) {
                amountScrolled -= (float) (slotHeight * 2 / 3);
                initialClickY = -2;
                bindAmountScrolled();
            } else if (button.id == scrollDownButtonID) {
                amountScrolled += (float) (slotHeight * 2 / 3);
                initialClickY = -2;
                bindAmountScrolled();
            }
        }
    }

    public void drawScreen(int mouseXIn, int mouseYIn, float partialTicks) {
        if (field_178041_q) {
            mouseX = mouseXIn;
            mouseY = mouseYIn;
            //drawBackground();

            bindAmountScrolled();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();

            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            int k = x;
            int l = top + 4 - (int) amountScrolled;

            if (hasListHeader) {
                drawListHeader(k, l, tessellator);
            }

            drawSelectionBox(k, l, mouseXIn, mouseYIn);
            GlStateManager.disableDepth();
            int i1 = 4;
            if (overlayBackground) {}

            drawScrollBar(tessellator, worldrenderer);

            func_148142_b(mouseXIn, mouseYIn);
            GlStateManager.enableTexture2D();
            GlStateManager.shadeModel(7424);
            GlStateManager.enableAlpha();
            GlStateManager.disableBlend();
        }
    }

    public void handleMouseInput() {
        int x = this.x;
        int x2 = this.x + slotWidth;

        if (isMouseYWithinSlotBounds(mouseY)) {
            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && mouseY >= top && mouseY <= bottom) {
                int k = mouseY - top - headerPadding + (int) amountScrolled - 4;
                int l = k / slotHeight;

                if (l < getSize() && mouseX >= x && mouseX <= x2 && l >= 0 && k >= 0) {
                    elementClicked(l, false, mouseX, mouseY);
                    selectedElement = l;
                } else if (mouseX >= x && mouseX <= x2 && k < 0) {
                    func_148132_a(mouseX - x, mouseY - top + (int) amountScrolled - 4);
                }
            }

            if (Mouse.isButtonDown(0) && getEnabled()) {
                if (initialClickY == -1) {
                    boolean flag1 = true;

                    if (mouseY >= top && mouseY <= bottom) {
                        int l2 = mouseY - top - headerPadding + (int) amountScrolled - 4;
                        int i1 = l2 / slotHeight;

                        if (i1 < getSize() && mouseX >= x && mouseX <= x2 && i1 >= 0 && l2 >= 0) {
                            boolean flag = i1 == selectedElement && Minecraft.getSystemTime() - lastClicked < 250L;
                            elementClicked(i1, flag, mouseX, mouseY);
                            selectedElement = i1;
                            lastClicked = Minecraft.getSystemTime();
                        } else if (mouseX >= x && mouseX <= x2 && l2 < 0) {
                            func_148132_a(mouseX - x, mouseY - top + (int) amountScrolled - 4);
                            flag1 = false;
                        }

                        int i3 = getScrollBarX();
                        int j1 = i3 + 6;

                        if (mouseX >= i3 && mouseX <= j1) {
                            scrollMultiplier = -1.0F;
                            int k1 = func_148135_f();

                            if (k1 < 1) {
                                k1 = 1;
                            }

                            int l1 = (int) ((float) ((bottom - top) * (bottom - top)) / (float) getContentHeight());
                            l1 = MathHelper.clamp_int(l1, 32, bottom - top - 8);
                            scrollMultiplier /= (float) (bottom - top - l1) / (float) k1;
                        } else {
                            scrollMultiplier = 1.0F;
                        }

                        if (flag1) {
                            initialClickY = mouseY;
                        } else {
                            initialClickY = -2;
                        }
                    } else {
                        initialClickY = -2;
                    }
                } else if (initialClickY >= 0) {
                    amountScrolled -= (float) (mouseY - initialClickY) * scrollMultiplier;
                    initialClickY = mouseY;
                }
            } else {
                initialClickY = -1;
            }

            if (Mouse.getEventButton() == 1 && Mouse.getEventButtonState() && mouseY >= top && mouseY <= bottom) {
                int k = mouseY - top - headerPadding + (int) amountScrolled - 4;
                int l = k / slotHeight;

                if (l < getSize() && mouseX >= x && mouseX <= x2 && l >= 0 && k >= 0) {
                    elementRightClicked(l, false, mouseX, mouseY);
                    selectedElement = l;
                } else if (mouseX >= x && mouseX <= x2 && k < 0) {
                    func_148132_a(mouseX - x, mouseY - top + (int) amountScrolled - 4);
                }
            }

            if (Mouse.isButtonDown(1) && getEnabled()) {
                if (initialClickY == -1) {
                    boolean flag1 = true;

                    if (mouseY >= top && mouseY <= bottom) {
                        int l2 = mouseY - top - headerPadding + (int) amountScrolled - 4;
                        int i1 = l2 / slotHeight;

                        if (i1 < getSize() && mouseX >= x && mouseX <= x2 && i1 >= 0 && l2 >= 0) {
                            boolean flag = i1 == selectedElement && Minecraft.getSystemTime() - lastClicked < 250L;
                            elementRightClicked(i1, flag, mouseX, mouseY);
                            selectedElement = i1;
                            lastClicked = Minecraft.getSystemTime();
                        } else if (mouseX >= x && mouseX <= x2 && l2 < 0) {
                            func_148132_a(mouseX - x, mouseY - top + (int) amountScrolled - 4);
                            flag1 = false;
                        }

                        int i3 = getScrollBarX();
                        int j1 = i3 + 6;

                        if (mouseX >= i3 && mouseX <= j1) {
                            scrollMultiplier = -1.0F;
                            int k1 = func_148135_f();

                            if (k1 < 1) {
                                k1 = 1;
                            }

                            int l1 = (int) ((float) ((bottom - top) * (bottom - top)) / (float) getContentHeight());
                            l1 = MathHelper.clamp_int(l1, 32, bottom - top - 8);
                            scrollMultiplier /= (float) (bottom - top - l1) / (float) k1;
                        } else {
                            scrollMultiplier = 1.0F;
                        }

                        if (flag1) {
                            initialClickY = mouseY;
                        } else {
                            initialClickY = -2;
                        }
                    } else {
                        initialClickY = -2;
                    }
                } else if (initialClickY >= 0) {
                    amountScrolled -= (float) (mouseY - initialClickY) * scrollMultiplier;
                    initialClickY = mouseY;
                }
            } else {
                initialClickY = -1;
            }

            int i2 = Mouse.getEventDWheel();

            if (i2 != 0) {
                if (i2 > 0) {
                    i2 = -1;
                } else {
                    i2 = 1;
                }

                amountScrolled += (float) (i2 * slotHeight / 2);
            }
        }
    }

    public boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabledIn) {
        enabled = enabledIn;
    }

    /**
     * Gets the width of the list
     */
    public int getSlotWidth() {
        return slotWidth;
    }

    public void setSlotWidth(int slotWidth) {
        this.slotWidth = slotWidth;
    }

    /**
     * Draws the selection box around the selected slot element.
     */
    protected void drawSelectionBox(int p_148120_1_, int p_148120_2_, int mouseXIn, int mouseYIn) {
        int i = getSize();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();

        for (int entryId = 0; entryId < i; ++entryId) {
            int k = p_148120_2_ + entryId * slotHeight + headerPadding;

            if (k > bottom || k + slotHeight < top) {
                func_178040_a(entryId, p_148120_1_, k);
            }

            if (showSelectionBox && isSelected(entryId)) {
                int i1 = x;
                int j1 = x + slotWidth;
                GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                GlStateManager.disableTexture2D();
                worldrenderer.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
                worldrenderer.pos(i1, k + slotHeight + 2, 0.0D).tex(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                worldrenderer.pos(j1, k + slotHeight + 2, 0.0D).tex(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
                worldrenderer.pos(j1, k - 2, 0.0D).tex(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                worldrenderer.pos(i1, k - 2, 0.0D).tex(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
                worldrenderer.pos(i1 + 1, k + slotHeight + 1, 0.0D).tex(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(j1 - 1, k + slotHeight + 1, 0.0D).tex(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(j1 - 1, k - 1, 0.0D).tex(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                worldrenderer.pos(i1 + 1, k - 1, 0.0D).tex(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
                tessellator.draw();
                GlStateManager.enableTexture2D();
            }

            drawSlot(entryId, p_148120_1_, k, slotHeight, mouseXIn, mouseYIn);
        }
    }

    protected int getScrollBarX() {
        return slotWidth + 124;
    }

    /**
     * Overlays the background to hide scrolled items
     */
    protected void overlayBackground(int startY, int endY, int color) {
        ScaledResolution sr = new ScaledResolution(mc);
        Gui.drawRect(0, startY, sr.getScaledWidth(), endY, color);
    }

    public int getSlotHeight() {
        return slotHeight;
    }

    public boolean isOverlayBackground() {
        return overlayBackground;
    }

    public void setOverlayBackground(boolean overlayBackground) {
        this.overlayBackground = overlayBackground;
    }

    public void drawScrollBar(Tessellator tessellator, WorldRenderer worldrenderer) {
        int j1 = func_148135_f();
        int i = getScrollBarX();
        int j = i + 4;

        if (j1 > 0) {
            int k1 = (bottom - top) * (bottom - top) / getContentHeight();
            k1 = MathHelper.clamp_int(k1, 32, bottom - top - 8);
            int l1 = (int) amountScrolled * (bottom - top - k1) / j1 + top;

            if (l1 < top)
                l1 = top;

            Gui.drawRect(i, top, j, bottom, 0xFF232323);
            Gui.drawRect(i, l1, j, l1 + k1, -1);
        }
    }
}
