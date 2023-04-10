package crispy.util;

import arithmo.gui.altmanager.Colors;

import crispy.Crispy;
import crispy.fonts.decentfont.FontUtil;
import crispy.fonts.greatfont.FontManager;
import crispy.util.animation.Translate;
import crispy.util.render.gui.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;

import java.awt.*;

public class MainButton extends GuiButton {
    public MainButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }

    private final Translate translate = new Translate(0, 0);

    @Override
    public void drawButton(Minecraft mc, int mouseX, int mouseY) {
        float translationFactor = (float) (14.4 / (float) Minecraft.getDebugFPS());
        this.hovered = mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height;
        if(hovered) {
            translate.interpolate(100, 100, translationFactor);
        } else {
            translate.interpolate(50, 0, translationFactor);
        }
        if (this.visible) {

            RenderUtil.color(1, 1, 1, 1);

            RenderUtil.drawRoundedRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height,3, new Color(0, 0, 0, (int) translate.getX()).getRGB());
            Crispy.INSTANCE.getFontManager().getFont("ROBO 22").drawCenteredString(this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 10) / 2, Colors.getColor(255, 255, 255));
        }
    }
}
