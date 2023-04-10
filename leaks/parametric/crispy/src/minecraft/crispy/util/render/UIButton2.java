package crispy.util.render;

import crispy.Crispy;
import crispy.fonts.greatfont.TTFFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

import java.awt.*;

public class UIButton2 extends GuiButton {
    private int fade;

    public UIButton2(final int buttonId, final int x, final int y, final int widthIn, final int heightIn, final String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
    }


    public void drawButton(Minecraft p_191745_1_, int mouseX, int mouseY, float p_191745_4_) {
        if (this.visible) {
            if (!(this.hovered = (mouseX >= this.xPosition && mouseY >= this.yPosition && mouseX < this.xPosition + this.width && mouseY < this.yPosition + this.height))) {
                this.fade = 0;
            } else if (this.fade < 60) {
                this.fade += 5;
            }
            final Color a2 = new Color(255, 255, 255, this.fade);
            Gui.drawRect(this.xPosition, this.yPosition, this.xPosition + this.width, this.yPosition + this.height, a2.getRGB());
            final Color a3 = new Color(149, 54, 250, 255);
            Gui.drawRect(this.xPosition - 2, this.yPosition + this.height, this.xPosition + this.width + 2, this.yPosition + this.height + 2, a3.getRGB());
            final Color a4 = new Color(120, 120, 120, 255);
            Gui.drawRect(this.xPosition - 2, this.yPosition, this.xPosition, this.yPosition + this.height, a4.getRGB());
            Gui.drawRect(this.xPosition + this.width, this.yPosition, this.xPosition + this.width + 2, this.yPosition + this.height, a4.getRGB());
            Gui.drawRect(this.xPosition - 2, this.yPosition - 2, this.xPosition + this.width + 2, this.yPosition, a4.getRGB());

            TTFFontRenderer var4 = Crispy.INSTANCE.getFontManager().getFont("clean 15");
            if (var4 != null) {
                drawCenteredString(Crispy.INSTANCE.getFontManager().getFont("clean 15"), this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, Color.white.getRGB());
            } else {
                drawCenteredString(Minecraft.fontRendererObj, this.displayString, this.xPosition + this.width / 2, this.yPosition + (this.height - 8) / 2, Color.white.getRGB());
            }
        }
    }
}
