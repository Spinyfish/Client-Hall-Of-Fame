package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.realms.RealmsSimpleScrolledSelectionList;
import net.minecraft.util.MathHelper;

public class GuiSimpleScrolledSelectionListProxy extends GuiSlot {
    private final RealmsSimpleScrolledSelectionList field_178050_u;

    public GuiSimpleScrolledSelectionListProxy(final RealmsSimpleScrolledSelectionList p_i45525_1_, final int widthIn, final int heightIn, final int topIn, final int bottomIn, final int slotHeightIn) {
        super(Minecraft.getMinecraft(), widthIn, heightIn, topIn, bottomIn, slotHeightIn);
        this.field_178050_u = p_i45525_1_;
    }

    protected int getSize() {
        return this.field_178050_u.getItemCount();
    }

    /**
     * The element in the slot that was clicked, boolean for whether it was double clicked or not
     */
    protected void elementClicked(final int slotIndex, final boolean isDoubleClick, final int mouseX, final int mouseY) {
        this.field_178050_u.selectItem(slotIndex, isDoubleClick, mouseX, mouseY);
    }

    /**
     * Returns true if the element passed in is currently selected
     */
    protected boolean isSelected(final int slotIndex) {
        return this.field_178050_u.isSelectedItem(slotIndex);
    }

    protected void drawBackground() {
        this.field_178050_u.renderBackground();
    }

    protected void drawSlot(final int entryID, final int p_180791_2_, final int p_180791_3_, final int p_180791_4_, final int mouseXIn, final int mouseYIn) {
        this.field_178050_u.renderItem(entryID, p_180791_2_, p_180791_3_, p_180791_4_, mouseXIn, mouseYIn);
    }

    public int getWidth() {
        return super.width;
    }

    public int getMouseY() {
        return super.mouseY;
    }

    public int getMouseX() {
        return super.mouseX;
    }

    /**
     * Return the height of the content being scrolled
     */
    protected int getContentHeight() {
        return this.field_178050_u.getMaxPosition();
    }

    protected int getScrollBarX() {
        return this.field_178050_u.getScrollbarPosition();
    }

    public void handleMouseInput() {
        super.handleMouseInput();
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
}
