/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.lwjgl.input.Mouse
 */
package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiResourcePackList;
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
    protected int top;
    protected int bottom;
    protected int right;
    protected int left;
    protected final int slotHeight;
    private int scrollUpButtonID;
    private int scrollDownButtonID;
    protected int mouseX;
    protected int mouseY;
    protected boolean field_148163_i = true;
    protected int initialClickY = -2;
    protected float scrollMultiplier;
    protected float amountScrolled;
    protected int selectedElement = -1;
    protected long lastClicked;
    protected boolean field_178041_q = true;
    protected boolean showSelectionBox = true;
    protected boolean hasListHeader;
    protected int headerPadding;
    private boolean enabled = true;

    public GuiSlot(Minecraft mcIn, int width, int height, int topIn, int bottomIn, int slotHeightIn) {
        this.mc = mcIn;
        this.width = width;
        this.height = height;
        this.top = topIn;
        this.bottom = bottomIn;
        this.slotHeight = slotHeightIn;
        this.left = 0;
        this.right = width;
    }

    public void setDimensions(int widthIn, int heightIn, int topIn, int bottomIn) {
        this.width = widthIn;
        this.height = heightIn;
        this.top = topIn;
        this.bottom = bottomIn;
        this.left = 0;
        this.right = widthIn;
    }

    public void setShowSelectionBox(boolean showSelectionBoxIn) {
        this.showSelectionBox = showSelectionBoxIn;
    }

    protected void setHasListHeader(boolean hasListHeaderIn, int headerPaddingIn) {
        this.hasListHeader = hasListHeaderIn;
        this.headerPadding = headerPaddingIn;
        if (!hasListHeaderIn) {
            this.headerPadding = 0;
        }
    }

    protected abstract int getSize();

    protected abstract void elementClicked(int var1, boolean var2, int var3, int var4);

    protected abstract boolean isSelected(int var1);

    protected int getContentHeight() {
        return this.getSize() * this.slotHeight + this.headerPadding;
    }

    protected abstract void drawBackground();

    protected void func_178040_a(int p_178040_1_, int p_178040_2_, int p_178040_3_) {
    }

    protected abstract void drawSlot(int var1, int var2, int var3, int var4, int var5, int var6);

    protected void drawListHeader(int p_148129_1_, int p_148129_2_, Tessellator p_148129_3_) {
    }

    protected void func_148132_a(int p_148132_1_, int p_148132_2_) {
    }

    protected void func_148142_b(int p_148142_1_, int p_148142_2_) {
    }

    public int getSlotIndexFromScreenCoords(int p_148124_1_, int p_148124_2_) {
        int i = this.left + this.width / 2 - this.getListWidth() / 2;
        int j = this.left + this.width / 2 + this.getListWidth() / 2;
        int k = p_148124_2_ - this.top - this.headerPadding + (int)this.amountScrolled - 4;
        int l = k / this.slotHeight;
        return p_148124_1_ < this.getScrollBarX() && p_148124_1_ >= i && p_148124_1_ <= j && l >= 0 && k >= 0 && l < this.getSize() ? l : -1;
    }

    public void registerScrollButtons(int scrollUpButtonIDIn, int scrollDownButtonIDIn) {
        this.scrollUpButtonID = scrollUpButtonIDIn;
        this.scrollDownButtonID = scrollDownButtonIDIn;
    }

    protected void bindAmountScrolled() {
        this.amountScrolled = MathHelper.clamp_float(this.amountScrolled, 0.0f, this.func_148135_f());
    }

    public int func_148135_f() {
        return Math.max(0, this.getContentHeight() - (this.bottom - this.top - 4));
    }

    public int getAmountScrolled() {
        return (int)this.amountScrolled;
    }

    public boolean isMouseYWithinSlotBounds(int p_148141_1_) {
        return p_148141_1_ >= this.top && p_148141_1_ <= this.bottom && this.mouseX >= this.left && this.mouseX <= this.right;
    }

    public void scrollBy(int amount) {
        this.amountScrolled += (float)amount;
        this.bindAmountScrolled();
        this.initialClickY = -2;
    }

    public void actionPerformed(GuiButton button) {
        if (button.enabled) {
            if (button.id == this.scrollUpButtonID) {
                this.amountScrolled -= (float)(this.slotHeight * 2 / 3);
                this.initialClickY = -2;
                this.bindAmountScrolled();
            } else if (button.id == this.scrollDownButtonID) {
                this.amountScrolled += (float)(this.slotHeight * 2 / 3);
                this.initialClickY = -2;
                this.bindAmountScrolled();
            }
        }
    }

    public void drawScreen(int mouseXIn, int mouseYIn, float p_148128_3_) {
        if (this.field_178041_q) {
            this.mouseX = mouseXIn;
            this.mouseY = mouseYIn;
            this.drawBackground();
            int i = this.getScrollBarX();
            int j = i + 6;
            this.bindAmountScrolled();
            GlStateManager.disableLighting();
            GlStateManager.disableFog();
            Tessellator tessellator = Tessellator.getInstance();
            WorldRenderer worldrenderer = tessellator.getWorldRenderer();
            this.drawContainerBackground(tessellator);
            int k = this.left + this.width / 2 - this.getListWidth() / 2 + 2;
            int l = this.top + 4 - (int)this.amountScrolled;
            if (this.hasListHeader) {
                this.drawListHeader(k, l, tessellator);
            }
            this.drawSelectionBox(k, l, mouseXIn, mouseYIn);
            GlStateManager.disableDepth();
            int i1 = 4;
            this.overlayBackground(0, this.top, 255, 255);
            this.overlayBackground(this.bottom, this.height, 255, 255);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 0, 1);
            GlStateManager.disableAlpha();
            GlStateManager.shadeModel(7425);
            GlStateManager.disableTexture2D();
            worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
            worldrenderer.pos(this.left, this.top + i1, 0.0).func_181673_a(0.0, 1.0).color(0, 0, 0, 0).end();
            worldrenderer.pos(this.right, this.top + i1, 0.0).func_181673_a(1.0, 1.0).color(0, 0, 0, 0).end();
            worldrenderer.pos(this.right, this.top, 0.0).func_181673_a(1.0, 0.0).color(0, 0, 0, 255).end();
            worldrenderer.pos(this.left, this.top, 0.0).func_181673_a(0.0, 0.0).color(0, 0, 0, 255).end();
            tessellator.draw();
            worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
            worldrenderer.pos(this.left, this.bottom, 0.0).func_181673_a(0.0, 1.0).color(0, 0, 0, 255).end();
            worldrenderer.pos(this.right, this.bottom, 0.0).func_181673_a(1.0, 1.0).color(0, 0, 0, 255).end();
            worldrenderer.pos(this.right, this.bottom - i1, 0.0).func_181673_a(1.0, 0.0).color(0, 0, 0, 0).end();
            worldrenderer.pos(this.left, this.bottom - i1, 0.0).func_181673_a(0.0, 0.0).color(0, 0, 0, 0).end();
            tessellator.draw();
            int j1 = this.func_148135_f();
            if (j1 > 0) {
                int k1 = (this.bottom - this.top) * (this.bottom - this.top) / this.getContentHeight();
                int l1 = (int)this.amountScrolled * (this.bottom - this.top - (k1 = MathHelper.clamp_int(k1, 32, this.bottom - this.top - 8))) / j1 + this.top;
                if (l1 < this.top) {
                    l1 = this.top;
                }
                worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
                worldrenderer.pos(i, this.bottom, 0.0).func_181673_a(0.0, 1.0).color(0, 0, 0, 255).end();
                worldrenderer.pos(j, this.bottom, 0.0).func_181673_a(1.0, 1.0).color(0, 0, 0, 255).end();
                worldrenderer.pos(j, this.top, 0.0).func_181673_a(1.0, 0.0).color(0, 0, 0, 255).end();
                worldrenderer.pos(i, this.top, 0.0).func_181673_a(0.0, 0.0).color(0, 0, 0, 255).end();
                tessellator.draw();
                worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
                worldrenderer.pos(i, l1 + k1, 0.0).func_181673_a(0.0, 1.0).color(128, 128, 128, 255).end();
                worldrenderer.pos(j, l1 + k1, 0.0).func_181673_a(1.0, 1.0).color(128, 128, 128, 255).end();
                worldrenderer.pos(j, l1, 0.0).func_181673_a(1.0, 0.0).color(128, 128, 128, 255).end();
                worldrenderer.pos(i, l1, 0.0).func_181673_a(0.0, 0.0).color(128, 128, 128, 255).end();
                tessellator.draw();
                worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
                worldrenderer.pos(i, l1 + k1 - 1, 0.0).func_181673_a(0.0, 1.0).color(192, 192, 192, 255).end();
                worldrenderer.pos(j - 1, l1 + k1 - 1, 0.0).func_181673_a(1.0, 1.0).color(192, 192, 192, 255).end();
                worldrenderer.pos(j - 1, l1, 0.0).func_181673_a(1.0, 0.0).color(192, 192, 192, 255).end();
                worldrenderer.pos(i, l1, 0.0).func_181673_a(0.0, 0.0).color(192, 192, 192, 255).end();
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
            int i2;
            if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState() && this.mouseY >= this.top && this.mouseY <= this.bottom) {
                int i = (this.width - this.getListWidth()) / 2;
                int j = (this.width + this.getListWidth()) / 2;
                int k = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                int l = k / this.slotHeight;
                if (l < this.getSize() && this.mouseX >= i && this.mouseX <= j && l >= 0 && k >= 0) {
                    this.elementClicked(l, false, this.mouseX, this.mouseY);
                    this.selectedElement = l;
                } else if (this.mouseX >= i && this.mouseX <= j && k < 0) {
                    this.func_148132_a(this.mouseX - i, this.mouseY - this.top + (int)this.amountScrolled - 4);
                }
            }
            if (Mouse.isButtonDown((int)0) && this.getEnabled()) {
                if (this.initialClickY != -1) {
                    if (this.initialClickY >= 0) {
                        this.amountScrolled -= (float)(this.mouseY - this.initialClickY) * this.scrollMultiplier;
                        this.initialClickY = this.mouseY;
                    }
                } else {
                    boolean flag1 = true;
                    if (this.mouseY >= this.top && this.mouseY <= this.bottom) {
                        int j2 = (this.width - this.getListWidth()) / 2;
                        int k2 = (this.width + this.getListWidth()) / 2;
                        int l2 = this.mouseY - this.top - this.headerPadding + (int)this.amountScrolled - 4;
                        int i1 = l2 / this.slotHeight;
                        if (i1 < this.getSize() && this.mouseX >= j2 && this.mouseX <= k2 && i1 >= 0 && l2 >= 0) {
                            boolean flag = i1 == this.selectedElement && Minecraft.getSystemTime() - this.lastClicked < 250L;
                            this.elementClicked(i1, flag, this.mouseX, this.mouseY);
                            this.selectedElement = i1;
                            this.lastClicked = Minecraft.getSystemTime();
                        } else if (this.mouseX >= j2 && this.mouseX <= k2 && l2 < 0) {
                            this.func_148132_a(this.mouseX - j2, this.mouseY - this.top + (int)this.amountScrolled - 4);
                            flag1 = false;
                        }
                        int i3 = this.getScrollBarX();
                        int j1 = i3 + 6;
                        if (this.mouseX >= i3 && this.mouseX <= j1) {
                            this.scrollMultiplier = -1.0f;
                            int k1 = this.func_148135_f();
                            if (k1 < 1) {
                                k1 = 1;
                            }
                            int l1 = (int)((float)((this.bottom - this.top) * (this.bottom - this.top)) / (float)this.getContentHeight());
                            l1 = MathHelper.clamp_int(l1, 32, this.bottom - this.top - 8);
                            this.scrollMultiplier /= (float)(this.bottom - this.top - l1) / (float)k1;
                        } else {
                            this.scrollMultiplier = 1.0f;
                        }
                        this.initialClickY = flag1 ? this.mouseY : -2;
                    } else {
                        this.initialClickY = -2;
                    }
                }
            } else {
                this.initialClickY = -1;
            }
            if ((i2 = Mouse.getEventDWheel()) != 0) {
                if (i2 > 0) {
                    i2 = -1;
                } else if (i2 < 0) {
                    i2 = 1;
                }
                this.amountScrolled += (float)(i2 * this.slotHeight / 2);
            }
        }
    }

    public void setEnabled(boolean enabledIn) {
        this.enabled = enabledIn;
    }

    public boolean getEnabled() {
        return this.enabled;
    }

    public int getListWidth() {
        return 220;
    }

    protected void drawSelectionBox(int p_148120_1_, int p_148120_2_, int mouseXIn, int mouseYIn) {
        int i = this.getSize();
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        for (int j = 0; j < i; ++j) {
            int k = p_148120_2_ + j * this.slotHeight + this.headerPadding;
            int l = this.slotHeight - 4;
            if (k > this.bottom || k + l < this.top) {
                this.func_178040_a(j, p_148120_1_, k);
            }
            if (this.showSelectionBox && this.isSelected(j)) {
                int i1 = this.left + (this.width / 2 - this.getListWidth() / 2);
                int j1 = this.left + this.width / 2 + this.getListWidth() / 2;
                GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
                GlStateManager.disableTexture2D();
                worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
                worldrenderer.pos(i1, k + l + 2, 0.0).func_181673_a(0.0, 1.0).color(128, 128, 128, 255).end();
                worldrenderer.pos(j1, k + l + 2, 0.0).func_181673_a(1.0, 1.0).color(128, 128, 128, 255).end();
                worldrenderer.pos(j1, k - 2, 0.0).func_181673_a(1.0, 0.0).color(128, 128, 128, 255).end();
                worldrenderer.pos(i1, k - 2, 0.0).func_181673_a(0.0, 0.0).color(128, 128, 128, 255).end();
                worldrenderer.pos(i1 + 1, k + l + 1, 0.0).func_181673_a(0.0, 1.0).color(0, 0, 0, 255).end();
                worldrenderer.pos(j1 - 1, k + l + 1, 0.0).func_181673_a(1.0, 1.0).color(0, 0, 0, 255).end();
                worldrenderer.pos(j1 - 1, k - 1, 0.0).func_181673_a(1.0, 0.0).color(0, 0, 0, 255).end();
                worldrenderer.pos(i1 + 1, k - 1, 0.0).func_181673_a(0.0, 0.0).color(0, 0, 0, 255).end();
                tessellator.draw();
                GlStateManager.enableTexture2D();
            }
            if (this instanceof GuiResourcePackList && (k < this.top - this.slotHeight || k > this.bottom)) continue;
            this.drawSlot(j, p_148120_1_, k, l, mouseXIn, mouseYIn);
        }
    }

    protected int getScrollBarX() {
        return this.width / 2 + 124;
    }

    protected void overlayBackground(int startY, int endY, int startAlpha, int endAlpha) {
        Tessellator tessellator = Tessellator.getInstance();
        WorldRenderer worldrenderer = tessellator.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
        worldrenderer.pos(this.left, endY, 0.0).func_181673_a(0.0, (float)endY / 32.0f).color(64, 64, 64, endAlpha).end();
        worldrenderer.pos(this.left + this.width, endY, 0.0).func_181673_a((float)this.width / 32.0f, (float)endY / 32.0f).color(64, 64, 64, endAlpha).end();
        worldrenderer.pos(this.left + this.width, startY, 0.0).func_181673_a((float)this.width / 32.0f, (float)startY / 32.0f).color(64, 64, 64, startAlpha).end();
        worldrenderer.pos(this.left, startY, 0.0).func_181673_a(0.0, (float)startY / 32.0f).color(64, 64, 64, startAlpha).end();
        tessellator.draw();
    }

    public void setSlotXBoundsFromLeft(int leftIn) {
        this.left = leftIn;
        this.right = leftIn + this.width;
    }

    public int getSlotHeight() {
        return this.slotHeight;
    }

    protected void drawContainerBackground(Tessellator p_drawContainerBackground_1_) {
        WorldRenderer worldrenderer = p_drawContainerBackground_1_.getWorldRenderer();
        this.mc.getTextureManager().bindTexture(Gui.optionsBackground);
        GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
        float f = 32.0f;
        worldrenderer.begin(7, DefaultVertexFormats.field_181709_i);
        worldrenderer.pos(this.left, this.bottom, 0.0).func_181673_a((float)this.left / f, (float)(this.bottom + (int)this.amountScrolled) / f).color(32, 32, 32, 255).end();
        worldrenderer.pos(this.right, this.bottom, 0.0).func_181673_a((float)this.right / f, (float)(this.bottom + (int)this.amountScrolled) / f).color(32, 32, 32, 255).end();
        worldrenderer.pos(this.right, this.top, 0.0).func_181673_a((float)this.right / f, (float)(this.top + (int)this.amountScrolled) / f).color(32, 32, 32, 255).end();
        worldrenderer.pos(this.left, this.top, 0.0).func_181673_a((float)this.left / f, (float)(this.top + (int)this.amountScrolled) / f).color(32, 32, 32, 255).end();
        p_drawContainerBackground_1_.draw();
    }
}

