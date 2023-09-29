package de.verschwiegener.atero.util.components;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class CustomGuiImageButton extends GuiButton {

    private ResourceLocation loc;
    private int notHoveredWidth, notHoveredHeight;
    private int scale = 0;

    public CustomGuiImageButton(int buttonId, int x, int y, String buttonText, ResourceLocation loc) {
        super(buttonId, x, y, buttonText);
        this.loc = loc;
        this.notHoveredWidth = 200;
        this.notHoveredHeight = 20;
    }

    public CustomGuiImageButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, ResourceLocation loc) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        this.loc = loc;
        this.notHoveredWidth = widthIn;
        this.notHoveredHeight = heightIn;
    }

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        if (this.visible)
        {
            FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(this.loc);
            this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
            int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);

            this.mouseDragged(mc, mouseX, mouseY);

            if(hovered) {
                if(scale < 10 && scale + 1 < 10) scale+=2;
                notHoveredWidth = width + scale;
                notHoveredHeight = height + scale;
            }else{
                if(scale > 0 && scale - 1 > 0) scale-=2;
                notHoveredWidth = width + scale;
                notHoveredHeight = height + scale;
            }
            drawImage(xPosition-scale/2, yPosition-scale/2, notHoveredWidth, notHoveredHeight, loc);
        }
    }

    private void drawImage(int x, int y, int width, int height, ResourceLocation resourceLocation) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(resourceLocation);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableAlpha();
        Gui.drawModalRectWithCustomSizedTexture(x, y, 0, 0, width, height, width, height);
        GlStateManager.disableAlpha();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    }

}
