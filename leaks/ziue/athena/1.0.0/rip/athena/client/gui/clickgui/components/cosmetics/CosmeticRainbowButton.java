package rip.athena.client.gui.clickgui.components.cosmetics;

import java.awt.*;
import rip.athena.client.gui.framework.draw.*;
import rip.athena.client.utils.render.*;
import rip.athena.client.modules.impl.other.*;
import rip.athena.client.utils.font.*;
import net.minecraft.client.*;

public class CosmeticRainbowButton extends CosmeticGenericButton
{
    public CosmeticRainbowButton(final String text, final int x, final int y, final int width, final int height) {
        super(text, x, y, width, height, true);
    }
    
    @Override
    public void onInitColors() {
        this.setColor(DrawType.BACKGROUND, ButtonState.NORMAL, new Color(35, 35, 35, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.ACTIVE, new Color(44, 44, 48, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVER, new Color(28, 28, 31, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.HOVERACTIVE, new Color(54, 54, 59, 255));
        this.setColor(DrawType.BACKGROUND, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.LINE, ButtonState.NORMAL, new Color(46, 46, 48, 255));
        this.setColor(DrawType.LINE, ButtonState.ACTIVE, new Color(63, 63, 66, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVER, new Color(58, 58, 61, 255));
        this.setColor(DrawType.LINE, ButtonState.HOVERACTIVE, new Color(76, 76, 79, 255));
        this.setColor(DrawType.LINE, ButtonState.DISABLED, new Color(100, 100, 100, 255));
        this.setColor(DrawType.TEXT, ButtonState.NORMAL, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.ACTIVE, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVER, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.HOVERACTIVE, new Color(255, 255, 255, 255));
        this.setColor(DrawType.TEXT, ButtonState.DISABLED, new Color(255, 255, 255, 255));
    }
    
    @Override
    public void onRender() {
        final int x = this.getRenderX();
        final int y = this.getRenderY();
        final int width = (this.width == -1 && this.height == -1) ? (this.getStringWidth(this.text) + this.minOffset * 2) : this.width;
        final int height = (this.width == -1 && this.height == -1) ? (this.getStringHeight(this.text) + this.minOffset * 2) : this.height;
        final int backgroundColor = this.getColor(DrawType.BACKGROUND, this.lastState);
        final int lineColor = this.getColor(DrawType.LINE, this.lastState);
        final int textColor = this.getColor(DrawType.TEXT, this.lastState);
        if (this.filledBackground) {
            DrawImpl.drawRect(x, y, width, height, backgroundColor);
        }
        DrawUtils.drawRoundedRect(x, y, x + width, y + height, 4.0f, new Color(50, 50, 50, 255).getRGB());
        DrawUtils.drawRoundedRect(x + 1, y + 1, x + width - 1, y + height - 1, 4.0f, new Color(35, 35, 35, 255).getRGB());
        if (Settings.customGuiFont) {
            FontManager.getProductSansRegular(30).drawString(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + (height / 2 - this.getStringHeight(this.text) / 2), -1);
        }
        else {
            Minecraft.getMinecraft().fontRendererObj.drawString(this.text, x + (width / 2 - this.getStringWidth(this.text) / 2), y + (height / 2 - this.getStringHeight(this.text) / 2), -1);
        }
        this.mouseDown = false;
    }
}
