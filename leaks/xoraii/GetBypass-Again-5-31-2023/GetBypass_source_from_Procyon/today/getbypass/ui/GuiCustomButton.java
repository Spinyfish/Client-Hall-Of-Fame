// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.ui;

import net.minecraft.client.gui.FontRenderer;
import today.getbypass.GetBypass;
import today.getbypass.utils.RoundedUtils;
import java.awt.Color;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;

public class GuiCustomButton extends GuiButton
{
    public GuiCustomButton(final int buttonId, final int x, final int y, final String buttonText) {
        super(buttonId, x, y, 200, 20, buttonText);
    }
    
    public GuiCustomButton(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }
    
    @Override
    public void drawButton(final Minecraft mc, final int mouseX, final int mouseY) {
        if (this.visible) {
            final FontRenderer fontrenderer = mc.fontRendererObj;
            mc.getTextureManager().bindTexture(GuiCustomButton.buttonTextures);
            GlStateManager.color(1.0f, 1.0f, 1.0f, 1.0f);
            this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height);
            final int i = this.getHoverState(this.hovered);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(770, 771, 1, 0);
            GlStateManager.blendFunc(770, 771);
            int color = new Color(1, 1, 1).getRGB();
            int opacity = 0;
            if (this.hovered) {
                while (opacity != 100) {
                    color = new Color(1, 1, 1, opacity).getRGB();
                    ++opacity;
                }
            }
            else {
                color = new Color(1, 1, 1).getRGB();
            }
            RoundedUtils.drawRoundedRect((float)this.xPosition, (float)this.yPosition, (float)(this.xPosition + this.getButtonWidth()), (float)(this.yPosition + this.height), 6.0f, color);
            this.mouseDragged(mc, mouseX, mouseY);
            GetBypass.normal.drawCenteredString(this.displayString, (float)(this.xPosition + this.width / 2), (float)(this.yPosition + (this.height - 8) / 2 - 4), Color.white);
        }
    }
}
