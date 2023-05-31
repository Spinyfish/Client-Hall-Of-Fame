// 
// Decompiled by Procyon v0.5.36
// 

package today.getbypass.ui.clickgui.mb;

import net.minecraft.client.Minecraft;
import today.getbypass.GetBypass;
import net.minecraft.client.renderer.GlStateManager;
import today.getbypass.utils.RoundedUtils;
import java.awt.Color;
import today.getbypass.ui.clickgui.ClickGUI;
import today.getbypass.module.Category;

public class CategoryButton
{
    public Category category;
    public ClickGUI guiInstance;
    int uinx;
    int x;
    int y;
    int w;
    int h;
    int offsetX;
    int textOffset;
    
    public CategoryButton(final int textOffset, final int categoryOffsetX, final int x, final int y, final ClickGUI guiInstance, final Category category) {
        this.w = 120;
        this.h = 35;
        this.category = category;
        this.guiInstance = guiInstance;
        this.x = x;
        this.uinx = x;
        this.y = y + categoryOffsetX;
        this.offsetX = categoryOffsetX;
        this.textOffset = textOffset;
    }
    
    public void drawButton(final int mouseX, final int mouseY) {
        final boolean isMouseOnButton = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.w && mouseY < this.y + this.h;
        RoundedUtils.drawRoundedRect((float)this.x, (float)this.y, (float)(this.x + this.w), (float)(this.y + this.h), 8.0f, new Color(50, 50, 50).getRGB());
        GlStateManager.pushMatrix();
        GlStateManager.scale(1.2, 1.2, 2.0);
        GetBypass.normal.drawString(this.category.toString(), (float)(this.x + 3 - 110 - 40 + 110), (float)(this.textOffset / 2 + this.uinx - 138 + Minecraft.getMinecraft().fontRendererObj.FONT_HEIGHT + 2), isMouseOnButton ? Color.blue : ((this.guiInstance.currentCategory == this.category) ? Color.white : GetBypass.blueClient));
        GlStateManager.popMatrix();
    }
    
    public void mouseClick(final int mouseX, final int mouseY) {
        final boolean isMouseOnButton = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.w && mouseY < this.y + this.h;
        if (isMouseOnButton) {
            this.guiInstance.currentCategory = this.category;
        }
    }
}
